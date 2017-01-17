package ru.wutiarn.edustor.android.view

import com.hannesdorfmann.mosby.mvp.lce.MvpLceView
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.data.models.MainListEntity
import ru.wutiarn.edustor.android.data.models.Tag

interface MainListView : MvpLceView<List<MainListEntity>> {
    fun onLessonClick(lesson: Lesson)
}