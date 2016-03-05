package ru.wutiarn.edustor.android.presenter

import com.hannesdorfmann.mosby.mvp.MvpPresenter
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.view.DocumentInfoView
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by wutiarn on 05.03.16.
 */
class DocumentInfoPresenter(val appComponent: AppComponent, val uuid: String) : MvpPresenter<DocumentInfoView> {

    var view: DocumentInfoView? = null

    override fun detachView(p0: Boolean) {
        view = null
    }

    override fun attachView(p0: DocumentInfoView?) {
        view = p0
    }

    fun loadData() {
        appComponent.documentsRepository.documentsApi.UUIDInfo(uuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view?.setData(it) }
    }
}