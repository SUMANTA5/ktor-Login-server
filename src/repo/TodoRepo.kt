package com.example.repo

import com.example.dao.TodoDao
import com.example.data.model.Todo
import com.example.data.table.TodoTable
import com.example.repo.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.InsertStatement

class TodoRepo : TodoDao{
    override suspend fun createTodo(userId: Int, todoData: String, done: Boolean): Todo? {
        var statement : InsertStatement<Number>? = null
        dbQuery {
            statement = TodoTable.insert { todo->
                todo[TodoTable.todo] = todoData
                todo[TodoTable.done] = done

            }
        }
        return rowToTodo(statement?.resultedValues?.get(0))
    }

    override suspend fun getAllTodo(userId: Int): List<Todo> =
        dbQuery {
            TodoTable.select {
               TodoTable.userId.eq(userId)
            }.mapNotNull {
                rowToTodo(it)
            }
        }
    override suspend fun getTodo(id: Int): Todo? =
        dbQuery {
            TodoTable.select {
                TodoTable.id.eq(id)
            }.map {
                rowToTodo(it)
            }.singleOrNull()
        }

    override suspend fun deleteTodo(id: Int): Int =
        dbQuery {
            TodoTable.deleteWhere {
                TodoTable.id.eq(id)
            }
        }

    override suspend fun deleteAllTodo(userId: Int): Int =
        dbQuery {
            TodoTable.deleteWhere {
                TodoTable.userId.eq(userId)
            }
        }

    override suspend fun updateTodo(id: Int, todoData: String, done: Boolean): Int =
        dbQuery {
            TodoTable.update({TodoTable.id.eq(id)}){ todo->
                todo[TodoTable.todo] = todoData
                todo[TodoTable.done] = done
            }
        }

    private fun rowToTodo(row: ResultRow?): Todo?{
        if (row == null) {
            return null
        }
       return Todo(
            id = row[TodoTable.id],
            done = row[TodoTable.done],
            userId = row[TodoTable.userId],
            todo = row[TodoTable.todo]
        )
    }



}