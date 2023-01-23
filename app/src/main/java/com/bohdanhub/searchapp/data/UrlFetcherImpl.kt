package com.bohdanhub.searchapp.data

import android.util.Log
import com.bohdanhub.searchapp.domain.data.fetch.UrlFetcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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

    private val mResult = mutableListOf<String>()
    private val m = Mutex()

    override suspend fun fetch(url: String): String = withContext(Dispatchers.IO) {
        var urlConnection: HttpURLConnection? = null
        //var result = ""
        m.withLock { if (mResult.size > 10) mResult.clear() }
        m.withLock { mResult.add("") }
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
                    m.withLock { mResult[mResult.size - 1] = mResult[mResult.size - 1] + line }
                }
                //Log.d("PerformanceDebug","url content = ${result.length}")
                stream.close()
            }
            return@withContext m.withLock { mResult[mResult.size - 1] }
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            urlConnection?.disconnect()
        }
        return@withContext m.withLock { mResult[mResult.size - 1] }
    }

//    override suspend fun fetch(url: String): String = withContext(Dispatchers.IO) {
//        kotlinx.coroutines.delay(10)
//        val index = java.net.URL(url).host.split(".").first().toInt()
//        return@withContext when (index) {
//            0 -> "{ https://1.com , https://2.com , https://3.com , https://4.com , https://5.com , https://6.com , }"
//            1 -> {
//                kotlinx.coroutines.delay(10000); "{ https://7.com , https://8.com , https://9.com , }"
//            }
//            2 -> "{ https://10.com , https://11.com , https://12.com , https://13.com , }"
//            3 -> "{ https://14.com , https://15.com  }"
//            4 -> "{ https://16.com , https://17.com , https://18.com , https://19.com , }"
//            5 -> {
//                "{ https://20.com , https://21.com , https://22.com , https://23.com , }"
//            }
//            6 -> "{ https://24.com , https://25.com , https://26.com , https://27.com , https://28.com , https://29.com , https://30.com , https://31.com , https://32.com , https://33.com , https://34.com , https://35.com , }"
//            7 -> {
//                //low
//                "{ https://36.com , }"
//            }
//            8 -> {
//                //low
//                "{ https://37.com , }"
//            }
//            9 -> {
//                //low
//                "{ https://38.com , }"
//            }
//            10 -> {
//                //high
//                kotlinx.coroutines.delay(3000)
//                "{ https://39.com , https://40.com , https://41.com , https://42.com , }"
//            }
//            11 -> {
//                kotlinx.coroutines.delay(10000)
//                "{ https://43.com , https://44.com , }"
//            }
//            12 -> {
//                kotlinx.coroutines.delay(10000)
//                "{ https://45.com , }"
//            }
//            13 -> {
//                kotlinx.coroutines.delay(1000)
//                "{ https://46.com , }"
//            }
//            14 -> {
//                kotlinx.coroutines.delay(1000)
//                //high
//                "{ https://47.com , https://48.com , https://49.com , https://50.com , }"
//            }
//            21 -> {
//                kotlinx.coroutines.delay(2000)
//                "{ https://51.com , https://52.com , https://53.com , https://54.com , https://55.com , https://56.com , https://57.com , https://58.com , }"
//            }
//            23 -> {
//                kotlinx.coroutines.delay(1000)
//                "{ https://59.com , https://60.com , https://61.com , https://62.com , }"
//            }
//            35 -> {
//                kotlinx.coroutines.delay(4000)
//                //high
//                "{ https://63.com , https://64.com , https://65.com , https://66.com , }"
//                //expect 67 after 68-73
//            }
//            38 -> "{ https://67.com , }"
//            39 -> "{ https://68.com , }"
//            45 -> {
//                "{ https://69.com , https://70.com , https://71.com , }"
//            }
//            58 -> "{ https://72.com , }"
//            63 -> {
//                "{ https://73.com , }"
//            }
//            71 -> "{ https://74.com , https://75.com , }"
//            75 -> {
//                val s = StringBuilder()
//                s.append("{ ")
//                for(i in 100..10_000){
//                    s.append("https://$i.com , ")
//                }
//                s.append(" }")
//                s.toString()
//            }
//            else -> "{}"
//        }
//    }
}