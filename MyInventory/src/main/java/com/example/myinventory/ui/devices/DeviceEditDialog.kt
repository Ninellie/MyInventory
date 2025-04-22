package com.example.myinventory.ui.devices

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.myinventory.R
import com.example.myinventory.data.models.Device
import com.example.myinventory.data.models.DeviceType
import com.example.myinventory.data.models.Field
import com.example.myinventory.data.models.FieldType
import com.example.myinventory.data.models.Location
import com.example.myinventory.data.models.Rack
import com.example.myinventory.ui.components.DropdownSelector
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun DeviceEditDialog(
    device: Device,
    deviceType: DeviceType?,
    fieldTypes: List<FieldType>,
    fields: List<Field>,
    locations: List<Location>,
    racks: List<Rack>,
    onDismiss: () -> Unit,
    onSave: (Device, List<Field>) -> Unit
) {
    // Используем rememberSaveable для сохранения состояния между перекомпозициями
    var deviceName by remember { mutableStateOf(device.name) }
    var selectedLocation by remember { mutableStateOf<Location?>(null) }
    var selectedRack by remember { mutableStateOf<Rack?>(null) }
    
    // Инициализируем выбранную локацию и стойку
    LaunchedEffect(device, locations, racks) {
        selectedLocation = device.locationId?.let { locId -> 
            locations.find { it.id == locId } 
        }
        
        selectedRack = device.rackId?.let { rackId -> 
            racks.find { it.id == rackId } 
        }
    }
    
    // Создаем Map для хранения значений полей по их ID
    val fieldValuesMap = remember {
        mutableStateMapOf<Int, String>().apply {
            fields.forEach { field ->
                put(field.fieldTypeId, field.value)
            }
        }
    }
    
    // Фильтруем стойки по выбранной локации
    val filteredRacks = remember(selectedLocation) {
        if (selectedLocation != null) {
            racks.filter { it.locationId == selectedLocation!!.id }
        } else {
            emptyList()
        }
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = stringResource(R.string.edit),
                    style = MaterialTheme.typography.titleLarge
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Поле для имени устройства
                OutlinedTextField(
                    value = deviceName,
                    onValueChange = { deviceName = it },
                    label = { Text(stringResource(R.string.device)) },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Выбор локации
                Text(
                    text = stringResource(R.string.location),
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                DropdownSelector(
                    label = stringResource(R.string.location),
                    items = locations,
                    selectedItem = selectedLocation,
                    onItemSelected = { 
                        selectedLocation = it
                        selectedRack = null
                    },
                    itemToString = { it.name }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Выбор стойки
                Text(
                    text = stringResource(R.string.rack),
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                DropdownSelector(
                    label = stringResource(R.string.rack),
                    items = filteredRacks,
                    selectedItem = selectedRack,
                    onItemSelected = { selectedRack = it },
                    itemToString = { it.name }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Информация о времени создания и обновления
                Text(
                    text = "Created: ".plus(device.createdAt),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Updated: ".plus(device.updatedAt),
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Список полей
                Text(
                    text = stringResource(R.string.fields),
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                deviceType?.let { type ->
                    type.fieldTypeIdList.forEach { fieldTypeId ->
                        val fieldType = fieldTypes.find { it.id == fieldTypeId }
                        fieldType?.let { ft ->
                            // Используем key для создания уникального ключа для каждого поля
                            key(ft.id) {
                                FieldEditor(
                                    fieldType = ft,
                                    value = fieldValuesMap[ft.id] ?: "",
                                    onValueChange = { value ->
                                        fieldValuesMap[ft.id] = value
                                    }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Кнопки
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.cancel))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                            
                            // Создаем список полей на основе значений из Map
                            val updatedFields = fieldValuesMap.map { (fieldTypeId, value) ->
                                // Находим существующее поле или создаем новое
                                fields.find { it.fieldTypeId == fieldTypeId }?.copy(value = value)
                                    ?: Field(
                                        deviceId = device.id,
                                        fieldTypeId = fieldTypeId,
                                        value = value
                                    )
                            }
                            
                            onSave(
                                device.copy(
                                    name = deviceName,
                                    locationId = selectedLocation?.id,
                                    rackId = selectedRack?.id,
                                    updatedAt = now
                                ),
                                updatedFields
                            )
                        },
                        enabled = deviceName.isNotBlank()
                    ) {
                        Text(stringResource(R.string.save))
                    }
                }
            }
        }
    }
}

@Composable
private fun FieldEditor(
    fieldType: FieldType,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(
            text = fieldType.name,
            style = MaterialTheme.typography.bodyMedium
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        when (fieldType.valueType) {
            "text" -> {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            "coordinates" -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val parts = value.split(",")
                    val lat = parts.getOrNull(0) ?: ""
                    val lng = parts.getOrNull(1) ?: ""
                    
                    OutlinedTextField(
                        value = lat,
                        onValueChange = { newLat ->
                            onValueChange("$newLat,$lng")
                        },
                        modifier = Modifier.weight(1f),
                        label = { Text("Latitude") }
                    )
                    OutlinedTextField(
                        value = lng,
                        onValueChange = { newLng ->
                            onValueChange("$lat,$newLng")
                        },
                        modifier = Modifier.weight(1f),
                        label = { Text("Longitude") }
                    )
                }
            }
            "web_url" -> {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            else -> {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
} 