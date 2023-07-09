package com.ribbit.users

import com.ribbit.ribbitJson
import org.http4k.connect.amazon.dynamodb.DynamoDb
import org.http4k.connect.amazon.dynamodb.mapper.DynamoDbTableMapper
import org.http4k.connect.amazon.dynamodb.mapper.tableMapper
import org.http4k.connect.amazon.dynamodb.model.Attribute
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.connect.amazon.dynamodb.model.value

fun DynamoDb.usersTable(name: TableName) = tableMapper<User, UserId, Unit>(name, Attribute.value(UserId).required("id"), null, ribbitJson)

class UserRepo internal constructor(private val users: DynamoDbTableMapper<User, UserId, Unit>) {
    operator fun get(id: UserId) = users[id]
    operator fun plusAssign(user: User) = users.save(user)
    operator fun minusAssign(user: User) = users.delete(user)
}