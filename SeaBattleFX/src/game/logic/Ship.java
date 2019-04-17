package game.logic;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by NEWIVAN on 23.09.2017.
 */
public class Ship implements Serializable {
    /*
    Описание класса корабль
    I.Поля:
        1)Массив (константы), который ограничевает количество кораблей каждого типа
        2)Статический массив, выполняющий роль счетчика кораблей
        3)Тип корабля(1 палбуный, 2-ух и т.д)
        4)Переменная отвечающая за расположение(горизонтально или нет)
        5)Координаты корабля где х1,у1 - координаты начала корабля(нажимаемая точка на поле),
          х2,у2 - координаты конца корабля(высчитывается с учетом типа корабля)
        6)Переменная, отвечающая за колличество не подбитых клеток корабля(при значении = 0, корабль уничтожается)

    II.Методы:
        1)конструктор
        2)Рассчет координат
        3)метод, определяющий является ли указанная клетка частью корабля
        4)при попадании по кораблю уменьшается количество его жизней
        5)необходимые гетеры
     */

    private final int[] COUNT_OF_SHIPS = {4, 3, 2, 1};
    private static int[] playerShips = {0, 0, 0, 0};
    private static int[] enemyShips = {0, 0, 0, 0};

    private int type;
    private boolean isHorizontal, isEnemyShip;
    private int[] coordinates = new int[4]; //(x1,x2,y1,y2)
    private int health;

    public Ship(int type, boolean isHorizontal, Cell cell, boolean isEnemyShip) {
        if (playerShips[type - 1] < COUNT_OF_SHIPS[type - 1]) {
            this.type = type;
            this.isHorizontal = isHorizontal;
            setCoordinates(cell.getX(), cell.getY());
            health = type;
            this.isEnemyShip = isEnemyShip;
        }
    }

    public void setShips(int index, int value) {
        if (isEnemyShip)
            enemyShips[index] = value;
        else
            playerShips[index] = value;
    }

    public boolean shipIsSet() {
        int sum = 0;
        int needSum = 0;
        int[] ships;

        if (isEnemyShip)
            ships = enemyShips;
        else
            ships = playerShips;

        for (int n : ships) {
            sum += n;
        }
        for (int n : COUNT_OF_SHIPS) {
            needSum += n;
        }
        return sum == needSum;
    }

    public boolean thisTypeIsNeed(int index){
        if (isEnemyShip){
            return enemyShips[index]==COUNT_OF_SHIPS[index];
        }
        else
            return playerShips[index]==COUNT_OF_SHIPS[index];
    }

    public int[] getCountOfShips(){
        if (isEnemyShip)
            return enemyShips;
        else
            return playerShips;
    }

    public int getHealth() {
        return health;
    }

    private void setCoordinates(int x, int y) {
        coordinates[0] = x;
        coordinates[2] = y;
        for (int i = 1; i < type; i++) {
            if (isHorizontal)
                y++;
            else
                x++;
        }
        coordinates[1] = x;
        coordinates[3] = y;
    }

    public boolean cellBelongShip(Cell cell) {
        int x = cell.getX();
        int y = cell.getY();
        //System.out.println("X=XB, XE=X, Y=YB, Y=YE");
        //System.out.println(x+"="+getX_Begin()+";"+getX_End()+"="+x+";"+y+"="+getY_Begin()+";"+y+"="+getY_End());
        if (getX_Begin() == x && getX_End() == x) {
            if (y >= getY_Begin() && y <= getY_End()) {
                return true;
            }
        } else if (getY_Begin() == y && getY_End() == y) {
            if (x >= getX_Begin() && x <= getX_End()) {
                return true;
            }
        }
        return false;
    }

    public void wound() {
        health--;
    }

    public boolean isDestroy() {
        return health == 0;
    }

    public boolean isHorizontal() {
        return isHorizontal;
    }

    public int getX_Begin() {
        return coordinates[0];
    }

    public int getY_Begin() {
        return coordinates[2];
    }

    public int getX_End() {
        return coordinates[1];
    }

    public int getY_End() {
        return coordinates[3];
    }

    public int getType() {
        return type;
    }

    public void increaseCountShip(int type) {
        if (isEnemyShip)
            enemyShips[type - 1]++;
        else
            playerShips[type - 1]++;
    }
}
