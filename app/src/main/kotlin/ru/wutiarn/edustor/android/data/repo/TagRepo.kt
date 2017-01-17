package ru.wutiarn.edustor.android.data.repo

import ru.wutiarn.edustor.android.data.models.Tag
import rx.Observable

interface TagRepo {
    val all: Observable<List<Tag>>
    fun byTagParentTagId(parentTagId: String?): Observable<List<Tag>>
}