package com.example.auth

import io.ktor.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

val hashKey = hex(System.getenv("SECRET_KEY"))
val hmacKey = SecretKeySpec(hashKey,"HmacSHA1")

fun hashPassword(password: String): String{
    val hMac = Mac.getInstance("HmacSHA1")
    hMac.init(hmacKey)
    return hex(hMac.doFinal(password.toByteArray(Charsets.UTF_8)))
}