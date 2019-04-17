package game.visual.controllers;

import game.logic.Bot;
import game.logic.Cell;
import game.logic.Field;
import game.logic.Ship;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.util.Scanner;

public class MainController {
    private final boolean showEnemyShips = false;

    @FXML
    private CheckBox isHorizontal;  //.isSelected

    @FXML
    private GridPane playerField;

    @FXML
    private GridPane enemyField;

    @FXML
    private Label moveShow;

    public MainController() throws InterruptedException {
    }

    @FXML
    public void initialize() throws InterruptedException {
        enemyMap.setRandomShips();
        update();
    }

    private int shipType = -1;
    private Field enemyMap = new Field(true);
    private Field playerMap = new Field(false);
    private Cell enemyCell = new Cell(-1, -1);
    private Cell playerCell = new Cell(-1, -1);
    private boolean isPlayerMove = true, isShipsSet, isPlayerWin = false, isEnemyWin = false;
    private Bot bot = new Bot(playerMap);
    private int move = 1;
    private String title, text;

    /*
    Для всех методов серии setCoordinatesForXXXField инвертированы Х и У из-за криворукости разработчика!!!
     */

    public void setCoordinatesForEnemyField(MouseEvent event) {
        enemyCell.setX(((int) event.getY()) / 20);
        enemyCell.setY((((int) event.getX()) / 20));
        selectCell(enemyCell, true);
    }

    public void setCoordinatesForPlayerField(MouseEvent event) {
        playerCell.setX((((int) event.getY()) / 20));
        playerCell.setY((((int) event.getX()) / 20));
        selectCell(playerCell, false);

    }

    private void selectCell(Cell cell, boolean isEnemy) {
        int coordinateX = cell.getX();
        int coordinateY = cell.getY();
        GridPane pane;
        if (isEnemy) {
            pane = enemyField;
        } else {
            pane = playerField;
        }
        update(playerMap.getCells(), false);
        if (showEnemyShips)
            update(enemyMap.getCells(), true);
        else
            update(enemyMap.getEnemyCells(), true);
        int x = 0, y = 0;
        for (Node node : pane.getChildren()) {
            if (y == 10) {
                y = 0;
                x++;
                if (x == 10)
                    return;
            }
            if (x == coordinateX && y == coordinateY) {
                node.setStyle("-fx-border-style: solid; -fx-border-width: 200%; -fx-border-color: black;");
            }
            y++;
        }
    }

    @FXML
    public void shoot(ActionEvent actionEvent) throws IOException {

        if (isShipsSet) {
            if (isPlayerMove && enemyCell.getX() >= 0 && enemyCell.getY() >= 0) {
                boolean isStrike = enemyMap.shoot(enemyCell);
                update();
                enemyCell.setX(-1);
                enemyCell.setY(-1);

                if (!isStrike) {
                    isPlayerMove = false;
                }
            }
            if (!isPlayerMove) {
                bot.fire();
                update();
                isPlayerMove = true;
            }
        } else {
            setFile(2, actionEvent);
        }
        move++;

        if (enemyMap.getLivingShips() == 0) isPlayerWin = true;
        if (playerMap.getLivingShips() == 0) isEnemyWin = true;
        if (isPlayerWin)
            setFile(1, actionEvent);
        else if (isEnemyWin)
            setFile(0, actionEvent);
    }

    private void setFile(int type, ActionEvent actionEvent) throws IOException {//0-enemy win, 1- player win, 2-error, 3-rules,4-about

        switch (type) {
            case 0:
                title = "End game";
                text = "You loose...";
                break;
            case 1:
                title = "End game";
                text = "You win!!!";
                break;
            case 2:
                title = "Error";
                text = "Set up all ships!";
                break;
            case 3:
                title = "Rules";
                text = "Go to Wikipedia, Boy!";
                break;
            case 4:
                title = "About";
                text = "Игру делали три не очень сообразительных студеня.";
                break;
        }

        boolean isMessage=(type<3);

        showWindow(actionEvent,isMessage);
    }

