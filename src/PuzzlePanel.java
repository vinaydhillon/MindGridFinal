import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.*;
import java.util.Timer;
import java.util.TimerTask;

public class PuzzlePanel {
    private static final int GRID_SIZE = 9; // Constant for grid size
    private static final int SUBGRID_SIZE = 3; // Constant for subgrid size

    private JFrame puzzleFrame; // Frame for displaying the puzzle
    private JPanel puzzlePanel; // Panel for displaying the Sudoku grid
    private JTextField[][] cells; // Array of text fields for the cells
    private int[][] solution; // Array to store the solution
    private int[][] puzzle; // Array to store the puzzle
    private boolean[][] isOriginal; // Array to track the original numbers
    private Timer timer; // Timer for tracking time
    private JLabel timerLabel; // Label for displaying the timer
    private boolean colorCodingEnabled = true; // Flag to enable or disable color coding
    private boolean puzzleSolved = false; // Flag to indicate if the puzzle is solved
    private int secondsElapsed = 0; // Variable to track elapsed time
    private Color backgroundColor; // Background color of the panel

    // Constructor to initialize the puzzle panel with the given puzzle, solution, and background color
    public PuzzlePanel(int[][] puzzle, int[][] solution, Color backgroundColor) {
        this.puzzle = puzzle;
        this.solution = solution;
        this.backgroundColor = backgroundColor;
        isOriginal = new boolean[GRID_SIZE][GRID_SIZE]; // Initialize the isOriginal array
        markOriginalNumbers(); // Mark the original numbers in the puzzle
    }

