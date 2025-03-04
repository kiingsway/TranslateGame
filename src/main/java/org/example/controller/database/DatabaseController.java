package org.example.controller.database;

import org.example.dao.TranslateItemDAO;
import org.example.model.TranslateItemModel;
import org.example.view.database.DatabaseFormView;
import org.example.view.database.DatabaseView;
import org.example.view.components.CardItemComponent;
import org.example.view.database.DBBatchAddView;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

import static org.example.model.TranslateItemModel.getTranslationItems;

public class DatabaseController {

  private final DatabaseView view;
  private final Runnable updateMainView;

  public DatabaseController(DatabaseView view, Runnable updateData) {
    this.view = view;
    this.updateMainView = updateData;

    JButton btnCreateItem = view.getBtnCreateItem();
    JButton btnBatchAdd = view.getBtnBatchAdd();
    JMenuItem menuCreateItem = view.menuCreateItem();
    JMenuItem menuBatchAdd = view.menuBatchAdd();
    JMenuItem menuRestoreItem = view.menuRestoreItem();

    btnCreateItem.addActionListener(_ -> openFormView(null));
    menuCreateItem.addActionListener(_ -> openFormView(null));
    btnBatchAdd.addActionListener(_ -> openBatchAdditionView());
    menuBatchAdd.addActionListener(_ -> openBatchAdditionView());
    menuRestoreItem.addActionListener(_ -> handleRestoreTranslateItems());

    renderItemsPanel();
  }

  private void openFormView(TranslateItemModel item) {
    SwingUtilities.invokeLater(() -> {
      try {
        DatabaseFormView view = new DatabaseFormView(item);
        new DatabaseFormController(view, this::renderItemsPanel);
        view.setVisible(true);
      } catch (SQLException er) {
        String msg = "Error opening database form: " + er.getMessage();
        JOptionPane.showMessageDialog(view, msg, "Error - SQLException", JOptionPane.ERROR_MESSAGE);
      }
    });
  }

  private void openBatchAdditionView() {
    SwingUtilities.invokeLater(() -> {
      DBBatchAddView view = new DBBatchAddView();
      new DBBatchAddController(view, this::renderItemsPanel);
      view.setVisible(true);
    });
  }

  private void handleRestoreTranslateItems() {
    String title = "Restore Translate Items";
    String msg = "Are you sure you want to restore all the translation items? New items will be keep.";

    int response = JOptionPane.showConfirmDialog(view, msg, title, JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE);
    if (response == JOptionPane.YES_OPTION) {
      try {
        TranslateItemDAO.restoreTranslateItems();
        String msg1 = "Success restoring all the translation items.";
        JOptionPane.showMessageDialog(view, msg1, "SUCCESS - Restoring itens", JOptionPane.INFORMATION_MESSAGE);
        renderItemsPanel();
      } catch (SQLException e) {
        JOptionPane.showMessageDialog(view, e.getMessage(), "ERROR - SQLException", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  private void renderItemsPanel() {
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

      if (updateMainView != null)
        updateMainView.run();
      view.revalidate();
      view.repaint();

    } catch (SQLException e) {
      String msg = "Error occurred while getting translation items: " + e.getMessage();
      throw new RuntimeException(msg, e);
    }
  }
}
