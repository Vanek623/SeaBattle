package game.logic;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * Created by NEWIVAN on 23.09.2017.
 * ПРОВЕРКА РАСПОЛОЖЕНИЯ!!
 */
public class Field implements Serializable{
    final private int HEIGHT_OF_FIELD = 10;
    final private int WEIGHT_OF_FIELD = 10;
    private boolean isEnemy;
    private int[][] cells;//0-пустая клетка, 1-мимо, 2-корабль, 3-подбитая часть корабля, 4 - уничтоженный корабль, 5 - запретная клетка(костыль ыыы)
    private boolean[][] shotCells;
    private int livingShips=10;
    private ArrayList<Ship> ships = new ArrayList<>();

    public Field(boolean isEnemy) throws InterruptedException {
        cells = new int[HEIGHT_OF_FIELD][WEIGHT_OF_FIELD];
        shotCells = new boolean[HEIGHT_OF_FIELD][WEIGHT_OF_FIELD];
        this.isEnemy = isEnemy;
    }

    public boolean shoot(Cell cell) {
        int x = cell.getX();
        int y = cell.getY();
        switch (cells[x][y]) {
            case 0:
                cells[x][y] = 1;
                break;
            case 5:
                cells[x][y] = 1;
                break;
            case 2:
                for (Ship ship : ships) {
                    if (ship.cellBelongShip(cell)) {
                        ship.wound();
                        if (ship.isDestroy()) {
                            setShipState(ship, true);
                            break;
                        }
                    } else {
                        cells[x][y] = 3;
                    }
                }
                return true;
        }
        shotCells[x][y] = true;
        return false;
    }

    public boolean[][] getShotCells() {
        return shotCells;
    }

    public void setShipState(Ship ship, boolean isDestroy) {
        if (check(ship) || isDestroy) {
            int shipCell, zone;
            if (isDestroy) {
                shipCell = 4;
                zone = 1;
                livingShips--;
            } else {
                shipCell = 2;
                zone = 5;
                ships.add(ship);
                ship.increaseCountShip(ship.getType());
            }
            for (int y = ship.getY_Begin() - 1; y <= ship.getY_End() + 1; y++) {
                for (int x = ship.getX_Begin() - 1; x <= ship.getX_End() + 1; x++) {
                    if (y >= 0 && y < WEIGHT_OF_FIELD && x >= 0 && x < HEIGHT_OF_FIELD) {
                        if (x >= ship.getX_Begin() && x <= ship.getX_End() && y >= ship.getY_Begin() && y <= ship.getY_End())
                            cells[x][y] = shipCell;
                        else {
                            cells[x][y] = zone;
                            if (isDestroy)
                                shotCells[x][y] = true;
                        }
                    }
                }
            }
        }
    }

    public boolean check(Ship ship) {
        if (ship.getX_End() < WEIGHT_OF_FIELD && ship.getY_End() < HEIGHT_OF_FIELD) {
            if (ship.isHorizontal()) {
                for (int y = ship.getY_Begin(); y <= ship.getY_End(); y++)
                    if (cells[ship.getX_End()][y] == 5 || cells[ship.getX_End()][y] == 2) {
                        return false;
                    }
            } else {
                for (int x = ship.getX_Begin(); x <= ship.getX_End(); x++)
                    if (cells[x][ship.getY_End()] == 5 || cells[x][ship.getY_End()] == 2) {
                        return false;
                    }
            }
            return true;
        }
        return false;
    }

    public int[][] getCells() {
        return cells;
    }

    public int[][] getEnemyCells() {
        int[][] enemyCells = new int[HEIGHT_OF_FIELD][WEIGHT_OF_FIELD];
        for (int x = 0; x < HEIGHT_OF_FIELD; x++)
            for (int y = 0; y < WEIGHT_OF_FIELD; y++) {
                if (cells[x][y] == 2 || cells[x][y] == 5)
                    enemyCells[x][y] = 0;
                else
                    enemyCells[x][y] = cells[x][y];
            }
        return enemyCells;
    }

    public int getCountShips() {
        return ships.size();
    }

    public void setCellsInNull() { //КОРАБЛИ ДЛЯ ПРОТИВНИКОВ!!!
        for (int x = 0; x < 10; x++)
            for (int y = 0; y < 10; y++)
                cells[x][y] = 0;
        if (ships.size() != 0) {
            Ship ship = ships.get(0);

            for (int i = 0; i < 4; i++)
                ship.setShips(i, 0);
        }
        livingShips=10;
        ships = new ArrayList<>();
    }

    public ArrayList<Ship> getShips() {
        return ships;
    }

    public void setRandomShips() throws InterruptedException {
        Random random = new Random();
        //int[] countShips;

        for (int i = 3; i >= 0; i--) {
            boolean check = false;
            int x, y;
            do {
                boolean isHorizontal = random.nextBoolean();
                do {
                    x = random.nextInt(10);
                    y = random.nextInt(10);
                    if (y < 10 - i && isHorizontal || x < 10 - i && !isHorizontal)
                        check = true;
                } while (!check);
                //System.out.println("X="+x+", Y="+y+", isH="+isHorizontal);
                setShipState(new Ship(i + 1, isHorizontal, new Cell(x, y), isEnemy), false);
                //System.out.println("Ship type: " + (i + 1) + ", ships this type: " + countShips[i]);
                //System.out.println();
                //TimeUnit.SECONDS.sleep(2);
            } while (!ships.get(0).thisTypeIsNeed(i));
        }
    }

    public int getLivingShips() {
        return livingShips;
    }

    public void save(boolean isEnemy) throws IOException {
        FileWriter writer;
        if (isEnemy)
            writer = new FileWriter("E:\\OneDrive\\SeaBattleFX\\User\\SaveEnemy.txt", false);
        else
            writer = new FileWriter("E:\\OneDrive\\SeaBattleFX\\User\\SaveMy.txt", false);


        for (int x = 0; x < HEIGHT_OF_FIELD; x++)
            for (int y = 0; y < WEIGHT_OF_FIELD; y++)
                writer.write((String.valueOf(cells[x][y]) + " "));



        writer.flush();
    }

    public void load(boolean isEnemy) throws IOException {
        Scanner reader;
        if (isEnemy)
            reader = new Scanner(new File("E:\\OneDrive\\SeaBattle\\User\\SaveEnemy.txt"));
        else
            reader = new Scanner(new File("E:\\OneDrive\\SeaBattle\\User\\SaveMe.txt"));
        for (int x = 0; x < HEIGHT_OF_FIELD; x++)
            for (int y = 0; y < WEIGHT_OF_FIELD; y++)
                cells[x][y] = reader.nextInt();
        reader.close();
    }

    public void printField() {
        for (int x = 0; x < HEIGHT_OF_FIELD; x++) {
            for (int y = 0; y < WEIGHT_OF_FIELD; y++)
                System.out.print(cells[x][y]);
            System.out.println();
        }
    }
}
