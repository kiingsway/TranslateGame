package org.example;

import org.example.controller.MainController;
import org.example.dao.TranslateItemDAO;
import org.example.view.MainView;

import javax.swing.*;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

  private static final Logger logger = Logger.getLogger(Main.class.getName());

  public static void main (String[] args) {
    SwingUtilities.invokeLater(() -> {
      try {
        MainView view = new MainView();
        new MainController(view);
        view.setVisible(true);
      } catch (SQLException e) {
        logger.log(Level.SEVERE, e.getMessage(), e);
        JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR - SQLException", JOptionPane.ERROR_MESSAGE);
      }
    });
  }
}