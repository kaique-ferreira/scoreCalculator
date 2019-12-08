package com.aruana.scorecalculator.networking

import io.reactivex.Single
import retrofit2.http.GET

interface ScoreService {

    @GET("form.json")
    fun get(): Single<Item>

    companion object {
        const val API_ENDPOINT = "https://lumiform-sandbox.s3.eu-central-1.amazonaws.com/"
    }
}