    private void showWindow(ActionEvent actionEvent, boolean isMessage) throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("../fxml/files/window.fxml"));

        for (Node node : root.getChildrenUnmodifiable()) {
            if (node instanceof Label) {
                ((Label) node).setText(text);
            }
        }

        stage.setTitle(title);
        if (!isMessage)
            stage.setScene(new Scene(root, 400, 50));
        else
            stage.setScene(new Scene(root, 300, 50));
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL);
        if (isMessage)
            stage.initOwner(((Node) actionEvent.getSource()).getScene().getWindow());
        stage.show();
    }

    private void update() {
        if (showEnemyShips)
            update(enemyMap.getCells(), true);
        else
            update(enemyMap.getEnemyCells(), true);

        update(playerMap.getCells(), false);
        moveShow.setText(String.valueOf(move));
    }

    private void update(int[][] cells, boolean isEnemyField) {
        int x, y;
        GridPane field;
        x = y = 0;
        if (isEnemyField) {
            field = enemyField;
        } else {
            field = playerField;
        }
        for (Node node : field.getChildren()) {
            if (y == 10) {
                x++;
                y = 0;
                if (x == 10)
                    return;
            }


            switch (cells[x][y]) {//0-пустая клетка, 1-мимо, 2-корабль, 3-подбитая часть корабля, 4 - уничтоженный корабль, 5 - запретная клетка(костыль ыыы)
                case 0:
                    node.setStyle("-fx-background-color: cornflowerblue;");
                    break;
                case 1:
                    node.setStyle("-fx-border-style: solid; -fx-border-radius: 10px; -fx-border-color: grey; -fx-background-color: pink; -fx-border-width: 1000%;");
                    break;
                case 2:
                    node.setStyle("-fx-background-color: black");
                    break;
                case 3:
                    node.setStyle("-fx-background-color: orange");
                    break;
                case 4:
                    node.setStyle("-fx-background-color: red");
                    break;
                case 5:
                    node.setStyle("-fx-background-color: cornflowerblue");
                    break;
            }
            y++;
        }
    }


    public void saveGame() throws IOException {

        FileOutputStream fos = new FileOutputStream("Save.txt", false);
        ObjectOutputStream oos = new ObjectOutputStream(fos);

        oos.writeObject(enemyMap);
        oos.flush();

        oos.writeObject(playerMap);
        oos.flush();

        oos.close();
        fos.close();

        FileWriter writer = new FileWriter("SaveConst.txt", false);
        String constants = "";
        boolean[] constantsValue = {isShipsSet, isPlayerWin, isEnemyWin};
        for (boolean constant : constantsValue) {
            if (constant) constants += "1 ";
            else constants += "0 ";
        }

        writer.append(constants + move);
        writer.flush();
        writer.close();
    }

    public void getStatistic(ActionEvent actionEvent) {
    }

    public void loadGame() throws IOException, ClassNotFoundException {
        FileInputStream fin = new FileInputStream("Save.txt");
        ObjectInputStream oin = new ObjectInputStream(fin);

        enemyMap = (Field) oin.readObject();
        playerMap = (Field) oin.readObject();

        oin.close();
        fin.close();

        Scanner scanner = new Scanner(new File("SaveConst.txt"));

        for (int i = 0; i < 4; i++) {
            int constant = scanner.nextInt();
            if ((constant > 1 || constant < 0) && i == 3) {
                System.out.println("File incorrect!");
                break;
            }

            switch (i) {
                case 0:
                    isShipsSet = (constant == 1);
                    break;
                case 1:
                    isPlayerWin = (constant == 1);
                    break;
                case 2:
                    isEnemyWin = (constant == 1);
                    break;
                case 3:
                    move = constant;
                    break;
            }
        }

        scanner.close();

        update();
    }

    public void newGame(ActionEvent actionEvent) throws InterruptedException {
        playerMap.setCellsInNull();
        enemyMap.setCellsInNull();
        isShipsSet = isPlayerWin = isEnemyWin = false;
        move = 1;
        enemyMap.setRandomShips();
        update();
    }

    public void setWayRandom(ActionEvent actionEvent) throws InterruptedException {
        if (!isShipsSet) {
            playerMap.setRandomShips();
            isShipsSet = true;
            update();
        }
    }

    public void addShip(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();

        if (!(source instanceof Button)) {
            return;
        }

        Button clickedButton = (Button) source;

        switch (clickedButton.getId()) {
            case "oneDeck":
                shipType = 1;
                break;
            case "twoDeck":
                shipType = 2;
                break;
            case "threeDeck":
                shipType = 3;
                break;
            case "fourDeck":
                shipType = 4;
                break;
        }
        if (playerCell.getX() >= 0 && playerCell.getY() >= 0) {
            playerMap.setShipState(new Ship(shipType, isHorizontal.isSelected(), playerCell, false), false);
            if (playerMap.getCountShips() == 10)
                isShipsSet = true;
            update(playerMap.getCells(), false);
            if (showEnemyShips)
                update(enemyMap.getCells(), true);
            else
                update(enemyMap.getEnemyCells(), true);
            playerCell.setX(-1);
            playerCell.setY(-1);
        }
    }

    public void rules(ActionEvent actionEvent) throws IOException {
        setFile(3, actionEvent);
    }

    public void about(ActionEvent actionEvent) throws IOException {
        setFile(4, actionEvent);
    }
}
