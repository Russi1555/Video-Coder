package com.example.codificador

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.codificador.ui.theme.CodificadorTheme
import android.widget.TextView
import android.widget.Button

class MainActivity : ComponentActivity() {
    private lateinit var mPathText: TextView
    private lateinit var mPathButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mPathText = findViewById(R.id.path_select)
        mPathButton = findViewById(R.id.button_file_exp)

    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    R.layout.activity_main
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CodificadorTheme {
        Greeting("Android")
    }
}