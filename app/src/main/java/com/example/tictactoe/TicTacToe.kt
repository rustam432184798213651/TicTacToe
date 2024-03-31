package com.example.tictactoe



import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tictactoe.ui.theme.TicTacToeTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class TicTacToe : ComponentActivity() {
    private lateinit var database: RecordsDatabase
    private lateinit var username: String
    private var hardLevel: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.database = RecordsDatabase(this)
        val extras = intent.extras
        if(extras != null) {
            this.username = (extras.getString("username") + "")
            if(extras.getString("level").equals("hard")) {
                this.hardLevel = true
            }
        }

        setContent {
            TicTacToeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TTTScreen(this.database, this.hardLevel, this.username)
                }
            }
        }
    }
}


fun gameIsStillGoing(m: List<Boolean?>): Boolean {
    for (i in 0..8) {
        if (m[i] == null) {
            return true
        }
    }
    return false
}
fun checkEndGame(m: List<Boolean?>): Win? {



    val win_states: Set<Set<Int>> = setOf(
        setOf(0, 3, 6),
        setOf(0, 1, 2),
        setOf(6, 7, 8),
        setOf(2, 5, 8),
        setOf(0, 4, 8),
        setOf(3, 4, 5),
        setOf(1, 4, 7),
        setOf(6, 4, 2),
    )
    var win: Win? = null
    var flag = false
    var offset_: Int = 0
    var tics = mutableSetOf<Int>()
    var tacs = mutableSetOf<Int>()
    var pos: Int? = null
    for (i in 0..2){
        for(j in 0..2) {
            pos = i * 3 + j
            if(m[pos] == true) {
                tics.add(pos)
            }
            else if (m[pos] == false) {
                tacs.add(pos)
            }
        }
    }
    for (win_state in win_states) {
        if (tics.containsAll(win_state)) {
            win = Win.PLAYER
            break
        }
        else if (tacs.containsAll(win_state)) {
            win = Win.AI
            break
        }
    }
    if(win == null && !gameIsStillGoing(m))
        win = Win.DRAW
    return win
}

class NextMove(private var m: List<Boolean?>, private var hardLevel: Boolean) {
    fun getNextMove(): Int? {
        var res: Int? = null
        if (hardLevel) {
            res = GFG.main(this.m.toTypedArray())
        }
        else {
            while(true) {
                val i = Random.nextInt(9)
                if (this.m[i] == null) {
                    res = i
                    break
                }
            }
        }
        return res
    }
}

@SuppressLint("RememberReturnType")
@Composable
fun TTTScreen(database: RecordsDatabase, hardLevel: Boolean, username: String) {
    // true - player's turn, false - AI's turn
    val context = LocalContext.current
    val playerTurn = remember{ mutableStateOf(true) }
    val moves = remember { mutableStateListOf<Boolean?>(null, null, null, null, null, null, null, null, null) }
    val win = remember {
        mutableStateOf<Win?>(null)
    }
    val onTap: (Offset) -> Unit = {
        if (playerTurn.value && win.value == null) {
            // x and y are up to 1000. Overall we have 3 position.
            val norm = 1000.0 / 600.0 // Virtual size to real size
            val x = (it.x*norm / 333).toInt()
            val y = (it.y*norm / 333).toInt()
            val posInMoves = y * 3 + x
            // Can set only if position is null
            if (moves[posInMoves] == null) {
                // Put tic
                moves[posInMoves] = true
                // After that AI turn
                playerTurn.value = false
                win.value = checkEndGame(moves)
            }
        }
    }



    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Tic Tac Toe", fontSize = 30.sp, modifier = Modifier.padding(16.dp))


        Header(playerTurn.value)

        Board(moves, onTap)

        if (!playerTurn.value && win.value == null) {
            CircularProgressIndicator(color = Color.Red, modifier = Modifier.padding(16.dp))

            val coroutineScope = rememberCoroutineScope()
            LaunchedEffect(key1 = Unit) {
                coroutineScope.launch {
                    delay(1500L)
                    val nm = NextMove(moves, hardLevel)
                    val nextMove = nm.getNextMove()
                    if(nextMove != null) {
                        moves[nextMove] = false
                        playerTurn.value = true
                        win.value = checkEndGame(moves)
                    }
                }
            }
        }
        when (win.value) {
            Win.PLAYER -> {
                Text(text = "Player has won \uD83C\uDF89", fontSize = 25.sp)

            }

            Win.AI -> {
                Text(text = "AI has won \uD83D\uDE24", fontSize = 25.sp)
            }

            Win.DRAW -> {
                Text(text = "It's a draw \uD83D\uDE33", fontSize = 25.sp)
            }

            null -> {
                // Continue the game
            }
        }
        if (win.value != null) {

            Button(onClick = {
                playerTurn.value = true
                win.value = null
                for (i in 0..8) {
                    moves[i] = null
                }
            }) {
                Text(text = "Click to start over")
            }
        }
        if(win.value != null) {
            var personWon = false
            if(win.value == Win.PLAYER) {
                personWon = true
            }
            database.updateRecord(username, personWon)
        }

    }



}


