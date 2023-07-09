package com.ribbit.posts

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

class PostRepo(private val table: DynamoDbTableMapper<Post, PostId, Unit>) {
    operator fun get(postId: PostId) = table[postId]
    operator fun get(subId: SubId) = table.index(subIndex).query(subId)
    operator fun get(authorId: UserId) = table.index(authorIndex).query(authorId)
    operator fun plusAssign(post: Post) = table.plusAssign(post)
    operator fun minusAssign(post: Post) = table.minusAssign(post)
}