package org.example.model;

import org.example.dao.TranslateItemDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GameSettings {
  private int totalQuestions;
  private String category;
  private String difficult;

  private final List<TranslateItemModel> translationItems;

  public GameSettings (Integer totalQuestions, String category, String difficult) throws SQLException {

    this.translationItems = getTranslationModelItems();
    String firstCategory = translationItems.getFirst().category();

    this.totalQuestions = (totalQuestions != null) ? totalQuestions : 20;
    this.category = (category != null) ? category : firstCategory;
    this.difficult = (difficult != null) ? difficult : "Easy";

  }

  // Getters e Setters
  public int totalQuestions () {return totalQuestions;}

  public String category () {return category;}

  public String difficult () {return difficult;}

  public void setTotalQuestions (int totalQuestions) {this.totalQuestions = totalQuestions;}

  public void setCategory (String category) {this.category = category;}

  public void setDifficult (String difficult) {this.difficult = difficult;}

  private List<TranslateItemModel> getTranslationModelItems () throws SQLException {

    List<String[]> items = TranslateItemDAO.getTranslations();
    List<TranslateItemModel> translationItems = new ArrayList<>();

    for (String[] item : items) {
      int id = Integer.parseInt(item[0]);
      String fr = item[1], pt = item[2], cat = item[3], diff = item[4];

      TranslateItemModel itemModel = new TranslateItemModel(id, fr, pt, cat, diff);
      translationItems.add(itemModel);
    }

    return translationItems;
  }

  public List<TranslateItemModel> translationItems () {return translationItems;}
}
