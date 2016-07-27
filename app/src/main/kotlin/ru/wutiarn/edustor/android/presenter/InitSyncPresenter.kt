package ru.wutiarn.edustor.android.presenter

import android.content.Context
import android.util.Log
import com.hannesdorfmann.mosby.mvp.MvpPresenter
import ru.wutiarn.edustor.android.activity.SubjectsListActivity
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.util.extension.makeToast
import ru.wutiarn.edustor.android.util.extension.startActivity
import ru.wutiarn.edustor.android.util.extension.syncNow
import ru.wutiarn.edustor.android.view.InitScreenView

class InitSyncPresenter(val appComponent: AppComponent, val context: Context) : MvpPresenter<InitScreenView> {
    val TAG: String = InitSyncPresenter::class.java.simpleName

    var view: InitScreenView? = null

    init {

        appComponent.api.sync.syncNow().subscribe(
                {
                    Log.w(TAG, "Initial sync failed with exception", it)
                    context.makeToast("Sync failed: $it")
                    appComponent.activeSession.logout()
                },
                { context.startActivity(SubjectsListActivity::class.java, true) }
        )
    }

    override fun detachView(retainInstance: Boolean) {
        this.view = null
    }

    override fun attachView(view: InitScreenView?) {
        this.view = view
    }
}