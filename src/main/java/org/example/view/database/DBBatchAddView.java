package org.example.view.database;

import org.example.controller.database.DBBatchAddController;
import org.example.view.ViewConstants;

import javax.swing.*;
import java.awt.*;

public class DBBatchAddView extends JFrame {

  private final JButton btnSave = new JButton("Send");
  private final JTextArea jsonTextArea = new JTextArea();

  public DBBatchAddView () {
    setTitle("Translate Game - Database: Batch Add");
    setSize(ViewConstants.APP_WIDTH, ViewConstants.APP_HEIGHT);
    setLocationRelativeTo(null);
    setResizable(false);
    setLayout(new BorderLayout());

    jsonTextArea.setLineWrap(true);
    jsonTextArea.setWrapStyleWord(true);
    jsonTextArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));

    JScrollPane scrollPane = new JScrollPane(jsonTextArea);
    scrollPane.setBounds(10, 10, 460, 250);
    add(scrollPane, BorderLayout.CENTER);

    // Botão de salvar
    btnSave.setBounds(10, 270, 100, 30);
    add(btnSave, BorderLayout.SOUTH);

    setVisible(true);
  }

  public final JTextArea jsonTextArea () {return jsonTextArea;}

  public final JButton btnSave () {return btnSave;}

  public static void main (String[] args) {
    SwingUtilities.invokeLater(() -> {
      DBBatchAddView view = new DBBatchAddView();
      new DBBatchAddController(view, null);
      view.setVisible(true);
    });
  }
}
