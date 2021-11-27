package com.example.data.table

import org.jetbrains.exposed.sql.Table

object TodoTable : Table() {

    val id = integer("id").autoIncrement()
    val userId = integer("userId").references(UserTable.userId)
    val todo = varchar("todo",512)
    val done = bool("done")

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}