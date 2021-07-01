import java.util.*;

// This array is used to keep track of where players have gone.
int[] grid;
static int ROWS = 3;
static int COLS = 3;

static int EMPTY = 0;
static int PLAYER = 1;
static int COMPUTER = 2;

void setup()
{
    size ( 300, 300 );
    // Set up initial values;
    grid = new int[ ROWS * COLS ]; // defaults to {0, 0, 0, ... }
}

void draw()
{
    background ( 255, 0, 0 );

    line ( 100, 0, 100, 300 );
    line ( 200, 0, 200, 300 );
    line ( 0, 100, 300, 100 );
    line ( 0, 200, 300, 200 );

    // For each grid space
    for ( int i=0; i < ROWS; i++ )
    {
        for ( int j=0; j < COLS; j++ )
        {
            // Determine what sign needs to go there.
            if ( grid[ i * ROWS + j ] == PLAYER )
            { // cross for player
                line ( i * 100 + 10, j * 100 + 10, ( i + 1 ) * 100 - 10, ( j + 1 ) * 100 - 10 );
                line ( i * 100 + 10, ( j + 1 ) * 100 - 10, ( i + 1 ) * 100 - 10, j * 100 + 10 );
            }
            if ( grid[ i * ROWS + j ] == COMPUTER )
            { // circle for computer
                noFill();
                circle( i * 100 + 50, j * 100 + 50, 90 );
            }
        }
    }
}

List<int[]> generateMoves( int[] tempGrid )
{
    List<int[]> nextMoves = new ArrayList<int[]>();

    // If gameover, no next move
    if ( hasWon( tempGrid, PLAYER ) || hasWon( tempGrid, COMPUTER ) )
    {
        return nextMoves;   // return empty list
    }

    // Search for empty cells and add to the List
    for ( int row = 0; row < ROWS; ++row )
    {
        for ( int col = 0; col < COLS; ++col )
        {
            if ( tempGrid[ row * ROWS + col ] == EMPTY )
            {
                nextMoves.add( new int[] {row, col} );
            }
        }
    }
    return nextMoves;
}

int evaluate( int[] tempGrid )
{
    int score = 0;
    // Evaluate score for each of the 8 lines (3 rows, 3 columns, 2 diagonals)
    score += evaluateLine( tempGrid, 0, 0, 0, 1, 0, 2 );  // row 0
    score += evaluateLine( tempGrid, 1, 0, 1, 1, 1, 2 );  // row 1
    score += evaluateLine( tempGrid, 2, 0, 2, 1, 2, 2 );  // row 2
    score += evaluateLine( tempGrid, 0, 0, 1, 0, 2, 0 );  // col 0
    score += evaluateLine( tempGrid, 0, 1, 1, 1, 2, 1 );  // col 1
    score += evaluateLine( tempGrid, 0, 2, 1, 2, 2, 2 );  // col 2
    score += evaluateLine( tempGrid, 0, 0, 1, 1, 2, 2 );  // diagonal
    score += evaluateLine( tempGrid, 0, 2, 1, 1, 2, 0 );  // alternate diagonal
    return score;
}

/** The heuristic evaluation function for the given line of 3 cells
@Return +100, +10, +1 for 3-, 2-, 1-in-a-line for computer.
-100, -10, -1 for 3-, 2-, 1-in-a-line for opponent.
0 otherwise */
int evaluateLine( int[] tempGrid, int row1, int col1, int row2, int col2, int row3, int col3 )
{
    int score = 0;

    // First cell
    if ( tempGrid[ row1 * ROWS + col1 ] == PLAYER )
    {
        score = 1;
    }
    else if ( tempGrid[ row1 * ROWS + col1 ] == COMPUTER )
    {
        score = -1;
    }

    // Second cell
    if ( tempGrid[ row2 * ROWS + col2 ] == PLAYER )
    {
        if ( score == 1 )
        {   // cell1 is mySeed
            score = 10;
        }
        else if ( score == -1 )
        {  // cell1 is oppSeed
            return 0;
        }
        else
        {  // cell1 is empty
            score = 1;
        }
    }
    else if ( tempGrid[ row2 * ROWS + col2 ] == COMPUTER )
    {
        if ( score == -1 )
        { // cell1 is oppSeed
            score = -10;
        }
        else if ( score == 1 )
        { // cell1 is mySeed
            return 0;
        }
        else
        {  // cell1 is empty
            score = -1;
        }
    }

    // Third cell
    if ( tempGrid[ row3 * ROWS + col3 ] == PLAYER )
    {
        if ( score > 0 )
        {  // cell1 and/or cell2 is mySeed
            score *= 10;
        }
        else if ( score < 0 )
        {  // cell1 and/or cell2 is oppSeed
            return 0;
        }
        else
        {  // cell1 and cell2 are empty
            score = 1;
        }
    }
    else if ( tempGrid[ row3 * ROWS + col3 ] == COMPUTER )
    {
        if ( score < 0 )
        {  // cell1 and/or cell2 is oppSeed
            score *= 10;
        }
        else if ( score > 1 )
        {  // cell1 and/or cell2 is mySeed
            return 0;
        }
        else
        {  // cell1 and cell2 are empty
            score = -1;
        }
    }
    return score;
}

