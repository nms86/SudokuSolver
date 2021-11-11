/*
 * Puzzle.java
 *
 * Implementation of a class that represents a Sudoku puzzle and solves
 * it using recursive backtracking.
 *
 * Computer Science S-111, Harvard University
 *
 * skeleton code by the course staff
 *
 * Nadav Soudry, nms86@cornell.edu
 */

import java.util.Scanner;

public class Puzzle {
    // the dimension of the puzzle grid
    public static final int DIM= 9;

    // the dimension of the smaller subgrids within the grid
    public static final int SUBGRID_DIM= 3;

    // The current contents of the cells of the puzzle.
    // values[r][c] gives the value in the cell at row r, column c.
    // The rows and columns are numbered from 0 to DIM-1.
    private int[][] values;

    // Indicates whether the value in a given cell is fixed
    // (i.e., part of the original puzzle).
    // valIsFixed[r][c] is true if the value in the cell
    // at row r, column c is fixed, and valIsFixed[r][c] is false
    // if the value in that cell is not fixed.
    private boolean[][] valIsFixed;

    // This 3-D array allows us to determine if a given
    // subgrid (i.e., a given SUBGRID_DIM x SUBGRID_DIM region
    // of the puzzle) already contains a given value.
    // We use 2 indices to identify a given subgrid.
    // For example:
    //
    // (0,0) (0,1) (0,2)
    //
    // (1,0) (1,1) (1,2)
    //
    // (2,0) (2,1) (2,2)
    //
    // For example, subgridHasValue[0][2][5] will be true if
    // the subgrid in the upper right-hand corner already has
    // a 5 in it, and false otherwise.
    //
    // If a given cell of the board has indices [r][c], it falls
    // within the subgrid with indices [r/3][c/3] using integer
    // division.
    //
    private boolean[][][] subgridHasValue; // [rowGrid][columnGrid][value]

    // ------------- add your additional fields here. -------------
    // In particular, we recommend adding fields to keep track
    // of whether a given row or column already contains a given value.
    // You should be able to use a similar approach to what we've
    // done for the subgrids, but it will be simpler, because each
    // row and column can be identified by a single integer.

    private boolean[][] rowHasValue; // [row][value]

    private boolean[][] columnHasValue; // [column][value]

    /** Constructs a new Puzzle object, which initially has all empty cells. */
    public Puzzle() {
        this.values= new int[DIM][DIM];
        this.valIsFixed= new boolean[DIM][DIM];

        // Note that the third dimension of the array has a length
        // of DIM + 1, because we want to be able to use the possible
        // values (1 through 9) as indices.
        this.subgridHasValue= new boolean[SUBGRID_DIM][SUBGRID_DIM][DIM + 1];

        this.rowHasValue= new boolean[DIM][DIM + 1];
        this.columnHasValue= new boolean[DIM][DIM + 1];
    }

    // Check if a value is able to be placed in a spot
    // Using coordinates
    public boolean isValid(int row, int col, int val) {
        if (!this.rowHasValue[row][val] &&
            !this.columnHasValue[col][val] &&
            !this.subgridHasValue[row / 3][col / 3][val] &&
            !this.valIsFixed[row][col]) {

            return true;
        }

        return false;
    }

    /** This is the key recursive-backtracking method. Returns true if a solution has been found,
     * and false otherwise.
     * 
     * Each invocation of the method is responsible for finding the value of a single cell of the
     * puzzle. The parameter n is the number of the cell that a given invocation of the method is
     * responsible for. We recommend that you consider the cells one row at a time, from top to
     * bottom and left to right, which means that they would be numbered as follows:
     * 
     * 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 ... */
    public boolean solve(int n) {
        // if it reaches the very last box
        if (n >= DIM * DIM) { return true; }

        if (this.valIsFixed[n / 9][n % DIM] && this.solve(n + 1)) {
            // System.out.println("reached fixed possition");
            return true; // fixed value in current spot so move on
        }

        // loop over all the possitble values (1-9)
        for (int val= 1; val <= DIM; val++ ) {
            // System.out.println("Val = " + val + " possition = " + n + " row = " + n / DIM + " col
            // = " + n % DIM);
            if (this.isValid(n / DIM, n % DIM, val)) {
                this.placeVal(val, n / DIM, n % DIM); // value, row, column
                if (this.solve(n + 1)) { return true; }

                // backtrack
                this.removeVal(val, n / DIM, n % DIM);
            }
        }
        return false;
    }

    /** place the specified value in the cell with the specified coordinates, and update the state
     * of the puzzle accordingly. */
    public void placeVal(int val, int row, int col) {
        // System.out.println("val: " + val + " row: " + row + " column: " + col);
        this.values[row][col]= val;

        this.subgridHasValue[row / SUBGRID_DIM][col / SUBGRID_DIM][val]= true;
        this.rowHasValue[row][val]= true;
        this.columnHasValue[col][val]= true;
    }

    /** remove the specified value from the cell with the specified coordinates, and update the
     * state of the puzzle accordingly. */
    public void removeVal(int val, int row, int col) {
        this.values[row][col]= 0;

        this.subgridHasValue[row / SUBGRID_DIM][col / SUBGRID_DIM][val]= false;
        this.rowHasValue[row][val]= false;
        this.columnHasValue[col][val]= false;
    }

    /** Reads in a puzzle specification from the specified Scanner, and uses it to initialize the
     * state of the puzzle. The specification should consist of one line for each row, with the
     * values in the row specified as digits separated by spaces. A value of 0 should be used to
     * indicate an empty cell. */
    public void readFrom(Scanner input) {
        for (int r= 0; r < DIM; r++ ) {
            for (int c= 0; c < DIM; c++ ) {
                int val= input.nextInt();
                this.placeVal(val, r, c);
                if (val != 0) {
                    this.valIsFixed[r][c]= true;
                }
            }
            input.nextLine();
        }
    }

    /** Displays the current state of the puzzle. You should not change this method. */
    public void display() {
        for (int r= 0; r < DIM; r++ ) {
            printRowSeparator();
            for (int c= 0; c < DIM; c++ ) {
                System.out.print("|");
                if (this.values[r][c] == 0) {
                    System.out.print("   ");
                } else {
                    System.out.print(" " + this.values[r][c] + " ");
                }
            }
            System.out.println("|");
        }
        printRowSeparator();
    }

    // A private helper method used by display()
    // to print a line separating two rows of the puzzle.
    private static void printRowSeparator() {
        for (int i= 0; i < DIM; i++ ) {
            System.out.print("----");
        }
        System.out.println("-");
    }
}
