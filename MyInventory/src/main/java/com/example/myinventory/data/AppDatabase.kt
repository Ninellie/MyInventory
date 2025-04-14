package com.example.myinventory.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.myinventory.data.dao.DeviceDao
import com.example.myinventory.data.dao.DeviceModelDao
import com.example.myinventory.data.dao.DeviceTypeDao
import com.example.myinventory.data.dao.FieldDao
import com.example.myinventory.data.dao.FieldTypeDao
import com.example.myinventory.data.dao.LocationDao
import com.example.myinventory.data.dao.RackDao
import com.example.myinventory.data.dao.SiteDao
import com.example.myinventory.data.dao.VendorDao
import com.example.myinventory.data.models.Device
import com.example.myinventory.data.models.DeviceModel
import com.example.myinventory.data.models.DeviceType
import com.example.myinventory.data.models.Field
import com.example.myinventory.data.models.FieldType
import com.example.myinventory.data.models.Location
import com.example.myinventory.data.models.Rack
import com.example.myinventory.data.models.Site
import com.example.myinventory.data.models.Vendor


@Database(
    entities = [
        Site::class,
        Vendor::class,
        DeviceType::class,
        Location::class,
        DeviceModel::class,
        Rack::class,
        Field::class,
        FieldType::class,
        Device::class,
    ],
    version = 4,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 3, to = 4)
    ]
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun siteDao(): SiteDao
    abstract fun vendorDao(): VendorDao
    abstract fun deviceTypeDao(): DeviceTypeDao
    abstract fun locationDao(): LocationDao
    abstract fun deviceModelDao(): DeviceModelDao
    abstract fun rackDao(): RackDao

    abstract fun fieldTypeDao(): FieldTypeDao
    abstract fun fieldDao(): FieldDao
    abstract fun deviceDao(): DeviceDao
}

