package com.ribbit.posts

import com.github.ksuid.Ksuid
import io.andrewohara.utils.ksuid.KsuidValue
import io.andrewohara.utils.ksuid.KsuidValueFactory

class PostId(value: Ksuid): KsuidValue(value) {
    companion object: KsuidValueFactory<PostId>(::PostId)
}