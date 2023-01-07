package com.bohdanhub.searchapp.ui.component.card

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bohdanhub.searchapp.domain.data.root.SearchRequest
import com.bohdanhub.searchapp.domain.data.root.SearchStatus

@Composable
fun SearchCard(
    data: SearchCardData,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(20.dp)
) {
    Column(
        modifier = modifier

    ) {
        Card(
            elevation = 4.dp,
            shape = RoundedCornerShape(20.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Request",
                    modifier = Modifier.padding(horizontal = 8.dp),
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.W700)
                )
                Text(
                    text = "Root url: ${data.request.url}",
                    modifier = Modifier.padding(horizontal = 8.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
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
                    modifier = Modifier.padding(horizontal = 8.dp),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Text entries: ${data.textEntriesCount}",
                    modifier = Modifier.padding(horizontal = 8.dp),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Latest url: ${data.latestUrl}",
                    modifier = Modifier.padding(horizontal = 8.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Processed/Founded urls: ${data.processedUrlsCount}/${data.totalUrlsCount}",
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = data.progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 8.dp),
                )
                if (data.status != SearchStatus.Completed) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Divider(color = MaterialTheme.colors.onBackground, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))
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
}

@Preview(showBackground = true)
@Composable
fun SearchCardPreview() {
    SearchCard(
        data = SearchCardData(
            request = SearchRequest(
                textForSearch = "brother",
                url = "https://google.com"
            ),
            status = SearchStatus.InProgress,
            latestUrl = "https://latestprocessed.com",
            processedUrlsCount = 978,
            totalUrlsCount = 2196,
            progress = 0.64f,
            textEntriesCount = 186,
        )
    )
}