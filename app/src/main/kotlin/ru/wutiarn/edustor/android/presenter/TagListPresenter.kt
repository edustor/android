package ru.wutiarn.edustor.android.presenter

import com.hannesdorfmann.mosby.mvp.MvpPresenter
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.data.models.Tag
import ru.wutiarn.edustor.android.util.extension.linkToLCEView
import ru.wutiarn.edustor.android.view.TagListView
import rx.Subscription

class TagListPresenter(val appComponent: AppComponent, val parentTagId: String?) : MvpPresenter<TagListView> {

    var view: TagListView? = null
    var tags: List<Tag>? = null

    var activeSubscription: Subscription? = null

    override fun detachView(p0: Boolean) {
        view = null
        appComponent.eventBus.unregister(this)
        activeSubscription?.unsubscribe()
    }

    override fun attachView(p0: TagListView?) {
        appComponent.eventBus.register(this)
        view = p0
    }

    fun loadData() {
        activeSubscription?.unsubscribe()
        activeSubscription = appComponent.repo.tag.byTagParentTagId(parentTagId)
                .linkToLCEView(view, { tags = it })
    }
}