package com.example.codificador

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.codificador.ui.theme.CodificadorTheme

class MainActivity : ComponentActivity() {
    private lateinit var mPathText: TextView
    private lateinit var mPathButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mPathText = findViewById(R.id.path_select)
        mPathButton = findViewById(R.id.button_file_exp)
        mPathButton.setOnClickListener{mPathText.text = "Path: Valor novo"} //funciona

    }
}

@Composable
fun Greeting() {
    R.layout.activity_main
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CodificadorTheme {
        Greeting()
    }
}