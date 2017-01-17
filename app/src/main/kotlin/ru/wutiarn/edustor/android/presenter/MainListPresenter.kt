package ru.wutiarn.edustor.android.presenter

import android.app.DatePickerDialog
import android.util.Log
import android.widget.DatePicker
import com.hannesdorfmann.mosby.mvp.MvpPresenter
import io.realm.Realm
import org.threeten.bp.LocalDate
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.data.models.MainListEntity
import ru.wutiarn.edustor.android.events.RequestSnackbarEvent
import ru.wutiarn.edustor.android.util.extension.linkToLCEView
import ru.wutiarn.edustor.android.view.MainListView
import rx.Subscription

class MainListPresenter(val appComponent: AppComponent, val parentTagId: String?) : MvpPresenter<MainListView>,
        DatePickerDialog.OnDateSetListener {
    var view: MainListView? = null
    var entities: List<MainListEntity>? = null

    var activeSubscription: Subscription? = null

    override fun detachView(p0: Boolean) {
        view = null
        appComponent.eventBus.unregister(this)
        activeSubscription?.unsubscribe()
    }

    override fun attachView(p0: MainListView?) {
        appComponent.eventBus.register(this)
        view = p0
    }

    fun loadData() {
        activeSubscription?.unsubscribe()
        activeSubscription = appComponent.repo.tag.byTagParentTagId(parentTagId)
                // TODO: Combine with lessons
                .map {
                    it.map {
                        @Suppress("USELESS_CAST")
                        it as MainListEntity // Cast is required since view accepts exactly List<MainListEntity>
                    }
                }
                .linkToLCEView(view, { entities = it })
    }

    fun onSyncSwitchChanged(b: Boolean) {
        val tagSyncStatus = appComponent.pdfSyncManager.getTagSyncStatus(parentTagId!!)

        Realm.getDefaultInstance().use {
            it.executeTransaction {
                tagSyncStatus.markedForSync = b
            }
        }
        appComponent.syncManager.requestSync(true, false)
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
        val date = LocalDate.of(year, month + 1, day)
        appComponent.repo.lessons.byDate(parentTagId!!, date.toEpochDay())
                .first()
                .subscribe(
                        { view?.onLessonClick(it) },
                        {
                            Log.w("LessonListPresenter", "Error in onDateSet", it)
                            appComponent.eventBus.post(RequestSnackbarEvent("Error: ${it.message}"))
                        }
                )    }
}