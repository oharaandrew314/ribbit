package com.ribbit.subs

import com.ribbit.core.Cursor
import com.ribbit.ribbitJson
import org.http4k.connect.amazon.dynamodb.DynamoDb
import org.http4k.connect.amazon.dynamodb.mapper.DynamoDbTableMapper
import org.http4k.connect.amazon.dynamodb.mapper.tableMapper
import org.http4k.connect.amazon.dynamodb.model.Attribute
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.connect.amazon.dynamodb.model.value

fun DynamoDb.subsTable(name: TableName) = tableMapper<Sub, SubId, Unit>(name, Attribute.value(SubId).required("id"), null, ribbitJson)

class SubRepo(private val table: DynamoDbTableMapper<Sub, SubId, Unit>) {
    operator fun get(id: SubId) = table[id]
    operator fun plusAssign(sub: Sub) = table.save(sub)
    operator fun minusAssign(sub: Sub) = table.delete(sub)

    fun list(limit: Int, cursor: SubId? = null): Cursor<Sub, SubId> {
        val page = table.primaryIndex().scanPage(
            Limit = limit,
            ExclusiveStartKey = cursor?.let { it to Unit }
        )

        return Cursor(
            items = page.items,
            next = null,
            getPage = { list(limit, it) }
        )
    }
}