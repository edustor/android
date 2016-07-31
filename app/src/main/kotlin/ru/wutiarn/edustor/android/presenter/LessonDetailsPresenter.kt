package ru.wutiarn.edustor.android.presenter

import android.os.Bundle
import android.util.Log
import com.hannesdorfmann.mosby.mvp.MvpPresenter
import com.squareup.otto.Subscribe
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.events.RealmSyncFinishedEvent
import ru.wutiarn.edustor.android.events.RequestSnackbarEvent
import ru.wutiarn.edustor.android.util.extension.linkToLCEView
import ru.wutiarn.edustor.android.util.extension.setUpSyncState
import ru.wutiarn.edustor.android.view.LessonDetailsView
import rx.Subscription

class LessonDetailsPresenter(val appComponent: AppComponent, arguments: Bundle) : MvpPresenter<LessonDetailsView> {

    var view: LessonDetailsView? = null
    var activeSubscription: Subscription? = null

    var uuid: String? = null
    var subjectId: String? = null
    var lessonEpochDay: Long? = null

    var lesson: Lesson? = null

    init {
        uuid = arguments.getString("uuid")
        subjectId = arguments.getString("subject")
        lessonEpochDay = arguments.getLong("date")
    }

    override fun detachView(p0: Boolean) {
        appComponent.eventBus.unregister(this)
        activeSubscription?.unsubscribe()
        view = null
    }

    override fun attachView(p0: LessonDetailsView?) {
        appComponent.eventBus.register(this)
        view = p0
    }

    @Subscribe fun onSyncFinished(event: RealmSyncFinishedEvent) {
        loadData()
    }

    fun loadData() {
        activeSubscription?.unsubscribe()
        when {
            uuid != null -> {
                activeSubscription = appComponent.repo.lessons.byUUID(uuid!!)
                        .setUpSyncState(appComponent.pdfSyncManager)
                        .linkToLCEView(view, { lesson = it })
            }
            subjectId != null -> {
                activeSubscription =
                        appComponent.repo.lessons.byDate(subjectId!!, lessonEpochDay!!)
                                .setUpSyncState(appComponent.pdfSyncManager)
                                .linkToLCEView(view, { lesson = it })
            }
        }
    }

    fun setTopic(topic: String) {
        lesson?.let {
            appComponent.repo.lessons.setTopic(lesson?.id!!, topic)
                    .subscribe(
                            { appComponent.eventBus.post(RequestSnackbarEvent("Successfully renamed")) },
                            { appComponent.eventBus.post(RequestSnackbarEvent("Error: ${it.message}")) }
                    )
        }
    }

    fun onQrCodeScanned(uuid: String) {

        if (lesson == null) {
            appComponent.eventBus.post(RequestSnackbarEvent("Error: lesson id is not found")); return
        }

        appComponent.repo.documents.activateUUID(uuid, lesson?.id!!).subscribe({
            appComponent.eventBus.post(RequestSnackbarEvent("Done ${it.shortUUID}! ID: ${it.id}"))
        }, {
            Log.w("LoginPresenter", "Error while creating document", it)
            appComponent.eventBus.post(RequestSnackbarEvent("Error: ${it.message}"))
        })
    }
}