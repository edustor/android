package ru.wutiarn.edustor.android.presenter

import com.hannesdorfmann.mosby.mvp.MvpPresenter
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.util.extension.linkToLCEView
import ru.wutiarn.edustor.android.view.LessonView
import rx.subscriptions.CompositeSubscription
import java.util.concurrent.TimeUnit

/**
 * Created by wutiarn on 05.03.16.
 */
class LessonPresenter(val appComponent: AppComponent, val uuid: String? = null, val id: String? = null) : MvpPresenter<LessonView> {

    var view: LessonView? = null
    var subscriptions: CompositeSubscription = CompositeSubscription()

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
                val subscription = appComponent.lessonsApi.byUUID(uuid)
                        .delay(1, TimeUnit.SECONDS)
                        .linkToLCEView(view)

                subscriptions.add(subscription)
            }
        }
    }
}