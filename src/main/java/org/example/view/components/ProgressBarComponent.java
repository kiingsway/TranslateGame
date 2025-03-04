package org.example.view.components;

import javax.swing.*;
import java.awt.*;

public class ProgressBarComponent extends JComponent {
  private int greenValue;
  private int redValue;
  private int totalValue;

  public ProgressBarComponent (int greenValue, int redValue, int totalValue) {
    this.greenValue = greenValue;
    this.redValue = redValue;
    this.totalValue = totalValue;
    setPreferredSize(new Dimension(80, 10));
  }

  @Override
  protected void paintComponent (Graphics g) {
    super.paintComponent(g);
    if (totalValue <= 0) return;

    int width = getWidth();
    int height = getHeight();
    Graphics2D g2 = (Graphics2D) g;

    // Percentual de cada cor
    int greenWidth = (int) ((double) greenValue / totalValue * width);
    int redWidth = (int) ((double) redValue / totalValue * width);

    // Desenha a barra verde
    g2.setColor(Color.GREEN);
    g2.fillRect(0, 0, greenWidth, height);

    // Desenha a barra vermelha ao lado da verde
    g2.setColor(Color.RED);
    g2.fillRect(greenWidth, 0, redWidth, height);

    // Desenha a borda
    g2.setColor(Color.BLACK);
    g2.drawRect(0, 0, width - 1, height - 1);
  }

  // Setters para atualizar os valores dinamicamente
  public void setGreenValue (int greenValue) {
    this.greenValue = greenValue;
    repaint();
  }

  public void setRedValue (int redValue) {
    this.redValue = redValue;
    repaint();
  }

  public void setTotalValue (int totalValue) {
    this.totalValue = totalValue;
    repaint();
  }
}
