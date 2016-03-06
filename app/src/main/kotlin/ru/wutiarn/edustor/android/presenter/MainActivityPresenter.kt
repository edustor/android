package ru.wutiarn.edustor.android.presenter

import android.os.Bundle
import com.hannesdorfmann.mosby.mvp.MvpPresenter
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.fragment.LessonFragment
import ru.wutiarn.edustor.android.util.extension.linkToLCEView
import ru.wutiarn.edustor.android.view.MainActivityView
import rx.subscriptions.CompositeSubscription

/**
 * Created by wutiarn on 06.03.16.
 */
class MainActivityPresenter(val appComponent: AppComponent) : MvpPresenter<MainActivityView> {
    private var view: MainActivityView? = null
    var subscriptions: CompositeSubscription = CompositeSubscription()

    override fun attachView(p0: MainActivityView?) {
        view = p0
    }

    override fun detachView(p0: Boolean) {
        view = null
        subscriptions.clear()
    }

    fun loadData() {
        val subscription = appComponent.lessonsApi.current()
                .linkToLCEView(view)

        subscriptions.add(subscription)
    }

    fun showLessonInfo(uuid: String) {
        val documentInfoFragment = LessonFragment()
        val fragmentBundle = Bundle()
        fragmentBundle.putString("uuid", uuid)
        documentInfoFragment.arguments = fragmentBundle

        view?.showSlidingPanelFragment(documentInfoFragment)
    }
}