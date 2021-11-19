package com.ddona.threading

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import com.ddona.threading.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class MainActivity : AppCompatActivity() {

    private val UPDATE_IMAGE = 1

    private lateinit var binding: ActivityMainBinding
    private lateinit var bitmap: Bitmap
    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == UPDATE_IMAGE) {
                val number1 = msg.arg1
                val number2 = msg.arg2
                val bm = msg.obj as Bitmap
                binding.imgAvatar.setImageBitmap(bm)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.imgDownload.setOnClickListener {
//            downloadImage()
            GlobalScope.launch {
                val bitmap = downloadFileWithCoroutine()
                withContext(Dispatchers.Main) {
                    binding.imgAvatar.setImageBitmap(bitmap)
                }
            }
//            DownloadFile().execute("https://photo-cms-baonghean.zadn.vn/w607/Uploaded/2021/ftgbtgazsnzm/2020_07_14/ngoctrinhmuonsinhcon1_swej7996614_1472020.jpg")
        }


    }

    suspend fun downloadFileWithCoroutine(): Bitmap {
        val url =
            URL("https://photo-cms-baonghean.zadn.vn/w607/Uploaded/2021/ftgbtgazsnzm/2020_07_14/ngoctrinhmuonsinhcon1_swej7996614_1472020.jpg")
        val connection = url.openConnection()
        val inputStream = connection.getInputStream()
        return BitmapFactory.decodeStream(inputStream)
    }

    inner class DownloadFile : AsyncTask<String, Void, Bitmap>() {
        override fun onPreExecute() {
            super.onPreExecute()
            Toast.makeText(applicationContext, "Start Downloading!", Toast.LENGTH_SHORT).show()
        }

        override fun doInBackground(vararg params: String?): Bitmap {
            val link = params[0]
            val url = URL(link)
            val connection = url.openConnection()
            val inputStream = connection.getInputStream()
            return BitmapFactory.decodeStream(inputStream)
        }

        override fun onProgressUpdate(vararg values: Void?) {
            super.onProgressUpdate(*values)
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)
            binding.imgAvatar.setImageBitmap(result)
        }

    }

    fun downloadImage() {
        val thread = Thread {
            val url =
                URL("https://photo-cms-baonghean.zadn.vn/w607/Uploaded/2021/ftgbtgazsnzm/2020_07_14/ngoctrinhmuonsinhcon1_swej7996614_1472020.jpg")
            val connection = url.openConnection()
            val inputStream = connection.getInputStream()
            bitmap = BitmapFactory.decodeStream(inputStream)
//            handler.sendEmptyMessage(UPDATE_IMAGE)
            val msg = Message()
            msg.what = UPDATE_IMAGE
            msg.arg1 = 10
            msg.arg2 = 20
            msg.obj = bitmap
            handler.sendMessage(msg)
//            runOnUiThread {
//                binding.imgAvatar.setImageBitmap(bitmap)
//            }
//            binding.imgAvatar.post {
//                binding.imgAvatar.setImageBitmap(bitmap)
//            }

        }
        thread.start()
    }
}