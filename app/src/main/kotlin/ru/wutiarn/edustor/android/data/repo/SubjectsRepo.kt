package ru.wutiarn.edustor.android.data.repo

import ru.wutiarn.edustor.android.data.models.Tag
import rx.Observable

interface SubjectsRepo {
    val all: Observable<List<Tag>>
}