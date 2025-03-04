package org.example.view.game;

import org.example.controller.game.GameController;
import org.example.model.GameSettings;
import org.example.view.MainView;
import org.example.view.ViewConstants;
import org.example.view.components.ProgressBarComponent;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class GameView extends JFrame {

  private final GridBagConstraints gbc = new GridBagConstraints();
  private final GameSettings settings;

  private ProgressBarComponent pbarScore;
  private final JLabel lblCorrect = new JLabel("✅: 0");
  private final JLabel lblWrong = new JLabel("❌: 0");

  public GameView (GameSettings settings) {
    this.settings = settings;

    setTitle("Translate Game");
    setSize(ViewConstants.APP_WIDTH, ViewConstants.APP_HEIGHT);
    setLocationRelativeTo(null);
    setResizable(false);
    setLayout(new GridBagLayout());

    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;

    gbc.gridx = 0;
    gbc.gridy = 0;
    renderScore();

    gbc.weightx = 3;
    gbc.gridx = 1;
    renderQuestion();

    gbc.gridx = 2;
    gbc.weightx = 1;
    renderTotalQuestions();

    gbc.gridy = 1;
    gbc.gridx = 0;
    gbc.gridwidth = 3;
    gbc.weighty = 2;
    renderInput();
  }

  private void renderScore () {
    JPanel scorePanel = new JPanel(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();

    pbarScore = new ProgressBarComponent(0, 0, 0);

    scorePanel.add(lblCorrect, c);
    c.gridy = 1;
    scorePanel.add(lblWrong, c);
    c.gridy = 2;
    scorePanel.add(pbarScore, c);

    add(scorePanel, gbc);
  }

  private void renderQuestion () {
    JPanel scorePanel = new JPanel(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    scorePanel.setBorder(BorderFactory.createTitledBorder("Question"));
    scorePanel.add(new JLabel("Question:"), c);
    add(scorePanel, gbc);
  }

  private void renderTotalQuestions () {
    JPanel scorePanel = new JPanel(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    scorePanel.setBorder(BorderFactory.createTitledBorder("Total Questions"));
    scorePanel.add(new JLabel("Total Questions:"), c);
    add(scorePanel, gbc);
  }

  private void renderInput () {
    JPanel scorePanel = new JPanel(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    scorePanel.setBorder(BorderFactory.createTitledBorder("Input"));
    scorePanel.add(new JLabel("Input:"), c);
    add(scorePanel, gbc);
  }

  public ProgressBarComponent pbarScore () {return pbarScore;}

  public GameSettings settings () {return settings;}

  public static void main (String[] args) {
    SwingUtilities.invokeLater(() -> {
      GameSettings settings = MainView.settings;
      GameView v = new GameView(settings);
      new GameController(v);
      v.setVisible(true);
    });
  }

}
