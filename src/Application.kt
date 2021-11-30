package com.example

import com.example.auth.JwtService
import com.example.auth.MySession
import com.example.repo.DatabaseFactory
import com.example.repo.TodoRepo
import com.example.repo.UserRepo
import com.example.routes.todoRoute
import com.example.routes.userRoutes
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.sessions.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.gson.*
import io.ktor.features.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    DatabaseFactory.init()
    val userDb = UserRepo()
    val jwt = JwtService()
    val todoDb = TodoRepo()
    val hash = {s: String->s}

    install(Locations) {
    }

    install(Sessions) {
        cookie<MySession>("MY_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }

    install(Authentication) {

        jwt("jwt"){
            verifier(jwt.verfier)
            realm = "Todo Server"
            validate {
                val payload = it.payload
                val claim = payload.getClaim("userId")
                val clamInt = claim.asInt()
                val user = userDb.findUserById(clamInt)
                user
            }
        }

    }

    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }

    routing {
        get("/"){
            call.respond("Hello Sumanta..")
        }
        userRoutes(userDb,todoDb,jwt,hash)
        todoRoute(userDb,todoDb)
    }
}



