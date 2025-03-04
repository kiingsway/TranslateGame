package org.example.controller;

import org.example.controller.database.DatabaseController;
import org.example.controller.game.GameController;
import org.example.dao.TranslateItemDAO;
import org.example.view.database.DatabaseView;
import org.example.view.MainView;
import org.example.view.game.GameView;

import javax.swing.*;
import java.sql.SQLException;
import java.util.List;

import static org.example.model.TranslateItemModel.*;
import static org.example.view.MainView.settings;

public class MainController {
  private final MainView view;

  public MainController (MainView view) throws SQLException {
    this.view = view;

    JButton btnDatabase = view.getBtnDatabase();
    JButton btnPlayGame = view.getBtnPlayGame();

    btnDatabase.addActionListener(_ -> btnDatabaseActionListener());
    btnPlayGame.addActionListener(_ -> btnPlayGameActionListener());

    fillComboBoxCategories();
    fillComboBoxDifficulties();

    view.spQuestions().setValue(settings.totalQuestions());
    view.cbCategory().setSelectedItem(settings.category());
    view.cbDifficult().setSelectedItem(settings.difficult());
  }

  private void btnDatabaseActionListener () {
    try {
      DatabaseView dbView = new DatabaseView();
      Runnable updateData = () -> {
        fillComboBoxCategories();
        fillComboBoxDifficulties();
      };
      new DatabaseController(dbView, updateData);
      dbView.setVisible(true);
    } catch (SQLException e) {
      showSQLErrorDialog(e);
    }
  }

  private void btnPlayGameActionListener () {
    try {
      List<String[]> items = TranslateItemDAO.getTranslations();
      if (items.isEmpty()) {
        String title = "ERROR - No translations items";
        String msg = "No translations items found. Create a new one to play.";
        JOptionPane.showMessageDialog(view, msg, title, JOptionPane.WARNING_MESSAGE);
        btnDatabaseActionListener();
      } else {
        SwingUtilities.invokeLater(() -> {
          try {
            GameView view = new GameView(settings);
            new GameController(view);
            view.setVisible(true);
          } catch (SQLException e) {
            showSQLErrorDialog(e);
          }
        });
      }
    } catch (SQLException e) {
      showSQLErrorDialog(e);
    }
  }

  private void fillComboBoxCategories () {
    try {
      JComboBox<String> cbCategory = view.cbCategory();
      cbCategory.setModel(new DefaultComboBoxModel<>(getTranslationCategories()));
    } catch (SQLException e) {
      showSQLErrorDialog(e);
    }
  }

  private void fillComboBoxDifficulties () {
    try {
      JComboBox<String> cbDifficult = view.cbDifficult();
      cbDifficult.setModel(new DefaultComboBoxModel<>(getTranslationDifficulties()));
    } catch (SQLException e) {
      showSQLErrorDialog(e);
    }
  }

  private void showSQLErrorDialog (SQLException e) {
    JOptionPane.showMessageDialog(view, e.getMessage(), "ERROR - SQLException", JOptionPane.ERROR_MESSAGE);
  }

}
