package com.example.routes

import com.example.auth.MySession
import com.example.repo.TodoRepo
import com.example.repo.UserRepo
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

fun Route.todoRoute(
    userDb: UserRepo,
    todoDb: TodoRepo
){
    authenticate("jwt"){
        post("/v1/todo"){
            val parameter = call.receive<Parameters>()

            val todo = parameter["todo"] ?: return@post call.respondText(
                "missing parameter",
                status = HttpStatusCode.Unauthorized
            )
            val done = parameter["done"] ?: return@post call.respondText(
                "missing parameter",
                status = HttpStatusCode.Unauthorized
            )
            val user = call.sessions.get<MySession>()?.let {
                userDb.findUserById(it.userId)
            }
            if (user == null){
                call.respondText("problem gating user...")
            }

            try {
                val currentTodo = user?.userId?.let { it1 -> todoDb.createTodo(it1,todo,done.toBoolean()) }
                currentTodo?.id?.let {
                    call.respond(currentTodo)
                }

            } catch (e: Throwable) {
                call.respondText("problem creating todo...")
            }
        }

    }

    get("/v1/todo"){

        val user = call.sessions.get<MySession>()?.let {
            userDb.findUserById(it.userId)
        }
        if (user == null){
            call.respondText("problem gating user...")
        }

        try {
            val allTodo = user?.userId?.let { it1 -> todoDb.getAllTodo(it1) }
            if (allTodo?.isEmpty() == true){
                call.respond(allTodo)
            }

        } catch (e: Throwable) {
            call.respondText("problem creating todo...")
        }
    }

    delete("/v1/todo/{id}"){
        val id = call.parameters["id"]

        val user = call.sessions.get<MySession>()?.let {
            userDb.findUserById(it.userId)
        }
        if (user == null){
            call.respondText("problem gating user...")
        }
        try {
            val allTodo  = user?.userId?.let { it1 -> todoDb.getAllTodo(it1) }
            allTodo?.forEach {
                if (it.id == id?.toInt()){
                    todoDb.deleteTodo(id.toInt())
                    call.respondText("delete successfully...")
                }else{
                    call.respondText("gating problem...")
                }
            }

        } catch (e: Throwable) {
            call.respondText("problem creating todo...")
        }
    }

    delete("/v1/todo"){
        val user = call.sessions.get<MySession>()?.let {
            userDb.findUserById(it.userId)
        }
        if (user == null){
            call.respondText("problem gating user...")
        }

        try {
            val allTodo = user?.userId?.let { it1 -> todoDb.deleteAllTodo(it1) }
            if (allTodo != null){
                if (allTodo>0){
                    call.respondText("delete successfully...")
                }else{
                    call.respondText("gating problem...")
                }
            }

        }catch (e: Throwable) {
            call.respondText("problem creating todo...")
        }
    }

    put("/v1/todo{id}"){
        val id = call.parameters["id"]

        val user = call.sessions.get<MySession>()?.let {
            userDb.findUserById(it.userId)
        }
        if (user == null){
            call.respondText("problem gating user...")
        }

        val parameter = call.receive<Parameters>()

        val todo = parameter["todo"] ?: return@put call.respondText(
            "missing parameter",
            status = HttpStatusCode.Unauthorized
        )
        val done = parameter["done"] ?: return@put call.respondText(
            "missing parameter",
            status = HttpStatusCode.Unauthorized
        )

        try {
            val allTodo = user?.userId?.let { it1 -> todoDb.getAllTodo(it1) }
            allTodo?.forEach {
                if (it.id == id?.toInt()){
                    todoDb.updateTodo(id.toInt(),todo,done.toBoolean())
                    call.respondText("update successfully...")
                }else{
                    call.respondText("gating problem...")
                }
            }

        }catch (e: Throwable) {
            call.respondText("problem creating todo...")
        }



    }
}