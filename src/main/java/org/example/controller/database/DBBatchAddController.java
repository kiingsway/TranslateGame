package org.example.controller.database;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.example.view.database.DBBatchAddView;

import javax.swing.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static org.example.view.ViewConstants.GO_DATABASE;
import static org.example.view.ViewConstants.SHOW_ERROR_DIALOG;

public class DBBatchAddController {

  private final DBBatchAddView view;
  private final Runnable updateTranslateList;

  public DBBatchAddController (DBBatchAddView view, Runnable updateTranslateList) {
    this.view = view;
    this.updateTranslateList = updateTranslateList;

    view.btnSave().addActionListener(_ -> handleJsonText());

    view.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing (WindowEvent e) {closeApp();}
    });
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
          GO_DATABASE(view, updateTranslateList);
        }
      } catch (Exception er) {
        String msg = "Error processing JSON: " + er.getMessage();
        Exception e = new Exception(msg, er);
        SHOW_ERROR_DIALOG(view, e);
      }
    } else {
      JOptionPane.showMessageDialog(view, "Por favor, insira um JSON válido.");
    }
  }

  private void closeApp () {
    GO_DATABASE(view, updateTranslateList);
  }
}
