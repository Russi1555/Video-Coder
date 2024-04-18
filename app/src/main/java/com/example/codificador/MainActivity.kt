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
import androidx.media3.common.util.Util

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
    private val OUTPUT_FILE_PATH = "content://media/picker/0/com.android.providers.media.photopicker/media/novo.mp4"
    private val MIME_TYPE = "video/avc"
    private val VIDEO_WIDTH = 720
    private val VIDEO_HEIGHT = 1208
    private val FRAME_RATE = 30
    private val BIT_RATE = 2000000

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
        try {
            // Create a MediaFormat for the encoder
            val format = MediaFormat.createVideoFormat(MIME_TYPE, VIDEO_WIDTH, VIDEO_HEIGHT)
            format.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE)
            format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE)
            format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)

            // Find a suitable encoder for the given format
            val codecName = MediaCodecList(MediaCodecList.REGULAR_CODECS).findEncoderForFormat(format)
            println("CODEC ESCOLHIDO:")
            println(codecName)
            codecName?.let { name ->
                // Create the encoder
                val encoder = MediaCodec.createByCodecName(name)
                encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)

                // Start the encoder
                encoder.start()

                // Read the file and feed its contents to the encoder
                val file = File(stringPathFile)
                val inputStream = FileInputStream(file)
                val extractor = MediaExtractor()
                extractor.setDataSource(inputStream.fd)

                // Find and select the first video track
                var trackIndex = -1
                val trackCount = extractor.trackCount
                for (i in 0 until trackCount) {
                    val trackFormat = extractor.getTrackFormat(i)
                    val mimeType = trackFormat.getString(MediaFormat.KEY_MIME)
                    if (mimeType?.startsWith("video/") == true) {
                        extractor.selectTrack(i)
                        trackIndex = i
                        break
                    }
                }

                // Feed input data and get encoded output
                val buffer = ByteBuffer.allocate(1024 * 1024)
                var sampleSize = 0
                while (sampleSize != -1) {
                    sampleSize = extractor.readSampleData(buffer, 0)
                    if (sampleSize >= 0) {
                        val inputBufferIndex = encoder.dequeueInputBuffer(-1)
                        if (inputBufferIndex >= 0) {
                            val inputBuffer = encoder.getInputBuffer(inputBufferIndex)
                            inputBuffer?.put(buffer.array(), 0, sampleSize)
                            encoder.queueInputBuffer(inputBufferIndex, 0, sampleSize, extractor.sampleTime, 0)
                        }
                        extractor.advance()
                    }
                }

                // Get the encoded output
                val bufferInfo = MediaCodec.BufferInfo()
                var outputBufferIndex = encoder.dequeueOutputBuffer(bufferInfo, -1)
                while (outputBufferIndex >= 0) {
                    val outputBuffer = encoder.getOutputBuffer(outputBufferIndex)
                    // Write the encoded data to the output file
                    val outputFile = File(OUTPUT_FILE_PATH)
                    val outputStream = FileOutputStream(outputFile, true)
                    outputBuffer?.let {
                        val outputData = ByteArray(bufferInfo.size)
                        it.get(outputData)
                        outputStream.write(outputData)
                    }
                    outputStream.close()
                    encoder.releaseOutputBuffer(outputBufferIndex, false)
                    outputBufferIndex = encoder.dequeueOutputBuffer(bufferInfo, -1)
                }


                // Stop and release the encoder
                encoder.stop()
                encoder.release()
                extractor.release()
                inputStream.close()

            } ?: run {
                // No suitable encoder found for the format
                // Handle the error accordingly
            }

        } catch (e: IOException) {
            e.printStackTrace()
            // Handle IOException
        }
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_READ_EXTERNAL_STORAGE && resultCode == Activity.RESULT_OK) {
            if(data != null && data.data != null ){
                pathFile = data.data!!
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

