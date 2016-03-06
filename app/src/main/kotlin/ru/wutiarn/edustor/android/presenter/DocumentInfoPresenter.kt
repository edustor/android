package ru.wutiarn.edustor.android.presenter

import com.hannesdorfmann.mosby.mvp.MvpPresenter
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.util.extension.linkToLCEView
import ru.wutiarn.edustor.android.view.DocumentInfoView
import java.util.concurrent.TimeUnit

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
                .delay(1, TimeUnit.SECONDS)
                .linkToLCEView(view)
    }
}