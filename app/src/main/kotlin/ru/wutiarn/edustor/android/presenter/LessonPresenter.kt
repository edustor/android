package ru.wutiarn.edustor.android.presenter

import android.os.Bundle
import com.hannesdorfmann.mosby.mvp.MvpPresenter
import com.squareup.otto.Subscribe
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.events.*
import ru.wutiarn.edustor.android.util.extension.configureAsync
import ru.wutiarn.edustor.android.util.extension.linkToLCEView
import ru.wutiarn.edustor.android.view.LessonView
import rx.subscriptions.CompositeSubscription

/**
 * Created by wutiarn on 05.03.16.
 */
class LessonPresenter(val appComponent: AppComponent, val arguments: Bundle) : MvpPresenter<LessonView> {

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

    fun setTopic(topic: String) {
        lesson?.let {
            lesson?.topic = topic
            appComponent.lessonsApi.setTopic(lesson?.id!!, topic)
                    .configureAsync()
                    .subscribe(
                            { appComponent.eventBus.post(RequestSnackbarEvent("Successfully renamed")) },
                            { appComponent.eventBus.post(RequestSnackbarEvent("Error: ${it.message}")) }
                    )
        }
    }

    @Subscribe fun onQrCodeScanned(event: NewDocumentQrCodeScanned) {
        if (isSecondary == event.shouldBeHandledBySecondaryFragment) {
            val uuid = event.string

            if (lesson == null) {
                appComponent.eventBus.post(RequestSnackbarEvent("Error: lesson id is not found")); return
            }

            appComponent.documentsApi.activateUUID(uuid, lesson?.id!!).configureAsync().subscribe({
                appComponent.eventBus.post(RequestSnackbarEvent("Done ${it.shortUUID}! ID: ${it.id}"))
                appComponent.eventBus.post(DocumentAddedEvent(lesson!!, document = it))
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
                view?.notifyDocumentsChanged(event)
            }
        }
    }

    @Subscribe fun onDocumentRemoved(event: DocumentRemovedEvent) {
        lesson?.let {
            val removed = lesson?.documents?.remove(event.document)
            if (removed == true) {
                view?.notifyDocumentsChanged(event)
            }
        }
    }

    @Subscribe fun onDocumentAdded(event: DocumentAddedEvent) {
        lesson?.documents?.add(event.document.copy())
        view?.notifyDocumentsChanged(event.copy(insertedPosition = lesson?.documents?.lastIndex!!))
    }
}