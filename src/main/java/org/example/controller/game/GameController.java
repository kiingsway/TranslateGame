package org.example.controller.game;

import org.example.view.components.ProgressBarComponent;
import org.example.view.game.GameView;

public class GameController {
  private final GameView view;

  public GameController (GameView view) {
    this.view = view;

    ProgressBarComponent pbarScore = view.pbarScore();
    pbarScore.setGreenValue(4);
    pbarScore.setTotalValue(view.settings().totalQuestions());
  }
}
