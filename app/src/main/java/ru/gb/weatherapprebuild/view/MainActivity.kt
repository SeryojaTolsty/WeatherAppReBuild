package ru.gb.weatherapprebuild.view

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import ru.gb.weatherapprebuild.databinding.MainActivityBinding
import ru.gb.weatherapprebuild.databinding.MainActivityWebviewBinding
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.net.MalformedURLException
import java.net.URL
import java.util.stream.Collectors
import java.util.stream.Collectors.joining
import javax.net.ssl.HttpsURLConnection

class MainActivity : AppCompatActivity() {
    private lateinit var binding: MainActivityBinding
//    private lateinit var binding: MainActivityWebviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MainActivityBinding.inflate(layoutInflater)
//        binding = MainActivityWebviewBinding.inflate(layoutInflater)


        val view = binding.getRoot()
        setContentView(view)
//        binding.ok.setOnClickListener(clicklistener)


//        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.container, MainFragment.newInstance())
//                .commitNow()
//        }
    }

//    var clicklistener: View.OnClickListener = object : View.OnClickListener {
//        @RequiresApi(Build.VERSION_CODES.N)
//
//        override fun onClick(p0: View?) {
//
//            try {
//                val uri = URL(binding.url.text.toString())
//                val handler = Handler()
//
//                Thread {
//                    var urlConnection: HttpsURLConnection? = null
//
//                    try {
//                        urlConnection = uri.openConnection() as HttpsURLConnection
//
//                        urlConnection.requestMethod = "GET"
//                        urlConnection.readTimeout = 10000
//
//                        val reader =
//                            BufferedReader(InputStreamReader(urlConnection.inputStream))
//
//                        val result = getLines(reader)
//
//                        handler.post {
//                            binding.webview.loadDataWithBaseURL(null, result, "text/html; charset=utf-8", "utf-8", null)
//                        }
//                    } catch(e: Exception){
//                        Log.e(" ", "Fail connection", e)
//                        e.printStackTrace()
//                    } finally {
//                        urlConnection?.disconnect()
//                    }
//                }.start()
//            } catch (e: MalformedURLException){
//                Log.e(" ", "Fail URL", e)
//                e.printStackTrace()
//            }
//        }
//
//        private fun getLines(reader: BufferedReader): String {
//            return reader.lines().collect(Collectors.joining("\n"))
//        }
//    }
}