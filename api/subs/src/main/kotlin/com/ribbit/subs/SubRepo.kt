package com.ribbit.subs

internal class SubRepo: Iterable<Sub> {
    private val subs = mutableMapOf<SubId, Sub>()

    operator fun get(id: SubId) = subs[id]

    override fun iterator() = subs.values.iterator()

    operator fun plusAssign(sub: Sub) {
        subs[sub.id] = sub
    }

    operator fun minusAssign(sub: Sub) {
        subs.remove(sub.id)
    }
}