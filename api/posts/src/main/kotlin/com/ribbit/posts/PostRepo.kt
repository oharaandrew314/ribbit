package com.ribbit.posts

import com.ribbit.subs.SubId
import com.ribbit.users.UserId

internal class PostRepo {
    private val posts = mutableMapOf<PostId, Post>()

    operator fun get(id: PostId) = posts[id]

    operator fun get(id: UserId) = posts.filter { it.value.author == id }.map { it.value }

    operator fun get(id: SubId) = posts.filter { it.value.sub == id }.map { it.value }

    operator fun plusAssign(post: Post) {
        posts[post.id] = post
    }

    operator fun minusAssign(post: Post) {
        posts.remove(post.id)
    }
}