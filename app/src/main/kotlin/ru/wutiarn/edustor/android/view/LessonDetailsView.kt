package ru.wutiarn.edustor.android.view

import com.hannesdorfmann.mosby.mvp.lce.MvpLceView
import ru.wutiarn.edustor.android.data.models.Lesson

interface LessonDetailsView : MvpLceView<Lesson> {
    fun setPdfSyncStatus(status: String)
}