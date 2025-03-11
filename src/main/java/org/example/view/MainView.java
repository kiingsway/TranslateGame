package org.example.view;

import org.example.model.GameSettings;
import org.example.view.components.SpinnerComponent;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class MainView extends JFrame {

  private final GridBagConstraints gbc = new GridBagConstraints();

  private final JSpinner spQuestions = new SpinnerComponent();
  private final JComboBox<String> cbCategory = new JComboBox<>();
  private final JComboBox<String> cbDifficult = new JComboBox<>();
  private final JButton btnDatabase = new JButton("Database");
  private final JButton btnPlayGame = new JButton("Play Game");
  private final JButton btnCloseGame = new JButton("Quit Game");

  public static GameSettings settings;

  public MainView () throws SQLException {
    setTitle("Translate Game");
    setSize(ViewConstants.APP_WIDTH, ViewConstants.APP_HEIGHT);
    setLocationRelativeTo(null);
    setResizable(false);
    setLayout(new GridBagLayout());
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

    settings = new GameSettings(5, null, null);

    gbc.insets = new Insets(10, 10, 10, 10);
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.fill = GridBagConstraints.HORIZONTAL;

    renderLblTitle();
    renderGameSettings();
    renderButtonPanel();
  }

  private void renderLblTitle () {
    JLabel lblTitle = new JLabel("Translate Game");
    lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
    lblTitle.setFont(ViewConstants.FONT_APP_NAME);
    gbc.gridy = 0;
    gbc.gridx = 0;
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    add(lblTitle, gbc);
  }

  private void renderGameSettings () {
    JLabel lblQuestions = new JLabel("Questions:", SwingConstants.RIGHT);
    JLabel lblCategory = new JLabel("Category:", SwingConstants.RIGHT);
    JLabel lblDifficult = new JLabel("Difficult:", SwingConstants.RIGHT);

    gbc.insets = new Insets(5, 100, 0, 5);
    gbc.gridwidth = 1;
    gbc.gridy = 1;
    add(lblQuestions, gbc);

    gbc.insets = new Insets(5, 0, 0, 25);
    gbc.gridx = 1;
    add(spQuestions, gbc);

    gbc.insets = new Insets(5, 100, 0, 5);
    gbc.gridy = 2;
    gbc.gridx = 0;
    add(lblCategory, gbc);

    gbc.insets = new Insets(5, 0, 0, 25);
    gbc.gridx = 1;
    add(cbCategory, gbc);

    gbc.insets = new Insets(5, 100, 0, 5);
    gbc.gridy = 3;
    gbc.gridx = 0;
    add(lblDifficult, gbc);

    gbc.insets = new Insets(5, 0, 0, 25);
    gbc.gridx = 1;
    add(cbDifficult, gbc);
  }

  private void renderButtonPanel () {
    btnDatabase.setFocusable(false);
    btnPlayGame.setFocusable(false);

    JPanel buttonPanel = new JPanel(new GridBagLayout());
    GridBagConstraints buttonGbc = new GridBagConstraints();

    buttonGbc.insets = new Insets(5, 5, 5, 5);
    buttonGbc.fill = GridBagConstraints.HORIZONTAL;
    buttonGbc.weightx = 0.10;
    buttonGbc.gridx = 0;
    buttonPanel.add(btnDatabase, buttonGbc);

    buttonGbc.weightx = 0.90;
    buttonGbc.gridx = 1;
    buttonPanel.add(btnPlayGame, buttonGbc);

    buttonGbc.weightx = 1;
    buttonGbc.gridy = 1;
    buttonGbc.gridx = 0;
    buttonGbc.gridwidth = 2;
    buttonPanel.add(btnCloseGame, buttonGbc);

    gbc.insets = new Insets(10, 10, 10, 10);
    gbc.gridy = 4;
    gbc.gridx = 0;
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    add(buttonPanel, gbc);
  }

  public JComboBox<String> cbCategory () {return cbCategory;}

  public JComboBox<String> cbDifficult () {return cbDifficult;}

  public JButton btnDatabase () {return btnDatabase;}

  public JButton btnPlayGame () {return btnPlayGame;}

  public JButton btnCloseGame () {return btnCloseGame;}

  public JSpinner spQuestions () {return this.spQuestions;}

}
