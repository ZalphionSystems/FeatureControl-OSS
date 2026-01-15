package com.zalphion.featurecontrol.lib

import dev.andrewohara.utils.pagination.Page
import dev.forkhandles.values.StringValue

fun <Item: Any, Cursor: Any> List<Item>.toPage(
    pageSize: Int, cursorFn: (Item) -> Cursor
) = Page(
    items = take(pageSize),
    next = if (size <= pageSize) null else get(pageSize - 1).let(cursorFn),
)