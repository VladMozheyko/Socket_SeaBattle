import java.awt.*;
import java.util.ArrayList;

public class Ship extends Engine {

    public ArrayList<Coord> positions = new ArrayList<>();
    int len;
    public boolean isSunk = false;

    public Ship(int x, int y, int length, int direction, int p) {
        if (p == 1) {
            for (int i = 0; i < length; i++) {
                if (direction == 1) {
                    Resource.enemyCoords.add(new Coord(x + i, y));
                    positions.add(new Coord(x + i, y));

                }

                if (direction == 2) {
                    Resource.enemyCoords.add(new Coord(x, y + i));
                    positions.add(new Coord(x, y + i));
                }


            }
        } else
            for (int i = 0; i < length; i++) {
                if (direction == 1) {
                    Resource.cells.add(new Cell(x + i, y));
                    Resource.playerCoords.add(new Coord(x + i, y));
                    positions.add(new Coord(x + i, y));
                }

                if (direction == 2) {
                    Resource.cells.add(new Cell(x, y + i));
                    Resource.playerCoords.add(new Coord(x, y + i));
                    positions.add(new Coord(x, y + i));
                }

            }
        len = positions.size();
    }

    public ArrayList<Coord> getPositions() {
        return positions;
    }

    public void setSunk() {
        isSunk = false;
    }

    public void isAlivePlayer() {
        int count = 0;
        for (int i = 0; i < positions.size(); i++) {
            for (int j = 0; j < Resource.enemyHits.size(); j++) {
                if (positions.get(i).x == Resource.enemyHits.get(j).x &&
                        positions.get(i).y == Resource.enemyHits.get(j).y) {
                    count++;

                }
            }
        }
        if (count == len) {
            isSunk = true;
            for (int i = 0; i < positions.size(); i++) {
                for (int j = 0; j < Resource.enemyHits.size(); j++) {
                    if (Resource.enemyHits.get(j).x == positions.get(i).x &&
                            Resource.enemyHits.get(j).y == positions.get(i).y) {
                        Resource.enemyHits.get(j).setRed();
                    }
                }
            }
        }

    }

    @Override
    public void draw(Graphics g) {
        for (int i = 0; i < Resource.cells.size(); i++) {
            Resource.cells.get(i).draw(g);
        }

    }

}
