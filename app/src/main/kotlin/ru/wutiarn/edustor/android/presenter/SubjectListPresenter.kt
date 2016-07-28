package ru.wutiarn.edustor.android.presenter

import com.hannesdorfmann.mosby.mvp.MvpPresenter
import com.squareup.otto.Subscribe
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.data.models.Subject
import ru.wutiarn.edustor.android.events.RealmSyncFinishedEvent
import ru.wutiarn.edustor.android.util.extension.linkToLCEView
import ru.wutiarn.edustor.android.view.SubjectsListView
import rx.Subscription

class SubjectListPresenter(val appComponent: AppComponent) : MvpPresenter<SubjectsListView> {

    var view: SubjectsListView? = null
    var subjects: List<Subject>? = null

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

    @Subscribe fun onSyncFinished(event: RealmSyncFinishedEvent) {
        loadData()
    }

    fun loadData() {
        activeSubscription?.unsubscribe()
        activeSubscription = appComponent.repo.subjects.all
                .linkToLCEView(view, { subjects = it })
    }
}