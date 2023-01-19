package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Logger;

public class Game {
    private  static final String DB_URL = "jdbc:postgresql://127.0.0.1:5432/GuessAnimal";
    private static final String USER = "root";
    private static final String PASSWORD = "root";
    private final BinaryTree tree;
    private Node currentNode;
    private Statement statement;

    public Game() throws SQLException {
        Logger LOGGER = Logger.getLogger(Game.class.getName());

        ResultSet result = null;
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            StringBuilder stringBuilder = new StringBuilder();
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(System.getProperty("user.dir") + "\\src\\main\\java\\org\\example\\" + "createTable.sql"))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
            }
            statement = connection.createStatement();
            statement.execute(stringBuilder.toString());
            result = statement.executeQuery("SELECT * FROM animal_tree;");
        } catch (ClassNotFoundException e) {
            LOGGER.warning("Cannot find PostgreSQL driver: " + e);
        } catch (SQLException e) {
            LOGGER.warning("Cannot connect to the database: " + e);
        } catch (IOException e) {
            LOGGER.warning("Cannot execute SQL query: " + e);
        } finally {
            if (result == null) {
                tree = new BinaryTree();
            } else {
                Map<Long, Row> rows = new HashMap<>();
                while (result.next()) {
                    long id = result.getLong("id");
                    String question = result.getString("question");
                    String animal = result.getString("animal");
                    rows.put(id, new Row(question, animal));
                }
                if (rows.size() == 0) {
                    tree = new BinaryTree();
                } else {
                    tree = new BinaryTree(rows);
                }
            }
            currentNode = tree.getHead();
        }
    }

    public Node getCurrentNode() {
        return currentNode;
    }

    public Statement getStatement() {
        return statement;
    }

    public void setCurrentNode(Node currentNode) {
        this.currentNode = currentNode;
    }

    public BinaryTree getTree() {
        return tree;
    }

    public void getNextNode(Boolean isYesAnswer) {
        if (isYesAnswer) {
            currentNode = currentNode.getYesAnswer();
        } else {
            currentNode = currentNode.getNoAnswer();
        }
    }

    public void changeTree(Node node, Leaf animal) {
        Node parent = currentNode.getParent();
        if (currentNode.getIsYesAnswer()) {
            parent.changeYesAnswer(node, animal);
        } else {
            parent.changeNoAnswer(node, animal);
        }
    }

    public String getPrintTree() {
        StringBuilder result = new StringBuilder();
        Stack<Node> globalStack = new Stack<>();
        globalStack.push(tree.getHead());
        TreePair treePair = tree.getTreePair();
        int gaps = (int) Math.pow(2, treePair.getDepth() - 2);
        boolean isRowEmpty = false;
        while (!isRowEmpty) {
            Stack<Node> localStack = new Stack<>();
            isRowEmpty = true;

            for (int j = 0; j < (gaps - 0.5) * treePair.getMaxNodeLength(); j++) {
                result.append(' ');
            }
            boolean isFirst = true;
            while (!globalStack.isEmpty()) {
                Node temp = globalStack.pop();
                String tempText;
                if (temp != null) {
                    if (!isFirst) {
                        for (int j = 0; j < 2 * (gaps - 0.5) * treePair.getMaxNodeLength(); j++) {
                            result.append(' ');
                        }
                    }
                    if (temp instanceof Leaf) {
                        tempText = ((Leaf) temp).getAnimal();
                        result.append('|');
                        result.append(" ".repeat(Math.max(0, (treePair.getMaxNodeLength() - tempText.length()) / 2 - 1)));
                        result.append(tempText);
                        result.append(" ".repeat(Math.max(0, (treePair.getMaxNodeLength() - tempText.length()) / 2 - 1)));
                        result.append('|');
                    } else {
                        tempText = temp.getQuestion();
                        result.append(" ".repeat(Math.max(0, (treePair.getMaxNodeLength() - tempText.length()) / 2)));
                        result.append(tempText);
                        result.append(" ".repeat(Math.max(0, (treePair.getMaxNodeLength() - tempText.length()) / 2)));
                    }
                    localStack.push(temp.getYesAnswer());
                    localStack.push(temp.getNoAnswer());
                    if (temp.getNoAnswer() != null || temp.getYesAnswer() != null) {
                        isRowEmpty = false;
                    }
                } else {
                    if (!isFirst) {
                        result.append(" ".repeat(Math.max(0, (int) (2 * (gaps - 0.5) * treePair.getMaxNodeLength()))));
                    }
                    result.append(" ".repeat(Math.max(0, treePair.getMaxNodeLength())));
                    localStack.push(null);
                    localStack.push(null);
                }
                isFirst = false;
            }
            result.append('\n');
            result.append('\n');
            gaps /= 2;
            while (!localStack.isEmpty()) {
                globalStack.push(localStack.pop());
            }
        }
        return result.toString();
    }
}

