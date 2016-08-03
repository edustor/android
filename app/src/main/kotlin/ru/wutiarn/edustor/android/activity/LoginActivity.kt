package ru.wutiarn.edustor.android.activity

import android.content.Intent
import android.os.Bundle
import com.hannesdorfmann.mosby.mvp.MvpActivity
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.activity_login.*
import ru.wutiarn.edustor.android.EdustorApplication
import ru.wutiarn.edustor.android.R
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.events.RequestSnackbarEvent
import ru.wutiarn.edustor.android.presenter.LoginPresenter
import ru.wutiarn.edustor.android.util.extension.show
import ru.wutiarn.edustor.android.view.LoginView

class LoginActivity : MvpActivity<LoginView, LoginPresenter>(), LoginView {
    private lateinit var appComponent: AppComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        appComponent = (application as EdustorApplication).appComponent

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sign_in_button.setOnClickListener { presenter.onLogin() }
    }

    override fun createPresenter(): LoginPresenter {
        return LoginPresenter(appComponent, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter.onActivityResult(requestCode, resultCode, data)
    }

    @Subscribe fun onSnackbarShowRequest(event: RequestSnackbarEvent) {
        event.show(container)
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