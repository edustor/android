package ru.wutiarn.edustor.android.view

import com.hannesdorfmann.mosby.mvp.lce.MvpLceView
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.events.DocumentChangedEvent

/**
 * Created by wutiarn on 05.03.16.
 */
interface LessonView : MvpLceView<Lesson> {
    fun notifyDocumentsChanged(event: DocumentChangedEvent)
}