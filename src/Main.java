import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            // Create an instance of SudokuApp
            SudokuApp app = new SudokuApp();
            // Make the app visible
            app.setVisible(true);
        });
    }
}
