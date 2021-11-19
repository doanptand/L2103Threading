package com.ddona.threading

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.ddona.threading.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import java.net.URL
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {


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

    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var job1: Job
    private lateinit var job2: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        job = Job()
        job1 = Job()
        job2 = Job()
        val scope1 = CoroutineScope(Dispatchers.IO + job1)
        val scope2 = CoroutineScope(Dispatchers.Main + job2)
//        runBlocking {
//            //block current thread until this coroutine scope finished job
//        }
//        scope1.launch {
//            //tao coroutine scope va run task ma khong block main thread
//        }
//        scope2.launch {
//
//        }
        val friends = async(Dispatchers.IO) {
            getAllFriends()
        }
        val messages = async(Dispatchers.IO) {
            getAllMessage()
        }
        launch {
            val user = friends.await() + messages.await()
            Log.d("doanpt", "user:$user")
        }


        binding.imgDownload.setOnClickListener {
            launch(Dispatchers.IO) {
                val bitmap = downloadFileWithCoroutine()
                withContext(Dispatchers.Main) {
                    binding.imgAvatar.setImageBitmap(bitmap)
                }
            }
//            downloadImage()
//            GlobalScope.launch {
//                val bitmap = downloadFileWithCoroutine()
//                withContext(Dispatchers.Main) {
//                    binding.imgAvatar.setImageBitmap(bitmap)
//                }
//            }
//            DownloadFile().execute("https://photo-cms-baonghean.zadn.vn/w607/Uploaded/2021/ftgbtgazsnzm/2020_07_14/ngoctrinhmuonsinhcon1_swej7996614_1472020.jpg")
        }


    }

    suspend fun getAllFriends(): String {
        return "This is list friends"
    }

    suspend fun getAllMessage(): String {
        return "This is message list for user"
    }

    override fun onDestroy() {
        job.cancel()
        job2.cancel()
        lifecycleScope.cancel()
        super.onDestroy()
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