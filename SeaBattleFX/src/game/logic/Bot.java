package game.logic;

import java.util.Random;

/**
 * Created by NEWIVAN on 23.09.2017.
 */
public class Bot {
    private boolean isUp, isDown, isLeft, isRight, isStrike, isDestroy;
    private Cell cell = new Cell(0, 0);
    private Ship ship;
    private Field playerField;
    private boolean[][] shootCells;

    public Bot(Field playerField) {
        this.playerField = playerField;
        this.shootCells = playerField.getShotCells();
        isUp=true;
        isDown=true;
        isLeft=true;
        isRight=true;
    }

    public void fire() {
        if (isStrike) {
            if (isDestroy) {
                isStrike = false;
                isDestroy = false;
                isLeft=isDown=isRight=isUp=true;
                randomShoot();
            } else {
                findShip(cell.getX(), cell.getY());
            }
        } else {
            randomShoot();
        }
    }

    private void randomShoot() {
            int x, y;
            Random random = new Random();
            do {
                x = random.nextInt(10);
                y = random.nextInt(10);

                if (!shootCells[x][y]) {
                    cell.setX(x);
                    cell.setY(y);
                    isStrike = playerField.shoot(cell);
                    break;
                }

            } while (true);
            if (isStrike) {
                getShip(cell.getX(), cell.getY());
                fire();
            }
    }

    private void findShip(int x, int y) {
        if (ship.getHealth() > 0) {
            if (isUp) {
                if (x - 1 >= 0) {
                    isUp = playerField.shoot(new Cell(x - 1, y));
                    if (isUp)
                        findShip(x - 1, y);
                }else isUp=false;
            } else if (isDown) {
                if (x + 1 < 10) {
                    isDown = playerField.shoot(new Cell(x + 1, y));
                    if (isDown)
                        findShip(x + 1, y);
                }else isDown=false;
            } else if (isRight) {
                if (y + 1 < 10) {
                    isRight = playerField.shoot(new Cell(x, y + 1));
                    if (isRight&&!shootCells[x][y])
                        findShip(x, y + 1);
                }else isRight=false;
            } else if (isLeft) {
                if (y - 1 >= 0) {
                    isLeft = playerField.shoot(new Cell(x, y - 1));
                    if (isLeft && !shootCells[x][y])
                        findShip(x, y - 1);
                }else isLeft=false;
            }
        } else isDestroy = true;

    }

    private void getShip(int x, int y) {
        for (Ship ship : playerField.getShips()) {
            if (ship.cellBelongShip(new Cell(x, y))) {
                this.ship = ship;
            }
        }
    }
}
