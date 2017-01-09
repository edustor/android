package ru.wutiarn.edustor.android.activity

import android.os.Bundle
import com.hannesdorfmann.mosby.mvp.MvpActivity
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.activity_sync.*
import ru.wutiarn.edustor.android.EdustorApplication
import ru.wutiarn.edustor.android.R
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.events.EdustorMetaSyncFinished
import ru.wutiarn.edustor.android.events.RequestSnackbarEvent
import ru.wutiarn.edustor.android.presenter.InitSyncPresenter
import ru.wutiarn.edustor.android.util.extension.makeSnack
import ru.wutiarn.edustor.android.util.extension.show
import ru.wutiarn.edustor.android.view.InitScreenView

class InitSyncActivity : MvpActivity<InitScreenView, InitSyncPresenter>(), InitScreenView {
    private lateinit var appComponent: AppComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        val app = application as EdustorApplication
        appComponent = app.appComponent

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sync)
    }

    override fun createPresenter(): InitSyncPresenter {
        val appComponent = (application as EdustorApplication).appComponent
        return InitSyncPresenter(appComponent, applicationContext)
    }

    @Subscribe fun onSnackbarShowRequest(event: RequestSnackbarEvent) {
        event.show(container)
    }

    @Subscribe fun onSyncFinished(event: EdustorMetaSyncFinished) {
        if (event.success) {
            presenter.onSyncFinished()
        } else {
            appComponent.eventBus.makeSnack("Sync finished with error: ${event.exception?.message}")
        }
    }

    override fun onStart() {
        super.onStart()
        appComponent.eventBus.register(this)

    }

    override fun onStop() {
        super.onStop()
        appComponent.eventBus.unregister(this)
    }
}