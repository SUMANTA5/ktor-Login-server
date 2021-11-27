package com.example.routes

import com.example.auth.JwtService
import com.example.auth.MySession
import com.example.repo.TodoRepo
import com.example.repo.UserRepo
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

fun Route.userRoutes(
    userDb: UserRepo,
    todoDb: TodoRepo,
    jwtService: JwtService,
    hash: (String) -> String
) {
    post("v1/create") {
        val parameter = call.receive<Parameters>()

        val name = parameter["name"] ?: return@post call.respondText(
            "Missing data",
            status = HttpStatusCode.Unauthorized
        )
        val email = parameter["email"] ?: return@post call.respondText(
            "Missing data",
            status = HttpStatusCode.Unauthorized
        )
        val password = parameter["password"] ?: return@post call.respondText(
            "Missing data",
            status = HttpStatusCode.Unauthorized
        )

        val hashPassword = hash(password)

        val currentUser = userDb.createUser(name, email, hashPassword)

        try {
            currentUser?.userId?.let {
                call.sessions.set(MySession(it))
                call.respondText(
                    jwtService.generateToken(currentUser),
                    status = HttpStatusCode.Created

                )
            }

        } catch (e: Throwable) {
            call.respondText("problem creating user...")
        }
    }

    post("/v1/login") {
        val parameter = call.receive<Parameters>()

        val email = parameter["email"] ?: return@post call.respondText(
            "Missing data",
            status = HttpStatusCode.Unauthorized
        )
        val password = parameter["password"] ?: return@post call.respondText(
            "Missing data",
            status = HttpStatusCode.Unauthorized
        )

        val hashPassword = hash(password)
        try {
            val currentUser = userDb.findUserByEmail(email)
            currentUser?.userId?.let {
                if (currentUser.password == hashPassword) {
                    call.sessions.set(MySession(it))
                    call.respondText(
                        jwtService.generateToken(currentUser)
                    )
                }
            }
        } catch (e: Throwable) {
            call.respondText("problem creating user...")
        }

    }

    delete("/v1/user") {
        val user = call.sessions.get<MySession>()?.let {
            userDb.findUserById(it.userId)
        }

        if (user == null) {
            call.respondText(
                "problem gating user...",
                status = HttpStatusCode.BadRequest
            )
        }
        try {
            user?.userId?.let { it1 -> todoDb.deleteAllTodo(it1) }
            val currentUser = user?.userId?.let { it1 -> userDb.deleteUser(it1) }
            if (currentUser == 1) {
                call.respondText("delete successfully...")
            } else {
                call.respondText("gating problem...")
            }

        } catch (e: Throwable) {
            call.respondText("problem creating user...")
        }
    }

    put("/v1/user") {
        val parameter = call.receive<Parameters>()

        val name = parameter["name"] ?: return@put call.respondText(
            "Missing data",
            status = HttpStatusCode.Unauthorized
        )
        val email = parameter["email"] ?: return@put call.respondText(
            "Missing data",
            status = HttpStatusCode.Unauthorized
        )
        val password = parameter["password"] ?: return@put call.respondText(
            "Missing data",
            status = HttpStatusCode.Unauthorized
        )

        val user = call.sessions.get<MySession>()?.let {
            userDb.findUserById(it.userId)
        }
        if (user == null) {
            call.respondText(
                "problem gating user...",
                status = HttpStatusCode.BadRequest
            )
        }
        val hashPassword = hash(password)

        try {
            val currentUser = user?.userId?.let { it1 -> userDb.updateUser(it1, name, email, hashPassword) }
            if (currentUser == 1) {
                call.respondText("update successfully...")
            } else {
                call.respondText("gating problem...")
            }
        } catch (e: Throwable) {
            call.respondText("problem creating user...")
        }
    }
}