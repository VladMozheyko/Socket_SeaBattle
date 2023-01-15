import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class ClientWindow extends JFrame implements TCPConnectionListener, MouseListener {

    static Ship ship;                                            //Блок переменных
    Shot shot;
    public static Cell cell;
    CanvasPanel leftPanel, playerPanel;
    ArrayList<Coord> sunkPositions = new ArrayList<>();
    ArrayList<Cell> redCells = new ArrayList<>();

    Random random = new Random();

    static boolean gameOver;
    static String msg;
    static String myShot;
    boolean hit, shotMade = false;


    private static final String IP = "192.168.1.36";
    private static final int PORT = 8001;
    private static TCPConnection connection;

    public static void main(String args[]) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientWindow();
            }
        });
    }


    public ClientWindow() {
        setTitle("Морской бой");                   //Устанавливаем название игры
        setDefaultCloseOperation(EXIT_ON_CLOSE);               //Закрытие окна
        setResizable(false);                                   //Запрещаем изменение размеров
        createPlayerShips();                                   //Создаем корабли


        leftPanel = new CanvasPanel();                          // Левая панель
        leftPanel.setPreferredSize(new Dimension(Resource.COMPUTER_FIELD_SIZE, Resource.COMPUTER_FIELD_SIZE));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE));

        try {
            connection = new TCPConnection(this, IP, PORT);
        } catch (IOException e) {

        }

        leftPanel.addMouseListener(this);


        playerPanel = new CanvasPanel();                         // Вторая панель
        playerPanel.setPreferredSize(new Dimension(Resource.PLAYER_FIELD_SIZE, Resource.PLAYER_FIELD_SIZE));
        playerPanel.setBackground(Color.WHITE);
        playerPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE));


        JButton btnNewGame = new JButton("New Game");            // Кнопки
        btnNewGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Resource.enemyCoords.clear();
                Resource.playerCoords.clear();
                Resource.enemyShots.clear();
                Resource.enemyHits.clear();
                Resource.playerHits.clear();
                Resource.playerShots.clear();
                Resource.playerShips.clear();
                Resource.cells.clear();
                Resource.labels.clear();


                createPlayerShips();
                for (int i = 0; i < Resource.enemyShips.size(); i++) {
                    Resource.enemyShips.get(i).setSunk();
                }
                gameOver = false;

                playerPanel.repaint();
                leftPanel.repaint();
            }
        });
        JButton btnExit = new JButton("Exit");
        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });


        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        Resource.log = new JTextArea();
        Resource.log.setEditable(false);
        JPanel bp = new JPanel();
        bp.setLayout(new GridLayout());                                  // Компоновка элементов
        bp.add(btnNewGame);
        bp.add(btnExit);
        rightPanel.add(playerPanel, BorderLayout.NORTH);
        rightPanel.add(bp, BorderLayout.SOUTH);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
        add(leftPanel);
        add(rightPanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);


    }


    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {


    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String str) {
        String[] shots = str.split("t");

        int x;
        int y;

        if (shots[0].equals("s")) {
            x = Integer.parseInt(shots[1]);
            y = Integer.parseInt(shots[2]);
            shot(x, y);
            hit = false;
            shotMade = false;
            if (isHit(x, y)) {
                connection.sendString("ht" + x + "t" + y);
                hit = true;
                isSunk();
                setSunk();
                String sunkCells = "sunk";
                for (int i = 0; i < sunkPositions.size(); i++) {
                    sunkCells += "t" + sunkPositions.get(i).x + "t" + sunkPositions.get(i).y;
                }
                connection.sendString(sunkCells);
            }
        }

        if (shots[0].equals("h")) {
            x = Integer.parseInt(shots[1]);
            y = Integer.parseInt(shots[2]);
            setHit(x, y);
        }

        if (shots[0].equals("sunk")) {
            for (int i = 1; i < shots.length; i = i + 2) {
                redCells.add(new Cell(Integer.parseInt(shots[i]), Integer.parseInt(shots[i + 1])));
            }
            System.out.println(str);
        }

        setRed();

    }

    public void setRed() {
        for (int i = 0; i < redCells.size(); i++) {
            redCells.get(i).setRed();
            Resource.playerHits.add(redCells.get(i));
        }
        leftPanel.repaint();
    }


    private void setHit(int x, int y) {
        cell = new Cell(x, y);
        cell.setYellow();
        Resource.playerHits.add(cell);
        leftPanel.repaint();
        shotMade = false;

    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {

    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception ex) {

    }


    private void fillCells(ArrayList<Coord> coords) {
        int[] Xx = new int[9];
        int[] Yy = new int[9];

        for (int i = 0; i < coords.size(); i++) {

            Xx[0] = coords.get(i).x - 1;
            Xx[1] = coords.get(i).x;
            Xx[2] = coords.get(i).x + 1; //Проверяем верхние координаты
            Yy[0] = coords.get(i).y + 1;
            Yy[1] = coords.get(i).y + 1;
            Yy[2] = coords.get(i).y + 1; //Проверяем верхние координаты

            Xx[3] = coords.get(i).x - 1;
            Xx[4] = coords.get(i).x;
            Xx[5] = coords.get(i).x + 1; //Проверяем боковые кординаты
            Yy[3] = coords.get(i).y;
            Yy[4] = coords.get(i).y;
            Yy[5] = coords.get(i).y;     //Проверяем боковые кординаты

            Xx[6] = coords.get(i).x - 1;
            Xx[7] = coords.get(i).x;
            Xx[8] = coords.get(i).x + 1; //Проверяем нижние координаты
            Yy[6] = coords.get(i).y - 1;
            Yy[7] = coords.get(i).y - 1;
            Yy[8] = coords.get(i).y - 1; //Проверяем нижние координаты

            for (int j = 0; j < 9; j++) {
                shot = new Shot(Xx[j], Yy[j], 0);
                Resource.playerShots.add(shot);
            }
        }
    }

    private boolean isSet(int x, int y) {
        for (int i = 0; i < Resource.labels.size(); i++) {
            if (x == Resource.labels.get(i).x && y == Resource.labels.get(i).y) {
                Resource.labels.remove(i);
                return true;
            }
        }
        return false;
    }

    private void shot(int x, int y) {                          // Ответный выстрел

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (isHit(x, y)) {
                    cell = new Cell(x, y);
                    cell.setYellow();
                    Resource.enemyHits.add(cell);
                    isSunk();


                } else {
                    shot = new Shot(x, y, 1);
                    Resource.enemyShots.add(shot);
                }
            }
        });

        leftPanel.repaint();
        playerPanel.repaint();

    }

    private static boolean isHit(int x, int y) {                                //Попадание
        for (int i = 0; i < Resource.playerCoords.size(); i++) {
            if (Resource.playerCoords.get(i).x == x && Resource.playerCoords.get(i).y == y) {
                return true;
            }
        }

        return false;
    }

    public static void setPlayersCells(Graphics g) {                           // Огонь по игроку

        for (int i = 0; i < Resource.enemyShots.size(); i++) {
            Resource.enemyShots.get(i).draw(g);
        }
        for (int i = 0; i < Resource.enemyHits.size(); i++) {
            Resource.enemyHits.get(i);
            Resource.enemyHits.get(i).draw(g);
        }
    }

    public static void draw(Graphics g) {                                     //Отрисовка огня игрока

        for (int i = 0; i < Resource.playerShots.size(); i++) {
            Resource.playerShots.get(i).draw(g);
        }

        for (int i = 0; i < Resource.playerHits.size(); i++) {
            Resource.playerHits.get(i).setCellSize(Resource.COMPUTER_CELL_SIZE);
            Resource.playerHits.get(i).draw(g);
        }

        for (int i = 0; i < Resource.labels.size(); i++) {
            Resource.labels.get(i).draw(g);
        }

        if (gameOver) {
            g.setColor(Color.blue);

            g.setFont(new Font("", Font.BOLD, 40));
            g.drawString(msg, 20, 100);
        }

    }


    private void createPlayerShips() {
        int x, y, direction;
        int length = 4;

        x = random.nextInt(7);
        y = random.nextInt(7);
        direction = random.nextInt(2) + 1;
        ship = new Ship(x, y, length, direction, 0);
        Resource.playerShips.add(ship);                                            //Создали первый корабль
        while (Resource.playerShips.size() < 10) {
            if (Resource.playerShips.size() >= 1 && Resource.playerShips.size() < 3) {
                length = 3;
                x = random.nextInt(8);
                y = random.nextInt(8);
            }
            if (Resource.playerShips.size() >= 3 && Resource.playerShips.size() < 6) {
                length = 2;
                x = random.nextInt(9);
                y = random.nextInt(9);
            }
            if (Resource.playerShips.size() >= 6) {
                length = 1;
                x = random.nextInt(10);
                y = random.nextInt(10);
            }

            direction = random.nextInt(2) + 1;
            if (isEnableShip(x, y, length, direction, 0)) {
                ship = new Ship(x, y, length, direction, 0);
                Resource.playerShips.add(ship);
            }
        }

    }


    public boolean isEnableShip(int x, int y, int length, int direction, int p) {          // Проверка возможности располоить корабль
        for (int i = 0; i < length; i++) {
            ;
            if (direction == 1) {
                if (!isEnableCell(x + i, y, p)) {
                    return false;
                }
            }
            if (direction == 2) {
                if (!isEnableCell(x, y + i, p)) {
                    return false;
                }
            }
        }
        return true;
    }


    public boolean isEnableCell(int x, int y, int p) {
        int[] Xx = new int[9];
        int[] Yy = new int[9];

        Xx[0] = x - 1;
        Xx[1] = x;
        Xx[2] = x + 1; //Проверяем верхние координаты
        Yy[0] = y + 1;
        Yy[1] = y + 1;
        Yy[2] = y + 1; //Проверяем верхние координаты

        Xx[3] = x - 1;
        Xx[4] = x;
        Xx[5] = x + 1; //Проверяем боковые кординаты
        Yy[3] = y;
        Yy[4] = y;
        Yy[5] = y;     //Проверяем боковые кординаты

        Xx[6] = x - 1;
        Xx[7] = x;
        Xx[8] = x + 1; //Проверяем нижние координаты
        Yy[6] = y - 1;
        Yy[7] = y - 1;
        Yy[8] = y - 1; //Проверяем нижние координаты

        if (p == 0) {
            for (int i = 0; i < Resource.playerCoords.size(); i++) {
                for (int j = 0; j < 9; j++) {
                    if (Resource.playerCoords.get(i).x == Xx[j] && Resource.playerCoords.get(i).y == Yy[j])
                        return false;
                }
            }
        }

        if (p == 1) {
            for (int i = 0; i < Resource.enemyCoords.size(); i++) {
                for (int j = 0; j < 9; j++) {
                    if (Resource.enemyCoords.get(i).x == Xx[j] && Resource.enemyCoords.get(i).y == Yy[j])
                        return false;
                }
            }
        }
        return true;
    }


    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int x = e.getX() / 40;
        int y = e.getY() / 40;
        myShot = "st" + x + "t" + y;


        if (e.getButton() == 1 && !gameOver) {

            for (int i = 0; i < Resource.playerShots.size(); i++) {
                if (x == Resource.playerShots.get(i).x && y == Resource.playerShots.get(i).y) {
                    return;
                }
            }

            if (!hit && !shotMade) {
                shotMade = true;
                connection.sendString(myShot);                           // Считаем выстрелы игрока
                Resource.playerShots.add(new Shot(x, y, 0));
            }

        }

        if (e.getButton() == 3 && !gameOver) {
            shot = new Shot(x, y, 0);
            shot.switchLabel();

            if (!isSet(x, y)) {
                Resource.labels.add(shot);
            }

        }


        leftPanel.repaint();


        leftPanel.repaint();


    }

    public void setSunk() {
        for (int i = 0; i < Resource.playerShips.size(); i++) {
            if (Resource.playerShips.get(i).isSunk) {
                for (int j = 0; j < Resource.playerShips.get(i).getPositions().size(); j++) {
                    sunkPositions.add(Resource.playerShips.get(i).getPositions().get(j));

                }
            }
        }

        for (int i = 0; i < sunkPositions.size(); i++) {
            System.out.println(sunkPositions.get(i).x + " " + sunkPositions.get(i).y);
        }

    }

    public void isSunk() {

        for (int i = 0; i < Resource.playerShips.size(); i++) {
            Resource.playerShips.get(i).isAlivePlayer();
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public class CanvasPanel extends JPanel {                   // Основа для рисования

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            int cellSize = (int) getSize().getWidth() / Resource.CELL_SIZE;
            g.setColor(Color.lightGray);
            for (int i = 0; i < Resource.CELL_SIZE; i++) {
                g.drawLine(0, i * cellSize, Resource.CELL_SIZE * cellSize, i * cellSize);
                g.drawLine(i * cellSize, 0, i * cellSize, Resource.CELL_SIZE * cellSize);
            }

            if (cellSize == Resource.PLAYER_CELL_SIZE) {
                ClientWindow.ship.draw(g);
                ClientWindow.setPlayersCells(g);
            } else
                ClientWindow.draw(g);
        }
    }

}
