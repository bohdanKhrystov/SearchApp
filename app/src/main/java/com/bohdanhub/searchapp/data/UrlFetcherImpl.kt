package com.bohdanhub.searchapp.data

import com.bohdanhub.searchapp.domain.UrlFetcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UrlFetcherImpl @Inject constructor() : UrlFetcher {

    override suspend fun fetch(url: String): String = withContext(Dispatchers.IO) {
        var urlConnection: HttpURLConnection? = null
        var result = ""
        try {
            val urlObj = URL(url)
            urlConnection = urlObj.openConnection() as HttpURLConnection
            val code = urlConnection.responseCode
            //Log.d("SearchRepository", "Code = $code")
            if (code == 200) {
                val stream = BufferedInputStream(urlConnection.inputStream)
                val bufferedReader = BufferedReader(InputStreamReader(stream))
                var line: String?
                while (run {
                        line = bufferedReader.readLine()
                        line
                    } != null) {
                    //Log.d("SearchRepository", "line = $line")
                    result += line
                }
                stream.close()
            }
            return@withContext result
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            urlConnection?.disconnect()
        }
        return@withContext result
    }
}