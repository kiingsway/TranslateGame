package org.example.controller.database;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.example.view.database.DBBatchAddView;

import javax.swing.*;

public class DBBatchAddController {

  private final DBBatchAddView view;
  private final Runnable updateTranslateList;

  public DBBatchAddController (DBBatchAddView view, Runnable updateTranslateList) {
    this.view = view;
    this.updateTranslateList = updateTranslateList;

    view.btnSave().addActionListener(_ -> handleJsonText());
  }

  private void handleJsonText () {
    String jsonString = view.jsonTextArea().getText();
    if (!jsonString.isEmpty()) {
      try {
        JsonArray jsonArray = JsonParser.parseString(jsonString).getAsJsonArray();
        //        int insertedCount = insertTranslationAndGetCount(jsonArray);
        int insertedCount = jsonArray.size();
        if (insertedCount == 0) {
          JOptionPane.showMessageDialog(view, "Nenhum item inserido.");
        } else {
          JOptionPane.showMessageDialog(view, insertedCount + " item(s) inserido(s) com sucesso!");
          updateTranslateList.run();
          view.dispose();
        }
      } catch (Exception e) {
        String message = "Error processing JSON: " + e.getMessage();
        JOptionPane.showMessageDialog(view, message, "ERROR - Exception", JOptionPane.ERROR_MESSAGE);
      }
    } else {
      JOptionPane.showMessageDialog(view, "Por favor, insira um JSON válido.");
    }
  }
}
