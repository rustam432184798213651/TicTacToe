package com.example.tictactoe

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


class EnterUsername : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setContent {

            // Displaying the created elements
            MainContent()
        }
    }
}

// Creating a composable
// function to display the Top Bar
// MyContent() is set as content
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainContent(){
    Scaffold(
        topBar = {TopAppBar(title = {
                Text(
                    "GFG | TextField Fetch Data",
                    color = Color.White
                )
            }) },
        content = { MyContent()}

    )
}

// Creating a composable function MyContent()
// to display editable TextField and a Button
@Composable
fun MyContent(){
    val context = LocalContext.current
    var mText by remember { mutableStateOf("") }

    val label = remember{mutableStateOf("Easy")}
    val cl = remember {mutableStateOf(Color.Green)}
    Column(
        Modifier
            .fillMaxHeight(1f)
            .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = {
                var i = Intent(context, MainActivity::class.java)
                context.startActivity(i)
            },
            modifier=Modifier.fillMaxWidth().padding(top= 80.dp)
        ) {
            Text("Back", fontSize=35.sp, color = Color.White)
        }
        Text("Enter your name", fontSize=19.sp, modifier=Modifier.padding(top=40.dp))
        Text("and choose level", fontSize=19.sp)
        TextField(
            value = mText,
            onValueChange = { mText = it },
            label = { Text("Enter username") },
            modifier = Modifier
                .fillMaxWidth()
                .absolutePadding(10.dp, 30.dp, 10.dp, 0.dp)
        )


        Button(onClick = {
            if(label.value == "Easy") {
                label.value = "Medium"
                cl.value = Color.Yellow
            }
            else if(label.value == "Medium")
            {
                label.value = "Hard"
                cl.value = Color.Red
            }
            else {
                label.value = "Easy"
                cl.value = Color.Green
            }
         }, modifier = Modifier
            .padding(top=20.dp)
            .fillMaxWidth(1f)
            ){
            Text(label.value, fontSize = 35.sp, color = cl.value)
        }
// Creating a Button to display a Toast
        // consisting mText value upon click
        Button(onClick = {
            var i = Intent(context, TicTacToe::class.java)
            i.putExtra("username", mText.filter{ !it.isWhitespace() })
            i.putExtra("level", label.value.lowercase())
            context.startActivity(i)
        },
            colors = ButtonDefaults.buttonColors(), modifier = Modifier
                .padding(top=110.dp, bottom=0.dp)
                .fillMaxWidth()) {
            Text(text = "Play", color = Color.White, fontSize = 45.sp)
        }
    }

}

// Displaying preview
// in Android Studio IDE
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MainContent()
}