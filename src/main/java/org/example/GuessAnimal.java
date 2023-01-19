package org.example;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;

public class GuessAnimal {
    private static Game game;
    private static final Logger LOGGER = Logger.getLogger(GuessAnimal.class.getName());

    public static void main(String[] args) {
        try {
            game = new Game();
        } catch (SQLException e) {
            LOGGER.warning("Cannot execute SQL query: " + e);
        }

        JFrame mainFrame = new JFrame("Угадай животное");
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Node head = game.getTree().getHead();
                removeAllRows();
                saveTree(head, 1L);
            }
        });
        mainFrame.setLayout(null);
        mainFrame.setResizable(false);
        mainFrame.setSize(1024, 640);
        menu(mainFrame);
    }

    private static void menu(JFrame mainFrame) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();

        int x = (screenSize.width - mainFrame.getWidth()) / 2;
        int y = (screenSize.height - mainFrame.getHeight()) / 2;
        mainFrame.setLocation(x, y);

        JLabel startPhrase = new JLabel("Загадай животное, а я попробую угадать...", SwingConstants.CENTER);
        startPhrase.setFont(new Font(null, Font.BOLD, 20));
        startPhrase.setSize(500, 100);
        x = (mainFrame.getWidth() - startPhrase.getWidth()) / 2;
        startPhrase.setLocation(x, 100);

        JButton startButton = new JButton("Начать игру");
        startButton.setSize(200, 100);
        x = (mainFrame.getWidth() - startButton.getWidth()) / 2;
        y = (mainFrame.getHeight() - startButton.getHeight()) / 2;
        startButton.setLocation(x, y);

        JButton showTree = new JButton("Текущее дерево игры");
        showTree.setSize(200, 50);
        x = (mainFrame.getWidth() - showTree.getWidth()) / 2;
        showTree.setLocation(x, y + 150);
        showTree.addActionListener(e -> {
            mainFrame.remove(startPhrase);
            mainFrame.remove(startButton);
            mainFrame.remove(showTree);
            mainFrame.repaint();
            showTree(mainFrame, true, "");
        });

        startButton.addActionListener(e -> {
            mainFrame.remove(startPhrase);
            mainFrame.remove(startButton);
            mainFrame.remove(showTree);
            mainFrame.repaint();
            turn(mainFrame);
        });

        mainFrame.add(startPhrase);
        mainFrame.add(startButton);
        mainFrame.add(showTree);
        mainFrame.setVisible(true);
    }

    private static void turn(JFrame mainFrame) {
        Node currentNode = game.getCurrentNode();
        if (currentNode instanceof Leaf) {
            String guessAnimal = ((Leaf) currentNode).getAnimal();
            JLabel guess = new JLabel("Загаданное животное - " + guessAnimal + "?", SwingConstants.CENTER);
            guess.setFont(new Font(null, Font.BOLD, 20));
            guess.setSize(500, 100);
            int x = (mainFrame.getWidth() - guess.getWidth()) / 2;
            guess.setLocation(x, 100);

            JButton yesAnswer = new JButton("Да");
            yesAnswer.setSize(200, 100);
            x = (mainFrame.getWidth() - yesAnswer.getWidth()) / 2;
            int y = (mainFrame.getHeight() - yesAnswer.getHeight()) / 2;
            yesAnswer.setLocation(x - 150, y);

            JButton noAnswer = new JButton("Нет");
            noAnswer.setSize(200, 100);
            x = (mainFrame.getWidth() - noAnswer.getWidth()) / 2;
            y = (mainFrame.getHeight() - noAnswer.getHeight()) / 2;
            noAnswer.setLocation(x + 150, y);

            yesAnswer.addActionListener(e -> {
                mainFrame.remove(guess);
                mainFrame.remove(yesAnswer);
                mainFrame.remove(noAnswer);
                mainFrame.repaint();
                askNewGame(mainFrame, "Отличная игра!");
            });

            noAnswer.addActionListener(e -> {
                mainFrame.remove(guess);
                mainFrame.remove(yesAnswer);
                mainFrame.remove(noAnswer);
                mainFrame.repaint();
                addNewAnimal(mainFrame, guessAnimal);
            });

            mainFrame.add(guess);
            mainFrame.add(yesAnswer);
            mainFrame.add(noAnswer);
        } else {
            JLabel question = new JLabel("Это животное " + currentNode.getQuestion() + "?", SwingConstants.CENTER);
            question.setFont(new Font(null, Font.BOLD, 20));
            question.setSize(500, 100);
            int x = (mainFrame.getWidth() - question.getWidth()) / 2;
            question.setLocation(x, 100);

            JButton yesAnswer = new JButton("Да");
            yesAnswer.setSize(200, 100);
            x = (mainFrame.getWidth() - yesAnswer.getWidth()) / 2;
            int y = (mainFrame.getHeight() - yesAnswer.getHeight()) / 2;
            yesAnswer.setLocation(x - 150, y);

            JButton noAnswer = new JButton("Нет");
            noAnswer.setSize(200, 100);
            x = (mainFrame.getWidth() - noAnswer.getWidth()) / 2;
            y = (mainFrame.getHeight() - noAnswer.getHeight()) / 2;
            noAnswer.setLocation(x + 150, y);

            yesAnswer.addActionListener(e -> {
                mainFrame.remove(question);
                mainFrame.remove(yesAnswer);
                mainFrame.remove(noAnswer);
                mainFrame.repaint();
                game.getNextNode(true);
                turn(mainFrame);
            });

            noAnswer.addActionListener(e -> {
                mainFrame.remove(question);
                mainFrame.remove(yesAnswer);
                mainFrame.remove(noAnswer);
                mainFrame.repaint();
                game.getNextNode(false);
                turn(mainFrame);
            });

            mainFrame.add(question);
            mainFrame.add(yesAnswer);
            mainFrame.add(noAnswer);
        }
    }

    private static void askNewGame(JFrame mainFrame, String message) {
        Node head = game.getTree().getHead();
        removeAllRows();
        saveTree(head, 1L);

        JLabel startPhrase = new JLabel(message, SwingConstants.CENTER);
        startPhrase.setFont(new Font(null, Font.BOLD, 20));
        startPhrase.setSize(500, 100);
        int x = (mainFrame.getWidth() - startPhrase.getWidth()) / 2;
        startPhrase.setLocation(x, 100);

        JButton startButton = new JButton("Новая игра");
        startButton.setSize(200, 100);
        x = (mainFrame.getWidth() - startButton.getWidth()) / 2;
        int y = (mainFrame.getHeight() - startButton.getHeight()) / 2;
        startButton.setLocation(x, y);

        JButton showTree = new JButton("Текущее дерево игры");
        showTree.setSize(200, 50);
        x = (mainFrame.getWidth() - showTree.getWidth()) / 2;
        showTree.setLocation(x, y + 150);
        showTree.addActionListener(e -> {
            mainFrame.remove(startPhrase);
            mainFrame.remove(startButton);
            mainFrame.remove(showTree);
            mainFrame.repaint();
            showTree(mainFrame, false, message);
        });

        startButton.addActionListener(e -> {
            mainFrame.remove(startPhrase);
            mainFrame.remove(startButton);
            mainFrame.remove(showTree);
            mainFrame.repaint();
            game.setCurrentNode(game.getTree().getHead());
            turn(mainFrame);
        });

        mainFrame.add(startPhrase);
        mainFrame.add(startButton);
        mainFrame.add(showTree);
    }

    private static void addNewAnimal(JFrame mainFrame, String guessAnimal) {
        JLabel animalText = new JLabel("Какое животное ты загадал: ", SwingConstants.CENTER);
        animalText.setFont(new Font(null, Font.BOLD, 15));
        animalText.setSize(250, 30);
        animalText.setLocation(100, 100);

        JTextField animalName = new JTextField();
        animalName.setFont(new Font(null, Font.BOLD, 15));
        animalName.setSize(300, 30);
        int x = (mainFrame.getWidth() - animalName.getWidth()) / 2;
        animalName.setLocation(x, 100);

        JButton next = new JButton("Далее");
        next.setSize(100, 50);
        x = (mainFrame.getWidth() - next.getWidth()) / 2;
        next.setLocation(x, 200);
        next.addActionListener(e -> {
            animalName.setEnabled(false);
            mainFrame.remove(next);
            mainFrame.repaint();
            java.util.List<Component> components = new ArrayList<>();
            components.add(animalText);
            components.add(animalName);
            addNewQuestion(mainFrame, animalName.getText(), guessAnimal, game, components);
        });

        mainFrame.add(animalText);
        mainFrame.add(animalName);
        mainFrame.add(next);
    }

    private static void addNewQuestion(JFrame mainFrame, String newAnimal, String guessAnimal, Game game, List<Component> components) {
        JLabel animalDiff = new JLabel("Чем \"" + newAnimal + "\" отличается от \"" + guessAnimal + "\": ", SwingConstants.CENTER);
        animalDiff.setFont(new Font(null, Font.BOLD, 15));
        animalDiff.setSize(300, 30);
        animalDiff.setLocation(50, 150);

        JTextField animalDiffText = new JTextField();
        animalDiffText.setFont(new Font(null, Font.BOLD, 15));
        animalDiffText.setSize(300, 30);
        int x = (mainFrame.getWidth() - animalDiffText.getWidth()) / 2;
        animalDiffText.setLocation(x, 150);

        JButton addNewAnimal = new JButton("Добавить животное");
        addNewAnimal.setSize(150, 50);
        x = (mainFrame.getWidth() - addNewAnimal.getWidth()) / 2;
        addNewAnimal.setLocation(x, 200);
        addNewAnimal.addActionListener(e -> {
            for (Component component : components) {
                mainFrame.remove(component);
            }
            mainFrame.remove(animalDiff);
            mainFrame.remove(animalDiffText);
            mainFrame.remove(addNewAnimal);
            mainFrame.repaint();
            Node newNode = new Node(animalDiffText.getText(), game.getCurrentNode().getParent(), game.getCurrentNode().getIsYesAnswer());
            game.changeTree(newNode, new Leaf(newAnimal, newNode, true));
            askNewGame(mainFrame, "Животное успешно добавлено!");
        });

        mainFrame.add(animalDiff);
        mainFrame.add(animalDiffText);
        mainFrame.add(addNewAnimal);
    }

    private static void showTree(JFrame mainFrame, Boolean isMenu, String message) {
        JTextArea tree = new JTextArea(10, 20);
        tree.setText(game.getPrintTree());
        tree.setFont(new Font("Consolas", Font.PLAIN, 15));
        tree.setBounds(50, 50, 900, 500);
        tree.setEditable(false);

        JScrollPane scroll = new JScrollPane(tree);
        scroll.setBounds(62, 50, 900, 500);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        JButton back = new JButton("Назад");
        back.setBounds(10, 10, 100, 30);
        back.addActionListener(e -> {
            mainFrame.remove(scroll);
            mainFrame.remove(back);
            mainFrame.repaint();
            if (isMenu) {
                menu(mainFrame);
            } else {
                askNewGame(mainFrame, message);
            }
        });

        mainFrame.add(scroll);
        mainFrame.add(back);
    }

    private static void removeAllRows() {
        try {
            game.getStatement().execute("DELETE FROM animal_tree;");
        } catch (SQLException e) {
            LOGGER.warning("Cannot delete all rows from table: " + e);
        }
    }

    private static void saveTree(Node node, Long id) {
        try {
            if (node.getQuestion() == null) {
                game.getStatement().executeUpdate("INSERT INTO animal_tree(id, question, animal) VALUES (" + id + ", 'null', '" + ((Leaf) node).getAnimal() + "');");
            } else {
                game.getStatement().executeUpdate("INSERT INTO animal_tree(id, question, animal) VALUES (" + id + ", '" + node.getQuestion() + "', 'null');");
            }
            if (node.getYesAnswer() != null) {
                saveTree(node.getYesAnswer(), 2L * id);
                saveTree(node.getNoAnswer(), 2L * id + 1);
            }
        } catch (SQLException e) {
            LOGGER.warning("Cannot update table: " + e);
        }
    }
}