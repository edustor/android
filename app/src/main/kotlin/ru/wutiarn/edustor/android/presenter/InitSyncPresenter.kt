package ru.wutiarn.edustor.android.presenter

import com.hannesdorfmann.mosby.mvp.MvpPresenter
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.view.InitScreenView
import ru.wutiarn.edustor.android.view.LoginView

class InitSyncPresenter(val appComponent: AppComponent) : MvpPresenter<InitScreenView> {
    val TAG = InitSyncPresenter::class.java.simpleName

    var view: InitScreenView? = null

    init {
//        TODO: Remove logging out. Sync and start SLActivity instead
        appComponent.activeSesison.token = null
    }

    override fun detachView(retainInstance: Boolean) {
        this.view = null
    }

    override fun attachView(view: InitScreenView?) {
        this.view = view
    }
}