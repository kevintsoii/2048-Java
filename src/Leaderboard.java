import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * This class represents the leaderboard frame.
 */
public class Leaderboard extends JFrame {
    private static final int FRAME_SIZE = 600;
    private static final Color FRAME_COLOR = new Color(250,248,239);
    private static final Color TEXT_COLOR = new Color(119,110,101);

    private ArrayList<Score> leaderboard;
    private ArrayList<String> lines;

    private ScorePanel score;
    private JPanel leaderboardGrid;
    private JTextField username;

    /**
     * Constructs a Leaderboard with a ScorePanel.
     * @param score a ScorePanel object
     */
    public Leaderboard(ScorePanel score) {
        this.score = score;

        setSize(FRAME_SIZE, FRAME_SIZE);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(FRAME_COLOR);

        JLabel title = new JLabel("Leaderboard");
        title.setForeground(TEXT_COLOR);
        title.setFont(new Font("Helvetica", Font.BOLD, 46));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        leaderboardGrid = new JPanel();
        leaderboardGrid.setBackground(FRAME_COLOR);
        leaderboardGrid.setLayout(new GridLayout(0, 3));
        load();
        JPanel leaderboardPanel = new JPanel();
        leaderboardPanel.setBackground(FRAME_COLOR);
        leaderboardPanel.setLayout(new BoxLayout(leaderboardPanel, BoxLayout.X_AXIS));
        leaderboardPanel.add(Box.createHorizontalGlue());
        leaderboardPanel.add(leaderboardGrid);
        leaderboardPanel.add(Box.createHorizontalGlue());

        JLabel usernameLabel = new JLabel("Username: ");
        usernameLabel.setFont(new Font("Helvetica", Font.BOLD, 22));

        username = new JTextField(20);
        Dimension dimension = new Dimension(0, FRAME_SIZE*1/26);
        username.setPreferredSize(dimension);
        username.setMinimumSize(dimension);
        username.setMaximumSize(dimension);
        username.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel addPanel = new JPanel();
        addPanel.setBackground(FRAME_COLOR);
        addPanel.setLayout(new BoxLayout(addPanel, BoxLayout.X_AXIS));
        addPanel.add(Box.createHorizontalGlue());
        addPanel.add(usernameLabel);
        addPanel.add(username);
        addPanel.add(Box.createHorizontalGlue());

        JButton addButton = new JButton("Add Score");
        dimension = new Dimension(FRAME_SIZE*1/2, FRAME_SIZE*1/20);
        addButton.setPreferredSize(dimension);
        addButton.setMinimumSize(dimension);
        addButton.setMaximumSize(dimension);
        addButton.setOpaque(true);
        addButton.setBackground(TEXT_COLOR);
        addButton.setForeground(FRAME_COLOR);
        addButton.setFocusable(false);
        addButton.setFont(new Font("Helvetica", Font.BOLD, 16));
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (username.getText().length() > 0) {
                    username.setEditable(false);
                    add(username.getText());
                    load();
                }
            }
        });

        JPanel addButtonPanel = new JPanel(new FlowLayout());
        addButtonPanel.setBackground(FRAME_COLOR);
        addButtonPanel.add(addButton);

        add(Box.createVerticalGlue());
        add(title);
        add(leaderboardPanel);
        add(Box.createVerticalGlue());
        add(addPanel);
        add(Box.createRigidArea(new Dimension(0, FRAME_SIZE*1/64)));
        add(addButtonPanel);
        add(Box.createVerticalGlue());
    }

    /**
     * Adds the player's score to the leaderboard.
     * @param username player's username
     */
    public void add(String username) {
        try {
            FileWriter writer = new FileWriter("leaderboard.txt", true);
            for (int i = 0; i < username.length(); i++) {
                if (username.charAt(i) == ',')
                    username = username.substring(0, i) + "_" + username.substring(i+1);
            }
            String text = username + "," + score.getScore() + "," + score.getWon();
            if (lines == null || !lines.contains(text)) {
                writer.write(text+"\n");
                writer.close();
            }
        } catch (IOException e) { 
            // No leaderboard yet, it will be created automatically
        }
    }

    /**
     * Loads the leaderboard data from a file and updates the leaderboard display.
     */
    public void load() {
        leaderboard = new ArrayList<Score>();
        try {
            Scanner s = new Scanner(new File("leaderboard.txt"));
            lines = new ArrayList<String>();
            while (s.hasNextLine()) {
                String line = s.nextLine();
                String[] v = line.split(","); // username, score, won
                if (v[2].equals("false"))
                    leaderboard.add(new Score(v[0], Integer.parseInt(v[1])));
                else
                    leaderboard.add(new WinScore(v[0], Integer.parseInt(v[1])));
                lines.add(line);
            }
        } catch (FileNotFoundException e) { }

        insertionSort();

        leaderboardGrid.removeAll();
        leaderboardGrid.revalidate();
        leaderboardGrid.repaint();
        JLabel[] headings = {new JLabel("Rank"), new JLabel("Username"), new JLabel("Score")};
        for (JLabel h: headings) {
            h.setForeground(TEXT_COLOR);
            h.setFont(new Font("Helvetica", Font.BOLD, 22));
            h.setHorizontalAlignment(JLabel.CENTER);
            leaderboardGrid.add(h);
        }

        for (int i = 0; i < leaderboard.size() && i < 10; i ++) {
            leaderboard.get(i).createLabels(i+1, leaderboardGrid);
        }
    }

    /**
     * Performs insertion sort on the leaderboard based on the scores.
     */
    public void insertionSort() {
        for (int i = 1; i < leaderboard.size(); i++) {
            Score temp = leaderboard.get(i);
            int j = i;
            while (j > 0 && temp.getScore() > leaderboard.get(j-1).getScore()) {
                leaderboard.set(j, leaderboard.get(j-1));
                j--;
            }
            leaderboard.set(j, temp);
        }
    }
}

/**
 * This class represents a player's score in the leaderboard.
 */
class Score {
    private String username;
    private int score;

    /**
     * Constructs a Score object with the specified username and score.
     * @param username the player's username
     * @param score the player's score
     */
    public Score(String username, int score) {
        if (username.length() > 10)
            username = username.substring(0, 10);
        this.username = username;
        this.score = score;
    }

    /**
     * Returns the color associated with the score.
     * @return the color
     */
    public Color getColor() {
        return new Color(119,110,101);
    }

    /**
     * Returns the player's score.
     * @return the score
     */
    public int getScore() {
        return score;
    }

    /**
     * Adds the score labels to the leaderboard grid.
     * @param position rank of the score
     * @param leaderboardGrid the leaderboard grid JPanel
     */
    public void createLabels(int position, JPanel leaderboardGrid) {
        JLabel[] labels = {new JLabel("" + position), new JLabel(username), new JLabel("" + score)};
        for (JLabel label: labels) {
            label.setForeground(getColor());
            label.setFont(new Font("Helvetica", Font.PLAIN, 22));
            label.setHorizontalAlignment(JLabel.CENTER);
            leaderboardGrid.add(label);
        }
    }
}

/**
 * This class represents a player's winning score.
 */
class WinScore extends Score {
    /**
     * Constructs a WinScore object with the specified username and score.
     * @param username player's username
     * @param score player's score
     */
    public WinScore(String username, int score) {
        super(username, score);
    }

    /**
     * Returns the color associated with the winning score.
     * @return the color
     */
    public Color getColor() {
        return new Color(237,194,46,255);
    }
}
