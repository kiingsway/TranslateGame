package org.example.view.database;

import org.example.model.TranslateItemModel;
import org.example.view.ViewConstants;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class DatabaseFormView extends JFrame {

  private final TranslateItemModel item;
  private final boolean isEdit;

  private final JTextField txtID = new JTextField();
  private final JTextField txtFR = new JTextField();
  private final JTextField txtPT = new JTextField();
  private final JTextField txtCat = new JTextField();

  private final JComboBox<String> cbDiff = new JComboBox<>();

  private final JButton btnSave = new JButton("Create");
  private final JButton btnCancel = new JButton("Cancel");
  private final JButton btnDelete = new JButton("Delete");

  private final GridBagConstraints gbc = new GridBagConstraints();

  public DatabaseFormView (TranslateItemModel item) {
    this.item = item;
    this.isEdit = item != null;

    String title = !isEdit ? "New Translate item" : "Edit Translate Item #" + item.id();
    setTitle("Translate Game - " + title);
    setSize(ViewConstants.APP_WIDTH, ViewConstants.APP_HEIGHT);
    setLocationRelativeTo(null);
    setResizable(false);
    setLayout(new GridBagLayout());

    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    renderForm();
    renderAction();
  }

  private void renderForm () {
    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints gbcForm = new GridBagConstraints();

    JLabel[] labels = {new JLabel("ID:"), new JLabel("Français:"), new JLabel("Português:"), new JLabel("Category:"), new JLabel("Difficult:")};

    txtID.setFocusable(false);
    txtID.setEditable(false);

    JTextField[] fields = {txtID, txtFR, txtPT, txtCat};

    if (isEdit) {
      txtID.setText(String.valueOf(item.id()));
      txtFR.setText(item.fr());
      txtPT.setText(item.pt());
      txtCat.setText(item.category());
      cbDiff.setSelectedItem(item.difficult());
      btnSave.setText("Save");
    }

    gbcForm.gridy = 0;
    gbcForm.weightx = 1;
    gbcForm.fill = GridBagConstraints.HORIZONTAL;

    for (int i = 0; i < labels.length; i++) {
      if (i == 0 && !isEdit) continue;

      gbcForm.insets = new Insets(0, 0, 5, 0);
      panel.add(labels[i], gbcForm);

      if (i < 4) {
        JTextField field = fields[i];
        field.setPreferredSize(new Dimension(200, 30));
        field.setFont(ViewConstants.FONT_TEXTFIELD);
        gbcForm.gridy++;
        gbcForm.insets = new Insets(0, 0, 10, 0);
        panel.add(field, gbcForm);
      } else {
        cbDiff.setPreferredSize(new Dimension(200, 30));
        cbDiff.setFont(ViewConstants.FONT_TEXTFIELD);
        gbcForm.gridy++;
        gbcForm.insets = new Insets(0, 0, 10, 0);
        panel.add(cbDiff, gbcForm);
      }

      gbcForm.gridy++;
    }

    panel.setPreferredSize(new Dimension(225, panel.getPreferredSize().height));
    add(panel, gbc);
  }

  private void renderAction () {
    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints gbcAction = new GridBagConstraints();

    gbcAction.insets = new Insets(0, 0, 0, 10);
    gbcAction.fill = GridBagConstraints.HORIZONTAL;
    gbcAction.weightx = 1;
    gbcAction.gridwidth = 1;
    gbcAction.gridx = 0;
    gbcAction.gridy = 0;

    if (isEdit) {
      panel.add(btnDelete, gbcAction);
    }

    gbcAction.gridx++;
    panel.add(btnCancel, gbcAction);
    gbcAction.insets = new Insets(0, 0, 0, 0);
    gbcAction.gridx++;
    panel.add(btnSave, gbcAction);

    gbc.gridy += 1;
    gbc.insets = new Insets(15, 0, 0, 0);
    panel.setPreferredSize(new Dimension(300, panel.getPreferredSize().height));
    add(panel, gbc);
  }

  public TranslateItemModel item () throws SQLException {
    if (item != null) return item;

    String fr = txtFR.getText().replaceAll("\\.$", "").trim();
    String pt = txtPT.getText().replaceAll("\\.$", "").trim();
    String cat = txtCat.getText().replaceAll("\\.$", "").trim();
    String diff = "Easy";
    if (cbDiff.getSelectedItem() != null) diff = cbDiff.getSelectedItem().toString();
    return new TranslateItemModel(0, fr, pt, cat, diff);
  }

  public JButton btnCancel () {return btnCancel;}

  public JButton btnDelete () {return btnDelete;}

  public JButton btnSave () {return btnSave;}

  public JTextField txtFR () {return txtFR;}

  public JTextField txtPT () {return txtPT;}

  public JTextField txtCat () {return txtCat;}

  public JComboBox<String> cbDiff () {return cbDiff;}
}
