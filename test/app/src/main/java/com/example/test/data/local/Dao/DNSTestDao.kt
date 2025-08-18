
package com.example.test.data.local.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.test.data.local.entity.DNSTestEntity
import com.example.test.data.local.entity.PingTestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DNSTestDao {
    @Insert
    suspend fun insertDNSTest(dnsTest: DNSTestEntity)

    @Query("SELECT * FROM dns_test WHERE testId = :testId")
    fun getResultsForTest(testId: Long): Flow<List<DNSTestEntity>>  // تغییرات جدید

    @Query("SELECT * FROM dns_test")
    fun getAllDNSTests(): Flow<List<DNSTestEntity>>

    @Query("DELETE FROM dns_test")
    suspend fun clearAllDNSTests()

    @Query("SELECT * FROM dns_test WHERE isuploaded = 0")
    suspend fun getUnsentDnsTests(): List<DNSTestEntity>

    @Query("UPDATE dns_test SET isuploaded = 1 WHERE id IN (:ids)")
    suspend fun markDnsTestsAsUploaded(ids: List<Long>)
}
