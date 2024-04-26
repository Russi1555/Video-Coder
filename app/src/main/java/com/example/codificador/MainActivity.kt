package com.example.codificador

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaCodecList
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer


class MainActivity : ComponentActivity() {
    private lateinit var mPathText: TextView
    private lateinit var mPathButton: Button
    private lateinit var mEncodeButton: Button
    private lateinit var pathFile: android.net.Uri
    private lateinit var stringPathFile: String
    private lateinit var extractor: MediaExtractor
    private lateinit var mediaCodec: MediaCodec
    private lateinit var mediaFormat: MediaFormat
    var downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    private val OUTPUT_FILE_PATH = File(downloadsDir, "result.mp4")
    private val MIME_TYPE = "video/avc"
    private val VIDEO_WIDTH = 640
    private val VIDEO_HEIGHT = 480
    private val FRAME_RATE = 30
    private val BIT_RATE = 180000


    private val REQUEST_FOR_VIDEO_FILE = 1001
    private val REQUEST_CODE = 1011






    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mPathText = findViewById(R.id.path_select)
        mPathButton = findViewById(R.id.button_file_exp)
        mPathButton.setOnClickListener{
            openFileExplorer()
        }
        Log.d("YourTag", "MODEL: " + Build.MODEL);
        Log.d("YourTAG","ID: " + Build.ID);
        Log.d("YourTAG","Manufacture: " + Build.MANUFACTURER);
        Log.d("YourTAG","brand: " + Build.BRAND);
        Log.d("YourTAG","type: " + Build.TYPE);
        Log.d("YourTAGTAG","user: " + Build.USER);
        Log.d("YourTAG","BASE: " + Build.VERSION_CODES.BASE);
        Log.d("YourTAG","INCREMENTAL " + Build.VERSION.INCREMENTAL);
        Log.d("YourTAG","SDK  " + Build.VERSION.SDK);
        Log.d("YourTAG","BOARD: " + Build.BOARD);
        Log.d("YourTAG","BRAND " + Build.BRAND);
        Log.d("YourTAG","HOST " + Build.HOST);
        Log.d("YourTAG","FINGERPRINT: "+Build.FINGERPRINT);
        Log.d("YourTAG","Version Code: " + Build.VERSION.RELEASE);

        mEncodeButton = findViewById(R.id.button_encode)
        mEncodeButton.setOnClickListener{
            val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // Permission not granted, request it
                ActivityCompat.requestPermissions(this, arrayOf(permission), REQUEST_CODE)
            } else {
                // Permission already granted, proceed with file creation
                encodeYUVToMP4()
            }

        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with file creation
                encodeYUVToMP4()
            } else {
                // Permission denied, handle accordingly
            }
        }
    }



    @Suppress("DEPRECATION")
    private fun openFileExplorer(){
        //val intent = Intent(Intent.ACTION_GET_CONTENT)
       // intent.addCategory(Intent.CATEGORY_OPENABLE)
        //https://developer.android.com/reference/androidx/media3/common/MimeTypes#VIDEO_RAW()
        val intent = Intent()
        intent.setType("*/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)

        startActivityForResult(intent, REQUEST_FOR_VIDEO_FILE);

    }


    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_FOR_VIDEO_FILE && resultCode == Activity.RESULT_OK) {
            if(data != null && data.data != null ){
                pathFile = data.data!!
                stringPathFile = pathFile.path!!

                mPathText.setText("PATH: "+pathFile)
                mEncodeButton.visibility = View.VISIBLE
            }

        }
    }
}
fun encodeYUVToMP4() {
    val inputFile = File("/storage/emulated/0/Download/Telegram/video_original_360p.y4m")
    val outputFile = File("/storage/emulated/0/Download/Telegram/teste_novo.y4m")
    val width = 640
    val height = 360
    val frameRate = 30

    val x = EncodeAndMuxTest()
    x.testEncodeVideoToMp4()
    Log.d("Final","FOI")

}


@Composable
fun Greeting() {
    R.layout.activity_main
}

