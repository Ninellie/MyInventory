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
import com.example.myinventory.data.models.Location
import com.example.myinventory.data.models.Rack
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
    val deviceModels by viewModel.deviceModels.collectAsState()
    val locations by viewModel.locations.collectAsState()
    val racks by viewModel.racks.collectAsState()
    
    var selectedModel by remember { mutableStateOf<DeviceModel?>(null) }
    var selectedLocation by remember { mutableStateOf<Location?>(null) }
    var selectedRack by remember { mutableStateOf<Rack?>(null) }
    var deviceName by remember { mutableStateOf("") }
    
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
            // Выбор модели устройства
            DropdownSelector(
                label = stringResource(R.string.model),
                items = deviceModels,
                selectedItem = selectedModel,
                onItemSelected = { selectedModel = it },
                itemToString = { it.name }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Выбор локации (только если выбрана модель)
            if (selectedModel != null) {
                DropdownSelector(
                    label = stringResource(R.string.location),
                    items = locations,
                    selectedItem = selectedLocation,
                    onItemSelected = { selectedLocation = it },
                    itemToString = { it.name }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Выбор стойки (только если выбрана локация)
            if (selectedLocation != null) {
                val racksInLocation = racks.filter { it.locationId == selectedLocation!!.id }
                
                DropdownSelector(
                    label = stringResource(R.string.rack),
                    items = racksInLocation,
                    selectedItem = selectedRack,
                    onItemSelected = { selectedRack = it },
                    itemToString = { it.name }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Поле для ввода имени устройства
            if (selectedModel != null) {
                OutlinedTextField(
                    value = deviceName,
                    onValueChange = { deviceName = it },
                    label = { Text(stringResource(R.string.device)) },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Кнопка сохранения
                Button(
                    onClick = {
                        if (deviceName.isNotBlank() && selectedModel != null) {
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
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = deviceName.isNotBlank() && selectedModel != null
                ) {
                    Text(stringResource(R.string.save))
                }
            }
        }
    }
} 