package com.example.myinventory.ui.settings.tabs

import androidx.compose.runtime.*
import com.example.myinventory.data.models.DeviceType
import com.example.myinventory.data.models.Vendor
import com.example.myinventory.ui.settings.SettingsViewModel

@Composable
fun DeviceModelsTab(viewModel: SettingsViewModel) {
    val allModels by viewModel.deviceModels.collectAsState()
    val allVendors by viewModel.vendors.collectAsState()
    val allTypes by viewModel.deviceTypes.collectAsState()

    var selectedVendor by remember { mutableStateOf<Vendor?>(null) }
    var selectedType by remember { mutableStateOf<DeviceType?>(null) }

    val filtered = allModels.filter { model ->
        val matchVendor = selectedVendor?.id?.let { it == model.vendorId } ?: true
        val matchType = selectedType?.id?.let { it == model.deviceTypeId } ?: true
        matchVendor && matchType
    }.sortedBy { it.name }

    DeviceModelsTabContent(
        models = filtered,
        vendors = allVendors,
        types = allTypes,
        selectedVendor = selectedVendor,
        onVendorSelected = { selectedVendor = it },
        selectedType = selectedType,
        onTypeSelected = { selectedType = it },
        onAdd = { name -> viewModel.addDeviceModel(name, selectedVendor!!.id, selectedType!!.id) },
        onEdit = { model, newName -> viewModel.updateDeviceModel(model.copy(name = newName)) },
        onDelete = { viewModel.deleteDeviceModel(it) }
    )
}