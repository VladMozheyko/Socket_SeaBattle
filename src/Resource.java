import javax.swing.*;
import java.util.ArrayList;

public class Resource {
    final static int COMPUTER_FIELD_SIZE = 400;
    final static int CELL_SIZE = 10;
    final static int COMPUTER_CELL_SIZE = COMPUTER_FIELD_SIZE / CELL_SIZE;
    final static int PLAYER_FIELD_SIZE = COMPUTER_FIELD_SIZE / 2;
    final static int PLAYER_CELL_SIZE = PLAYER_FIELD_SIZE / CELL_SIZE;


    static JTextArea log;

    static ArrayList<Ship> playerShips = new ArrayList<>();
    static ArrayList<Shot> playerShots = new ArrayList<>();
    static ArrayList<Cell> playerHits = new ArrayList<>();
    static ArrayList<Cell> enemyHits = new ArrayList<>();

    static ArrayList<Shot> enemyShots = new ArrayList<>();
    static ArrayList<Cell> cells = new ArrayList<>();
    static ArrayList<Coord> playerCoords = new ArrayList<>();
    static ArrayList<Coord> enemyCoords = new ArrayList<>();

    static ArrayList<Ship> enemyShips = new ArrayList<>();

    static ArrayList<Shot> labels = new ArrayList<>();
}
