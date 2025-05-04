package com.example.myinventory.ui.components

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myinventory.R

// todo add color tag to items
@Composable
fun <T> ItemList(
    modifier: Modifier = Modifier,
    items: List<T>,
    getTitle: (T) -> String,
    getSubtitle: (T) -> String = { "" },
    onEdit: (T) -> Unit = {},
    onDelete: (List<T>) -> Unit = {},
    onCopy: ((List<T>) -> Unit)? = null,
    emptyMessage: String = ""
) {
    var selectedItems by remember { mutableStateOf(setOf<T>()) }
    val selectionMode = selectedItems.isNotEmpty()

    fun toggleSelection(item: T) {
        selectedItems = if (item in selectedItems) {
            selectedItems - item
        } else {
            selectedItems + item
        }
    }

    fun clearSelection() {
        selectedItems = emptySet()
    }

    Column(modifier = modifier) {
        if (items.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text(
                    text = emptyMessage,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                items(items) { item ->
                    val isSelected = item in selectedItems

                    val backgroundColor = if (isSelected) {
                        MaterialTheme.colorScheme.surfaceTint
                    } else {
                        Color.Unspecified
                    }

                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .combinedClickable(
                                onClick = {
                                    if (selectionMode) toggleSelection(item)
                                    else onEdit(item)
                                },
                                onLongClick = {
                                    toggleSelection(item)
                                }
                            ),
                        headlineContent = { Text(
                            text = getTitle(item),
                            color = backgroundColor,
                            style = MaterialTheme.typography.titleSmall
                        ) },
                        supportingContent = {
                            val subtitle = getSubtitle(item)
                            if (subtitle.isNotEmpty()) {
                                Text(subtitle, style = MaterialTheme.typography.bodySmall)
                            }
                        },
                    )
                    HorizontalDivider()
                }
            }

            if (selectionMode) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (onCopy != null) {
                        Button(
                            onClick = {
                                onCopy.invoke(selectedItems.toList())
                                clearSelection()
                            }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(Modifier.width(4.dp))
                            Text(stringResource(R.string.copy))
                        }
                    }
                    Button(
                        onClick = {
                            onDelete.invoke(selectedItems.toList())
                            clearSelection()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(R.string.delete))
                    }
                }
            }
        }
    }
}