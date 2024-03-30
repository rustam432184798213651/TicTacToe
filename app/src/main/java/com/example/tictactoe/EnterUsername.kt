package com.example.tictactoe

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
class EnterUsername : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    var mText by remember { mutableStateOf("") }

    // Creating an editable TextField,
    // storing the value in mText
    Column(Modifier.fillMaxWidth()) {
        TextField(
            value = mText,
            onValueChange = { mText = it },
            label = { Text("Enter username") },
            modifier = Modifier
                .fillMaxWidth()
                .absolutePadding(10.dp, 100.dp, 10.dp, 0.dp)
        )
    }
    val context = LocalContext.current
    // Creating a Button to display a Toast
    // consisting mText value upon click
    Box(Modifier.fillMaxSize(), Alignment.Center){
        Button(onClick = {
            context.startActivity(Intent(context, TicTacToe::class.java))
        },
            colors = ButtonDefaults.buttonColors()) {
            Text(text = "Click", color = Color.White)
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