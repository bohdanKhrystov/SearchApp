package com.bohdanhub.searchapp.ui.feature.search

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bohdanhub.searchapp.domain.data.RootSearchRequest
import com.bohdanhub.searchapp.ui.component.card.SearchCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchScreen(vm: SearchViewModel = hiltViewModel()) {
    val searchCardState = vm.searchCardData.collectAsState(initial = null)
    searchCardState.value?.let { searchCardData ->
        SearchCard(searchCardData)
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
                RootSearchRequest(
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