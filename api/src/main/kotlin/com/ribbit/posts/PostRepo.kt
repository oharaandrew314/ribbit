package com.ribbit.posts

import com.ribbit.core.Cursor
import com.ribbit.ribbitJson
import com.ribbit.subs.SubId
import com.ribbit.users.Username
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

val postsSubIndex = DynamoDbTableMapperSchema.GlobalSecondary(
    indexName = IndexName.of("sub"),
    hashKeyAttribute = Attribute.value(SubId).required("subId"),
    sortKeyAttribute = Attribute.value(PostId).required("id")
)

val postsAuthorIndex =  DynamoDbTableMapperSchema.GlobalSecondary(
    indexName = IndexName.of("author"),
    hashKeyAttribute = Attribute.value(Username).required("authorName"),
    sortKeyAttribute = Attribute.value(PostId).required("id")
)

fun DynamoDb.postsTable(name: TableName) = tableMapper<Post, PostId, Unit>(
    tableName = name,
    primarySchema = primaryIndex,
    autoMarshalling = ribbitJson
)

class PostRepo(private val table: DynamoDbTableMapper<Post, PostId, Unit>) {
    operator fun get(postId: PostId) = table[postId]

    operator fun get(subId: SubId, limit: Int, cursor: PostId? = null): Cursor<Post, PostId> {
        val page = table.index(postsSubIndex).queryPage(
            HashKey = subId,
            ScanIndexForward = false,
            Limit = limit,
            ExclusiveStartKey = cursor
        )

        return Cursor(
            items = page.items,
            next = page.nextSortKey
        )
    }

    operator fun get(author: Username, limit: Int, cursor: PostId? = null): Cursor<Post, PostId> {
        val page = table.index(postsAuthorIndex).queryPage(
            HashKey = author,
            ScanIndexForward = false,
            Limit = limit,
            ExclusiveStartKey = cursor
        )

        return Cursor(
            items = page.items,
            next = page.nextSortKey
        )
    }
    operator fun plusAssign(post: Post) = table.plusAssign(post)
    operator fun minusAssign(post: Post) = table.minusAssign(post)
}