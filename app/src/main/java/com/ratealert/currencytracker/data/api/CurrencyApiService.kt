package com.ratealert.currencytracker.data.api

import com.ratealert.currencytracker.data.model.CurrencyResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CurrencyApiService {
    @GET("latest/{base}")
    suspend fun getRates(
        @Path("base") baseCurrency: String
    ): Response<CurrencyResponse>
}
