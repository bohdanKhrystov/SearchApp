package com.bohdanhub.searchapp.ui.feature.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bohdanhub.searchapp.domain.data.child.ChildSearchRequest
import com.bohdanhub.searchapp.domain.data.root.SearchResult

@Composable
fun DetailsScreen(vm: DetailsViewModel = hiltViewModel()) {
    val searchState = vm.searchResult.collectAsState(initial = null)
    val expandedItems = remember { mutableStateListOf<ChildSearchRequest>() }
    searchState.value?.let { searchResult ->
        searchResult.requestsByParentId?.get(-1L)?.first()?.let { request ->
            LazyColumn {
                nodes(
                    listOf(request),
                    isExpanded = {
                        expandedItems.contains(it)
                    },
                    toggleExpanded = {
                        if (expandedItems.contains(it)) {
                            expandedItems.remove(it)
                        } else {
                            expandedItems.add(it)
                        }
                    },
                    result = searchResult
                )
            }
        }
    }
}

fun LazyListScope.nodes(
    nodes: List<ChildSearchRequest>,
    isExpanded: (ChildSearchRequest) -> Boolean,
    toggleExpanded: (ChildSearchRequest) -> Unit,
    result: SearchResult,
) {
    nodes.forEach { node ->
        node(
            node,
            isExpanded = isExpanded,
            toggleExpanded = toggleExpanded,
            result = result
        )
    }
}

fun LazyListScope.node(
    node: ChildSearchRequest,
    isExpanded: (ChildSearchRequest) -> Boolean,
    toggleExpanded: (ChildSearchRequest) -> Unit,
    result: SearchResult
) {
    item(key = node.id) {
        Column {
            Text(
                text = "Url: ${node.url}",
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clickable {
                        toggleExpanded(node)
                    },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Status: ${node.status}",
                modifier = Modifier.padding(horizontal = 8.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row {
                for (i in 0..node.deep) {
                    Text("|")
                }
            }
        }
    }
    if (isExpanded(node)) {
        nodes(
            nodes = result.requestsByParentId?.get(node.id) ?: listOf(),
            isExpanded = isExpanded,
            toggleExpanded = toggleExpanded,
            result = result
        )
    }
}