package com.ribbit.subs

import com.ribbit.subs.api.subsJson
import org.http4k.connect.amazon.dynamodb.DynamoDb
import org.http4k.connect.amazon.dynamodb.mapper.DynamoDbTableMapper
import org.http4k.connect.amazon.dynamodb.mapper.tableMapper
import org.http4k.connect.amazon.dynamodb.model.Attribute
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.connect.amazon.dynamodb.model.value

internal fun DynamoDb.subsTable(name: TableName) = tableMapper<Sub, SubId, Unit>(name, Attribute.value(SubId).required("id"), null, subsJson)

internal class SubRepo(private val table: DynamoDbTableMapper<Sub, SubId, Unit>): Iterable<Sub> {
    operator fun get(id: SubId) = table[id]
    override fun iterator() = table.primaryIndex().scan().iterator()
    operator fun plusAssign(sub: Sub) = table.save(sub)
    operator fun minusAssign(sub: Sub) = table.delete(sub)
}