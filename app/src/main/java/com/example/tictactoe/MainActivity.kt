package com.example.tictactoe

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tictactoe.ui.theme.TicTacToeTheme

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

@SuppressLint("RememberReturnType")
@Composable
fun TTTScreen() {
    // true - player's turn, false - AI's turn
    val playerTurn = remember{ mutableStateOf(true)}

    val moves = remember {mutableStateListOf<Boolean?>(true, null, false, null, true, false, null, null, null) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Tic Tac Toe", fontSize = 30.sp, modifier = Modifier.padding(16.dp))


        Header(playerTurn.value)

        Board(moves)
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
            Text(text = "AI", modifier = Modifier.padding(8.dp).align(Alignment.Center))
        }

    }
}

@Composable
fun Board(moves: List<Boolean?>) {
    Box(modifier = Modifier.aspectRatio(1f).padding(32.dp).background(Color.LightGray)) {
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