@Composable
fun Header(playerTurn: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        val playerBoxColor = if (playerTurn) Color.Blue else Color.LightGray
        val aiBoxColor = if (playerTurn) Color.LightGray else Color.Red

        Box(modifier = Modifier
            .width(100.dp)
            .background(playerBoxColor))
        {
            Text(text = "Player", modifier = Modifier
                .padding(8.dp)
                .align(Alignment.Center))

        }
        Spacer(modifier = Modifier.width(50.dp))

        Box(modifier = Modifier
            .width(100.dp)
            .background(aiBoxColor)) {
            Text(text = "AI", modifier = Modifier
                .padding(8.dp)
                .align(Alignment.Center))
        }

    }
}

@Composable
fun Board(moves: List<Boolean?>, onTap: (Offset) -> Unit) {
    Box(modifier = Modifier
        .aspectRatio(1f)
        .padding(32.dp)
        .background(Color.LightGray)
        .pointerInput(Unit) {
            detectTapGestures(
                onTap = onTap
            )
        }
    ) {
        Column(verticalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxSize(1f)) {
            Row(modifier = Modifier
                .height(2.dp)
                .fillMaxWidth(1f)
                .background(Color.Black)) {}
            Row(modifier = Modifier
                .height(2.dp)
                .fillMaxWidth(1f)
                .background(Color.Black)) {}
        }
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxSize(1f)) {
            Column(modifier = Modifier
                .width(2.dp)
                .fillMaxHeight(1f)
                .background(Color.Black)) {}
            Column(modifier = Modifier
                .width(2.dp)
                .fillMaxHeight(1f)
                .background(Color.Black)) {}
        }
        Column(modifier = Modifier.fillMaxSize(1f)) {
            for(i in 0..2) {
                Row(modifier=Modifier.weight(1f)) {
                    for (j in 0..2) {
                        Column(modifier=Modifier.weight(1f)) {
                            getComposableFromMove(move = moves[i * 3 + j])
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun getComposableFromMove(move: Boolean?) {
    when (move) {
        true -> Image(painter = painterResource(R.drawable.ic_x),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(1f),
            colorFilter = ColorFilter.tint(Color.Blue)
        )
        false -> Image(painter = painterResource(R.drawable.ic_o),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(1f),
            colorFilter = ColorFilter.tint(Color.Red)
        )
        null -> Image(painter = painterResource(R.drawable.ic_null),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(1f)
        )
    }

}


// Kotlin program to find the
// next optimal move for a player
internal object GFG {
    var player = 'x'
    var opponent = 'o'

    // This function returns true if there are moves
    // remaining on the board. It returns false if
    // there are no moves left to play.
    fun isMovesLeft(board: Array<CharArray>): Boolean {
        for (i in 0..2) for (j in 0..2) if (board[i][j] == '_') return true
        return false
    }

    // This is the evaluation function as discussed
    // in the previous article ( http://goo.gl/sJgv68 )
    fun evaluate(b: Array<CharArray>): Int {
        // Checking for Rows for X or O victory.
        for (row in 0..2) {
            if (b[row][0] == b[row][1] &&
                b[row][1] == b[row][2]
            ) {
                if (b[row][0] == player) return +10 else if (b[row][0] == opponent) return -10
            }
        }

        // Checking for Columns for X or O victory.
        for (col in 0..2) {
            if (b[0][col] == b[1][col] &&
                b[1][col] == b[2][col]
            ) {
                if (b[0][col] == player) return +10 else if (b[0][col] == opponent) return -10
            }
        }

        // Checking for Diagonals for X or O victory.
        if (b[0][0] == b[1][1] && b[1][1] == b[2][2]) {
            if (b[0][0] == player) return +10 else if (b[0][0] == opponent) return -10
        }
        if (b[0][2] == b[1][1] && b[1][1] == b[2][0]) {
            if (b[0][2] == player) return +10 else if (b[0][2] == opponent) return -10
        }

        // Else if none of them have won then return 0
        return 0
    }

    // This is the minimax function. It considers all
    // the possible ways the game can go and returns
    // the value of the board
    fun minimax(
        board: Array<CharArray>,
        depth: Int, isMax: Boolean
    ): Int {
        val score = evaluate(board)

        // If Maximizer has won the game
        // return his/her evaluated score
        if (score == 10) return score

        // If Minimizer has won the game
        // return his/her evaluated score
        if (score == -10) return score

        // If there are no more moves and
        // no winner then it is a tie
        if (isMovesLeft(board) == false) return 0

        // If this maximizer's move
        return if (isMax) {
            var best = -1000

            // Traverse all cells
            for (i in 0..2) {
                for (j in 0..2) {
                    // Check if cell is empty
                    if (board[i][j] == '_') {
                        // Make the move
                        board[i][j] = player

                        // Call minimax recursively and choose
                        // the maximum value
                        best = max(
                            best.toDouble(), minimax(
                                board,
                                depth + 1, !isMax
                            ).toDouble()
                        ).toInt()

                        // Undo the move
                        board[i][j] = '_'
                    }
                }
            }
            best
        } else {
            var best = 1000

            // Traverse all cells
            for (i in 0..2) {
                for (j in 0..2) {
                    // Check if cell is empty
                    if (board[i][j] == '_') {
                        // Make the move
                        board[i][j] = opponent

                        // Call minimax recursively and choose
                        // the minimum value
                        best = min(
                            best.toDouble(), minimax(
                                board,
                                depth + 1, !isMax
                            ).toDouble()
                        ).toInt()

                        // Undo the move
                        board[i][j] = '_'
                    }
                }
            }
            best
        }
    }

    // This will return the best possible
    // move for the player
    fun findBestMove(board: Array<CharArray>): Move {
        var bestVal = -1000
        val bestMove = Move()
        bestMove.row = -1
        bestMove.col = -1

        // Traverse all cells, evaluate minimax function
        // for all empty cells. And return the cell
        // with optimal value.
        for (i in 0..2) {
            for (j in 0..2) {
                // Check if cell is empty
                if (board[i][j] == '_') {
                    // Make the move
                    board[i][j] = player

                    // compute evaluation function for this
                    // move.
                    val moveVal = minimax(board, 0, false)

                    // Undo the move
                    board[i][j] = '_'

                    // If the value of the current move is
                    // more than the best value, then update
                    // best/
                    if (moveVal > bestVal) {
                        bestMove.row = i
                        bestMove.col = j
                        bestVal = moveVal
                    }
                }
            }
        }
        return bestMove
    }

    // Driver code
    @JvmStatic
    fun main(m: Array<Boolean?>): Int {
        var board = arrayOf(
            charArrayOf('_', '_', '_'),
            charArrayOf('_', '_', '_'),
            charArrayOf('_', '_', '_')
        )
        var pos: Int? = null
        for(i in 0..2) {
            for (j in 0..2) {
                pos = (i * 3 + j)
                if (m[pos] == true) {
                    board[i][j] = 'x'
                }
                else if (m[pos] == false) {
                    board[i][j] = 'o'
                }
            }
        }
        val bestMove = findBestMove(board)
        return 3 * bestMove.row + bestMove.col
    }

    internal class Move {
        var row = 0
        var col = 0
    }
}