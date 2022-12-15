package com.bohdanhub.searchapp.ui.component.card

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bohdanhub.searchapp.domain.data.SearchRequest
import com.bohdanhub.searchapp.domain.data.SearchStatus

@Composable
fun SearchCard(data: SearchCardData) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Card(
            elevation = 4.dp,
            shape = RoundedCornerShape(20.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = "Request",
                    modifier = Modifier.padding(horizontal = 8.dp),
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.W700)
                )
                Text(
                    text = "Root Url: ${data.request.url}",
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Search for: ${data.request.textForSearch}",
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Divider(color = MaterialTheme.colors.onBackground, thickness = 1.dp)
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Result",
                    modifier = Modifier.padding(horizontal = 8.dp),
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.W700)
                )
                Text(
                    text = "Status: ${data.status}",
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Founded/Processed urls: ${data.totalUrlsCount}/${data.processedUrlsCount}",
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                if (data.status != SearchStatus.Completed) {
                    Divider(color = MaterialTheme.colors.onBackground, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Button(onClick = {

                    }) {
                        Text(
                            text = when (data.status) {
                                is SearchStatus.InProgress -> "Pause"
                                SearchStatus.Paused -> "Resume"
                                else -> throw java.lang.IllegalStateException()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchCardPreview() {
    Column(modifier = Modifier.padding(20.dp)) {
        SearchCard(
            data = SearchCardData(
                request = SearchRequest(
                    textForSearch = "brother",
                    url = "https://google.com"
                ),
                status = SearchStatus.InProgress(0.32f),
                latestUrl = "https://latestprocessed.com",
                processedUrlsCount = 1024,
                totalUrlsCount = 2048,
            )
        )
    }
}