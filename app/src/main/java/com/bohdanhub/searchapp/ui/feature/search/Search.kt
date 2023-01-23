package com.bohdanhub.searchapp.ui.feature.search

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.bohdanhub.searchapp.domain.data.root.SearchRequest
import com.bohdanhub.searchapp.domain.data.root.SearchStatus
import com.bohdanhub.searchapp.ui.component.card.SearchCard
import com.bohdanhub.searchapp.ui.component.card.SearchCardData
import com.bohdanhub.searchapp.ui.feature.details.recomposeHighlighter
import com.bohdanhub.searchapp.ui.feature.main.MainScreens
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchScreen(
    mainNavController: NavHostController,
    vm: SearchViewModel = hiltViewModel(),
) {
    //val searchCardState: State<SearchCardData?> = vm.searchCardData.collectAsState(initial = null)
    if(vm.isSearchInProgress.value){
        SearchCardInner(
            mainNavController = mainNavController,
        )
    }

    val sheetState: ModalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = {
            it != ModalBottomSheetValue.HalfExpanded
        }
    )
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    BackHandler(sheetState.isVisible) {
        coroutineScope.launch { sheetState.hide() }
    }
    SearchRequestBottomSheet(sheetState, coroutineScope, vm)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchRequestBottomSheet(
    sheetState: ModalBottomSheetState,
    coroutineScope: CoroutineScope,
    vm: SearchViewModel,
) {
    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = { BottomSheet(vm, sheetState, coroutineScope) },
        modifier = Modifier.fillMaxSize(),

        ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 72.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
        ) {
            Button(onClick = {
                coroutineScope.launch { sheetState.animateTo(ModalBottomSheetValue.Expanded) }
            }) {
                Text(text = "New Search")
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchCardInner(
    mainNavController: NavHostController? = null,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(20.dp)
        .recomposeHighlighter()
) {
    Column(
        modifier = modifier

    ) {
        Card(
            elevation = 4.dp,
            shape = RoundedCornerShape(20.dp),
            onClick = {
                mainNavController?.navigate(MainScreens.SearchDetails.route)
            }
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
            ) {
                RequestHeader()
                Text(
                    text = "Result",
                    modifier = Modifier.padding(horizontal = 8.dp),
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.W700)
                )
                StatusView()
                TextEntries()
                LatestUrl()
                ProcessFoundedUrls()
                ProgressView()
                ActionButtons()
            }
        }
    }
}

@Composable
fun ActionButtons(vm: SearchViewModel = hiltViewModel()) {
    val status = vm.statusState.value
    if (status != SearchStatus.Completed) {
        Spacer(modifier = Modifier.height(4.dp))
        Divider(color = MaterialTheme.colors.onBackground, thickness = 1.dp)
        Spacer(modifier = Modifier.height(12.dp))
        Column(
            modifier = Modifier
                .recomposeHighlighter()
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(onClick = {

            }) {
                Text(
                    text = when (status) {
                        is SearchStatus.InProgress -> "Pause"
                        SearchStatus.Paused -> "Resume"
                        else -> throw java.lang.IllegalStateException()
                    }
                )
            }
        }
    }
}

@Composable
fun ProgressView(vm: SearchViewModel = hiltViewModel()) {
    val progress = vm.progressState.value
    LinearProgressIndicator(
        progress = progress,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .recomposeHighlighter()
            .padding(horizontal = 8.dp),
    )
}

@Composable
fun ProcessFoundedUrls(vm: SearchViewModel = hiltViewModel()) {
    val processedUrlsCount = vm.processedUrlsCountState.value
    val totalUrlsCount = vm.totalUrlsCountState.value
    Text(
        text = "Processed/Founded urls: ${processedUrlsCount}/${totalUrlsCount}",
        modifier = Modifier.padding(horizontal = 8.dp).recomposeHighlighter()
    )
    Spacer(modifier = Modifier.height(4.dp))
}

@Composable
fun LatestUrl(vm: SearchViewModel = hiltViewModel()) {
    val latestUrl = vm.latestUrlState.value
    Text(
        text = "Latest url: ${latestUrl}",
        modifier = Modifier.padding(horizontal = 8.dp).recomposeHighlighter(),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
    Spacer(modifier = Modifier.height(4.dp))
}

@Composable
fun TextEntries(vm: SearchViewModel = hiltViewModel()) {
    val textEntries = vm.textEntriesState.value
    Text(
        text = "Text entries: ${textEntries}",
        modifier = Modifier.padding(horizontal = 8.dp).recomposeHighlighter(),
    )
    Spacer(modifier = Modifier.height(4.dp))
}

@Composable
fun StatusView(vm: SearchViewModel = hiltViewModel()) {
    val status = vm.statusState.value
    Text(
        text = "Status: ${status}",
        modifier = Modifier.padding(horizontal = 8.dp).recomposeHighlighter(),
    )
    Spacer(modifier = Modifier.height(4.dp))
}

@Composable
fun RequestHeader(vm: SearchViewModel = hiltViewModel()) {
    val request = vm.requestState.value
    Spacer(modifier = Modifier.height(12.dp))
    Text(
        text = "Request",
        modifier = Modifier.padding(horizontal = 8.dp),
        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.W700)
    )
    Text(
        text = "Root url: ${request?.url ?: ""}",
        modifier = Modifier.padding(horizontal = 8.dp).recomposeHighlighter(),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = "Search for: ${request?.textForSearch}",
        modifier = Modifier.padding(horizontal = 8.dp).recomposeHighlighter()
    )
    Divider(color = MaterialTheme.colors.onBackground, thickness = 1.dp)
    Spacer(modifier = Modifier.height(12.dp))
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheet(
    vm: SearchViewModel,
    sheetState: ModalBottomSheetState,
    coroutineScope: CoroutineScope,
) {
    Column(
        modifier = Modifier
            .padding(32.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        var urlText by remember { mutableStateOf(TextFieldValue("")) }
        OutlinedTextField(
            value = urlText,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            label = { Text(text = "Root url") },
            placeholder = { Text(text = "Enter your url") },
            onValueChange = {
                urlText = it
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        var searchText by remember { mutableStateOf(TextFieldValue("")) }
        OutlinedTextField(
            value = searchText,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            label = { Text(text = "Search") },
            placeholder = { Text(text = "Enter your request") },
            onValueChange = {
                searchText = it
            }
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = {
            vm.search(
                SearchRequest(
                    textForSearch = searchText.text,
                    url = urlText.text
                )
            )
            coroutineScope.launch {
                sheetState.hide()
            }
        }) {
            Text(text = "Start Search")
        }
    }
}