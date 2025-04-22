package com.example.myinventory.ui.devices

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myinventory.R
import com.example.myinventory.data.models.*
import com.example.myinventory.ui.components.ClearFiltersButton
import com.example.myinventory.ui.components.DropdownSelector
import com.example.myinventory.ui.components.ItemList
import com.example.myinventory.ui.components.MultiSelectDropdown
import com.example.myinventory.ui.settings.*
import com.example.myinventory.ui.settings.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevicesScreen(
    viewModel: SettingsViewModel,
    onNavigateToAddDevice: () -> Unit
) {
    val devices by viewModel.devices.collectAsState()
    val deviceModels by viewModel.deviceModels.collectAsState()
    val deviceTypes by viewModel.deviceTypes.collectAsState()
    val fieldTypes by viewModel.fieldTypes.collectAsState()
    val fields by viewModel.fields.collectAsState()
    val locations by viewModel.locations.collectAsState()
    val racks by viewModel.racks.collectAsState()
    val sites by viewModel.sites.collectAsState()
    
    var showFilters by remember { mutableStateOf(false) }
    var showGrouping by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedSite by remember { mutableStateOf<Site?>(null) }
    var selectedLocation by remember { mutableStateOf<Location?>(null) }
    var selectedRack by remember { mutableStateOf<Rack?>(null) }
    var selectedFields by remember { mutableStateOf<List<FieldType>>(emptyList()) }
    var groupByDeviceType by remember { mutableStateOf(false) }
    
    var editing by remember { mutableStateOf<Device?>(null) }
    var deleting by remember { mutableStateOf<Device?>(null) }
    
    val filtered = devices.filter { device -> 
        val matchName = device.name.contains(searchQuery, ignoreCase = true)
        val matchSite = selectedSite?.id == null || 
            locations.find { it.id == device.locationId }?.siteId == selectedSite!!.id
        val matchLocation = selectedLocation?.id == null || device.locationId == selectedLocation!!.id
        val matchRack = selectedRack?.id == null || device.rackId == selectedRack!!.id
        val matchFields = selectedFields.isEmpty() || selectedFields.all { fieldType ->
            fields.any { it.deviceId == device.id && it.fieldTypeId == fieldType.id }
        }
        matchName && matchSite && matchLocation && matchRack && matchFields
    }
    
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddDevice,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_device))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.devices),
                style = MaterialTheme.typography.headlineMedium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Поиск по названию
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text(stringResource(R.string.search_by_name)) },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Кнопки фильтров и группировки
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Кнопка фильтров
                OutlinedButton(
                    onClick = { showFilters = !showFilters },
                    modifier = Modifier.weight(1f)
                ) {
                    //Icon(Icons.Default., contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.filters))
                }
                
                // Кнопка группировки
                OutlinedButton(
                    onClick = { showGrouping = !showGrouping },
                    modifier = Modifier.weight(1f)
                ) {
                    //Icon(Icons.Default.GroupWork, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.grouping))
                }
            }
            
            // Раскрываемая панель фильтров
            if (showFilters) {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Фильтр по Site
                DropdownSelector(
                    label = stringResource(R.string.site),
                    items = sites,
                    selectedItem = selectedSite,
                    onItemSelected = { 
                        selectedSite = it
                        selectedLocation = null
                        selectedRack = null
                    },
                    itemToString = { it.name }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Фильтр по Location (только если выбран Site)
                if (selectedSite != null) {
                    val locationsInSite = locations
                        .filter { it.siteId == selectedSite!!.id }
                        .sortedBy { it.name }
                    DropdownSelector(
                        label = stringResource(R.string.location),
                        items = locationsInSite,
                        selectedItem = selectedLocation,
                        onItemSelected = { 
                            selectedLocation = it
                            selectedRack = null
                        },
                        itemToString = { it.name }
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                // Фильтр по Rack (только если выбрана Location)
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
                
                // Фильтр по полям
                MultiSelectDropdown(
                    label = stringResource(R.string.fields),
                    items = fieldTypes,
                    selectedItems = selectedFields,
                    onItemsSelected = { selectedFields = it },
                    itemToString = { it.name }
                )
                
                // Кнопка сброса фильтров
                if (selectedSite != null || selectedLocation != null || 
                    selectedRack != null || selectedFields.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    ClearFiltersButton(
                        onReset = { 
                            selectedSite = null
                            selectedLocation = null
                            selectedRack = null
                            selectedFields = emptyList()
                        }
                    )
                }
            }
            
            // Раскрываемая панель группировки
            if (showGrouping) {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Группировка по типу устройства
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = groupByDeviceType,
                        onCheckedChange = { groupByDeviceType = it }
                    )
                    Text(stringResource(R.string.group_by_device_type))
                }
                
                // Кнопка сброса группировки
                if (groupByDeviceType) {
                    Spacer(modifier = Modifier.height(8.dp))
                    ClearFiltersButton(
                        onReset = { groupByDeviceType = false }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Список устройств
            if (groupByDeviceType) {
                // Группировка по типу устройства
                val groupedDevices = filtered.groupBy { device ->
                    val model = deviceModels.find { it.id == device.modelId }
                    val deviceType = model?.let {
                        deviceTypes.find { type -> type.id == it.deviceTypeId } }
                    deviceType?.name ?: "Unknown"
                }.toSortedMap()
                
                groupedDevices.forEach { (groupName, devicesInGroup) ->
                    Text(
                        text = groupName,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    ItemList(
                        items = devicesInGroup.sortedBy { it.name },
                        getTitle = { it.name },
                        getSubtitle = { modelNameFor(it.modelId, deviceModels) },
                        onEdit = { editing = it },
                        onDelete = { deleting = it },
                        emptyMessage = "Empty"
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                }
            } else {
                // Обычный список без группировки
                ItemList(
                    items = filtered.sortedBy { it.name },
                    getTitle = { it.name },
                    getSubtitle = { modelNameFor(it.modelId, deviceModels) },
                    onEdit = { editing = it },
                    onDelete = { deleting = it },
                    emptyMessage = "Empty"
                )
            }
        }
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
                viewModel.updateDevice(updatedDevice)
                updatedFields.forEach { field ->
                    if (field.id == 0) {
                        viewModel.addField(field.fieldTypeId, field.deviceId, field.value)
                    } else {
                        viewModel.updateField(field)
                    }
                }
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