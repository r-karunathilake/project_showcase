import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class Tile extends JPanel {
    private static final int TILE_SIZE = 60; // Adjust this as per your requirement
    private boolean isSelected = false;

    public Tile() {
        setPreferredSize(new Dimension(TILE_SIZE, TILE_SIZE));
        addMouseListener(new TileMouseListener());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Paint the tile background
        g.setColor(isSelected ? Color.YELLOW : Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Draw the border
        if (isSelected) {
            g.setColor(Color.RED);
            g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        }
    }

    private class TileMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            // Toggle the selection state of the tile
            isSelected = !isSelected;

            // Repaint the tile to update its appearance
            repaint();

            // You can perform additional actions here when a tile is clicked
        }
    }
}

public class ChessboardGUI {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Chessboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(8, 8));

        // Create and add tiles to the chessboard
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Tile tile = new Tile();
                frame.add(tile);
            }
        }

        frame.pack();
        frame.setVisible(true);
    }
}
