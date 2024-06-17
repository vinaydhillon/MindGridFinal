import java.awt.Color;
import java.io.Serializable;

// Class to represent the state of a single cell in the puzzle
public class CellState implements Serializable {
    private final String text; // The text in the cell
    private final Color color; // The color of the text
    private final boolean original; // Flag to indicate if the cell is an original number

    // Constructor to initialize the cell state
    public CellState(String text, Color color, boolean original) {
        this.text = text;
        this.color = color;
        this.original = original;
    }

    // Getter method for the text
    public String getText() {
        return text;
    }

    // Getter method for the color
    public Color getColor() {
        return color;
    }

    // Getter method for the original flag
    public boolean isOriginal() {
        return original;
    }
}
