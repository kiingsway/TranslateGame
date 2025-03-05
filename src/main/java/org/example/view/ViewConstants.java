package org.example.view;

import org.example.Main;
import org.example.controller.database.DBBatchAddController;
import org.example.controller.database.DatabaseController;
import org.example.controller.database.DatabaseFormController;
import org.example.controller.game.GameController;
import org.example.model.GameSettings;
import org.example.model.TranslateItemModel;
import org.example.view.database.DBBatchAddView;
import org.example.view.database.DatabaseFormView;
import org.example.view.database.DatabaseView;
import org.example.view.game.GameView;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class ViewConstants {

  public static final int INITIAL_N_QUESTIONS = 3;
  public static final int MAX_QUESTIONS = 50;

  public static final Font FONT_TEXTFIELD = new Font("Segoe UI", Font.PLAIN, 15);

  public static final int APP_WIDTH = 800;
  public static final int APP_HEIGHT = 600;

  public static void SHOW_ERROR_DIALOG (Component view, Exception e) {
    String errorType = e.getClass().getSimpleName();
    JOptionPane.showMessageDialog(view, e.getMessage(), "ERROR - " + errorType, JOptionPane.ERROR_MESSAGE);
  }

  public static void GO_HOME (JFrame view) {
    view.dispose();

    String[] args = {};
    Main.main(args);
  }

  public static void GO_DATABASE (JFrame view, Runnable updateData) {
    view.dispose();

    DatabaseView dbView = new DatabaseView();
    new DatabaseController(dbView, updateData);
    dbView.setVisible(true);
  }

  public static void GO_DATABASE_FORM (JFrame view, TranslateItemModel item, Runnable updateData) throws SQLException {
    view.dispose();

    DatabaseFormView dbView = new DatabaseFormView(item);
    new DatabaseFormController(dbView, updateData);
    dbView.setVisible(true);
  }

  public static void GO_DB_BATCH_ADD (JFrame view, Runnable updateData) {
    view.dispose();

    DBBatchAddView dbBatchAddView = new DBBatchAddView();
    new DBBatchAddController(dbBatchAddView, updateData);
    dbBatchAddView.setVisible(true);
  }

  public static void GO_PLAY_GAME (JFrame view, GameSettings settings) throws Exception {
    view.dispose();

    GameView gameView = new GameView();
    new GameController(gameView, settings);
    gameView.setVisible(true);
  }
}