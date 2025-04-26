package com.example.myinventory.ui.devices

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myinventory.R
import com.example.myinventory.data.models.Device
import com.example.myinventory.data.models.DeviceModel
import com.example.myinventory.data.models.DeviceType
import com.example.myinventory.data.models.Location
import com.example.myinventory.data.models.Rack
import com.example.myinventory.data.models.Site
import com.example.myinventory.data.models.Vendor
import com.example.myinventory.ui.components.AddItemField
import com.example.myinventory.ui.components.ClearFiltersButton
import com.example.myinventory.ui.components.DropdownSelector
import com.example.myinventory.ui.settings.SettingsViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDeviceScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val sites by viewModel.sites.collectAsState()
    val locations by viewModel.locations.collectAsState()
    val racks by viewModel.racks.collectAsState()

    val vendors by viewModel.vendors.collectAsState()
    val deviceTypes by viewModel.deviceTypes.collectAsState()
    val deviceModels by viewModel.deviceModels.collectAsState()

    var selectedSite by remember { mutableStateOf<Site?>(null) }
    var selectedLocation by remember { mutableStateOf<Location?>(null) }
    var selectedRack by remember { mutableStateOf<Rack?>(null) }

    var selectedVendor by remember { mutableStateOf<Vendor?>(null) }
    var selectedDeviceType by remember { mutableStateOf<DeviceType?>(null) }
    var selectedModel by remember { mutableStateOf<DeviceModel?>(null) }

    var deviceName by remember { mutableStateOf("") }

    val filteredLocations = locations.filter { location ->
        selectedSite?.id?.let { it == location.siteId } ?: true
    }

    val filteredRacks = racks.filter { rack ->
        when {
            selectedLocation != null -> {
                selectedLocation?.id?.let { it == rack.locationId } ?: true
            }
            selectedSite != null -> {
                val siteLocationIds = filteredLocations.map { it.id }
                rack.locationId in siteLocationIds
            }
            else -> true
        }
    }

    val filteredModels = deviceModels.filter { model ->
        val matchVendor = selectedVendor?.id?.let { it == model.vendorId } ?: true
        val matchType = selectedDeviceType?.id?.let { it == model.deviceTypeId } ?: true
        matchVendor && matchType
    }

    var addEnable = deviceName != "" && selectedModel != null && selectedLocation != null;
    var clearingEnable = deviceName.isNotBlank()
            || selectedSite != null
            || selectedLocation != null
            || selectedRack != null
            || selectedVendor != null
            || selectedDeviceType != null
            || selectedModel != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_device)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)
        ) {
            // Выпадающие списка выбора
            // Вендора
            DropdownSelector(
                label = stringResource(R.string.vendor),
                items = vendors.sortedBy { it.name },
                selectedItem = selectedVendor,
                onItemSelected = { selectedVendor = it },
                itemToString = { it.name }
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Типа девайса
            DropdownSelector(
                label = stringResource(R.string.device_type),
                items = deviceTypes.sortedBy { it.name },
                selectedItem = selectedDeviceType,
                onItemSelected = { selectedDeviceType = it },
                itemToString = { it.name }
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Модели
            DropdownSelector(
                label = stringResource(R.string.model),
                items = filteredModels.sortedBy { it.name },
                selectedItem = selectedModel,
                onItemSelected = { selectedModel = it },
                itemToString = { it.name }
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Места
            DropdownSelector(
                label = stringResource(R.string.site),
                items = sites.sortedBy { it.name },
                selectedItem = selectedSite,
                onItemSelected = { selectedSite = it },
                itemToString = { it.name }
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Локации
            DropdownSelector(
                label = stringResource(R.string.location),
                items = filteredLocations.sortedBy { it.name },
                selectedItem = selectedLocation,
                onItemSelected = { selectedLocation = it },
                itemToString = { it.name }
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Стойки
            DropdownSelector(
                label = stringResource(R.string.rack),
                items = filteredRacks.sortedBy { it.name },
                selectedItem = selectedRack,
                onItemSelected = { selectedRack = it },
                itemToString = { it.name }
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (clearingEnable) {
                ClearFiltersButton(
                    onReset = {
                        selectedSite = null
                        selectedLocation = null
                        selectedRack = null

                        selectedVendor = null
                        selectedDeviceType = null
                        selectedModel = null

                        deviceName = ""
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Поле для ввода имени устройства
            AddItemField(
                label = stringResource(R.string.new_model),
                onAdd = {
                    if (addEnable) {
                        val now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        viewModel.addDevice(
                            Device(
                                name = deviceName,
                                modelId = selectedModel!!.id,
                                locationId = selectedLocation?.id,
                                rackId = selectedRack?.id,
                                createdAt = now,
                                updatedAt = now
                            )
                        )
                        onNavigateBack()
                    } },
                onValueChange = {deviceName = it},
                isAddEnabled = addEnable
            )
        }
    }
} 