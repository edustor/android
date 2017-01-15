package ru.wutiarn.edustor.android.presenter

import com.hannesdorfmann.mosby.mvp.MvpPresenter
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.data.models.Tag
import ru.wutiarn.edustor.android.util.extension.linkToLCEView
import ru.wutiarn.edustor.android.view.SubjectsListView
import rx.Subscription

class SubjectListPresenter(val appComponent: AppComponent) : MvpPresenter<SubjectsListView> {

    var view: SubjectsListView? = null
    var tags: List<Tag>? = null

    var activeSubscription: Subscription? = null

    override fun detachView(p0: Boolean) {
        view = null
        appComponent.eventBus.unregister(this)
        activeSubscription?.unsubscribe()
    }

    override fun attachView(p0: SubjectsListView?) {
        appComponent.eventBus.register(this)
        view = p0
    }

    fun loadData() {
        activeSubscription?.unsubscribe()
        activeSubscription = appComponent.repo.subjects.all
                .linkToLCEView(view, { tags = it })
    }
}