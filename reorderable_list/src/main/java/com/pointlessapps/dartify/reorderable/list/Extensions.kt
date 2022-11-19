package com.pointlessapps.dartify.reorderable.list

import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState

internal fun LazyListState.getVisibleItemInfoFor(index: Int?) =
    layoutInfo.visibleItemsInfo.firstOrNull { it.index == index }

internal fun LazyListState.getVisibleItemIndexFor(key: Any?) =
    layoutInfo.visibleItemsInfo.firstOrNull { it.key == key }?.index

internal val LazyListItemInfo.offsetEnd: Int
    get() = this.offset + this.size
