package com.example.myinventory.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myinventory.data.AppDatabase
import com.example.myinventory.data.models.Device
import com.example.myinventory.data.models.DeviceModel
import com.example.myinventory.data.models.DeviceType
import com.example.myinventory.data.models.Field
import com.example.myinventory.data.models.FieldType
import com.example.myinventory.data.models.Location
import com.example.myinventory.data.models.Rack
import com.example.myinventory.data.models.Site
import com.example.myinventory.data.models.Vendor
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SettingsViewModel(
    private val db: AppDatabase
) : ViewModel() {
    // Состояния — наблюдаемые данные
    val sites = db.siteDao().getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val locations = db.locationDao().getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val vendors = db.vendorDao().getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val deviceTypes = db.deviceTypeDao().getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val deviceModels = db.deviceModelDao().getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val racks = db.rackDao().getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val fieldTypes = db.fieldTypeDao().getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val devices = db.deviceDao().getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val fields = db.fieldDao().getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    // 🔸 ДОБАВЛЕНИЕ
    fun addSite(name: String) = viewModelScope.launch {
        db.siteDao().insert(Site(name = name))
    }

    fun addLocation(name: String, siteId: Int) = viewModelScope.launch {
        db.locationDao().insert(Location(name = name, siteId = siteId))
    }

    fun addVendor(name: String) = viewModelScope.launch {
        db.vendorDao().insert(Vendor(name = name))
    }

    fun addDeviceType(name: String, fieldTypeIdList: List<Int>) = viewModelScope.launch {
        db.deviceTypeDao().insert(DeviceType(name = name, fieldTypeIdList = fieldTypeIdList))
    }

    fun addDeviceModel(name: String, vendorId: Int, typeId: Int) = viewModelScope.launch {
        db.deviceModelDao().insert(DeviceModel(name = name, vendorId = vendorId, deviceTypeId = typeId))
    }

    fun addRack(name: String, locationId: Int) = viewModelScope.launch {
        db.rackDao().insert(Rack(name = name, locationId = locationId))
    }

    fun addFieldType(name: String, valueType: String) = viewModelScope.launch {
        db.fieldTypeDao().insert(FieldType(name = name, valueType = valueType))
    }

    fun addDevice(device: Device) = viewModelScope.launch {
        db.deviceDao().insert(device)
    }

    fun copyDevices(originalDevices: List<Device>) = viewModelScope.launch {
        val allDevices = devices.value.toMutableList()

        for (original in originalDevices) {
            val baseName = original.name.replace(Regex(""" \(copy(?: \d+)?\)$"""), "")

            val matchingCopies = allDevices.filter {
                it.name == "$baseName (copy)" || it.name.matches(Regex("""$baseName \(copy \d+\)"""))
            }

            val newName = when {
                matchingCopies.isEmpty() -> "$baseName (copy)"
                else -> "$baseName (copy ${matchingCopies.size + 1})"
            }

            val now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

            val copiedDevice = original.copy(
                id = 0,
                name = newName,
                createdAt = now,
                updatedAt = now
            )

            db.deviceDao().insert(copiedDevice)

            // Обновляем локальный список, чтобы следующий знал о новых копиях
            allDevices.add(copiedDevice)
        }
    }

    fun copyDevice(originalDevice: Device) = viewModelScope.launch {
        val baseName = originalDevice.name.replace(Regex(""" \(copy(?: \d+)?\)$"""), "")

        val matchingCopies = devices.value.filter {
            it.name == "$baseName (copy)" || it.name.matches(Regex("""$baseName \(copy \d+\)"""))
        }

        val newName = when {
            matchingCopies.isEmpty() -> "$baseName (copy)"
            else -> "$baseName (copy ${matchingCopies.size + 1})"
        }

        val now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

        val copiedDevice = originalDevice.copy(
            id = 0, // т.к. PrimaryKey = autoGenerate
            name = newName,
            createdAt = now,
            updatedAt = now
        )

        db.deviceDao().insert(copiedDevice)
    }

    fun addField(fieldTypeId: Int, deviceId: Int, value: String = "") = viewModelScope.launch {
        db.fieldDao().insert(Field(fieldTypeId = fieldTypeId, deviceId = deviceId, value = value))
    }

    // 🔸 ОБНОВЛЕНИЕ
    fun updateSite(site: Site) = viewModelScope.launch {
        db.siteDao().update(site)
    }

    fun updateLocation(location: Location) = viewModelScope.launch {
        db.locationDao().update(location)
    }

    fun updateVendor(vendor: Vendor) = viewModelScope.launch {
        db.vendorDao().update(vendor)
    }

    fun updateDeviceType(type: DeviceType) = viewModelScope.launch {
        // Получаем старый тип устройства
        val oldType = deviceTypes.value.find { it.id == type.id }
        
        // Обновляем тип устройства
        db.deviceTypeDao().update(type)
        
        // Если список полей изменился, удаляем поля, которых больше нет в типе
        if (oldType != null && oldType.fieldTypeIdList != type.fieldTypeIdList) {
            // Получаем все устройства данного типа
            val devicesOfType = devices.value.filter { device ->
                val model = deviceModels.value.find { it.id == device.modelId }
                model?.deviceTypeId == type.id
            }
            
            // Получаем все поля этих устройств
            val fieldsToCheck = fields.value.filter { field ->
                devicesOfType.any { it.id == field.deviceId }
            }
            
            // Удаляем поля, которых больше нет в типе
            fieldsToCheck.forEach { field ->
                if (!type.fieldTypeIdList.contains(field.fieldTypeId)) {
                    db.fieldDao().delete(field)
                }
            }
        }
    }

    fun updateDeviceModel(model: DeviceModel) = viewModelScope.launch {
        // Получаем старую модель устройства
        val oldModel = deviceModels.value.find { it.id == model.id }
        
        // Обновляем модель устройства
        db.deviceModelDao().update(model)
        
        // Если тип устройства изменился, удаляем поля, которых больше нет в новом типе
        if (oldModel != null && oldModel.deviceTypeId != model.deviceTypeId) {
            // Получаем тип устройства
            val deviceType = deviceTypes.value.find { it.id == model.deviceTypeId }
            
            // Получаем все устройства данной модели
            val devicesOfModel = devices.value.filter { it.modelId == model.id }
            
            // Получаем все поля этих устройств
            val fieldsToCheck = fields.value.filter { field ->
                devicesOfModel.any { it.id == field.deviceId }
            }
            
            // Удаляем поля, которых больше нет в типе
            if (deviceType != null) {
                fieldsToCheck.forEach { field ->
                    if (!deviceType.fieldTypeIdList.contains(field.fieldTypeId)) {
                        db.fieldDao().delete(field)
                    }
                }
            }
        }
    }

    fun updateRack(rack: Rack) = viewModelScope.launch {
        db.rackDao().update(rack)
    }

    fun updateFieldType(fieldType: FieldType) = viewModelScope.launch {
        db.fieldTypeDao().update(fieldType)
    }

    fun updateDevice(device: Device) = viewModelScope.launch {
        db.deviceDao().update(device)
    }

    fun updateField(field: Field) = viewModelScope.launch {
        db.fieldDao().update(field)
    }

    // 🔸 УДАЛЕНИЕ
    fun deleteSite(site: Site) = viewModelScope.launch {
        db.siteDao().delete(site)
    }

    fun deleteSites(sites: List<Site>) = viewModelScope.launch {
        sites.forEach {
            db.siteDao().delete(it)
        }
    }

    fun deleteLocation(location: Location) = viewModelScope.launch {
        db.locationDao().delete(location)
    }

    fun deleteLocations(locations: List<Location>) = viewModelScope.launch{
        locations.forEach {
            db.locationDao().delete(it)
        }
    }

    fun deleteVendor(vendor: Vendor) = viewModelScope.launch {
        db.vendorDao().delete(vendor)
    }

    fun deleteVendors(vendors: List<Vendor>) = viewModelScope.launch {
        vendors.forEach {
            db.vendorDao().delete(it)
        }
    }

    fun deleteDeviceType(type: DeviceType) = viewModelScope.launch {
        db.deviceTypeDao().delete(type)
    }

    fun deleteDeviceTypes(types: List<DeviceType>) = viewModelScope.launch {
        types.forEach { db.deviceTypeDao().delete(it) }
    }

    fun deleteDeviceModel(model: DeviceModel) = viewModelScope.launch {
        db.deviceModelDao().delete(model)
    }

    fun deleteDeviceModels(models: List<DeviceModel>) = viewModelScope.launch {
        models.forEach { db.deviceModelDao().delete(it) }
    }

    fun deleteRack(rack: Rack) = viewModelScope.launch {
        db.rackDao().delete(rack)
    }

    fun deleteRacks(racks: List<Rack>) = viewModelScope.launch {
        racks.forEach {
            db.rackDao().delete(it)
        }
    }

    fun deleteFieldType(fieldType: FieldType) = viewModelScope.launch {
        // Сначала удаляем все поля данного типа
        val fieldsOfType = fields.value.filter { it.fieldTypeId == fieldType.id }
        fieldsOfType.forEach { field ->
            db.fieldDao().delete(field)
        }

        // Затем удаляем сам тип поля
        db.fieldTypeDao().delete(fieldType)
    }

    fun deleteFieldTypes(fieldTypes: List<FieldType>) = viewModelScope.launch {
        fieldTypes.forEach { fieldType ->
            val fieldsOfType = fields.value.filter { it.fieldTypeId == fieldType.id }
            fieldsOfType.forEach { field ->
                db.fieldDao().delete(field)
            }

            // Затем удаляем сам тип поля
            db.fieldTypeDao().delete(fieldType)
        }
    }

    fun deleteDevice(device: Device) = viewModelScope.launch {
        // Сначала удаляем все поля устройства
        val deviceFields = fields.value.filter { it.deviceId == device.id }
        deviceFields.forEach { field ->
            db.fieldDao().delete(field)
        }
        // Затем удаляем само устройство
        db.deviceDao().delete(device)
    }

    fun deleteDevices(devices: List<Device>) = viewModelScope.launch {
        devices.forEach { device ->
            val deviceFields = fields.value.filter { it.deviceId == device.id }
            deviceFields.forEach { db.fieldDao().delete(it) }
            db.deviceDao().delete(device)
        }
    }

    fun deleteField(field: Field) = viewModelScope.launch {
        db.fieldDao().delete(field)
    }

    // 🔸 ФИЛЬТРАЦИЯ

    fun getLocationsBySite(siteId: Int): Flow<List<Location>> {
        return db.locationDao().getBySite(siteId)
    }

    fun getDeviceModelsByVendorAndType(vendorId: Int, typeId: Int): Flow<List<DeviceModel>> {
        return db.deviceModelDao().getByVendorAndType(vendorId, typeId)
    }

    fun getRacksByLocation(locationId: Int): Flow<List<Rack>> {
        return db.rackDao().getByLocation(locationId)
    }

    fun getFieldsByDevice(deviceId: Int): Flow<List<Field>> {
        return db.fieldDao().getByDevice(deviceId)
    }



}

