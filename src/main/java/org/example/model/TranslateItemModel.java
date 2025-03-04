package org.example.model;

import org.example.dao.TranslateItemDAO;

import java.awt.Component;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

public class TranslateItemModel {

  private final int id;
  private final String fr;
  private final String pt;
  private final String category;
  private final String difficultStr;

  // Construtor
  public TranslateItemModel (int id, String fr, String pt, String category, String difficultString){
    this.id = id;
    this.fr = fr;
    this.pt = pt;
    this.category = category;
    this.difficultStr = difficultString;
  }

  // Getters e Setters
  public int id () {return id;}

  public String fr () {return fr;}

  public String pt () {return pt;}

  public String category () {return category;}

  public String difficult () {return difficultStr;}

  public int difficultId () throws SQLException {return getDifficultyIdByName(difficultStr);}

  // Métodos Estáticos
  public static List<String[]> getTranslationItems () throws SQLException {
    return TranslateItemDAO.getTranslations();
  }

  public static String[] getTranslationDifficulties () throws SQLException {
    List<String[]> items = TranslateItemDAO.getDifficulties();
    return mapArrayList(items, 1);
  }

  public static int getDifficultyIdByName (String difficultyName) throws SQLException {
    List<String[]> items = TranslateItemDAO.getDifficulties();

    for (String[] item : items) if (item[1].equals(difficultyName)) return Integer.parseInt(item[0]);

    return -1; // Caso a dificuldade não seja encontrada, retorna −1
  }

  public static String[] getTranslationCategories () throws SQLException {
    List<String[]> items = TranslateItemDAO.getTranslations();
    return mapArrayList(items, 3);
  }

  public static void validateDeleteItem (TranslateItemModel item, Component view) {
    if (item == null) return;

    String title = "Confirm Deletion";
    String msg = "Are you sure you want to delete this item?";

    try {

      int response = JOptionPane.showConfirmDialog(view, msg, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
      if (response == JOptionPane.YES_OPTION) {
        TranslateItemDAO.deleteTranslation(item.id());
      }
    } catch (SQLException e) {
      msg = "Error deleting item: " + e.getMessage();
      JOptionPane.showMessageDialog(view, msg, "ERROR - SQLException", JOptionPane.ERROR_MESSAGE);
    }
  }

  private static String[] mapArrayList (List<String[]> items, int colIndex) {
    Set<String> colSet = new HashSet<>();
    for (String[] item : items) colSet.add(item[colIndex]);
    return colSet.toArray(new String[0]);
  }
}
