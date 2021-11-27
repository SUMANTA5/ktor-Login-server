package com.example.data.table

import org.jetbrains.exposed.sql.Table

object UserTable: Table() {
    val userId = integer("userId").autoIncrement()
    val email = varchar("email",512).uniqueIndex()
    val name = varchar("name",512)
    val password = varchar("password",512)

    override val primaryKey: PrimaryKey = PrimaryKey(userId)
}