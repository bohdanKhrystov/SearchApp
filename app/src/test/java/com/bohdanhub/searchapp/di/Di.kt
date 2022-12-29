package com.bohdanhub.searchapp.di

import com.bohdanhub.searchapp.domain.UrlFetcher
import kotlinx.coroutines.delay
import java.net.URL

val rootTestUrl = "https://0.com "

fun provideMockFetcher(): UrlFetcher {
    return object : UrlFetcher {
        override suspend fun fetch(url: String): String {
            delay(10)
            val index = URL(url).host.split(".").first().toInt()
            return when (index) {
                0 -> "{ https://1.com , https://2.com , https://3.com , https://4.com , https://5.com , https://6.com , }"
                1 -> "{ https://7.com , https://8.com , https://9.com , }"
                2 -> "{ https://10.com , https://11.com , https://12.com , https://13.com , }"
                3 -> "{ https://14.com , https://15.com  }"
                4 -> "{ https://16.com , https://17.com , https://18.com , https://19.com , }"
                5 -> "{ https://20.com , https://21.com , https://22.com , https://23.com , }"
                6 -> "{ https://24.com , https://25.com , https://26.com , https://27.com , https://28.com , https://29.com , https://30.com , https://31.com , https://32.com , https://33.com , https://34.com , https://35.com , }"
                7 -> "{ https://36.com , }"
                8 -> "{ https://37.com , }"
                9 -> "{ https://38.com , }"
                10 -> "{ https://39.com , https://40.com , https://41.com , https://42.com , }"
                11 -> "{ https://43.com , https://44.com , }"
                12 -> "{ https://45.com , }"
                13 -> "{ https://46.com , }"
                14 -> "{ https://47.com , https://48.com , https://49.com , https://50.com , }"
                21 -> "{ https://51.com , https://52.com , https://53.com , https://54.com , https://55.com , https://56.com , https://57.com , https://58.com , }"
                23 -> "{ https://59.com , https://60.com , https://61.com , https://62.com , }"
                35 -> "{ https://63.com , https://64.com , https://65.com , https://66.com , }"
                38 -> "{ https://67.com , }"
                39 -> "{ https://68.com , }"
                45 -> "{ https://69.com , https://70.com , https://71.com , }"
                58 -> "{ https://72.com , }"
                63 -> "{ https://73.com , }"
                71 -> "{ https://74.com , https://75.com , }"
                else -> "{}"
            }
        }
    }
}

fun provideMockFetcherThatThrowErrorSomeTimes(): UrlFetcher {
    return object : UrlFetcher {
        override suspend fun fetch(url: String): String {
            delay(10)
            val index = URL(url).host.split(".").first().toInt()
            return when (index) {
                0 -> "{ https://1.com , https://2.com , https://3.com , https://4.com , https://5.com , https://6.com , }"
                1 -> "{ https://7.com , https://8.com , https://9.com , }"
                2 -> "{ https://10.com , https://11.com , https://12.com , https://13, }"
                3 -> "{ https://14.com , https://15.com  }"
                4 -> "{ https://16.com , https://17.com , https://18.com , https://19, }"
                5 -> "{ https://20.com , https://21.com , https://22.com , https://23, }"
                6 -> "{ https://24.com , https://25.com , https://26.com , https://27, https://28.com , https://29.com , https://30.com , https://31, https://32.com , https://33.com , https://34.com , https://35, }"
                7 -> "{ https://36.com , }"
                8 -> "{ https://37.com , }"
                9 -> "{ https://38.com , }"
                10 -> "{ https://39.com , https://40.com , https://41.com , https://42, }"
                11 -> "{ https://43.com , https://44.com , }"
                12 -> "{ https://45.com , }"
                13 -> "{ https://46.com , }"
                14 -> "{ https://47.com , https://48.com , https://49.com , https://50, }"
                21 -> "{ https://51.com , https://52.com , https://53.com , https://54, https://55.com , https://56.com , https://57.com , https://58, }"
                23 -> "{ https://59.com , https://60.com , https://61.com , https://62, }"
                35 -> "{ https://63.com , https://64.com , https://65.com , https://66, }"
                38 -> "{ https://67.com , }"
                39 -> "{ https://68.com , }"
                45 -> "{ https://69.com , https://70.com , https://71.com , }"
                58 -> "{ https://72.com , }"
                63 -> "{ https://73.com , }"
                71 -> "{ https://74.com , https://75.com , }"
                else -> throw RuntimeException("Mock network error")
            }
        }
    }
}