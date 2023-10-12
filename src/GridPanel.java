import java.util.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

/**
 * This class represents a panel that displays a grid of tiles.
 */
public class GridPanel extends JPanel {
    private static final int GRID_SEGMENTS = 4;
    private static final Color BACKGROUND_COLOR = new Color(187,173,160);

    private Tile[][] grid;
    private BufferedImage image;
    
    /**
     * Constructs a GridPanel object with the specified size.
     * @param size panel's size
     */
    public GridPanel(int size) {
        int tileSize = size / (GRID_SEGMENTS + 1);
        grid = new Tile[GRID_SEGMENTS][GRID_SEGMENTS];
        for (int r = 0; r < GRID_SEGMENTS; r++) {
            for (int c = 0; c < GRID_SEGMENTS; c++) {
                grid[r][c] = new Tile(0, c*tileSize + (c+1)*tileSize/5, r*tileSize + (r+1)*tileSize/5, tileSize);
            }
        }
        spawn();
        spawn();

        image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Dimension dimension = new Dimension(size, size);
        setMinimumSize(dimension);
        setMaximumSize(dimension);
        setPreferredSize(dimension);
    }

    /**
     * Retrieves a row from the grid.
     * @param index index of the row
     * @return row of Tiles as an array list
     */
    public ArrayList<Tile> getRow(int index) {
        return new ArrayList<Tile>(Arrays.asList(grid[index]));
    }

    /**
     * Retrieves a column from the grid.
     * @param index index of the row
     * @return column of Tiles as an array list
     */
    public ArrayList<Tile> getColumn(int index) {
        ArrayList<Tile> column = new ArrayList<Tile>();
        for (int i = 0; i < GRID_SEGMENTS; i++) {
            column.add(grid[i][index]);
        }
        return column;
    }

    /**
     * Reverses the order of the elements in the given ArrayList.
     * @param array ArrayList to reverse
     * @return the reversed ArrayList
     */
    public ArrayList<Tile> reverse(ArrayList<Tile> array) {
        int i = 0;
        while (i < array.size()/2) {
            Tile temp = array.get(i);
            array.set(i, array.get(array.size()-1-i));
            array.set(array.size()-1-i, temp);
            i++;
        }
        return array;
    }

    /**
     * Checks if there is a valid move that can be made.
     * @return true if a valid move exists, otherwise false
     */
    public boolean canMove() {
        for (int r = 0; r < GRID_SEGMENTS; r++) {
            for (int c = 0; c < GRID_SEGMENTS; c++) {
                if (grid[r][c].getValue() == 0 || r < GRID_SEGMENTS-1 && grid[r+1][c].getValue() == grid[r][c].getValue() || c < GRID_SEGMENTS-1 && grid[r][c+1].getValue() == grid[r][c].getValue()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Spawns a new tile on the grid.
     * @return true if a tile was successfully spawned, otherwise false
     */
    public boolean spawn() {
        ArrayList<Tile> empty = new ArrayList<Tile>();
        for (int r = 0; r < GRID_SEGMENTS; r++) {
            for (int c = 0; c < GRID_SEGMENTS; c++) {
                if (grid[r][c].getValue() == 0) {
                    empty.add(grid[r][c]);
                }
            }
        }

        if (empty.size() != 0) {
            empty.get((int) (Math.random()*empty.size())).setValue(Math.random() < 0.9 ? 2 : 4); // 10% chance for a 4
            return true;
        }
        return false;
    }

    /**
     * Shifts the tiles in the grid in the specified direction.
     * @param direction the direction ("UP", "DOWN", "LEFT", or "RIGHT")
     * @return array containing the score and a flag indicating whether any tiles were moved
     */
    public int[] shiftTiles(String direction) {
        int score = 0;
        boolean moved = false;
        ArrayList<ArrayList<Tile>> segments = new ArrayList<ArrayList<Tile>>();

        // Saves Tile references to an array, depending on orientation
        if (direction.equals("UP")) {
            for (int c = 0; c < GRID_SEGMENTS; c++) {
                segments.add(getColumn(c));
            }
        } else if (direction.equals("DOWN")) {
            for (int c = 0; c < GRID_SEGMENTS; c++) {
                segments.add(reverse(getColumn(c)));
            }
        } else if (direction.equals("RIGHT")) {
            for (int r = 0; r < GRID_SEGMENTS; r++) {
                segments.add(reverse(getRow(r)));
            }
        } else {
            for (int r = 0; r < GRID_SEGMENTS; r++) {
                segments.add(getRow(r));
            }
        }
        
        for (ArrayList<Tile> segment: segments) {
            // Shifts non-zero Tiles to Left
            int j = 0;
            for (int i = 0; i < segment.size(); i++) {
                if (segment.get(i).getValue() != 0) {
                    if (j != i) {
                        segment.get(i).swap(segment.get(j));
                        moved = true;
                    }
                    j++;
                }
            }

            int scoreAdd = 0;
            // Merges Tile pairs, adds value of merged tile to score
            for (int i = 0; i < segment.size()-1; i++) {
                if (segment.get(i).getValue() == segment.get(i+1).getValue()) {
                    segment.get(i).merge(segment.get(i+1));
                    scoreAdd += segment.get(i).getValue();
                    i++;
                }
            }
            if (scoreAdd == 0) {
                continue;
            }

            // Shifts non-zero Tiles to Left, filling up new gaps
            j = 0;
            for (int i = 0; i < segment.size(); i++) {
                if (segment.get(i).getValue() != 0) {
                    if (j != i) {
                        segment.get(i).swap(segment.get(j));
                    }
                    j++;
                }
            }
            score += scoreAdd;
        }

        int[] returnArr = {score, moved ? 1 : 0};
        return returnArr;
    }

    /**
     * Paints the grid panel on the screen.
     * @param g the graphics context
     */    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) image.getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setColor(BACKGROUND_COLOR);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), getWidth()/20, getHeight()/20); // corner radii = 5%
        g2d.drawRoundRect(0, 0, getWidth(), getHeight(), getWidth()/20, getHeight()/20);
        
        for (Tile[] row: grid) {
            for (Tile tile: row) {
                tile.draw(g2d);
            }
        }

        g.drawImage(image, 0, 0, null);
        g2d.dispose();
    }
}
