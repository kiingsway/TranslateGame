package org.example.view.components;

import org.example.model.TranslateItemModel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CardItemComponent extends JPanel {

  private final TranslateItemModel item;
  private final GridBagConstraints gbc = new GridBagConstraints();

  private final Runnable updateData;
  private final Runnable openFormView;

  private static final Font FONT_TITLE_1 = new Font("Segoe UI", Font.PLAIN, 28);
  private static final Font FONT_TITLE_2 = new Font("Segoe UI", Font.PLAIN, 22);
  private static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 12);
  private static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 12);

  private static final int CARD_WIDTH = 550;
  private static final int CARD_HEIGHT = 125;

  public CardItemComponent(TranslateItemModel item, Runnable openFormView, Runnable updateData) {
    this.item = item;
    this.openFormView = openFormView;
    this.updateData = updateData;

    setLayout(new GridBagLayout());
    setBorder(BorderFactory.createLineBorder(Color.lightGray, 1, true));
    setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));

    setCursor(new Cursor(Cursor.HAND_CURSOR));
    String msg = "Edit item #" + item.id();
    Color defaultColor = getBackground();
    setToolTipText(msg);

    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {openFormView.run();}

      @Override
      public void mouseEntered(MouseEvent e) {setBackground(Color.LIGHT_GRAY);}

      @Override
      public void mouseExited(MouseEvent e) {setBackground(defaultColor);}
    });

    renderInfo();
    renderContextMenu();
  }

  private void renderInfo() {
    JLabel lblFR = new JLabel(item.fr());
    JLabel lblPT = new JLabel(item.pt());
    JLabel lblCategory = new JLabel("Category:");
    JLabel lblCategoryValue = new JLabel(item.category());
    JLabel lblDifficult = new JLabel("Difficult:");
    JLabel lblDifficultValue = new JLabel(item.difficult());

    lblFR.setFont(FONT_TITLE_1);
    lblPT.setFont(FONT_TITLE_2);
    lblCategory.setFont(FONT_BOLD);
    lblCategoryValue.setFont(FONT_REGULAR);
    lblDifficult.setFont(FONT_BOLD);
    lblDifficultValue.setFont(FONT_REGULAR);

    lblCategory.setForeground(Color.gray);
    lblDifficult.setForeground(Color.gray);

    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.insets = new Insets(0, 10, 0, 0);
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.gridy = 0;
    add(lblFR, gbc);

    gbc.gridy = 1;
    add(lblPT, gbc);

    gbc.gridy = 2;
    gbc.gridwidth = 1;
    gbc.weightx = 0;
    add(lblCategory, gbc);

    gbc.gridx = 1;
    add(lblCategoryValue, gbc);

    gbc.gridy = 3;
    gbc.gridx = 0;
    add(lblDifficult, gbc);

    gbc.gridx = 1;
    add(lblDifficultValue, gbc);
  }

  private void renderContextMenu() {
    JPopupMenu popupMenu = new JPopupMenu();
    JMenuItem menuTitle = new JMenuItem("\"" + shortText(item.fr(), 25) + "\"");
    JMenuItem menuEditItem = new JMenuItem("Edit Item");
    JMenuItem menuDelItem = new JMenuItem("Delete Item");

    menuTitle.setEnabled(false);

    menuEditItem.addActionListener(_ -> openFormView.run());
    menuDelItem.addActionListener(_ -> {
      TranslateItemModel.validateDeleteItem(item, this);
      updateData.run();
    });
    
    popupMenu.add(menuTitle);
    popupMenu.add(menuEditItem);
    popupMenu.add(menuDelItem);

    addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {showPopup(e);}

      @Override
      public void mouseReleased(MouseEvent e) {showPopup(e);}

      private void showPopup(MouseEvent e) {
          if (e.isPopupTrigger()) popupMenu.show(e.getComponent(), e.getX(), e.getY());
      }
  });
  }

  public static String shortText(String text, int min) {
    int minLength = Math.max(min, 1);
    return text.length() <= minLength ? text : text.substring(0, minLength) + "...";
  }

}
