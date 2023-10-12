import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

/**
 * This class is a panel displaying the user's current score.
 */
public class ScorePanel extends JPanel {
    private static final Color BACKGROUND_COLOR = new Color(250,248,239);
    private static final Color TITLE_COLOR = new Color(119,110,101);
    private static final Color SCORE_COLOR = new Color(187,173,160);
    
    private int score;
    private boolean won;
    private BufferedImage image;
    
    /**
     * Constructs a ScorePanel with the specified size.
     * @param size the panel's size
     */
    public ScorePanel(int size) {
        score = 0;
        won = false;
        image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);

        Dimension dimension = new Dimension(size, size);
        setMinimumSize(dimension);
        setMaximumSize(dimension);
        setPreferredSize(dimension);
    }

    /**
     * Gets the score.
     * @return the current score
     */
    public int getScore() {
        return score;
    }
    
    /**
     * Gets if the game has been won.
     * @return true if the game is won, false otherwise
     */
    public boolean getWon() {
        return won;
    }

    /**
     * Sets if the game has been won.
     * @param b true if the game is won, false otherwise
     */
    public void setWon(boolean b) {
        won = b;
    }

    /**
     * Adds the specified value to the score and repaints the panel.
     * @param s the value to add to the score
     */
    public void add(int s) {
        score += s;
        repaint();
    }

    /**
     * Paints the component and renders the score panel.
     * @param g graphics context to draw on
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) image.getGraphics();
        Font f = new Font("Helvetica", Font.BOLD, 26);
        FontMetrics fm = g2d.getFontMetrics(f);
        g2d.setFont(f);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setColor(BACKGROUND_COLOR);
        g2d.fillRoundRect(0, 0, getWidth(), getWidth(), getWidth()/5, getWidth()/5);

        String text = "Score";
        g2d.setColor(TITLE_COLOR);
        g2d.drawString(text, (getWidth() - fm.stringWidth(text))/2, fm.getAscent() + (getHeight() - fm.getHeight())/8);

        text = "" + score;
        g2d.setColor(SCORE_COLOR);
        g2d.drawString(text, (getWidth() - fm.stringWidth(text))/2, fm.getAscent() + (getHeight() - fm.getHeight())*5/8);

        g.drawImage(image, 0, 0, null);
        g2d.dispose();
    }
}