    // Method to show the puzzle in a new window
    public void showPuzzle() {
        puzzleFrame = new JFrame("MindGrid PUZZLE");
        puzzleFrame.setSize(600, 600); // Set the size of the frame
        puzzleFrame.setLayout(new BorderLayout()); // Set the layout of the frame

        puzzlePanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE)); // Create a grid layout for the puzzle panel
        puzzlePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Set the border of the panel
        puzzlePanel.setBackground(backgroundColor); // Set the background color of the panel

        cells = new JTextField[GRID_SIZE][GRID_SIZE]; // Initialize the array of text fields
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                final int row = i;
                final int col = j;
                cells[i][j] = new JTextField(puzzle[i][j] == 0 ? "" : String.valueOf(puzzle[i][j])); // Create a text field for each cell
                cells[i][j].setFont(new Font("Arial", Font.PLAIN, 20)); // Set the font of the text field
                cells[i][j].setHorizontalAlignment(JTextField.CENTER); // Center the text
                cells[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Set the border of the text field
                if (isOriginal[i][j]) {
                    cells[i][j].setForeground(Color.BLUE); // Set the color of the original numbers to blue
                    cells[i][j].setEditable(false); // Make the original numbers non-editable
                } else {
                    // Add a document listener to handle user input
                    cells[i][j].getDocument().addDocumentListener(new DocumentListener() {
                        @Override
                        public void insertUpdate(DocumentEvent e) {
                            updateCellColor(row, col); // Update the cell color when text is inserted
                        }

                        @Override
                        public void removeUpdate(DocumentEvent e) {
                            updateCellColor(row, col); // Update the cell color when text is removed
                        }

                        @Override
                        public void changedUpdate(DocumentEvent e) {
                            updateCellColor(row, col); // Update the cell color when text is changed
                        }

                        // Method to update the color of the cell based on the user's input
                        private void updateCellColor(int i, int j) {
                            SwingUtilities.invokeLater(() -> {
                                String text = cells[i][j].getText();
                                if (text.isEmpty()) {
                                    cells[i][j].setForeground(Color.BLACK); // Set the color to black if the cell is empty
                                } else {
                                    try {
                                        int value = Integer.parseInt(text);
                                        if (colorCodingEnabled) {
                                            if (value == solution[i][j]) {
                                                cells[i][j].setForeground(Color.GREEN); // Set the color to green if the input is correct
                                            } else {
                                                cells[i][j].setForeground(Color.RED); // Set the color to red if the input is incorrect
                                            }
                                        } else {
                                            cells[i][j].setForeground(Color.BLACK); // Set the color to black if color coding is disabled
                                        }
                                        checkAndShowSolution(); // Check the solution and show the congratulatory message if solved
                                    } catch (NumberFormatException ex) {
                                        cells[i][j].setForeground(Color.RED); // Set the color to red if the input is invalid
                                    }
                                }
                            });
                        }
                    });
                }
                puzzlePanel.add(cells[i][j]); // Add the text field to the puzzle panel

                // Set borders for 3x3 subgrids
                if ((i + 1) % SUBGRID_SIZE == 0 && i != GRID_SIZE - 1) {
                    cells[i][j].setBorder(BorderFactory.createMatteBorder(1, 1, 3, 1, Color.BLACK));
                }
                if ((j + 1) % SUBGRID_SIZE == 0 && j != GRID_SIZE - 1) {
                    cells[i][j].setBorder(BorderFactory.createMatteBorder(1, 1, 1, 3, Color.BLACK));
                }
                if ((i + 1) % SUBGRID_SIZE == 0 && i != GRID_SIZE - 1 && (j + 1) % SUBGRID_SIZE == 0 && j != GRID_SIZE - 1) {
                    cells[i][j].setBorder(BorderFactory.createMatteBorder(1, 1, 3, 3, Color.BLACK));
                }
            }
        }

        // Create buttons for revealing the solution, toggling colors, saving and loading the puzzle
        JButton revealButton = new JButton("Reveal Solution");
        revealButton.addActionListener(e -> revealSolution());

        JButton toggleColorButton = new JButton("Toggle Colors");
        toggleColorButton.addActionListener(e -> toggleColors());

        JButton saveButton = new JButton("Save Puzzle");
        saveButton.addActionListener(e -> savePuzzle());

        JButton loadButton = new JButton("Load Puzzle");
        loadButton.addActionListener(e -> loadPuzzle());

        timerLabel = new JLabel("Time: 0"); // Initialize the timer label

        // Create a bottom panel for the timer and buttons
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(timerLabel, BorderLayout.WEST); // Add the timer label to the left
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(revealButton);
        buttonPanel.add(toggleColorButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);
        bottomPanel.add(buttonPanel, BorderLayout.EAST); // Add the buttons to the right

        // Add the puzzle panel and bottom panel to the frame
        puzzleFrame.add(puzzlePanel, BorderLayout.CENTER);
        puzzleFrame.add(bottomPanel, BorderLayout.SOUTH);
        puzzleFrame.setVisible(true); // Make the frame visible

        initializeTimer(); // Initialize the timer
    }

    // Method to initialize the timer
    private void initializeTimer() {
        if (timer != null) {
            timer.cancel(); // Cancel the existing timer if it exists
        }
        secondsElapsed = 0; // Reset the elapsed time
        timerLabel.setText("Time: 0"); // Reset the timer label
        timer = new Timer(true); // Create a new timer
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    secondsElapsed++; // Increment the elapsed time
                    timerLabel.setText("Time: " + secondsElapsed); // Update the timer label
                });
            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000); // Schedule the task to run every second
    }

    // Method to reveal the solution
    private void revealSolution() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                cells[i][j].setText(String.valueOf(solution[i][j])); // Set the text of each cell to the solution
                cells[i][j].setForeground(Color.BLUE); // Set the color to blue
            }
        }
        if (!puzzleSolved) {
            puzzleSolved = true;
            JOptionPane.showMessageDialog(null, "Congratulations! You solved the puzzle!"); // Show the congratulatory message
        }
    }

    // Method to toggle the color coding for user input
    private void toggleColors() {
        colorCodingEnabled = !colorCodingEnabled; // Toggle the color coding flag
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (!isOriginal[i][j]) {
                    String text = cells[i][j].getText();
                    if (!text.isEmpty()) {
                        try {
                            int value = Integer.parseInt(text);
                            if (colorCodingEnabled) {
                                if (value == solution[i][j]) {
                                    cells[i][j].setForeground(Color.GREEN); // Set the color to green if the input is correct
                                } else {
                                    cells[i][j].setForeground(Color.RED); // Set the color to red if the input is incorrect
                                }
                            } else {
                                cells[i][j].setForeground(Color.BLACK); // Set the color to black if color coding is disabled
                            }
                        } catch (NumberFormatException ex) {
                            cells[i][j].setForeground(Color.RED); // Set the color to red if the input is invalid
                        }
                    } else {
                        cells[i][j].setForeground(Color.BLACK); // Set the color to black if the cell is empty
                    }
                }
            }
        }
    }

    // Method to save the current puzzle state to a file
    private void savePuzzle() {
        JFileChooser fileChooser = new JFileChooser(); // Create a file chooser
        int option = fileChooser.showSaveDialog(null); // Show the save dialog
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile(); // Get the selected file
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
                out.writeObject(puzzle); // Write the puzzle array to the file
                out.writeObject(solution); // Write the solution array to the file
                out.writeObject(isOriginal); // Write the isOriginal array to the file
                for (int i = 0; i < GRID_SIZE; i++) {
                    for (int j = 0; j < GRID_SIZE; j++) {
                        // Create a CellState object for each cell and write it to the file
                        CellState cellState = new CellState(cells[i][j].getText(), cells[i][j].getForeground(), isOriginal[i][j]);
                        out.writeObject(cellState);
                    }
                }
                out.writeObject(backgroundColor); // Write the background color to the file
                out.writeBoolean(colorCodingEnabled); // Write the color coding flag to the file
                JOptionPane.showMessageDialog(null, "Puzzle saved successfully."); // Show a success message
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to save the puzzle."); // Show an error message
            }
        }
    }

    // Method to load a saved puzzle state from a file
    private void loadPuzzle() {
        JFileChooser fileChooser = new JFileChooser(); // Create a file chooser
        int option = fileChooser.showOpenDialog(null); // Show the open dialog
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile(); // Get the selected file
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                puzzle = (int[][]) in.readObject(); // Read the puzzle array from the file
                solution = (int[][]) in.readObject(); // Read the solution array from the file
                isOriginal = (boolean[][]) in.readObject(); // Read the isOriginal array from the file
                for (int i = 0; i < GRID_SIZE; i++) {
                    for (int j = 0; j < GRID_SIZE; j++) {
                        // Read the CellState object for each cell and update the text and color
                        CellState cellState = (CellState) in.readObject();
                        cells[i][j].setText(cellState.getText());
                        cells[i][j].setForeground(cellState.getColor());
                        if (cellState.isOriginal()) {
                            cells[i][j].setForeground(Color.BLUE); // Set the color of original numbers to blue
                            cells[i][j].setEditable(false); // Make the original numbers non-editable
                        } else {
                            cells[i][j].setEditable(true); // Make the user input cells editable
                        }
                    }
                }
                backgroundColor = (Color) in.readObject(); // Read the background color from the file
                colorCodingEnabled = in.readBoolean(); // Read the color coding flag from the file
                initializeTimer(); // Initialize the timer
                JOptionPane.showMessageDialog(null, "Puzzle loaded successfully."); // Show a success message
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to load the puzzle. The file might be invalid."); // Show an error message
            }
        }
    }

    // Method to mark the original numbers in the puzzle
    private void markOriginalNumbers() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                isOriginal[i][j] = puzzle[i][j] != 0; // Mark cells that are not zero as original numbers
            }
        }
    }

    // Method to check if the solution is correct and show a congratulatory message if solved
    private void checkAndShowSolution() {
        if (puzzleSolved) {
            return; // Exit if the puzzle is already solved
        }
        boolean isCorrect = true;
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                String text = cells[i][j].getText();
                if (!text.isEmpty()) {
                    try {
                        int value = Integer.parseInt(text);
                        if (value != solution[i][j]) {
                            isCorrect = false; // Set isCorrect to false if any cell is incorrect
                            break;
                        }
                    } catch (NumberFormatException ex) {
                        isCorrect = false; // Set isCorrect to false if any cell has an invalid input
                        break;
                    }
                } else {
                    isCorrect = false; // Set isCorrect to false if any cell is empty
                    break;
                }
            }
        }
        if (isCorrect) {
            puzzleSolved = true; // Mark the puzzle as solved
            JOptionPane.showMessageDialog(null, "Congratulations! You solved the puzzle!"); // Show the congratulatory message
        }
    }
}
