package com.example.myinventory.ui.settings.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myinventory.R
import com.example.myinventory.data.models.FieldType
import com.example.myinventory.ui.settings.ConfirmDeleteDialog
import com.example.myinventory.ui.settings.DropdownSelector
import com.example.myinventory.ui.settings.EntityListSectionWithFilter
import com.example.myinventory.ui.settings.ItemAddField
import com.example.myinventory.ui.settings.ClearFiltersButton
import com.example.myinventory.ui.settings.SettingsViewModel

@Composable
fun FieldTypesTab(viewModel: SettingsViewModel) {
    val fieldTypes by viewModel.fieldTypes.collectAsState()
    var selectedValueType by remember { mutableStateOf<String?>(null) }

    val filtered = fieldTypes.filter {
        selectedValueType == "" || selectedValueType == null || it.valueType == selectedValueType!!
    }

    var editing by remember { mutableStateOf<FieldType?>(null) }
    var deleting by remember { mutableStateOf<FieldType?>(null) }

    val valueTypes = listOf(
        stringResource(R.string.field_type_text),
        stringResource(R.string.field_type_multiline_text),
        stringResource(R.string.field_type_coordinates),
        stringResource(R.string.field_type_url),
        stringResource(R.string.field_type_phone),
        stringResource(R.string.field_type_integer),
        stringResource(R.string.field_type_float),
    )


    Column(Modifier.padding(16.dp)) {
        DropdownSelector(
            label = stringResource(R.string.field_type),
            items = valueTypes,
            selectedItem = selectedValueType,
            onItemSelected = { selectedValueType = it },
            itemToString = { it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (selectedValueType != null) {
            ClearFiltersButton(
                onReset = { selectedValueType = null }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedValueType != null) {
            ItemAddField(label = stringResource(R.string.new_field_type),) {
                viewModel.addFieldType(it, selectedValueType!!)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        EntityListSectionWithFilter(
            items = filtered.sortedBy { it.name },
            getTitle = { it.name },
            getSubtitle = { it.valueType },
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
                        items = valueTypes.filter { it.isNotEmpty() },
                        selectedItem = editValueType,
                        onItemSelected = { editValueType = it },
                        itemToString = { it }
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