package com.ribbit.users

import com.ribbit.core.User
import com.ribbit.core.UserId
import com.ribbit.users.api.usersJson
import org.http4k.connect.amazon.dynamodb.DynamoDb
import org.http4k.connect.amazon.dynamodb.mapper.DynamoDbTableMapper
import org.http4k.connect.amazon.dynamodb.mapper.tableMapper
import org.http4k.connect.amazon.dynamodb.model.Attribute
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.connect.amazon.dynamodb.model.value

internal val userIdAttr = Attribute.value(UserId).required("id")

class UserRepo internal constructor(private val users: DynamoDbTableMapper<User, UserId, Unit>) {
    operator fun get(id: UserId) = users[id]
    operator fun plusAssign(user: User) = users.save(user)
    operator fun minusAssign(user: User) = users.delete(user)
}

fun userRepo(dynamoDb: DynamoDb, name: TableName) = UserRepo(
    dynamoDb.tableMapper<User, UserId, Unit>(name, userIdAttr, null, autoMarshalling = usersJson)
)