package ru.wutiarn.edustor.android.view

import com.hannesdorfmann.mosby.mvp.lce.MvpLceView
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.data.models.MainListEntity

interface MainListView : MvpLceView<List<MainListEntity>> {
    fun onLessonClick(lesson: Lesson)
    fun setTitle(title: String)
}