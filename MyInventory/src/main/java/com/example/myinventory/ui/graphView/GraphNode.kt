package com.example.myinventory.ui.graphView

data class GraphNode(
    val id: String,
    val name: String,
    val type: NodeType,
    val children: List<GraphNode> = emptyList()
)