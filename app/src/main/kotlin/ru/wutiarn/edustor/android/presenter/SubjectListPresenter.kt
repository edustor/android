package ru.wutiarn.edustor.android.presenter

import com.hannesdorfmann.mosby.mvp.MvpPresenter
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.data.models.Subject
import ru.wutiarn.edustor.android.util.extension.configureAsync
import ru.wutiarn.edustor.android.util.extension.linkToLCEView
import ru.wutiarn.edustor.android.view.SubjectsListView

class SubjectListPresenter(val appComponent: AppComponent) : MvpPresenter<SubjectsListView> {

    var view: SubjectsListView? = null
    var subjects: List<Subject>? = null

    override fun detachView(p0: Boolean) {
        view = null
    }

    override fun attachView(p0: SubjectsListView?) {
        view = p0
    }

    fun loadData() {
        appComponent.subjectsApi.list()
                .configureAsync()
                .linkToLCEView(view, { subjects = it })
    }
}