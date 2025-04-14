package com.example.myinventory.ui.settings.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myinventory.R
import com.example.myinventory.data.models.Device
import com.example.myinventory.data.models.DeviceModel
import com.example.myinventory.data.models.Location
import com.example.myinventory.data.models.Rack
import com.example.myinventory.ui.settings.ConfirmDeleteDialog
import com.example.myinventory.ui.settings.DeviceEditDialog
import com.example.myinventory.ui.settings.DropdownSelector
import com.example.myinventory.ui.settings.EntityListSectionWithFilter
import com.example.myinventory.ui.settings.ItemAddField
import com.example.myinventory.ui.settings.ClearFiltersButton
import com.example.myinventory.ui.settings.SettingsViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun DevicesTab(viewModel: SettingsViewModel) {
    val devices by viewModel.devices.collectAsState()
    val deviceModels by viewModel.deviceModels.collectAsState()
    val deviceTypes by viewModel.deviceTypes.collectAsState()
    val fieldTypes by viewModel.fieldTypes.collectAsState()
    val fields by viewModel.fields.collectAsState()
    val locations by viewModel.locations.collectAsState()
    val racks by viewModel.racks.collectAsState()
    
    var selectedModel by remember { mutableStateOf<DeviceModel?>(null) }
    var selectedLocation by remember { mutableStateOf<Location?>(null) }
    var selectedRack by remember { mutableStateOf<Rack?>(null) }
    
    var editing by remember { mutableStateOf<Device?>(null) }
    var deleting by remember { mutableStateOf<Device?>(null) }
    
    val filtered = devices.filter { device -> 
        val matchModel = selectedModel?.id == null || device.modelId == selectedModel!!.id
        val matchLocation = selectedLocation?.id == null || device.locationId == selectedLocation!!.id
        val matchRack = selectedRack?.id == null || device.rackId == selectedRack!!.id
        matchModel && matchLocation && matchRack
    }
    
    Column(Modifier.padding(16.dp)) {
        // Фильтр по модели устройства
        DropdownSelector(
            label = stringResource(R.string.model),
            items = deviceModels,
            selectedItem = selectedModel,
            onItemSelected = { selectedModel = it },
            itemToString = { it.name }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Фильтр по локации (только если выбрана модель)
        if (selectedModel != null) {
            DropdownSelector(
                label = stringResource(R.string.location),
                items = locations,
                selectedItem = selectedLocation,
                onItemSelected = { selectedLocation = it },
                itemToString = { it.name }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Фильтр по стойке (только если выбрана локация)
        if (selectedLocation != null) {
            val racksInLocation = racks.filter { it.locationId == selectedLocation!!.id }
            
            DropdownSelector(
                label = stringResource(R.string.rack),
                items = racksInLocation,
                selectedItem = selectedRack,
                onItemSelected = { selectedRack = it },
                itemToString = { it.name }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Кнопка сброса фильтров (если есть выбранные фильтры)
        if (selectedModel != null || selectedLocation != null || selectedRack != null) {
            ClearFiltersButton(
                onReset = { 
                    selectedModel = null
                    selectedLocation = null
                    selectedRack = null
                }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Добавление нового устройства
        if (selectedModel != null) {
            ItemAddField(label = stringResource(R.string.new_device),) {
                if (it.isNotBlank() && selectedModel != null) {
                    val now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    viewModel.addDevice(
                        Device(
                            name = it,
                            modelId = selectedModel!!.id,
                            locationId = selectedLocation?.id,
                            rackId = selectedRack?.id,
                            createdAt = now,
                            updatedAt = now
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Список устройств
        EntityListSectionWithFilter(
            items = filtered.sortedBy { it.name },
            getTitle = { it.name },
            getSubtitle = { modelNameFor(it.modelId, deviceModels) },
            onEdit = { editing = it },
            onDelete = { deleting = it }
        )
    }
    
    // Диалог редактирования
    editing?.let { device ->
        val deviceModel = deviceModels.find { it.id == device.modelId }
        val deviceType = deviceModel?.let { model ->
            deviceTypes.find { it.id == model.deviceTypeId }
        }
        val deviceFields = fields.filter { it.deviceId == device.id }
        
        DeviceEditDialog(
            device = device,
            deviceType = deviceType,
            fieldTypes = fieldTypes,
            fields = deviceFields,
            locations = locations,
            racks = racks,
            onDismiss = { editing = null },
            onSave = { updatedDevice, updatedFields ->
                // Сначала обновляем устройство
                viewModel.updateDevice(updatedDevice)
                
                // Затем обрабатываем поля
                updatedFields.forEach { field ->
                    if (field.id == 0) {
                        // Для новых полей используем addField с правильными параметрами
                        viewModel.addField(field.fieldTypeId, field.deviceId, field.value)
                    } else {
                        // Для существующих полей обновляем их
                        viewModel.updateField(field)
                    }
                }
                
                // Закрываем диалог
                editing = null
            }
        )
    }
    
    // Диалог удаления
    deleting?.let {
        ConfirmDeleteDialog(
            itemName = it.name,
            onDismiss = { deleting = null },
            onConfirm = {
                viewModel.deleteDevice(it)
                deleting = null
            }
        )
    }
}

private fun modelNameFor(modelId: Int, models: List<DeviceModel>) =
    models.find { it.id == modelId }?.name ?: "unknown" 