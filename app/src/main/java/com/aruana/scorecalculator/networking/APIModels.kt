package com.aruana.scorecalculator.networking

data class Item(
        val type: String,
        val response_type: String?,
        val weight: Int?,
        val response: List<String>,
        val items: List<Item>?,
        val params: Params?)

data class Params(val response_set: String?, val response_sets: List<ResponseSet>?)

data class ResponseSet(val uuid: String, val responses: List<Response>?)

data class Response(val uuid: String, val score: Int?)
