package org.example.controller.database;

import org.example.dao.TranslateItemDAO;
import org.example.model.TranslateItemModel;
import org.example.view.database.DatabaseFormView;

import javax.swing.*;
import java.sql.SQLException;
import java.util.Objects;

import static org.example.model.TranslateItemModel.*;

public class DatabaseFormController {
  private final DatabaseFormView view;
  private final boolean isEdit;
  private final Runnable updateTranslateList;

  public DatabaseFormController(DatabaseFormView view, Runnable updateTranslateList) throws SQLException {
    this.view = view;
    this.isEdit = view.item().id() != 0;
    this.updateTranslateList = updateTranslateList;

    fillComboBoxCategories();

    view.btnSave().addActionListener(_ -> validateSaveItem());
    view.btnCancel().addActionListener(_ -> view.dispose());
    view.btnDelete().addActionListener(_ -> validateDeleteItem());
  }

  private void validateDeleteItem() {
    TranslateItemModel.validateDeleteItem(view.item(), view);
    updateTranslateList.run();
    view.dispose();
  }

  private void fillComboBoxCategories() throws SQLException {
    JComboBox<String> cbDiff = view.cbDiff();
    cbDiff.setModel(new DefaultComboBoxModel<>(getTranslationDifficulties()));
    if (view.item() != null) cbDiff.setSelectedItem(view.item().difficult());
  }

  private void validateSaveItem() {
    try {
      int id = view.item().id();
      String fr = view.txtFR().getText().replaceAll("\\.$", "").trim();
      String pt = view.txtPT().getText().replaceAll("\\.$", "").trim();
      String category = view.txtCat().getText().replaceAll("\\.$", "").trim();
      String difficult = Objects.toString(view.cbDiff().getSelectedItem(), "").replaceAll("\\.$", "").trim();

      if (Objects.equals(fr, "") || Objects.equals(pt, "") || Objects.equals(category, "")
          || Objects.equals(difficult, "")) {
        JOptionPane.showMessageDialog(view, "All fields must be filled in", "ERROR", JOptionPane.ERROR_MESSAGE);
        return;
      }

      TranslateItemModel item = new TranslateItemModel(id, fr, pt, category, difficult);

      if(!isEdit) TranslateItemDAO.insertTranslation(item);
      else {
        String msg = "Are you sure you want to save this item?";
        int response = JOptionPane.showConfirmDialog(view, msg, "Edit Translate Item", JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);
        if (response == JOptionPane.OK_OPTION) TranslateItemDAO.updateTranslation(item);
        else return;
      }

      updateTranslateList.run();
      view.dispose();
    } catch (SQLException e) {
      showSQLErrorDialog(e.getMessage());
    }
  }

  private void showSQLErrorDialog(String er) {
    JOptionPane.showMessageDialog(view, er, "ERROR - SQLException", JOptionPane.ERROR_MESSAGE);
  }
}
