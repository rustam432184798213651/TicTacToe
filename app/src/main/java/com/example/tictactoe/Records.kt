package com.example.tictactoe

import androidx.recyclerview.widget.RecyclerView
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tictactoe.ui.theme.TicTacToeTheme

class Records : ComponentActivity() {
    private lateinit var db: RecordsDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = RecordsDatabase(this)
        setContent {
            TicTacToeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var allRecords = db.getAll()

                    RecordList(records = allRecords)
                }
            }
        }
    }
}


data class Record(val name: String, val wins: Int, val losses: Int)
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "records_2.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "record"
        private const val CREATE_TABLE_RECORD = """
            CREATE TABLE IF NOT EXISTS $TABLE_NAME (
                name TEXT PRIMARY KEY,
                wins INTEGER,
                losses INTEGER
            )
        """
    }

    override fun onCreate(db: SQLiteDatabase) {
        // create the record table
        db.execSQL(CREATE_TABLE_RECORD)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        // create the new record table
        onCreate(db)
    }
}


class RecordsDatabase(context: Context) {
    val table_name = "record"
    private val databaseHelper = DatabaseHelper(context)

    fun insert(name: String, wins: Int, losses: Int) {
            // get the writable database
            val db = databaseHelper.writableDatabase

            // create the ContentValues object
            val values = ContentValues().apply {
            put("name", name)
            put("wins", wins)
            put("losses", losses)
        }

        // insert the data into the table
        db.insert("record", null, values)

        // close the database connection
        db.close()
    }

@SuppressLint("Range")
fun updateRecord(name: String, personWon: Boolean, draw: Boolean = false){
    var db = databaseHelper.readableDatabase
    val selectQuery = "SELECT  * FROM $table_name WHERE name = ?"
    var cursor = db.rawQuery(selectQuery, arrayOf(name))

    if(cursor.count == 0) {
        var wins = 0
        var losses = 0
        if(!draw) {
            if (personWon) {
                wins += 1
            } else {
                losses += 1
            }
        }
        val values = ContentValues().apply {
            put("name", name)
            put("wins", wins)
            put("losses", losses)
        }
        db.insert(table_name, null, values)
    }
    else {
        if(draw) {
            return
        }
        cursor.moveToNext()
        var wins = cursor.getInt(1)
        var losses = cursor.getInt(2)
        if (personWon) {
            wins += 1
        }
        else {
            losses += 1
        }
        val values = ContentValues().apply {
            put("wins", wins)
            put("losses", losses)
        }
        db = databaseHelper.writableDatabase
        db.update("record", values, "name = ?", arrayOf(name))
    }
}
    @SuppressLint("Range")
fun getAll(): List<Record> {
    val list = mutableListOf<Record>()

    // get the readable database
    val db = databaseHelper.readableDatabase

    // select all data from the table
    val cursor = db.rawQuery("SELECT * FROM $table_name", null)

    // iterate through the cursor and add the data to the list
    while (cursor.moveToNext()) {
        val name = cursor.getString(cursor.getColumnIndex("name"))
        val wins = cursor.getInt(cursor.getColumnIndex("wins"))
        val losses = cursor.getInt(cursor.getColumnIndex("losses"))
        list.add(Record(name, wins, losses))
    }
        list.sortBy{ -1 * it.wins }


// close the cursor and database connection
cursor.close()
db.close()

return list
}



fun delete(name: String) {
    // get the writable database
    val db = databaseHelper.writableDatabase

    // delete the data from the table
    db.delete("record", "name = ?", arrayOf(name))

    // close the database connection
    db.close()
}


}
@Composable
fun RecordList(records: List<Record>) {
    var context = LocalContext.current
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Button(
            onClick = {
                      var i = Intent(context, MainActivity::class.java)
                      context.startActivity(i)
            }, modifier=Modifier.fillMaxWidth().padding(top= 80.dp)
        ) {
            Text("Back", fontSize=35.sp, color = Color.White)
        }
        records.forEach { record ->
            RecordRow(record)
        }
    }
}

@Composable
fun RecordRow(record: Record) {
    Card(modifier = Modifier
        .padding(all = 10.dp)
        .fillMaxWidth()) {
        Column(modifier = Modifier.padding(all = 10.dp)) {
            Text(record.name, fontSize = 25.sp, fontWeight = FontWeight.W700, modifier = Modifier.padding(10.dp))
            Text("Wins: " + record.wins.toString() + "    Losses: " + record.losses.toString(), color = Color.Gray, modifier = Modifier.padding(10.dp))
        }
    }
}
//class RecordAdapter(private val recordList: List<RecordsDatabase.Record>): RecyclerView.Adapter<RecordAdapter.RecordViewHolder>()
//{
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
//        val view = Layo
//    }
//}
//
