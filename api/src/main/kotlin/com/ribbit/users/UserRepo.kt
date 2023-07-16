package com.ribbit.users

import com.ribbit.ribbitJson
import org.http4k.connect.amazon.dynamodb.DynamoDb
import org.http4k.connect.amazon.dynamodb.mapper.DynamoDbTableMapper
import org.http4k.connect.amazon.dynamodb.mapper.DynamoDbTableMapperSchema
import org.http4k.connect.amazon.dynamodb.mapper.tableMapper
import org.http4k.connect.amazon.dynamodb.model.Attribute
import org.http4k.connect.amazon.dynamodb.model.IndexName
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.connect.amazon.dynamodb.model.value

fun DynamoDb.usersTable(name: TableName) = tableMapper<User, EmailHash, Unit>(
    name,
    Attribute.value(EmailHash).required("emailHash"),
    null,
    ribbitJson
)

val userNamesIndex = DynamoDbTableMapperSchema.GlobalSecondary<Username, Unit>(
    IndexName.of("names"),
    Attribute.value(Username).required("name"),
    null
)

class UserRepo internal constructor(private val users: DynamoDbTableMapper<User, EmailHash, Unit>) {
    operator fun get(hash: EmailHash) = users[hash]
    operator fun get(name: Username) = users.index(userNamesIndex).query(name).firstOrNull()
    operator fun plusAssign(user: User) = users.save(user)
}