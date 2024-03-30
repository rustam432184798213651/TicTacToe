package com.example.tictactoe

import android.util.Log
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tictactoe.ui.theme.TicTacToeTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TicTacToeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TTTScreen()
                }
            }
        }
    }
}

//fun calculate_best_decisions() {
//    val win_state_for_player = mutableStateListOf(
//        mutableStateListOf(0, 3, 6),
//        mutableStateListOf(0, 1, 2),
//        mutableStateListOf(6, 7, 8),
//        mutableStateListOf(2, 5, 8),
//        mutableStateListOf(0, 4, 8),
//        mutableStateListOf(3, 4, 5),
//        mutableStateListOf(1, 4, 7)
//    )
//
//}


@SuppressLint("RememberReturnType")
@Composable
fun TTTScreen() {
    // true - player's turn, false - AI's turn
    val playerTurn = remember{ mutableStateOf(true)}

    val moves = remember {mutableStateListOf<Boolean?>(true, null, false, null, true, false, null, null, null) }

    val onTap: (Offset) -> Unit = {
        if (playerTurn.value) {
            // x and y are up to 1000. Overall we have 3 position.
            val norm = 1000.0 / 600.0 // Virtual size to real size
            val x = (it.x*norm / 333).toInt()
            val y = (it.y*norm / 333).toInt()
            val posInMoves = y * 3 + x
            Log.d("TAG", "x: $x")
            Log.d("TAG","y: $y")
            // Can set only if position is null
            if (moves[posInMoves] == null) {
                // Put tic
                moves[posInMoves] = true
                // After that AI turn
                playerTurn.value = false
            }
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Tic Tac Toe", fontSize = 30.sp, modifier = Modifier.padding(16.dp))


        Header(playerTurn.value)

        Board(moves, onTap)

        if (!playerTurn.value) {
            CircularProgressIndicator(color = Color.Red, modifier = Modifier.padding(16.dp))

            val coroutineScope = rememberCoroutineScope()
            LaunchedEffect(key1 = Unit) {
                coroutineScope.launch {
                    delay(1500L)
                    while(true) {
                        val i = Random.nextInt(9)
                        if (moves[i] == null) {
                            moves[i] = false
                            playerTurn.value = true
                            break
                        }
                    }
                }
            }
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