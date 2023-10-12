import java.awt.*;

/**
 * This class represents a Tile and contains it's value, position, visual details, and  utility methods.
 */
public class Tile {
    private static final Color[] TILE_COLORS = {
        new Color(204,192,179),     // default color
        new Color(238,228,218),     // 2
        new Color(237,224,200),     // 4
        new Color(242,177,121),     // 8
        new Color(245,149,99),      // 16
        new Color(246,124,95),      // 32
        new Color(246,94,59),       // 64
        new Color(237,207,114),     // 128
        new Color(237,204,97),      // 256
        new Color(237,200,80,255),  // 512
        new Color(237,197,63,255),  // 1024
        new Color(237,194,46,255),  // 2048
        new Color(62,57,51)         // > 2048
    };
    private static final Color[] TEXT_COLORS = {
        new Color(119, 110, 101),   // value <= 4
        new Color(249, 246, 242)
    };
    
    private int value;
    
    private int x;
    private int y;
    private int size;

    private Color color;
    private Color textColor;
    
    /**
     * Recursively calculuates the logarithm base 2 of the specified value.
     * @param n an integer value
     * @return the logarithm base 2 of n
     */
    public static int logBase2(int n) {
        if (n <= 1)
            return 0;
        return 1 + logBase2(n/2);
    }
    
    /**
     * Constructs a new Tile object with the specified value, position, and size.
     * @param value the Tile's value
     * @param x the Tile's size x-coordinate
     * @param y the Tile's size y-coordinate
     * @param size the Tile's size
     */
    public Tile(int value, int x, int y, int size) {
        setValue(value);
        this.x = x;
        this.y = y;
        this.size = size;
    }
    
    /**
     * Retrieves the value of the Tile.
     * @return the Tile's value
     */
    public int getValue() {
        return value;
    }
    
    /**
     * Sets the value of the Tile and updates the color.
     * @param value the Tile's new value
     */
    public void setValue(int value) {
        this.value = value;
        setColor();
    }
    
    /**
     * Sets the Tile color based on value.
     */
    public void setColor() {
        int power = logBase2(value);
        color = power < TILE_COLORS.length ? TILE_COLORS[power] : TILE_COLORS[TILE_COLORS.length-1];
        textColor = power <= 2 ? TEXT_COLORS[0] : TEXT_COLORS[1];
    }

    /**
     * Swapts the Tile's value with Tile t
     * @param t Tile to swap values with
     */
    public void swap(Tile t) {
        int temp = value;
        setValue(t.getValue());
        t.setValue(temp);
    }

    /**
     * Merges Tiles by doubling the original's value and setting the other to 0.
     * @param t Tile to merge with
     */
    public void merge(Tile t) {
        setValue(value * 2);
        t.setValue(0);
    }
    
    /**
     * Draws the Tile on the graphics context.
     * @param g2d graphics context to draw on
     */
    public void draw(Graphics2D g2d) {
        g2d.setColor(color);
        g2d.fillRoundRect(x, y, size, size, size / 5, size / 5); // corner radii = 20%
        g2d.drawRoundRect(x, y, size, size, size / 5, size / 5);
        
        if (value > 0) {
            String text = toString();
            Font f = new Font("Helvetica", Font.BOLD, size * 1 / 2 - (text.length() - 1) * 3); // smaller font for larger numbers
            FontMetrics fm = g2d.getFontMetrics(f);
            
            g2d.setFont(f);
            g2d.setColor(textColor);
            g2d.drawString(text, x + (size - fm.stringWidth(text)) / 2, fm.getAscent() + y + (size - fm.getHeight()) / 2);
        }
    }

    /**
     * Converts the value of the Tile to its string representation.
     * @return the Tile's value as a string
     */
    public String toString() {
        return "" + value;
    }
}
