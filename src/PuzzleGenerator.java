import java.util.Random;

public class PuzzleGenerator {
    // Constants for the grid size and subgrid size
    private static final int GRID_SIZE = 9;
    private static final int SUBGRID_SIZE = 3;
    private int[][] solution; // Array to store the solution
    private int[][] puzzle; // Array to store the puzzle

    // Method to generate a puzzle with a given number of hints
    public int[][] generatePuzzle(int hints) {
        puzzle = new int[GRID_SIZE][GRID_SIZE];
        solution = new int[GRID_SIZE][GRID_SIZE];
        fillGrid(solution); // Generate a full grid solution
        copyArray(solution, puzzle); // Copy the solution to the puzzle
        removeNumbers(hints); // Remove numbers to create the puzzle
        while (!hasUniqueSolution(puzzle)) { // Ensure the puzzle has a unique solution
            copyArray(solution, puzzle);
            removeNumbers(hints);
        }
        return puzzle; // Return the generated puzzle
    }

    // Method to get the solution
    public int[][] getSolution() {
        return solution;
    }

    // Method to fill the grid using a backtracking algorithm
    private void fillGrid(int[][] grid) {
        Random random = new Random();
        int[] nums = new int[GRID_SIZE];
        for (int i = 0; i < GRID_SIZE; i++) {
            nums[i] = i + 1; // Initialize the nums array with values 1 to 9
        }
        shuffleArray(nums, random); // Shuffle the numbers to ensure randomness
        solveSudoku(grid, 0, 0, nums); // Solve the Sudoku puzzle starting from the top-left corner
    }

    // Method to shuffle an array
    private void shuffleArray(int[] array, Random random) {
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1); // Generate a random index
            int temp = array[index]; // Swap the elements
            array[index] = array[i];
            array[i] = temp;
        }
    }

    // Method to solve the Sudoku puzzle using backtracking
    private boolean solveSudoku(int[][] grid, int row, int col, int[] nums) {
        if (row == GRID_SIZE) {
            row = 0;
            if (++col == GRID_SIZE) {
                return true; // Puzzle solved
            }
        }

        if (grid[row][col] != 0) {
            return solveSudoku(grid, row + 1, col, nums); // Move to the next cell
        }

        for (int num : nums) {
            if (isValidPlacement(grid, row, col, num)) {
                grid[row][col] = num; // Place the number
                if (solveSudoku(grid, row + 1, col, nums)) {
                    return true; // Continue solving
                }
                grid[row][col] = 0; // Backtrack
            }
        }
        return false; // No valid placement found
    }

    // Method to check if placing a number at a specific position is valid
    private boolean isValidPlacement(int[][] grid, int row, int col, int num) {
        for (int i = 0; i < GRID_SIZE; i++) {
            // Check if the number is already present in the row, column, or subgrid
            if (grid[row][i] == num || grid[i][col] == num ||
                    grid[row - row % SUBGRID_SIZE + i / SUBGRID_SIZE][col - col % SUBGRID_SIZE + i % SUBGRID_SIZE] == num) {
                return false; // Number is already present
            }
        }
        return true; // Valid placement
    }

    // Method to remove numbers from the puzzle to create the game
    private void removeNumbers(int hints) {
        Random random = new Random();
        int totalCellsToRemove = GRID_SIZE * GRID_SIZE - hints; // Calculate the number of cells to remove
        while (totalCellsToRemove > 0) {
            int row = random.nextInt(GRID_SIZE); // Generate a random row index
            int col = random.nextInt(GRID_SIZE); // Generate a random column index
            if (puzzle[row][col] != 0) {
                puzzle[row][col] = 0; // Remove the number
                totalCellsToRemove--;
            }
        }
    }

    // Method to ensure the puzzle has a unique solution
    private boolean hasUniqueSolution(int[][] grid) {
        int[][] gridCopy = new int[GRID_SIZE][GRID_SIZE];
        copyArray(grid, gridCopy); // Create a copy of the grid
        return countSolutions(gridCopy, 0) == 1; // Count the number of solutions
    }

    // Method to count the number of solutions for a given puzzle
    private int countSolutions(int[][] grid, int count) {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (grid[row][col] == 0) {
                    for (int num = 1; num <= GRID_SIZE; num++) {
                        if (isValidPlacement(grid, row, col, num)) {
                            grid[row][col] = num;
                            count = countSolutions(grid, count);
                            if (count > 1) {
                                return count; // More than one solution found
                            }
                            grid[row][col] = 0;
                        }
                    }
                    return count; // No valid placement found
                }
            }
        }
        return count + 1; // Solution found
    }

    // Method to copy the content of one array to another
    private void copyArray(int[][] source, int[][] destination) {
        for (int i = 0; i < GRID_SIZE; i++) {
            System.arraycopy(source[i], 0, destination[i], 0, GRID_SIZE); // Copy each row
        }
    }
}
