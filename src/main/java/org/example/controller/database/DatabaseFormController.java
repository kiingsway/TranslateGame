package org.example.controller.database;

import org.example.dao.TranslateItemDAO;
import org.example.model.TranslateItemModel;
import org.example.view.database.DatabaseFormView;

import javax.swing.*;
import java.sql.SQLException;
import java.util.Objects;

import static org.example.dao.TranslateItemDAO.deleteTranslation;
import static org.example.model.TranslateItemModel.*;

public class DatabaseFormController {
  private final DatabaseFormView view;
  private final boolean isEdit;
  private final Runnable updateTranslateList;

  public DatabaseFormController (DatabaseFormView view, Runnable updateTranslateList) throws SQLException {
    this.view = view;
    this.isEdit = view.item().id() != 0;
    this.updateTranslateList = updateTranslateList;

    fillComboBoxCategories();

    view.btnSave().addActionListener(_ -> validateSaveItem());
    view.btnCancel().addActionListener(_ -> view.dispose());
    view.btnDelete().addActionListener(_ -> validateDeleteItem());

  }

  private void fillComboBoxCategories () throws SQLException {
    JComboBox<String> cbDiff = view.cbDiff();
    cbDiff.setModel(new DefaultComboBoxModel<>(getTranslationDifficulties()));
    if (view.item() != null) cbDiff.setSelectedItem(view.item().difficult());
  }

  private void validateSaveItem () {
    try {
      int id = view.item().id();
      String fr = view.txtFR().getText().replaceAll("\\.$", "").trim();
      String pt = view.txtPT().getText().replaceAll("\\.$", "").trim();
      String category = view.txtCat().getText().replaceAll("\\.$", "").trim();
      String difficult = Objects.toString(view.cbDiff().getSelectedItem(), "").replaceAll("\\.$", "").trim();

      if (Objects.equals(fr, "") || Objects.equals(pt, "") || Objects.equals(category, "") || Objects.equals(difficult, "")) {
        JOptionPane.showMessageDialog(view, "All fields must be filled in", "ERROR", JOptionPane.ERROR_MESSAGE);
        return;
      }

      TranslateItemModel item = new TranslateItemModel(id, fr, pt, category, difficult);

      if (isEdit) {
        String msg = "Are you sure you want to save this item?";
        int response = JOptionPane.showConfirmDialog(view, msg, "Edit Translate Item", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (response == JOptionPane.OK_OPTION) TranslateItemDAO.updateTranslation(item);
        else return;

      } else TranslateItemDAO.insertTranslation(item);

      updateTranslateList.run();
      view.dispose();
    } catch (SQLException e) {
      showSQLErrorDialog(e.getMessage());
    }
  }

  private void validateDeleteItem () {

    String title = "Confirm Deletion";
    String msg = "Are you sure you want to delete this item?";

    try {
      TranslateItemModel item = view.item();
      if (item == null) return;

      int response = JOptionPane.showConfirmDialog(view, msg, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
      if (response == JOptionPane.YES_OPTION) {
        deleteTranslation(item.id());
        updateTranslateList.run();
        view.dispose();
      }
    } catch (SQLException e) {
      msg = "Error deleting item: " + e.getMessage();
      JOptionPane.showMessageDialog(view, msg, "ERROR - SQLException", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void showSQLErrorDialog (String er) {
    JOptionPane.showMessageDialog(view, er, "ERROR - SQLException", JOptionPane.ERROR_MESSAGE);
  }
}
