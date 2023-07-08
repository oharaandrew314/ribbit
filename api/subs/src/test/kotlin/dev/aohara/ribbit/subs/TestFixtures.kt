package dev.aohara.ribbit.subs

import com.ribbit.core.UserId
import com.ribbit.subs.Sub
import com.ribbit.subs.SubData
import com.ribbit.subs.SubId
import com.ribbit.subs.SubService
import dev.aohara.ribbit.valueOrThrow

fun SubService.create(
    creator: UserId,
    id: String,
    name: String = "sub$id"
): Sub {
    val data = SubData(
        id = SubId.of(id),
        name = name
    )

    return createSub(creator, data).valueOrThrow()
}