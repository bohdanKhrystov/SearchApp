package com.bohdanhub.searchapp.domain.data.root

import com.bohdanhub.searchapp.domain.data.child.ChildSearchRequest

data class Node(
    val request: ChildSearchRequest,
    val children: List<Node>
)