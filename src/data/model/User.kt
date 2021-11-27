package com.example.data.model

import io.ktor.auth.*

data class User(
    val userId: Int,
    val email: String,
    val name: String,
    val password: String
) : Principal
