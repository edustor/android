package ru.wutiarn.edustor.android.activity

import android.os.Bundle
import com.hannesdorfmann.mosby.mvp.MvpActivity
import kotlinx.android.synthetic.main.activity_login.*
import ru.wutiarn.edustor.android.EdustorApplication
import ru.wutiarn.edustor.android.R
import ru.wutiarn.edustor.android.presenter.InitSyncPresenter
import ru.wutiarn.edustor.android.view.InitScreenView

class InitSyncActivity : MvpActivity<InitScreenView, InitSyncPresenter>(), InitScreenView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sync)
    }

    override fun createPresenter(): InitSyncPresenter {
        val appComponent = (application as EdustorApplication).appComponent
        return InitSyncPresenter(appComponent)
    }
}