package ru.wutiarn.edustor.android.presenter

import android.os.Bundle
import com.hannesdorfmann.mosby.mvp.MvpPresenter
import com.squareup.otto.Bus
import com.squareup.otto.Subscribe
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.events.DocumentRemovedEvent
import ru.wutiarn.edustor.android.util.extension.linkToLCEView
import ru.wutiarn.edustor.android.view.LessonView
import rx.subscriptions.CompositeSubscription

/**
 * Created by wutiarn on 05.03.16.
 */
class LessonPresenter(val appComponent: AppComponent, val arguments: Bundle, var bus: Bus) : MvpPresenter<LessonView> {

    var view: LessonView? = null
    var subscriptions: CompositeSubscription = CompositeSubscription()

    var uuid: String? = null
    var lessonId: String? = null

    init {
        bus.register(this)

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

    @Subscribe fun onDocumentRemoved(event: DocumentRemovedEvent) {
        val document = event.document
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
}