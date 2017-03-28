package ru.wutiarn.edustor.android.data.repo

import ru.wutiarn.edustor.android.data.models.Tag

interface TagRepo {
    val all: List<Tag>
    fun byParentTagId(parentTagId: String?): List<Tag>
    fun byId(id: String): Tag
}