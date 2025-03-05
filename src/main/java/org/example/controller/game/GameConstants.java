package org.example.controller.game;

import java.text.Normalizer;

public class GameConstants {

  public static boolean isSameText (String t1, String t2) {
    String text1 = normalizeText(t1), text2 = normalizeText(t2);
    return text1.equals(text2);
  }

  private static String normalizeText (String text) {
    if (text == null) return "";

    String regex = "[\\p{InCombiningDiacriticalMarks}'-.]";

    return Normalizer.normalize(text, Normalizer.Form.NFD) // Separa acentos das letras
            .replaceAll(regex, "") // Remove acentos, apóstrofos, hífens e pontos
            .toLowerCase(); // Texto minúsculo
  }
}
