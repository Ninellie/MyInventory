package com.example.myinventory.ui.settings.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myinventory.R
import com.example.myinventory.data.models.DeviceModel
import com.example.myinventory.data.models.DeviceType
import com.example.myinventory.data.models.Vendor
import com.example.myinventory.ui.components.AddItemField
import com.example.myinventory.ui.components.ClearFiltersButton
import com.example.myinventory.ui.components.DropdownSelector
import com.example.myinventory.ui.components.ItemList
import com.example.myinventory.ui.settings.ConfirmDeleteDialog
import com.example.myinventory.ui.settings.EditItemDialog
import com.example.myinventory.ui.settings.FilterRow
import com.example.myinventory.ui.settings.SettingsViewModel

@Composable
fun DeviceModelsTab(viewModel: SettingsViewModel) {
    val allModels by viewModel.deviceModels.collectAsState()
    val allVendors by viewModel.vendors.collectAsState()
    val allTypes by viewModel.deviceTypes.collectAsState()

    var selectedVendor by remember { mutableStateOf<Vendor?>(null) }
    var selectedType by remember { mutableStateOf<DeviceType?>(null) }
    val modelName = remember { mutableStateOf("") }

    val filtered = allModels.filter { model ->
        val matchVendor = selectedVendor?.id?.let { it == model.vendorId } ?: true
        val matchType = selectedType?.id?.let { it == model.deviceTypeId } ?: true
        val matchName = model.name.contains(modelName.value, ignoreCase = true)
        matchVendor && matchType && matchName
    }.sortedBy { it.name }

    var editing by remember { mutableStateOf<DeviceModel?>(null) }
    var deleting by remember { mutableStateOf<DeviceModel?>(null) }

    val isAddEnable = selectedVendor != null && selectedType != null && modelName.value != ""


    Column(Modifier.padding(16.dp)) {
        FilterRow {
            DropdownSelector(
                label = stringResource(R.string.vendor),
                items = allVendors.sortedBy { it.name },
                selectedItem = selectedVendor,
                onItemSelected = { selectedVendor = it },
                itemToString = { it.name },
                modifier = Modifier.weight(1f)
            )
            DropdownSelector(
                label = stringResource(R.string.device_type),
                items = allTypes.sortedBy { it.name },
                selectedItem = selectedType,
                onItemSelected = { selectedType = it },
                itemToString = { it.name },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        AddItemField(
            label = stringResource(R.string.new_model),
            text = modelName,
            onAdd = { name ->
                viewModel.addDeviceModel(name, selectedVendor!!.id, selectedType!!.id) },
            onValueChange = { },
            isAddEnabled = isAddEnable
        )

        if (selectedVendor != null || selectedType != null || modelName.value != "") {
            Spacer(modifier = Modifier.height(8.dp))
            ClearFiltersButton(
                onReset = {
                    selectedVendor = null
                    selectedType = null
                    modelName.value = ""
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        ItemList(
            items = filtered,
            getTitle = { it.name },
            getSubtitle = {model ->
                val vendor = allVendors.find { it.id == model.vendorId }
                val type = allTypes.find { it.id == model.deviceTypeId }
                listOfNotNull(vendor?.name, type?.name).joinToString(", ")
            },
            onEdit = { editing = it },
            onDelete = { deleting = it },
            emptyMessage = "Empty"
        )
    }

    editing?.let {
        EditItemDialog(
            currentName = it.name,
            onDismiss = { editing = null },
            onConfirm = { newName ->
                viewModel.updateDeviceModel(it.copy(name = newName))
                editing = null
            }
        )
    }

    deleting?.let {
        ConfirmDeleteDialog(
            itemName = it.name,
            onDismiss = { deleting = null },
            onConfirm = {
                viewModel.deleteDeviceModel(it)
                deleting = null
            }
        )
    }
}