package com.example.simpleapicalldemo

import android.app.Dialog
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        CallAPILoginAsyncTask().execute()
    }

    private inner class CallAPILoginAsyncTask() : AsyncTask<Any, Void, String>() {
        private lateinit var customProgressDialog: Dialog
        override fun doInBackground(vararg params: Any?): String {
            var result: String
            var connection: HttpURLConnection? = null
            try {
                val url = URL("https://run.mocky.io/v3/bba27790-56ac-409b-b455-d464bba5ebb8")
                connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.doOutput = true
                val httpResult: Int = connection.responseCode
                if (httpResult == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val stringBuilder = StringBuilder()
                    var line:String?
                    try{
                         while(reader.readLine().also { line = it}!=null){
                             stringBuilder.append(line+"\n")
                         }
                    }catch (e:IOException){
                        e.printStackTrace()
                    }finally {
                        try{
                            inputStream.close()
                        }catch (e:IOException){
                           e.printStackTrace()
                        }
                    }
                    result = stringBuilder.toString()
                }else{
                    result = connection.responseMessage
                }
            }catch (e:SocketTimeoutException){
                result = "Connection Timeout"
            }catch (e:Exception){
                result = "Error : "+e.message
            }finally {
                connection?.disconnect()
            }
            return result
        }

        override fun onPreExecute(){
            super.onPreExecute()
            showProgressDialog()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            cancelProgressDialog()
            if (result != null) {
                Log.i("JSON RESPONSE RESULT", result)
            }
        }

        private fun showProgressDialog() {
            customProgressDialog = Dialog(this@MainActivity)
            customProgressDialog.setContentView(R.layout.dialog_custom_progress)
            customProgressDialog.show()
        }

        private fun cancelProgressDialog() {
            customProgressDialog.dismiss()
        }
    }
}