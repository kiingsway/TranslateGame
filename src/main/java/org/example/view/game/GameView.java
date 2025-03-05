package org.example.view.game;

import org.example.view.ViewConstants;
import org.example.view.components.ProgressBarComponent;

import javax.swing.*;
import java.awt.*;

public class GameView extends JFrame {

  private final GridBagConstraints gbc = new GridBagConstraints();

  private ProgressBarComponent pbarScore;
  private final JLabel lblCorrect = new JLabel("✅: 0");
  private final JLabel lblWrong = new JLabel("❌: 0");

  private final JLabel lblTotalQuestions = new JLabel(), lblQuestion = new JLabel();
  private final JTextField tfAnswer = new JTextField();
  private final JButton btnAnswer = new JButton("Check Answer");

  public GameView () {

    setTitle("Translate Game");
    setSize(ViewConstants.APP_WIDTH, ViewConstants.APP_HEIGHT);
    setLocationRelativeTo(null);
    setResizable(false);
    setLayout(new GridBagLayout());
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

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
    scorePanel.add(lblQuestion, c);
    add(scorePanel, gbc);
  }

  private void renderTotalQuestions () {
    JPanel scorePanel = new JPanel(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();

    scorePanel.add(lblTotalQuestions, c);
    add(scorePanel, gbc);
  }

  private void renderInput () {
    JPanel scorePanel = new JPanel(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();

    tfAnswer.setPreferredSize(new Dimension(200, 35));
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 0;
    c.gridy = 0;
    c.insets = new Insets(0, 0, 40, 0);
    scorePanel.add(tfAnswer, c);

    btnAnswer.setEnabled(false);
    c.gridy = 1;
    c.insets = new Insets(0, 0, 0, 0);
    scorePanel.add(btnAnswer, c);

    add(scorePanel, gbc);
  }

  public ProgressBarComponent pbarScore () {return pbarScore;}

  public JLabel lblTotalQuestions () {return lblTotalQuestions;}

  public JTextField tfAnswer () {return tfAnswer;}

  public JButton btnAnswer () {return btnAnswer;}

  public void setLblCorrect (int value) {lblCorrect.setText("✅: " + value);}

  public void setLblWrong (int value) {lblWrong.setText("❌: " + value);}

  public JLabel lblQuestion () {return lblQuestion;}
}
