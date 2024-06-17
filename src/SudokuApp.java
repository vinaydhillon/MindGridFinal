import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class SudokuApp extends JFrame {

    // Constants for the number of hints based on difficulty level
    private static final int EASY_HINTS = 40;
    private static final int HARD_HINTS = 30;

    // GUI components
    private JPanel mainPanel;
    private JButton startButton, customizationButton, exitButton;
    private ImageIcon logoIcon;
    private Color backgroundColor = Color.WHITE;

    // Constructor to set up the main window and components
    public SudokuApp() {
        // Set the title of the window
        setTitle("MindGrid");
        // Set the size of the window
        setSize(600, 400);
        // Define the default close operation
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Use BorderLayout for the main layout
        setLayout(new BorderLayout());

        // Initialize the main panel with a grid layout
        mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(173, 216, 230)); // Set background color to light blue
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Create a title panel and label
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(173, 216, 230));
        JLabel titleLabel = new JLabel("MindGrid");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titlePanel.add(titleLabel);

        // Add the title label to the main panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);

        // Add the start button to the main panel
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(startButton = new JButton("Start"), gbc);

        // Add the customization button to the main panel
        gbc.gridy = 2;
        mainPanel.add(customizationButton = new JButton("Customization"), gbc);

        // Add the exit button to the main panel
        gbc.gridy = 3;
        mainPanel.add(exitButton = new JButton("Exit"), gbc);

        // Add the main panel to the center of the frame
        add(mainPanel, BorderLayout.CENTER);

        // Add action listeners to the buttons
        startButton.addActionListener(e -> showDifficultySelection());
        customizationButton.addActionListener(e -> showCustomizationOptions());
        exitButton.addActionListener(e -> System.exit(0));


    }

    // Method to show the difficulty selection dialog
    private void showDifficultySelection() {
        // Options for the difficulty levels
        String[] options = {"Easy", "Hard"};
        // Show a dialog to select the difficulty
        int choice = JOptionPane.showOptionDialog(this, "Select Difficulty", "Difficulty",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, logoIcon, options, options[0]);
        int hints = 0;
        switch (choice) {
            case 0:
                hints = EASY_HINTS; // Set hints to EASY_HINTS if Easy is selected
                break;
            case 1:
                hints = HARD_HINTS; // Set hints to HARD_HINTS if Hard is selected
                break;
            default:
                return; // Exit if no selection is made
        }
        // Create a new PuzzleGenerator to generate a puzzle
        PuzzleGenerator generator = new PuzzleGenerator();
        // Generate a puzzle with the selected number of hints
        int[][] puzzle = generator.generatePuzzle(hints);
        // Get the solution of the puzzle
        int[][] solution = generator.getSolution();
        // Show the puzzle using the PuzzlePanel
        new PuzzlePanel(puzzle, solution, backgroundColor).showPuzzle();
    }

    // Method to show customization options
    private void showCustomizationOptions() {
        // Show a color chooser dialog to select the background color
        Color newColor = JColorChooser.showDialog(this, "Choose Background Color", backgroundColor);
        if (newColor != null) {
            backgroundColor = newColor; // Set the selected color as the background color
            getContentPane().setBackground(backgroundColor); // Update the background color of the main frame
        }
    }
}
