package ru.wutiarn.edustor.android.presenter

import android.os.Bundle
import com.hannesdorfmann.mosby.mvp.MvpPresenter
import com.squareup.otto.Subscribe
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.events.DocumentAddedEvent
import ru.wutiarn.edustor.android.events.DocumentMovedEvent
import ru.wutiarn.edustor.android.events.DocumentRemovedEvent
import ru.wutiarn.edustor.android.events.RequestSnackbarEvent
import ru.wutiarn.edustor.android.util.extension.configureAsync
import ru.wutiarn.edustor.android.util.extension.linkToLCEView
import ru.wutiarn.edustor.android.view.LessonDetailsView
import rx.subscriptions.CompositeSubscription

class LessonPresenter(val appComponent: AppComponent, arguments: Bundle) : MvpPresenter<LessonDetailsView> {

    var view: LessonDetailsView? = null
    var subscriptions: CompositeSubscription = CompositeSubscription()

    var uuid: String? = null
    var lessonId: String? = null

    var lesson: Lesson? = null

    init {
        uuid = arguments.getString("uuid")
        lessonId = arguments.getString("id")
    }

    override fun detachView(p0: Boolean) {

        appComponent.eventBus.unregister(this)
        subscriptions.clear()
        view = null
    }

    override fun attachView(p0: LessonDetailsView?) {
        appComponent.eventBus.register(this)
        view = p0
    }

    fun loadData() {
        when {
            uuid != null -> {
                subscriptions.add(
                        appComponent.lessonsRepo.byUUID(uuid!!)
                                .linkToLCEView(view, { lesson = it })
                )
            }
            lessonId != null -> {
                subscriptions.add(
                        appComponent.lessonsRepo.byId(lessonId!!)
                                .linkToLCEView(view, { lesson = it })
                )
            }
        }
    }

    fun setTopic(topic: String) {
        lesson?.let {
            appComponent.lessonsRepo.setTopic(lesson?.id!!, topic)
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

        appComponent.documentsApi.activateUUID(uuid, lesson?.id!!).configureAsync().subscribe({
            appComponent.eventBus.post(RequestSnackbarEvent("Done ${it.shortUUID}! ID: ${it.id}"))
            appComponent.eventBus.post(DocumentAddedEvent(lesson!!, document = it))
        }, {
            appComponent.eventBus.post(RequestSnackbarEvent("Error: ${it.message}"))
        })
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
            lesson?.documents?.remove(event.document)
            view?.notifyDocumentsChanged(event)
        }
    }

    @Subscribe fun onDocumentAdded(event: DocumentAddedEvent) {
        lesson?.documents?.add(event.document)
        view?.notifyDocumentsChanged(event.copy(insertedPosition = lesson?.documents?.lastIndex!!))
    }
}