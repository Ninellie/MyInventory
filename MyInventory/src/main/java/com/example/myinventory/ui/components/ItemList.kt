package com.example.myinventory.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myinventory.R

@Composable
fun <T> ItemList(
    modifier: Modifier = Modifier,
    items: List<T>,
    getTitle: (T) -> String,
    getSubtitle: (T) -> String = { "" },
    onEdit: (T) -> Unit = {},
    onDelete: (T) -> Unit = {},
    emptyMessage: String = ""
) {
    if (items.isEmpty()) {
        Box(modifier = modifier.fillMaxWidth().padding(16.dp)) {
            Text(
                text = emptyMessage,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(items) { item ->
                ListItem(
                    headlineContent = { Text(getTitle(item)) },
                    supportingContent = { 
                        if (getSubtitle(item).isNotEmpty()) {
                            Text(getSubtitle(item))
                        }
                    },
                    trailingContent = {
                        Row {
                            IconButton(onClick = { onEdit(item) }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = stringResource(R.string.edit)
                                )
                            }
                            IconButton(onClick = { onDelete(item) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = stringResource(R.string.delete)
                                )
                            }
                        }
                    }
                )
                HorizontalDivider()
            }
        }
    }
} 