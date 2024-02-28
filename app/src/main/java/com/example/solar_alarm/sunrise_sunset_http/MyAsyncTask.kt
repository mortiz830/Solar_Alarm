package com.example.solar_alarm.sunrise_sunset_http

import android.os.AsyncTask
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MyAsyncTask(private val listener: (String) -> Unit) : AsyncTask<String, Void, String>() {
    override fun doInBackground(vararg params: String): String {
        val url = URL(params[0])
        val connection = url.openConnection() as HttpURLConnection

        return try {
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val result = StringBuilder()
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                result.append(line).append("\n")
            }

            result.toString()
        } finally {
            connection.disconnect()
        }
    }

    override fun onPostExecute(result: String) {
        // Handle the result on the main thread
        listener(result)
    }
}
