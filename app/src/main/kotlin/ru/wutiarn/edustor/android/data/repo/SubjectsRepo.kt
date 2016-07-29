package ru.wutiarn.edustor.android.data.repo

import ru.wutiarn.edustor.android.data.models.Subject
import rx.Observable

interface SubjectsRepo {
    val all: Observable<List<Subject>>
}