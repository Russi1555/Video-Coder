package com.example.codificador

import android.R.attr.mimeType
import android.R.string
import android.app.Activity
import android.content.Intent
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CBR
import android.media.MediaCodecList
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever;
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.Q
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable

import java.io.File
import java.io.FileInputStream


class MainActivity : ComponentActivity() {
    private lateinit var mPathText: TextView
    private lateinit var mPathButton: Button
    private lateinit var mEncodeButton: Button
    private lateinit var pathFile: android.net.Uri
    private lateinit var stringPathFile: String
    private lateinit var extractor: MediaExtractor
    private lateinit var mediaCodec: MediaCodec
    private lateinit var mediaFormat: MediaFormat
    private val REQUEST_CODE_READ_EXTERNAL_STORAGE = 1001
    private val REQUEST_VIDEO_CODE = 1011


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mPathText = findViewById(R.id.path_select)
        mPathButton = findViewById(R.id.button_file_exp)
        mPathButton.setOnClickListener{
            openFileExplorer()
        }
        println("MODEL: " + Build.MODEL);
        println("ID: " + Build.ID);
        println("Manufacture: " + Build.MANUFACTURER);
        println("brand: " + Build.BRAND);
        println("type: " + Build.TYPE);
        println("user: " + Build.USER);
        println("BASE: " + Build.VERSION_CODES.BASE);
        println("INCREMENTAL " + Build.VERSION.INCREMENTAL);
        println("SDK  " + Build.VERSION.SDK);
        println("BOARD: " + Build.BOARD);
        println("BRAND " + Build.BRAND);
        println("HOST " + Build.HOST);
        println("FINGERPRINT: "+Build.FINGERPRINT);
        println("Version Code: " + Build.VERSION.RELEASE);

        mEncodeButton = findViewById(R.id.button_encode)
        mEncodeButton.setOnClickListener{
            startEncoding()
        }
    }

    @Suppress("DEPRECATION")
    private fun openFileExplorer(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        //https://developer.android.com/reference/androidx/media3/common/MimeTypes#VIDEO_RAW()
        intent.type = "video/mp4"
        startActivityForResult(intent, REQUEST_CODE_READ_EXTERNAL_STORAGE)
    }

    private fun startEncoding(){
        println("COMEÇAMOS A CODIFICAR")
        val file = File(stringPathFile)
        val fileInputStream = FileInputStream(file)
        val arquivo_descript = fileInputStream.fd
        extractor = MediaExtractor()
        extractor.setDataSource(arquivo_descript)
        val numTracks = extractor.trackCount
        println(numTracks)
        for(i in 0 until numTracks){
            val inputFormat = extractor.getTrackFormat(i)
            val mime = inputFormat.getString(MediaFormat.KEY_MIME)!!
            println(mime)
        }


        mediaCodec = MediaCodec.createEncoderByType("video/avc")
        mediaFormat= MediaFormat.createVideoFormat("video/avc",320,240)
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE,125000)
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 15);
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar);
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 5);
        mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mediaCodec.start()
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_READ_EXTERNAL_STORAGE && resultCode == Activity.RESULT_OK) {
            if(data != null){
                pathFile = data.getData()!!
                stringPathFile = pathFile.toString()

                mPathText.setText("PATH: "+pathFile)
                mEncodeButton.visibility = View.VISIBLE
            }

        }
    }
}

@Composable
fun Greeting() {
    R.layout.activity_main
}

