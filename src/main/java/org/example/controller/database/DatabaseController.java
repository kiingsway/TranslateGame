package org.example.controller.database;

import org.example.dao.TranslateItemDAO;
import org.example.model.TranslateItemModel;
import org.example.view.database.DatabaseView;
import org.example.view.components.CardItemComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.List;

import static org.example.model.TranslateItemModel.getTranslationItems;
import static org.example.view.ViewConstants.*;

public class DatabaseController {

  private final DatabaseView view;
  private final Runnable updateMainView;

  public DatabaseController (DatabaseView view, Runnable updateData) {
    this.view = view;
    this.updateMainView = updateData;

    JButton btnCreateItem = view.getBtnCreateItem();
    JButton btnBatchAdd = view.getBtnBatchAdd();
    JMenuItem menuCreateItem = view.menuCreateItem();
    JMenuItem menuBatchAdd = view.menuBatchAdd();
    JMenuItem menuRestoreItem = view.menuRestoreItem();

    btnCreateItem.addActionListener(_ -> openFormView(null));
    menuCreateItem.addActionListener(_ -> openFormView(null));
    btnBatchAdd.addActionListener(_ -> GO_DB_BATCH_ADD(view, this::renderItemsPanel));
    menuBatchAdd.addActionListener(_ -> GO_DB_BATCH_ADD(view, this::renderItemsPanel));
    menuRestoreItem.addActionListener(_ -> handleRestoreTranslateItems());

    view.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing (WindowEvent e) {GO_HOME(view);}
    });

    renderItemsPanel();
  }

  private void openFormView (TranslateItemModel item) {
    try {
      GO_DATABASE_FORM(view, item, this::renderItemsPanel);
    } catch (SQLException er) {
      String msg = "Error opening database form: " + er.getMessage();
      SQLException error = new SQLException(msg, er);
      SHOW_ERROR_DIALOG(view, error);
    }
  }

  private void handleRestoreTranslateItems () {
    String title = "Restore Translate Items";
    String msg = "Are you sure you want to restore all the translation items? New items will be keep.";

    int response = JOptionPane.showConfirmDialog(view, msg, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    if (response == JOptionPane.YES_OPTION) {
      try {
        TranslateItemDAO.restoreTranslateItems();
        String msg1 = "Success restoring all the translation items.";
        JOptionPane.showMessageDialog(view, msg1, "SUCCESS - Restoring itens", JOptionPane.INFORMATION_MESSAGE);
        renderItemsPanel();
      } catch (SQLException e) {
        SHOW_ERROR_DIALOG(view, e);
      }
    }
  }

  private void renderItemsPanel () {
    JPanel itemsPanel = view.itemsPanel();
    GridBagConstraints gbc = new GridBagConstraints();
    itemsPanel.removeAll();
    try {
      List<String[]> items = getTranslationItems();
      gbc.gridy = 0;
      for (String[] item : items) {
        int id = Integer.parseInt(item[0]);
        String fr = item[1], pt = item[2];
        String cat = item[3], diff = item[4];
        TranslateItemModel itemModel = new TranslateItemModel(id, fr, pt, cat, diff);
        Runnable openFormView = () -> this.openFormView(itemModel);
        JPanel card = new CardItemComponent(itemModel, openFormView, this::renderItemsPanel);

        gbc.insets = new Insets(5, 0, 5, 0);
        itemsPanel.add(card, gbc);
        gbc.gridy++;
      }

      if (updateMainView != null) updateMainView.run();
      view.revalidate();
      view.repaint();

    } catch (SQLException er) {
      String msg = "Error occurred while getting translation items: " + er.getMessage();
      Exception e = new Exception(msg, er);
      SHOW_ERROR_DIALOG(view, e);
    }
  }
}
