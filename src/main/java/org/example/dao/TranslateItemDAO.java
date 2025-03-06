package org.example.dao;

import org.example.model.TranslateItemModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TranslateItemDAO {

  private static final String DB_URL = "jdbc:sqlite:database";

  private static final String translationTableSQL = """
          CREATE TABLE IF NOT EXISTS translations (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            fr TEXT NOT NULL UNIQUE,
            pt TEXT NOT NULL,
            category TEXT NOT NULL,
            difficultId INTEGER NOT NULL,
            FOREIGN KEY (difficultId) REFERENCES difficulties(id) ON DELETE CASCADE
          );""";

  public static void ensureData () throws SQLException {
    ensureDifficultData();
    ensureTranslationsData();
  }

  private static void ensureDifficultData () throws SQLException {
    String tableSQL = """
            CREATE TABLE IF NOT EXISTS difficulties (
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              title TEXT NOT NULL UNIQUE
            );
            """;

    String itemsSQL = """
            INSERT OR IGNORE INTO difficulties (title) VALUES
              ('Easy'),
              ('Medium'),
              ('Hard');
            """;

    try (Connection conn = DriverManager.getConnection(DB_URL); Statement stmt = conn.createStatement()) {
      stmt.execute(tableSQL);
      stmt.executeUpdate(itemsSQL);
    } catch (SQLException e) {
      String msg = "Error creating 'difficulties' table:\n" + e.getMessage();
      throw new SQLException(msg);
    }
  }

  private static void ensureTranslationsData () throws SQLException {
    try (Connection conn = DriverManager.getConnection(DB_URL); Statement stmt = conn.createStatement()) {

      // Definir o PRAGMA para permitir leituras concorrentes
      stmt.execute("PRAGMA journal_mode = WAL;");

      // Criar a tabela se ela não existir
      stmt.execute(translationTableSQL);

      // Verificar se a tabela está vazia
      String countQuery = "SELECT COUNT(*) FROM translations";
      try (ResultSet rs = stmt.executeQuery(countQuery)) {
        if (rs.next() && rs.getInt(1) == 0) {
          restoreTranslateItems(); // Restaurar itens se a tabela estiver vazia
        }
      }
    } catch (SQLException e) {
      String msg = "Error creating 'translations' table:\n" + e.getMessage();
      throw new SQLException(msg);
    }
  }


  public static List<String[]> getTranslations () throws SQLException {
    List<String[]> translations = new ArrayList<>();
    String sql = """
            SELECT t.id, t.fr, t.pt, t.category, td.title AS difficultTitle
            FROM translations t
            JOIN difficulties td ON t.difficultId = td.id
            """;

    try (Connection conn = DriverManager.getConnection(DB_URL); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
      while (rs.next()) {
        translations.add(new String[]{                          // Columns:
                String.valueOf(rs.getInt("id")),     // ID
                rs.getString("fr"),                  // FR
                rs.getString("pt"),                  // PT
                rs.getString("category"),            // Category
                rs.getString("difficultTitle")       // Difficult Title
        });
      }
    } catch (SQLException e) {
      String msg = "Error getting translation items:\n" + e.getMessage();
      throw new SQLException(msg);
    }
    return translations;
  }

  public static List<String[]> getDifficulties () throws SQLException {
    List<String[]> difficulties = new ArrayList<>();
    String sql = "SELECT * FROM difficulties;";

    try (Connection conn = DriverManager.getConnection(DB_URL); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
      while (rs.next()) {
        difficulties.add(new String[]{                          // Columns:
                String.valueOf(rs.getInt("id")),     // ID
                rs.getString("title"),                  // Title
        });
      }
    } catch (SQLException e) {
      String msg = "Error getting translation difficulties:\n" + e.getMessage();
      throw new SQLException(msg);
    }

    return difficulties;
  }

  public static void deleteTranslation (int id) throws SQLException {
    String sql = "DELETE FROM translations WHERE id = ?";
    try (Connection conn = DriverManager.getConnection(DB_URL); PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setInt(1, id);
      stmt.executeUpdate();
    }
  }

  public static void insertTranslation (TranslateItemModel item) throws SQLException {
    String sql = "INSERT INTO translations (fr, pt, category, difficultId) VALUES (?, ?, ?, ?)";
    try (Connection conn = DriverManager.getConnection(DB_URL); PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, item.fr());
      stmt.setString(2, item.pt());
      stmt.setString(3, item.category());
      stmt.setInt(4, item.difficultId());
      stmt.executeUpdate();
    }
  }

  public static void updateTranslation (TranslateItemModel item) throws SQLException {
    String sql = "UPDATE translations SET fr = ?, pt = ?, category = ?, difficultId = ? WHERE id = ?";
    try (Connection conn = DriverManager.getConnection(DB_URL); PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, item.fr());
      stmt.setString(2, item.pt());
      stmt.setString(3, item.category());
      stmt.setInt(4, item.difficultId());
      stmt.setInt(5, item.id());
      stmt.executeUpdate();
    }
  }

  public static void restoreTranslateItems () throws SQLException {

    try (Connection conn = DriverManager.getConnection(DB_URL)) {
      Statement stmt = conn.createStatement();

      // 1. Obter todas as traduções atuais
      ResultSet rs = stmt.executeQuery("SELECT id, fr, pt, category, difficultId FROM translations");
      List<String> currentTranslations = new ArrayList<>();

      while (rs.next()) {
        String entry = String.format("INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (%d, '%s', '%s', '%s', %d);", rs.getInt("id"), rs.getString("fr"), rs.getString("pt"), rs.getString("category"), rs.getInt("difficultId"));
        currentTranslations.add(entry);
      }

      // 2. Remover os itens que já estão na lista padrão
      List<String> missingTranslations = new ArrayList<>(currentTranslations);
      missingTranslations.removeAll(DEFAULT.INSERTS);

      // 3. Criar a nova tabela manualmente
      stmt.execute("DROP TABLE IF EXISTS translations_new;");
      stmt.execute(translationTableSQL.replaceAll("translations", "translations_new"));

      // 4. Inserir os itens padrão primeiro
      for (String insert : DEFAULT.INSERTS) {
        stmt.execute(insert.replace("translations", "translations_new"));
      }

      // 5. Inserir os itens restantes com novo ID (ID será auto incrementado)
      for (String insert : missingTranslations) {
        String values = insert.substring(insert.indexOf("VALUES") + 6);
        String newInsert = String.format("INSERT INTO translations_new (fr, pt, category, difficultId) VALUES %s", values.substring(values.indexOf("(") + 1));
        stmt.execute(newInsert);
      }

      // 6. Substituir a tabela antiga pela nova
      stmt.execute("DROP TABLE translations;");
      stmt.execute("ALTER TABLE translations_new RENAME TO translations;");

    } catch (SQLException e) {
      String msg = "Error restoring translate items:\n" + e.getMessage();
      throw new SQLException(msg);
    }
  }
}

