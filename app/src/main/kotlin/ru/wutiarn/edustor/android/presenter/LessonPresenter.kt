package ru.wutiarn.edustor.android.presenter

import android.os.Bundle
import com.hannesdorfmann.mosby.mvp.MvpPresenter
import com.squareup.otto.Subscribe
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.data.adapter.DocumentsAdapter
import ru.wutiarn.edustor.android.data.models.Document
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.events.DocumentMovedEvent
import ru.wutiarn.edustor.android.events.NewDocumentQrCodeScanned
import ru.wutiarn.edustor.android.events.RequestSnackbarEvent
import ru.wutiarn.edustor.android.util.extension.configureAsync
import ru.wutiarn.edustor.android.util.extension.linkToLCEView
import ru.wutiarn.edustor.android.view.LessonView
import rx.subscriptions.CompositeSubscription

/**
 * Created by wutiarn on 05.03.16.
 */
class LessonPresenter(val appComponent: AppComponent, val arguments: Bundle) : MvpPresenter<LessonView>, DocumentsAdapter.EventListener {

    var view: LessonView? = null
    var subscriptions: CompositeSubscription = CompositeSubscription()

    var isSecondary: Boolean = false // true if located in bottom panel
    var uuid: String? = null
    var lessonId: String? = null

    var lesson: Lesson? = null

    init {
        isSecondary = arguments.getBoolean("isSecondary")
        uuid = arguments.getString("uuid")
        lessonId = arguments.getString("id")
    }

    override fun detachView(p0: Boolean) {

        appComponent.eventBus.unregister(this)
        subscriptions.clear()
        view = null
    }

    override fun attachView(p0: LessonView?) {
        appComponent.eventBus.register(this)
        view = p0
    }

    fun loadData() {
        when {
            uuid != null -> {
                subscriptions.add(
                        appComponent.lessonsApi.byUUID(uuid!!)
                                .linkToLCEView(view, { lesson = it })
                )
            }
            lessonId == "current" -> {
                subscriptions.add(
                        appComponent.lessonsApi.current()
                                .linkToLCEView(view, { lesson = it })
                )
            }
            lessonId != null -> {
                subscriptions.add(
                        appComponent.lessonsApi.byId(lessonId!!)
                                .linkToLCEView(view, { lesson = it })
                )
            }
        }
    }

    @Subscribe fun onQrCodeScanned(event: NewDocumentQrCodeScanned) {
        if (isSecondary) {
            val uuid = event.string

            val lid = lesson?.id ?: lessonId

            if (lid == null) {
                appComponent.eventBus.post(RequestSnackbarEvent("Error: lesson id is not found")); return
            }

            appComponent.documentsApi.activateUUID(uuid, lid).configureAsync().subscribe({
                appComponent.eventBus.post(RequestSnackbarEvent("Done! ID: ${it.id}"))
            }, {
                appComponent.eventBus.post(RequestSnackbarEvent("Error: ${it.message}"))
            })
        }
    }

    @Subscribe fun onLessonDocumentMoved(event: DocumentMovedEvent) {
        if ((lesson?.id != null) and (event.lesson.id == lesson?.id)) {
            val documents = this.lesson?.documents
            documents?.let {
                documents.remove(event.document)
                val targetIndex = if (event.after != null) documents.indexOf(event.after) + 1 else 0
                documents.add(targetIndex, event.document)
                view?.notifyDocumentsUpdated(event)
            }
        }
    }

    override fun onDocumentRemoved(document: Document) {
        appComponent.documentsApi.delete(document.id!!)
                .configureAsync().subscribe(
                { appComponent.eventBus.post(RequestSnackbarEvent("Successfully removed: ${document.shortUUID}")) },
                { appComponent.eventBus.post(RequestSnackbarEvent("Error removing ${document.shortUUID}: ${it.message}")) }
        )
    }
}