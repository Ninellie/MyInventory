package com.example.myinventory.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.myinventory.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> MultiSelectDropdown(
    label: String,
    items: List<T>,
    selectedItems: List<T>,
    onItemsSelected: (List<T>) -> Unit,
    itemToString: (T) -> String
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = if (selectedItems.isEmpty()) "" else "${selectedItems.size} ".plus(
                stringResource(R.string.selected)
            ),
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                val isSelected = selectedItems.contains(item)
                
                DropdownMenuItem(
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(itemToString(item))
                            if (isSelected) {
                                Text("✓")
                            }
                        }
                    },
                    onClick = {
                        val newSelection = if (isSelected) {
                            selectedItems - item
                        } else {
                            selectedItems + item
                        }
                        onItemsSelected(newSelection)
                    }
                )
            }
        }
    }
} 