// This magic numbers are the decimal values for binary numbers below
// We use them, because Processing 3.5.4 use very old version of Java(Java 1.8) and they don't support binary literals, yet :(
static final int[] winningPatterns = {
    448, 56, 7,    // rows
    292, 146, 73,  // cols
    273, 84        // diagonals
};
//static final int[] winningPatterns = {
//      0b111000000, 0b000111000, 0b000000111, // rows
//      0b100100100, 0b010010010, 0b001001001, // cols
//      0b100010001, 0b001010100               // diagonals
//};

// 0b111000000
// 0b111000000
// 0b111000000
/** Returns true if thePlayer wins */
boolean hasWon( int[] tempGrid, int thePlayer )
{
    int pattern = 0;  // 9-bit pattern for the 9 cells
    for ( int row = 0; row < ROWS; ++row )
    {
        for ( int col = 0; col < COLS; ++col )
        {
            if ( tempGrid[ row * ROWS + col ] == thePlayer )
            {
                pattern |= ( 1 << ( row * COLS + col ) );
            }
        }
    }
    for ( int winningPattern : winningPatterns )
    {
        if ( ( pattern & winningPattern ) == winningPattern ) return true;
    }
    return false;
}

boolean checkForWin()
{
    boolean result = false;
    if ( hasWon( grid, PLAYER ) )
    {
        javax.swing.JOptionPane.showMessageDialog( null, "Player win" );
        result = true;
    }
    else if ( hasWon( grid, COMPUTER ) )
    {
        javax.swing.JOptionPane.showMessageDialog( null, "Computer win" );
        result = true;
    }
    else
    {
        // check if has more space on the board
        boolean moreSpace = false;
        for ( int i=0; i < ROWS * COLS; i++ )
        {
            if ( grid[ i ] == EMPTY )
            {
                moreSpace = true;
                break;
            }
        }
        if ( !moreSpace )
        {
            javax.swing.JOptionPane.showMessageDialog( null, "Draw" );
            result = true;
        }
    }
    return result;
}

int[] minimax( int[] tempGrid, int depth, int player, int alpha, int beta )
{
    // Generate possible next moves in a list of int[2] of {row, col}.
    List<int[]> nextMoves = generateMoves( tempGrid );

    // mySeed is maximizing; while oppSeed is minimizing
    int score;
    int bestRow = -1;
    int bestCol = -1;

    if ( nextMoves.isEmpty() || depth == 0 )
    {
        // Gameover or depth reached, evaluate score
        score = evaluate( tempGrid );
        return new int[] {score, bestRow, bestCol};
    }
    else
    {
        for ( int[] move : nextMoves )
        {
            // try this move for the current "player"
            tempGrid[ move[ 0 ] * ROWS + move[ 1 ] ] = player;
            if ( player == PLAYER )
            {  // mySeed (computer) is maximizing player
                score = minimax( tempGrid, depth - 1, COMPUTER, alpha, beta )[ 0 ];
                if ( score > alpha )
                {
                    alpha = score;
                    bestRow = move[ 0 ];
                    bestCol = move[ 1 ];
                }
            }
            else
            {  // oppSeed is minimizing player
                score = minimax( tempGrid, depth - 1, PLAYER, alpha, beta )[ 0 ];
                if ( score < beta )
                {
                    beta = score;
                    bestRow = move[ 0 ];
                    bestCol = move[ 1 ];
                }
            }
            // undo move
            tempGrid[ move[ 0 ] * ROWS + move[ 1 ] ] = EMPTY;
            // cut-off
            if ( alpha >= beta ) break;
        }
        return new int[] {( player == PLAYER ) ? alpha : beta, bestRow, bestCol};
    }
}

void mouseClicked()
{
    // Reset with the right mouse button.
    if ( mouseButton == RIGHT )
    {
        for ( int i=0; i < ROWS * COLS; i++ )
        {
            grid[ i ] = EMPTY;
        }
        return;
    }
    int clickedRow = 0;
    int clickedColumn = 0;
    // Otherwise try to place a piece
    // Work out where the mouse was clicked.
    if ( mouseX > width / COLS )
    {
        clickedColumn++;
    }
    if ( mouseX > 2 * width / COLS )
    {
        clickedColumn++;
    }
    if ( mouseY > height / ROWS )
    {
        clickedRow++;
    }
    if ( mouseY > 2 * height / ROWS )
    {
        clickedRow++;
    }
    // If this spot is blank, fill it with the right color...
    if ( grid[ clickedColumn * COLS + clickedRow ] == EMPTY )
    {
        grid[ clickedColumn * ROWS + clickedRow ] = PLAYER;
        // Check for a win...
        if ( checkForWin() )
        {
            return;
        }

        int[] tempGrid = Arrays.copyOf( grid, grid.length );
        int[] result = minimax( tempGrid, 3/*depth*/, COMPUTER, Integer.MIN_VALUE, Integer.MAX_VALUE );
        clickedRow = result[ 1 ];
        clickedColumn = result[ 2 ];
        grid[ clickedRow * ROWS + clickedColumn ] = COMPUTER;
        if ( checkForWin() )
        {
            return;
        }
    }
}
