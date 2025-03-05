package org.example;

import org.example.controller.MainController;
import org.example.dao.TranslateItemDAO;
import org.example.view.MainView;

import javax.swing.*;
import java.sql.SQLException;

import static org.example.view.ViewConstants.SHOW_ERROR_DIALOG;

public class Main {

  public static void main (String[] args) {
    SwingUtilities.invokeLater(() -> {
      try {
        TranslateItemDAO.ensureData();
        MainView view = new MainView();
        new MainController(view);
        view.setVisible(true);
      } catch (SQLException e) {
        SHOW_ERROR_DIALOG(null, e);
      }
    });
  }
}