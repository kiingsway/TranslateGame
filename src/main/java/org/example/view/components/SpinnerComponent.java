package org.example.view.components;

import org.example.view.ViewConstants;

import javax.swing.*;

public class SpinnerComponent extends JSpinner {
  public SpinnerComponent () {
    int value = ViewConstants.INITIAL_N_QUESTIONS;
    int maxSize = ViewConstants.MAX_QUESTIONS;
    SpinnerNumberModel snm = new SpinnerNumberModel(value, 1, maxSize, 1);
    setModel(snm);
  }
}