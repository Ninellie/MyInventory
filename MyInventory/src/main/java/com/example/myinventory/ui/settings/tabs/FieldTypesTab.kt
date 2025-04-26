package com.example.myinventory.ui.settings.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myinventory.R
import com.example.myinventory.data.models.FieldType
import com.example.myinventory.ui.components.AddItemField
import com.example.myinventory.ui.components.ClearFiltersButton
import com.example.myinventory.ui.components.DropdownSelector
import com.example.myinventory.ui.components.ItemList
import com.example.myinventory.ui.settings.ConfirmDeleteDialog
import com.example.myinventory.ui.settings.SettingsViewModel

@Composable
fun FieldTypesTab(viewModel: SettingsViewModel) {
    val fieldTypes by viewModel.fieldTypes.collectAsState()

    var selectedValueTypeKey by remember { mutableStateOf<String?>(null) }
    val fieldName = remember { mutableStateOf("") }

    val filtered = fieldTypes.filter { fieldType ->
        val matchName = fieldType.name.contains(fieldName.value, ignoreCase = true)
        val matchType = selectedValueTypeKey?.let { it == fieldType.valueType } ?: true

        matchName && matchType
    }.sortedBy { it.name }

    var editing by remember { mutableStateOf<FieldType?>(null) }
    var deleting by remember { mutableStateOf<FieldType?>(null) }

    val valueTypeKeys: Map<String, String> = mapOf(
        stringResource(R.string.field_type_text_key) to stringResource(R.string.field_type_text),
        stringResource(R.string.field_type_phone_key) to stringResource(R.string.field_type_phone),
        stringResource(R.string.field_type_url_key) to stringResource(R.string.field_type_url),
        stringResource(R.string.field_type_number_key) to stringResource(R.string.field_type_number),
    )

    Column(Modifier.padding(16.dp)) {
        DropdownSelector(
            label = stringResource(R.string.field_type),
            items = valueTypeKeys.keys.toList(),
            selectedItem = selectedValueTypeKey,
            onItemSelected = { selectedValueTypeKey = it },
            itemToString = { valueTypeKeys[it]!! }
        )

        Spacer(modifier = Modifier.height(8.dp))

        AddItemField(
            label = stringResource(R.string.new_field_type),
            text = fieldName,
            onAdd = { viewModel.addFieldType(it, selectedValueTypeKey!!) },
            onValueChange = { },
            isAddEnabled = fieldName.value != "" && selectedValueTypeKey != null
        )

        if (selectedValueTypeKey != null || fieldName.value != "") {
            Spacer(modifier = Modifier.height(8.dp))
            ClearFiltersButton(
                onReset = {
                    selectedValueTypeKey = null
                    fieldName.value = ""
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        ItemList(
            items = filtered,
            getTitle = { it.name },
            getSubtitle = { valueTypeKeys.getOrElse(it.valueType) { it.valueType } },
            onEdit = { editing = it },
            onDelete = { deleting = it }
        )
    }

    // Диалог редактирования
    editing?.let { fieldType ->
        var editName by remember { mutableStateOf(fieldType.name) }
        var editValueType by remember { mutableStateOf(fieldType.valueType) }

        AlertDialog(
            onDismissRequest = { editing = null },
            title = { Text(stringResource(R.string.edit)) },
            text = {
                Column {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text(stringResource(R.string.field_type)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    DropdownSelector(
                        label = stringResource(R.string.field_type_select),
                        items = valueTypeKeys.keys.toList(), // keys
                        selectedItem = editValueType, //key
                        onItemSelected = { editValueType = it }, // key
                        itemToString = { valueTypeKeys.getOrElse(it) { it } } // value (showing)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateFieldType(fieldType.copy(
                            name = editName,
                            valueType = editValueType
                        ))
                        editing = null
                    }
                ) {
                    Text(stringResource(R.string.save))
                }
            },
            dismissButton = {
                TextButton(onClick = { editing = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    // Диалог удаления
    deleting?.let {
        ConfirmDeleteDialog(
            itemName = it.name,
            onDismiss = { deleting = null },
            onConfirm = {
                viewModel.deleteFieldType(it)
                deleting = null
            }
        )
    }
} 