package ru.wutiarn.edustor.android.view

import com.hannesdorfmann.mosby.mvp.lce.MvpLceView
import ru.wutiarn.edustor.android.data.models.Lesson

interface LessonsListView : MvpLceView<List<Lesson>> {
    fun onLessonClick(lesson: Lesson)
}