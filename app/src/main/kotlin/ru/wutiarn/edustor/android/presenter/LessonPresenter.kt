package ru.wutiarn.edustor.android.presenter

import android.os.Bundle
import com.hannesdorfmann.mosby.mvp.MvpPresenter
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.data.adapter.DocumentsAdapter
import ru.wutiarn.edustor.android.data.models.Document
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

    var uuid: String? = null
    var lessonId: String? = null

    init {
        uuid = arguments.getString("uuid")
        lessonId = arguments.getString("id")
    }

    override fun detachView(p0: Boolean) {
        subscriptions.clear()
        view = null
    }

    override fun attachView(p0: LessonView?) {
        view = p0
    }

    fun loadData() {
        when {
            uuid != null -> {
                subscriptions.add(
                        appComponent.lessonsApi.byUUID(uuid!!)
                                .linkToLCEView(view))
            }
            lessonId == "current" -> {
                subscriptions.add(
                        appComponent.lessonsApi.current()
                                .linkToLCEView(view)
                )
            }
            lessonId != null -> {
                subscriptions.add(
                        appComponent.lessonsApi.byId(lessonId!!)
                                .linkToLCEView(view)
                )
            }
        }
    }

    override fun onDocumentRemoved(document: Document) {
        appComponent.documentsApi.delete(document.id!!)
                .configureAsync().subscribe(
                { view?.makeSnackbar("Successfully removed: ${document.shortUUID}") },
                { view?.makeSnackbar("Error removing ${document.shortUUID}: ${it.message}") }
        )
    }
}