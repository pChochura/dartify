package com.pointlessapps.dartify.compose.utils

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

internal fun <T> emptyImmutableList(): ImmutableList<T> = emptyList<T>().toImmutableList()
