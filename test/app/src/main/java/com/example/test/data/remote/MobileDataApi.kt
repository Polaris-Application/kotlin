
package com.example.test.data.remote

import com.example.test.data.remote.dto.MobileDataRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface MobileDataApi {

    @POST("mobile/location-data/")
    suspend fun sendMobileData(
        @Header("Authorization") token: String,
        @Body data: MobileDataRequest
    ): Response<Unit>
}
