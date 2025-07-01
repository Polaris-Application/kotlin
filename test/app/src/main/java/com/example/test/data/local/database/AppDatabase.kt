package com.example.test.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.test.data.local.Dao.CellInfoDao
import com.example.test.data.local.Dao.PingTestDao
import com.example.test.data.local.Dao.HttpUploadTestDao
import com.example.test.data.local.Dao.HttpDownloadTestDao
import com.example.test.data.local.Dao.DNSTestDao
import com.example.test.data.local.Dao.WebTestDao
import com.example.test.data.local.Dao.*
import com.example.test.data.local.entity.CellInfoEntity
import com.example.test.data.local.entity.PingTestEntity
import com.example.test.data.local.entity.HttpUploadTestEntity
import com.example.test.data.local.entity.HttpDownloadTestEntity
import com.example.test.data.local.entity.DNSTestEntity
import com.example.test.data.local.entity.WebTestEntity
import com.example.test.data.local.entity.SMSTestEntity
import com.example.test.data.local.entity.*

@Database(
    entities = [
        CellInfoEntity::class,
        PingTestEntity::class,
        HttpUploadTestEntity::class,
        HttpDownloadTestEntity::class,
        DNSTestEntity::class,
        WebTestEntity::class,
        SMSTestEntity::class,
        NetworkTest::class,
        LoginEntity::class
    ],
    version = 12,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cellInfoDao(): CellInfoDao
    abstract fun pingTestDao(): PingTestDao
    abstract fun httpDownloadTestDao(): HttpDownloadTestDao
    abstract fun httpUploadTestDao(): HttpUploadTestDao
    abstract fun dnsTestDao(): DNSTestDao
    abstract fun webTestDao(): WebTestDao
    abstract fun smsTestDao(): SMSTestDao
    abstract fun networkTestDao(): NetworkTestDao
    abstract fun loginDao(): LoginDao
}

