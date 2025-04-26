package com.example.myinventory.ui.settings.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myinventory.R
import com.example.myinventory.data.models.DeviceType
import com.example.myinventory.data.models.FieldType
import com.example.myinventory.ui.components.AddItemField
import com.example.myinventory.ui.components.ClearFiltersButton
import com.example.myinventory.ui.components.ItemList
import com.example.myinventory.ui.settings.ConfirmDeleteDialog
import com.example.myinventory.ui.components.MultiSelectDropdown
import com.example.myinventory.ui.settings.SettingsViewModel

@Composable
fun DeviceTypeTab(viewModel: SettingsViewModel) {
    val deviceTypes by viewModel.deviceTypes.collectAsState()
    val fieldTypes by viewModel.fieldTypes.collectAsState()

    var selectedFieldTypes by remember { mutableStateOf<List<FieldType>>(emptyList()) }
    
    var editing by remember { mutableStateOf<DeviceType?>(null) }
    var deleting by remember { mutableStateOf<DeviceType?>(null) }

    val deviceTypeName = remember { mutableStateOf("") }

    val selectedFieldTypeIdList = selectedFieldTypes.map { it.id }

    val filteredDeviceTypeList = deviceTypes.filter { deviceType ->
        val matchName = deviceType.name.contains(deviceTypeName.value, ignoreCase = true)
        val matchType = deviceType.fieldTypeIdList.any { it in selectedFieldTypeIdList }
        matchName && (matchType || selectedFieldTypes.isEmpty())
    }.sortedBy { it.name }

    Column(Modifier.padding(16.dp)) {
        MultiSelectDropdown(
            label = stringResource(R.string.field_types),
            items = fieldTypes.sortedBy { it.name },
            selectedItems = selectedFieldTypes,
            onItemsSelected = { selectedFieldTypes = it },
            itemToString = { it.name }
        )

        Spacer(modifier = Modifier.height(8.dp))

        AddItemField(
            label =  stringResource(R.string.new_device_type),
            text = deviceTypeName,
            onAdd = { name ->
                if (name.isNotBlank()) {
                    viewModel.addDeviceType(name, selectedFieldTypes.map { it.id })
                    selectedFieldTypes = emptyList()
                }
            },
            onValueChange = { },
        )

        if (selectedFieldTypes.isNotEmpty() || deviceTypeName.value != "") {
            Spacer(modifier = Modifier.height(8.dp))
            ClearFiltersButton(
                onReset = {
                    selectedFieldTypes = emptyList()
                    deviceTypeName.value = ""
                }
            )

        }

        Spacer(modifier = Modifier.height(16.dp))

        ItemList(
            items = filteredDeviceTypeList.sortedBy { it.name },
            getTitle = { it.name },
            getSubtitle = { 
                val fieldTypeNames = it.fieldTypeIdList
                    .mapNotNull { id -> fieldTypes.find { ft -> ft.id == id }?.name }
                    .sortedBy { it }
                    .joinToString(", ")
                fieldTypeNames.ifBlank { "" }
            },
            onEdit = { editing = it },
            onDelete = { deleting = it },
            emptyMessage = "Create first device type"
        )
    }
    
    // Диалог редактирования
    editing?.let { deviceType ->
        var editName by remember { mutableStateOf(deviceType.name) }
        var editFieldTypes by remember { 
            mutableStateOf(
                deviceType.fieldTypeIdList
                    .mapNotNull { id -> fieldTypes.find { it.id == id } }
                    .sortedBy { it.name }
            ) 
        }
        
        AlertDialog(
            onDismissRequest = { editing = null },
            title = { Text(stringResource(R.string.edit)) },
            text = {
                Column {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text(stringResource(R.string.device_type)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    MultiSelectDropdown(
                        label = stringResource(R.string.field_types),
                        items = fieldTypes.sortedBy { it.name },
                        selectedItems = editFieldTypes,
                        onItemsSelected = { editFieldTypes = it },
                        itemToString = { it.name }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateDeviceType(deviceType.copy(
                            name = editName,
                            fieldTypeIdList = editFieldTypes.map { it.id }
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
                viewModel.deleteDeviceType(it)
                deleting = null
            }
        )
    }
}
