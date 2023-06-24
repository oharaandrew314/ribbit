package com.ribbit.subs

import com.ribbit.core.RibbitError

data class SubNotFound(val id: SubId): RibbitError(404, "Sub $id not found")

data class DuplicateSub(val name: SubName): RibbitError(400, "Duplicate sub name: $name")

data class SubNameNotFound(val name: SubName): RibbitError(404, "Sub $name not found")