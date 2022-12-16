package com.bohdanhub.searchapp.data

import android.util.Log
import com.bohdanhub.searchapp.domain.data.*
import com.bohdanhub.searchapp.util.countEntries
import com.bohdanhub.searchapp.util.extractUrls
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
class SearchRepository @Inject constructor() {

    private val _rootSearchResult: MutableStateFlow<RootSearchResult?> = MutableStateFlow(null)
    val rootSearchResult: StateFlow<RootSearchResult?> = _rootSearchResult

    suspend fun startSearch(request: RootSearchRequest) {
        val parseResult = parseText(request.textForSearch, fetchUrl(request.url))
        _rootSearchResult.value = RootSearchResult(
            request = request,
            totalTextEntries = parseResult.foundedTextEntries,
            foundedUrls = parseResult.foundedUrls,
            processedUrls = listOf(request.url),
            status = SearchStatus.InProgress,
        )
    }

    private suspend fun parseText(toSearch: String, originText: String): ParseResult =
        withContext(Dispatchers.Default) {
            ParseResult(
                foundedTextEntries = originText.countEntries(toSearch),
                foundedUrls = originText.extractUrls()
            )
        }

    private suspend fun fetchUrl(url: String): String = withContext(Dispatchers.IO) {
        var urlConnection: HttpURLConnection? = null
        var result = ""
        try {
            val urlObj = URL(url)
            urlConnection = urlObj.openConnection() as HttpURLConnection
            val code = urlConnection.responseCode
            Log.d("SearchRepository", "Code = $code")
            if (code == 200) {
                val stream = BufferedInputStream(urlConnection.inputStream)
                val bufferedReader = BufferedReader(InputStreamReader(stream))
                var line: String?
                while (run {
                        line = bufferedReader.readLine()
                        line
                    } != null) {
                    Log.d("SearchRepository", "line = $line")
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