package ru.wutiarn.edustor.android.presenter

import android.content.Context
import com.hannesdorfmann.mosby.mvp.MvpPresenter
import ru.wutiarn.edustor.android.activity.MainActivity
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.util.extension.startActivity
import ru.wutiarn.edustor.android.view.InitScreenView

class InitSyncPresenter(val appComponent: AppComponent, val context: Context) : MvpPresenter<InitScreenView> {
    var view: InitScreenView? = null

    override fun detachView(retainInstance: Boolean) {
        this.view = null
    }

    override fun attachView(view: InitScreenView?) {
        this.view = view
        appComponent.syncManager.requestSync(true, false)
    }

    fun onSyncFinished() {
        context.startActivity(MainActivity::class.java, true)
    }
}