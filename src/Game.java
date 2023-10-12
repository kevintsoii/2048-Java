import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * This class represents the main game window.
 * It manages the game components, such as the grid, score, and buttons.
 */
public class Game extends JFrame implements KeyListener {
    private static final int FRAME_SIZE = 800;
    private static final Color FRAME_COLOR = new Color(250,248,239);
    private static final Color TEXT_COLOR = new Color(119,110,101);
    
    private GridPanel grid;
    private ScorePanel score;
    private Leaderboard leaderboard;

    private JLabel info;
    private boolean playing;
    
    /**
     * Constructs a new instance of the Game class.
     */
    public Game() {
        setSize(FRAME_SIZE, FRAME_SIZE);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(FRAME_COLOR);

        score = new ScorePanel(FRAME_SIZE * 1/7);
        score.setBackground(FRAME_COLOR);
        leaderboard = new Leaderboard(score);
        
        JLabel title = new JLabel("2048");
        title.setForeground(TEXT_COLOR);
        title.setFont(new Font("Helvetica", Font.BOLD, 92));

        JButton newGame = new JButton("New Game");
        Dimension dimension = new Dimension(FRAME_SIZE*1/6, FRAME_SIZE*1/20);
        newGame.setPreferredSize(dimension);
        newGame.setMinimumSize(dimension);
        newGame.setMaximumSize(dimension);
        newGame.setOpaque(true);
        newGame.setBackground(TEXT_COLOR);
        newGame.setForeground(FRAME_COLOR);
        newGame.setFocusable(false);
        newGame.setFont(new Font("Helvetica", Font.BOLD, 16));
        newGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                reset();
            }
        });

        JButton showLeaderboard = new JButton("Leaderboard");
        dimension = new Dimension(FRAME_SIZE*1/6, FRAME_SIZE*1/20);
        showLeaderboard.setPreferredSize(dimension);
        showLeaderboard.setMinimumSize(dimension);
        showLeaderboard.setMaximumSize(dimension);
        showLeaderboard.setOpaque(true);
        showLeaderboard.setBackground(TEXT_COLOR);
        showLeaderboard.setForeground(FRAME_COLOR);
        showLeaderboard.setFocusable(false);
        showLeaderboard.setFont(new Font("Helvetica", Font.BOLD, 16));
        showLeaderboard.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                leaderboard.setVisible(true);
            }
        });

        JPanel panel1 = new JPanel();
        panel1.setBackground(FRAME_COLOR);
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));
        panel1.add(Box.createHorizontalGlue());
        panel1.add(title);
        panel1.add(Box.createHorizontalGlue());
        panel1.add(Box.createHorizontalGlue());
        panel1.add(score);
        panel1.add(Box.createHorizontalGlue());

        info = new JLabel("Use arrows keys to reach 2048!");
        info.setForeground(TEXT_COLOR);
        info.setFont(new Font("Helvetica", Font.BOLD, 22));

        JPanel panel2 = new JPanel();
        panel2.setBackground(FRAME_COLOR);
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));
        panel2.add(Box.createHorizontalGlue());
        panel2.add(info);
        panel2.add(Box.createHorizontalGlue());
        panel2.add(newGame);
        panel2.add(Box.createRigidArea(new Dimension(10, 0)));
        panel2.add(showLeaderboard);
        panel2.add(Box.createHorizontalGlue());

        grid = new GridPanel(FRAME_SIZE * 5/8);
        grid.setBackground(FRAME_COLOR);

        add(Box.createVerticalGlue());
        add(panel1);
        add(Box.createVerticalGlue());
        add(panel2);
        add(Box.createVerticalGlue());
        add(grid);
        add(Box.createVerticalGlue());

        playing = true;
        addKeyListener(this);
    }
    
    /**
     * Resets the game by creating a new instance and disposing the current window.
     */
    public void reset() {
        Game g = new Game();
        g.setVisible(true);
        leaderboard.dispose();
        this.dispose();
    }

    /**
     * Displays a message indicating that the player has won the game.
     */
    public void win() {
        info.setText("You win!");
    }

    /**
     * Displays a message indicating that the player has lost the game.
     */
    public void lose() {
        info.setText("You lost :(");
    }

    /**
     * Handles the key pressed event.
     * @param e KeyEvent object
     */
    public void keyPressed(KeyEvent e) {
        if (!playing)
            return;

        String direction;
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_W || key == 38) {
            direction = "UP";
        } else if (key == KeyEvent.VK_A || key == 37) {
            direction = "LEFT";
        } else if (key == KeyEvent.VK_S || key == 40) {
            direction = "DOWN";
        } else if (key == KeyEvent.VK_D || key == 39) {
            direction = "RIGHT";
        } else {
            return;
        }

        int[] scoreDiff = grid.shiftTiles(direction);
        // if merged or shifted
        if (scoreDiff[0] > 0 || scoreDiff[1] > 0) {
            score.add(scoreDiff[0]);
            if (grid.spawn())
                grid.repaint();
            if (scoreDiff[0] >= 2048 && !score.getWon()) {
                score.setWon(true);
                win();
            }
        }
        // no merge or shift + can't move = lose
        else if (!grid.canMove()) {
            playing = false;
            lose();
        }
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }
}