class DEFAULT {
  public static final List<String> INSERTS = Arrays.asList("INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (1, 'chez', 'na casa de', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (2, 'J''ai parlé', 'Eu falei', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (3, 'Après cela', 'Depois disso', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (4, 'Je suis allé', 'Eu fui', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (5, 'Nous avons mangé', 'Nós comemos', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (6, 'Ils ont bu', 'Eles beberam', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (7, 'Tu as regardé', 'Você assistiu', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (8, 'Elle a écouté', 'Ela ouviu', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (9, 'Nous avons dormi', 'Nós dormimos', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (10, 'Il a écrit', 'Ele escreveu', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (11, 'Je lis', 'Eu leio', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (12, 'Vous avez dit', 'Vocês disseram', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (13, 'J''ai un chien', 'Eu tenho um cachorro', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (14, 'Tu as un livre', 'Você tem um livro', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (15, 'Il a une voiture', 'Ele tem um carro', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (16, 'Elle a une idée', 'Ela tem uma ideia', 'temps de verbs', 2);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (17, 'Nous avons une maison', 'Nós temos uma casa', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (18, 'Vous avez une question', 'Vocês têm uma pergunta', 'temps de verbs', 2);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (19, 'Ils ont des amis', 'Eles têm amigos', 'temps de verbs', 2);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (20, 'Elles ont une solution', 'Elas têm uma solução', 'temps de verbs', 2);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (21, 'J''avais un rêve', 'Eu tinha um sonho', 'temps de verbs', 3);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (22, 'Tu avais du temps', 'Você tinha tempo', 'temps de verbs', 3);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (23, 'Il avait une chance', 'Ele tinha uma chance', 'temps de verbs', 3);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (24, 'Elle avait raison', 'Ela tinha razão', 'temps de verbs', 3);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (25, 'Nous avions un projet', 'Nós tínhamos um projeto', 'temps de verbs', 3);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (26, 'Vous aviez une bonne idée', 'Vocês tinham uma boa ideia', 'temps de verbs', 3);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (27, 'Ils avaient un problème', 'Eles tinham um problema', 'temps de verbs', 3);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (28, 'Elles avaient un rendez-vous', 'Elas tinham um compromisso', 'temps de verbs', 3);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (29, 'J''aurai une opportunité', 'Eu terei uma oportunidade', 'temps de verbs', 3);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (30, 'Tu auras une surprise', 'Você terá uma surpresa', 'temps de verbs', 3);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (31, 'Il aura du courage', 'Ele terá coragem', 'temps de verbs', 3);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (32, 'Elle aura du succès', 'Ela terá sucesso', 'temps de verbs', 3);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (33, 'Nous aurons de la chance', 'Nós teremos sorte', 'temps de verbs', 3);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (34, 'Vous aurez du travail', 'Vocês terão trabalho', 'temps de verbs', 3);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (35, 'Ils auront un examen', 'Eles terão uma prova', 'temps de verbs', 3);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (36, 'Elles auront des nouvelles', 'Elas terão notícias', 'temps de verbs', 3);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (37, 'J''ai eu un problème', 'Eu tive um problema', 'temps de verbs', 3);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (38, 'Tu as eu une idée', 'Você teve uma ideia', 'temps de verbs', 3);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (39, 'Il a eu un accident', 'Ele teve um acidente', 'temps de verbs', 3);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (40, 'Elle a eu une bonne note', 'Ela teve uma boa nota', 'temps de verbs', 3);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (41, 'Nous avons eu des invités', 'Nós tivemos convidados', 'temps de verbs', 3);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (42, 'Vous avez eu une promotion', 'Vocês tiveram uma promoção', 'temps de verbs', 4);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (43, 'Ils ont eu un match', 'Eles tiveram um jogo', 'temps de verbs', 4);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (44, 'Elles ont eu une réunion', 'Elas tiveram uma reunião', 'temps de verbs', 4);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (45, 'J''aurais du temps', 'Eu teria tempo', 'temps de verbs', 4);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (46, 'Tu aurais une chance', 'Você teria uma chance', 'temps de verbs', 4);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (47, 'Il aurait un travail', 'Ele teria um trabalho', 'temps de verbs', 4);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (48, 'Elle aurait une maison', 'Ela teria uma casa', 'temps de verbs', 4);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (49, 'Nous aurions des vacances', 'Nós teríamos férias', 'temps de verbs', 4);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (50, 'Vous auriez une meilleure option', 'Vocês teriam uma opção melhor', 'temps de verbs', 4);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (51, 'Ils auraient une opportunité', 'Eles teriam uma oportunidade', 'temps de verbs', 4);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (52, 'Elles auraient une solution', 'Elas teriam uma solução', 'temps de verbs', 4);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (53, 'Elles sont venues', 'Elas vieram', 'temps de verbs', 4);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (54, 'Il est parti', 'Ele partiu', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (55, 'Nous sommes entrés', 'Nós entramos', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (56, 'Ils sont sortis', 'Eles saíram', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (57, 'Je reste ici', 'Eu fico aqui', 'temps de verbs', 2);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (58, 'Tu mets la table', 'Você põe a mesa', 'temps de verbs', 2);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (59, 'Nous avons pris le bus', 'Nós pegamos o ônibus', 'temps de verbs', 2);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (60, 'Je pense à toi', 'Eu penso em você', 'temps de verbs', 3);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (61, 'Elle croit en Dieu', 'Ela acredita em Deus', 'temps de verbs', 3);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (62, 'Nous voulons partir', 'Nós queremos partir', 'temps de verbs', 3);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (63, 'Tu peux m''aider', 'Você pode me ajudar', 'temps de verbs', 2);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (64, 'Il doit travailler', 'Ele deve trabalhar', 'temps de verbs', 2);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (65, 'J''aime voyager', 'Eu gosto de viajar', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (66, 'Elle déteste attendre', 'Ela odeia esperar', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (67, 'Vous allez à Paris', 'Vocês vão para Paris', 'temps de verbs', 2);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (68, 'Ils font du sport', 'Eles fazem esporte', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (69, 'Tu regardes la télé', 'Você assiste TV', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (70, 'Nous entendons un bruit', 'Nós ouvimos um barulho', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (71, 'Je vois un chat', 'Eu vejo um gato', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (72, 'Elle entend bien', 'Ela escuta bem', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (73, 'Ils habitent ici', 'Eles moram aqui', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (74, 'Tu apprends vite', 'Você aprende rápido', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (75, 'Nous comprenons la leçon', 'Nós entendemos a lição', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (76, 'Je travaille demain', 'Eu trabalho amanhã', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (77, 'Il ouvre la porte', 'Ele abre a porta', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (78, 'Elles ferment la fenêtre', 'Elas fecham a janela', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (79, 'Vous demandez de l''aide', 'Vocês pedem ajuda', 'temps de verbs', 2);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (80, 'Je réponds à l''email', 'Eu respondo ao e-mail', 'temps de verbs', 2);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (81, 'Ils attendent le bus', 'Eles esperam o ônibus', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (82, 'Nous trouvons une solution', 'Nós encontramos uma solução', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (83, 'Tu donnes un cadeau', 'Você dá um presente', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (84, 'Je cherche mon téléphone', 'Eu procuro meu telefone', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (85, 'Elle porte une robe bleue', 'Ela veste um vestido azul', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (86, 'Nous arrivons bientôt', 'Nós chegamos em breve', 'temps de verbs', 3);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (87, 'Vous partez ce soir', 'Vocês partem esta noite', 'temps de verbs', 2);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (88, 'Il tombe dans l''eau', 'Ele cai na água', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (89, 'J''entends de la musique', 'Eu ouço música', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (90, 'Le chien dort sous le canapé', 'O cachorro está dormindo debaixo do sofá', 'prépositions de lieu', 2);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (91, 'Le chien dort sous le canapé.', 'O cachorro está dormindo debaixo do sofá.', 'prépositions de lieu', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (92, 'L''oiseau est dans la cage', 'O pássaro está na gaiola', 'prépositions de lieu', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (93, 'Le tableau est accroché au mur', 'O quadro está pendurado na parede', 'prépositions de lieu', 2);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (94, 'Les fleurs sont devant la fenêtre', 'As flores estão na frente da janela', 'prépositions de lieu', 2);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (95, 'Elle se cache derrière le rideau', 'Ela está se escondendo atrás da cortina', 'prépositions de lieu', 2);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (96, 'Le chat se trouve entre la chaise et la table', 'O gato está entre a cadeira e a mesa', 'prépositions de lieu', 3);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (97, 'Le magasin est en face du cinéma', 'A loja está em frente ao cinema', 'prépositions de lieu', 2);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (98, 'Nous sommes à côté du parc', 'Nós estamos ao lado do parque', 'prépositions de lieu', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (99, 'Il y a un arbre au-dessus de la maison', 'Há uma árvore acima da casa', 'prépositions de lieu', 3);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (100, 'La voiture est garée près de la banque', 'O carro está estacionado perto do banco', 'prépositions de lieu', 2);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (101, 'Le restaurant est loin de la gare', 'O restaurante está longe da estação de trem', 'prépositions de lieu', 3);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (102, 'Le livre est caché sous le lit', 'O livro está escondido debaixo da cama', 'prépositions de lieu', 2);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (103, 'Ils sont assis près de la fenêtre', 'Eles estão sentados perto da janela', 'prépositions de lieu', 2);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (104, 'Le chat a sauté par-dessus la clôture', 'O gato pulou por cima da cerca', 'prépositions de lieu', 3);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (105, 'Le tableau est au-dessus du canapé', 'O quadro está acima do sofá', 'prépositions de lieu', 3);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (106, 'La clé est sur la table', 'A chave está sobre a mesa', 'prépositions de lieu', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (107, 'Le livre est dans la boîte', 'O livro está dentro da caixa', 'prépositions de lieu', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (108, 'Les enfants jouent devant la maison', 'As crianças estão brincando na frente da casa', 'prépositions de lieu', 2);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (109, 'La banque est en face de l''école', 'O banco está em frente à escola', 'prépositions de lieu', 2);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (110, 'Nous nous trouvons entre les deux bâtiments', 'Estamos entre os dois prédios', 'prépositions de lieu', 2);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (111, 'Le chat est sous la table', 'O gato está embaixo da mesa', 'prépositions de lieu', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (112, 'Le restaurant est à côté du parc', 'O restaurante está ao lado do parque', 'prépositions de lieu', 2);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (113, 'La voiture est derrière la maison', 'O carro está atrás da casa', 'prépositions de lieu', 2);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (114, 'Les enfants sont parmi les arbres', 'As crianças estão entre as árvores', 'prépositions de lieu', 3);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (115, 'La montagne est au-delà de la vallée', 'A montanha está além do vale', 'prépositions de lieu', 3);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (116, 'Je vois', 'Eu vejo', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (117, 'Je mange', 'Eu como', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (118, 'Je bois', 'Eu bebo', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (119, 'Je parle', 'Eu falo', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (120, 'Je marche', 'Eu ando', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (121, 'Je dors', 'Eu durmo', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (122, 'J''écoute', 'Eu escuto', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (123, 'Je travaille', 'Eu trabalho', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (124, 'Je prends', 'Eu pego', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (125, 'Je veux', 'Eu quero', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (126, 'Je peux', 'Eu posso', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (127, 'Je sais', 'Eu sei', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (128, 'Je comprends', 'Eu entendo', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (129, 'Je pense', 'Eu penso', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (130, 'Je dis', 'Eu digo', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (131, 'J''écris', 'Eu escrevo', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (132, 'J''étudie', 'Eu estudo', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (133, 'Je chante', 'Eu canto', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (134, 'Je danse', 'Eu danço', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (135, 'Je cours', 'Eu corro', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (136, 'Je joue', 'Eu jogo', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (137, 'Je regarde', 'Eu assisto', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (138, 'Je visite', 'Eu visito', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (139, 'Je sors', 'Eu saio', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (140, 'Je rentre', 'Eu volto para casa', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (141, 'Je reste', 'Eu fico', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (142, 'J''achète', 'Eu compro', 'temps de verbs', 1);", "INSERT INTO translations (id, fr, pt, category, difficultId) VALUES (143, 'Je vends', 'Eu vendo', 'temps de verbs', 1);");
}