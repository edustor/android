package ru.wutiarn.edustor.android.presenter

import android.content.Context
import android.util.Log
import com.hannesdorfmann.mosby.mvp.MvpPresenter
import ru.wutiarn.edustor.android.activity.SubjectsListActivity
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.util.extension.configureAsync
import ru.wutiarn.edustor.android.util.extension.fullSyncNow
import ru.wutiarn.edustor.android.util.extension.makeSnack
import ru.wutiarn.edustor.android.util.extension.startActivity
import ru.wutiarn.edustor.android.view.InitScreenView

class InitSyncPresenter(val appComponent: AppComponent, val context: Context) : MvpPresenter<InitScreenView> {
    val TAG: String = InitSyncPresenter::class.java.simpleName

    var view: InitScreenView? = null

    init {

        appComponent.api.sync.fullSyncNow().configureAsync().subscribe(
                { context.startActivity(SubjectsListActivity::class.java, true) },
                {
                    Log.w(TAG, "Initial sync failed with exception", it)
                    appComponent.eventBus.makeSnack("Sync failed: $it")
                    appComponent.activeSession.logout()
                }
        )
    }

    override fun detachView(retainInstance: Boolean) {
        this.view = null
    }

    override fun attachView(view: InitScreenView?) {
        this.view = view
    }
}