package org.example.model;

import org.example.controller.game.GameController;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.example.controller.game.GameConstants.isSameText;
import static org.example.dao.TranslateItemDAO.getTranslations;

public class Player {

  private final String category, difficult;
  private String question, answer;

  private final GameController controller;

  private static final int FR = 1, PT = 2;
  private int totalQuestions;
  private int correct = 0, questionsAnswered = 0;

  private boolean isGameOver = false;

  private final List<String[]> translateItems;
  private List<String[]> questionsList;

  public Player (GameController controller) throws Exception {
    this.controller = controller;
    this.totalQuestions = controller.settings().totalQuestions();
    this.category = controller.settings().category();
    this.difficult = controller.settings().difficult();
    this.translateItems = getTranslations();

    newQuestions();
  }

  private void newQuestions () throws Exception {
    List<String[]> list = translateItems;

    Stream<String[]> stream = list.stream();
    stream = stream.filter(item -> item[3].equals(this.category));
    stream = stream.filter(item -> item[4].equals(this.difficult));

    list = stream.collect(Collectors.toList());

    if (list.isEmpty()) {
      String msg = "Não há questões com os filtros selecionados.";
      throw new Exception(msg);
    }

    if (totalQuestions > list.size()) {
      totalQuestions = list.size();
      String msg = String.format("Apenas %s questões para a dificuldade e categoria selecionados.", totalQuestions);
      JOptionPane.showMessageDialog(controller.view(), msg);
    }

    list.subList(0, Math.max(1, totalQuestions));
    Collections.shuffle(list);
    questionsList = list;

    nextQuestion();
  }

  private void nextQuestion () {

    String[] questionTranslateItem = questionsList.get(questionsAnswered);

    boolean randomProp = new Random().nextBoolean();
    int questionProp = randomProp ? FR : PT, answerProp = randomProp ? PT : FR;

    question = questionTranslateItem[questionProp];
    answer = questionTranslateItem[answerProp];

    // if (answerInConsole) System.out.println("Answer: " + answer);
  }

  private void finishGame () {
    isGameOver = true;
  }

  public void checkAnswer () {
    String userAnswer = controller.answer();
    if (userAnswer.isEmpty()) return;

    if (isSameText(userAnswer, answer)) {
      correct++;
    } else {
      String message = "Your answer: \n\"" + userAnswer + "\"\n\nAnswer: \n\"" + answer + "\"";
      JOptionPane.showMessageDialog(controller.view(), message, "Wrong Answer", JOptionPane.ERROR_MESSAGE);
    }

    if (questionsAnswered < totalQuestions - 1) {
      questionsAnswered++;
      nextQuestion();
    } else {
      finishGame();
    }
  }

  public boolean isGameOver () {return isGameOver;}

  public int correct () {return correct;}

  public int questionsAnswered () {return questionsAnswered;}
  public int totalQuestions () {return totalQuestions;}

  public String question () {return question;}
}
