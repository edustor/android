package ru.wutiarn.edustor.android.presenter

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import com.hannesdorfmann.mosby.mvp.MvpPresenter
import io.realm.Realm
import org.threeten.bp.LocalDate
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.events.RequestSnackbarEvent
import ru.wutiarn.edustor.android.util.extension.linkToLCEView
import ru.wutiarn.edustor.android.view.LessonsListView
import rx.Subscription

class LessonListPresenter(val appComponent: AppComponent, arguments: Bundle) : MvpPresenter<LessonsListView>,
        DatePickerDialog.OnDateSetListener {

    var subjectId: String = arguments.getString("subject_id")

    var view: LessonsListView? = null
    var lessons: List<Lesson> = emptyList()

    var activeSubscription: Subscription? = null


    override fun detachView(p0: Boolean) {
        view = null
        appComponent.eventBus.unregister(this)
        activeSubscription?.unsubscribe()
    }

    override fun attachView(p0: LessonsListView?) {
        appComponent.eventBus.register(this)
        view = p0
    }

    fun onSyncSwitchChanged(b: Boolean) {
        val subjectSyncStatus = appComponent.pdfSyncManager.getSubjectSyncStatus(subjectId)

        Realm.getDefaultInstance().use {
            it.executeTransaction {
                subjectSyncStatus.markedForSync = b
            }
        }
        appComponent.syncManager.requestSync(true, false)
    }

    fun loadData() {
        activeSubscription?.unsubscribe()
        activeSubscription = appComponent.repo.lessons.bySubjectId(subjectId)
                .map { it.filter { it.pages.count() > 0 }.sortedByDescending { it.date } }
                .linkToLCEView(view, { lessons = it })
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
        val date = LocalDate.of(year, month + 1, day)
        appComponent.repo.lessons.byDate(subjectId, date.toEpochDay())
                .first()
                .subscribe(
                        { view?.onLessonClick(it) },
                        {
                            Log.w("LessonListPresenter", "Error in onDateSet", it)
                            appComponent.eventBus.post(RequestSnackbarEvent("Error: ${it.message}"))
                        }
                )
    }
}