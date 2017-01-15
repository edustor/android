package ru.wutiarn.edustor.android.data.models

class FullSyncData {
    lateinit var account: Account.AccountDTO
    lateinit var tags: List<Tag.TagDTO>
    lateinit var lessons: List<Lesson.LessonDTO>
}