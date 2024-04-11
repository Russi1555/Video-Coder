package com.example.codificador

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.documentfile.provider.DocumentFile
import com.example.codificador.ui.theme.CodificadorTheme


class MainActivity : ComponentActivity() {
    private lateinit var mPathText: TextView
    private lateinit var mPathButton: Button
    private val REQUEST_CODE_READ_EXTERNAL_STORAGE = 1001

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->
            if (result.resultCode == RESULT_OK){
                val data: Intent? = result.data
                val uri = data?.data
                uri?.let {
                    val documentFile = DocumentFile.fromSingleUri(this, it)
                    val filePath = documentFile?.uri?.path
                    // Use filePath as the path of the selected file
                    mPathText.text = filePath
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mPathText = findViewById(R.id.path_select)
        mPathButton = findViewById(R.id.button_file_exp)
        mPathButton.setOnClickListener{
            mPathText.text = "Path: Valor novo"
            openFileExplorer()
        } //funciona
    }


    private fun openFileExplorer(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        //https://developer.android.com/reference/androidx/media3/common/MimeTypes#VIDEO_RAW()
        intent.type = "video/mp4"
        startActivityForResult(intent, REQUEST_CODE_READ_EXTERNAL_STORAGE)
        //filePickerLauncher.launch(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_READ_EXTERNAL_STORAGE && resultCode == Activity.RESULT_OK) {
            if(data != null){
                mPathText.setText("PATH: "+data.getData())
            }

        }
    }
}

@Composable
fun Greeting() {
    R.layout.activity_main
}


@Composable
fun GreetingPreview() {
    CodificadorTheme {
        Greeting()
    }
}