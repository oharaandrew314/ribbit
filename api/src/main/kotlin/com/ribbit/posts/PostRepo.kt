package com.ribbit.posts

import com.ribbit.core.Cursor
import com.ribbit.ribbitJson
import com.ribbit.subs.SubId
import com.ribbit.users.UserId
import org.http4k.connect.amazon.dynamodb.DynamoDb
import org.http4k.connect.amazon.dynamodb.mapper.DynamoDbTableMapper
import org.http4k.connect.amazon.dynamodb.mapper.DynamoDbTableMapperSchema
import org.http4k.connect.amazon.dynamodb.mapper.minusAssign
import org.http4k.connect.amazon.dynamodb.mapper.plusAssign
import org.http4k.connect.amazon.dynamodb.mapper.tableMapper
import org.http4k.connect.amazon.dynamodb.model.Attribute
import org.http4k.connect.amazon.dynamodb.model.IndexName
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.connect.amazon.dynamodb.model.value

private val primaryIndex = DynamoDbTableMapperSchema.Primary(
    hashKeyAttribute = Attribute.value(PostId).required("id")
)

private val subIndex = DynamoDbTableMapperSchema.GlobalSecondary(
    indexName = IndexName.of("sub"),
    hashKeyAttribute = Attribute.value(SubId).required("subId"),
    sortKeyAttribute = Attribute.value(PostId).required("id")
)

private val authorIndex =  DynamoDbTableMapperSchema.GlobalSecondary(
    indexName = IndexName.of("author"),
    hashKeyAttribute = Attribute.value(UserId).required("authorId"),
    sortKeyAttribute = Attribute.value(PostId).required("id")
)

fun DynamoDb.postsTable(name: TableName) = tableMapper<Post, PostId, Unit>(
    TableName = name,
    primarySchema = primaryIndex,
    autoMarshalling = ribbitJson
)

fun DynamoDbTableMapper<Post, PostId, Unit>.createWithIndices() = createTable(subIndex, authorIndex)

class PostRepo(
    private val table: DynamoDbTableMapper<Post, PostId, Unit>,
    private val pageSize: UInt
) {
    operator fun get(postId: PostId) = table[postId]

    operator fun get(subId: SubId, cursor: PostId? = null): Cursor<Post, PostId> {
        // TODO support cursor
        println(cursor)
        val items = table.index(subIndex).query(subId, scanIndexForward = false)
            .take(pageSize.toInt())
            .toList()

        return Cursor(
            items = items,
            next = null,
            getPage = { get(subId, it) }
        )
    }

    operator fun get(authorId: UserId, cursor: PostId? = null): Cursor<Post, PostId> {
        // TODO support cursor
        println(cursor)
        val items = table.index(authorIndex).query(authorId, scanIndexForward = false)
            .take(pageSize.toInt())
            .toList()

        return Cursor(
            items = items,
            next = null,
            getPage = { get(authorId, it) }
        )
    }
    operator fun plusAssign(post: Post) = table.plusAssign(post)
    operator fun minusAssign(post: Post) = table.minusAssign(post)
}