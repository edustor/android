package ru.wutiarn.edustor.android.presenter

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.DatePicker
import com.hannesdorfmann.mosby.mvp.MvpPresenter
import com.squareup.otto.Subscribe
import org.threeten.bp.LocalDate
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.events.RealmSyncFinishedEvent
import ru.wutiarn.edustor.android.events.RequestSnackbarEvent
import ru.wutiarn.edustor.android.util.extension.linkToLCEView
import ru.wutiarn.edustor.android.view.LessonsListView
import rx.Subscription

class LessonListPresenter(val appComponent: AppComponent, arguments: Bundle?) : MvpPresenter<LessonsListView>,
        DatePickerDialog.OnDateSetListener {

    var subjectId: String? = null

    var view: LessonsListView? = null
    var lessons: List<Lesson> = emptyList()

    var activeSubscription: Subscription? = null


    init {
        subjectId = arguments?.getString("subject_id")
    }

    override fun detachView(p0: Boolean) {
        view = null
        appComponent.eventBus.unregister(this)
        activeSubscription?.unsubscribe()
    }

    override fun attachView(p0: LessonsListView?) {
        appComponent.eventBus.register(this)
        view = p0
    }

    @Subscribe fun onSyncFinished(event: RealmSyncFinishedEvent) {
        loadData()
    }

    fun loadData() {
        activeSubscription?.unsubscribe()
        activeSubscription = appComponent.repo.lessons.bySubjectId(subjectId!!)
                .map { it.sortedByDescending { it.date } }
                .linkToLCEView(view, { lessons = it })
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
        val date = LocalDate.of(year, month + 1, day)
        appComponent.repo.lessons.byDate(subjectId!!, date.toEpochDay())
                .subscribe(
                        { view?.onLessonClick(it) },
                        { appComponent.eventBus.post(RequestSnackbarEvent("Error: ${it.message}")) }
                )
    }
}