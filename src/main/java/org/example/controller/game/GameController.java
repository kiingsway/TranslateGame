package org.example.controller.game;

import org.example.Main;
import org.example.model.GameSettings;
import org.example.model.Player;
import org.example.view.game.GameView;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;

import static org.example.view.ViewConstants.GO_HOME;
import static org.example.view.ViewConstants.SHOW_ERROR_DIALOG;

public class GameController {

  private final GameView view;
  private final GameSettings settings;
  private Player player;

  public GameController (GameView view, GameSettings settings) throws Exception {
    this.settings = settings;
    this.view = view;
    this.player = new Player(this);

    setValuesAndListeners();
    updateView();
  }

  private void setValuesAndListeners () {
    // btnAnswer pressed, tfAnswer Enter listener
    view.btnAnswer().addActionListener(_ -> sendAnswer());
    view.tfAnswer().addActionListener(_ -> sendAnswer());

    // tfAnswer changed
    view.tfAnswer().getDocument().addDocumentListener((SimpleDocumentListener) () -> {
      String txt = view.tfAnswer().getText();
      view.btnAnswer().setEnabled(!txt.isEmpty() || player.isGameOver());
    });

    // Closing app
    view.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing (WindowEvent e) {closeApp();}
    });
  }

  private void sendAnswer () {
    try {
      if (!player.isGameOver()) player.checkAnswer();
      else {
        this.settings.setTotalQuestions(player.totalQuestions());
        player = new Player(this);
      }
    } catch (Exception e) {
      SHOW_ERROR_DIALOG(view, e);
    }
    updateView();
  }

  private void updateView () {
    int totalQuestions = player.totalQuestions(), correct = player.correct();
    int questionsAnswered = player.questionsAnswered() + (player.isGameOver() ? 1 : 0);
    int wrong = questionsAnswered - correct;
    boolean isGameOver = player.isGameOver();

    view.setLblCorrect(correct);
    view.setLblWrong(wrong);

    view.pbarScore().setGreenValue(correct);
    view.pbarScore().setRedValue(wrong);
    view.pbarScore().setTotalValue(totalQuestions);

    view.tfAnswer().setText("");

    double percentageScore = questionsAnswered == 0 ? 0 : (double) correct / questionsAnswered;
    int totalScore = (int) (percentageScore * 100);
    String score = String.format("Score: %s%%", totalScore);

    String totalQuestionsText = String.format("<html>%d of %d questions<br>%s</html>", questionsAnswered, totalQuestions, score);
    String btnAnswerTitle = "Check Answer", lblQuestionText = player.question();

    if (isGameOver) {
      totalQuestionsText = String.format("<html>%d questions answered<br>%s</html>", totalQuestions, score);
      btnAnswerTitle = "New Game";
      lblQuestionText = "✅ Completed!";
    }

    view.tfAnswer().setFocusable(!isGameOver);
    view.tfAnswer().setEnabled(!isGameOver);
    view.tfAnswer().setVisible(!isGameOver);

    view.btnAnswer().setText(btnAnswerTitle);
    view.lblTotalQuestions().setText(totalQuestionsText);

    view.lblQuestion().setText(lblQuestionText);

    if (isGameOver) view.btnAnswer().requestFocusInWindow();
    else view.tfAnswer().requestFocusInWindow();
  }

  private void closeApp () {
    String msg = "Tem certeza que deseja encerrar a partida?";
    String title = "Close Game";
    int response = JOptionPane.showConfirmDialog(view, msg, title, JOptionPane.YES_NO_OPTION);

    if (response == JOptionPane.YES_OPTION) GO_HOME(view);
  }

  public GameView view () {return view;}

  public GameSettings settings () {return settings;}

  public String answer () {return view.tfAnswer().getText().trim();}
}

@FunctionalInterface
interface SimpleDocumentListener extends DocumentListener {
  void onChange ();

  @Override
  default void insertUpdate (DocumentEvent e) {onChange();}

  @Override
  default void removeUpdate (DocumentEvent e) {onChange();}

  @Override
  default void changedUpdate (DocumentEvent e) {onChange();}
}