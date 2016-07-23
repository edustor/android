package ru.wutiarn.edustor.android.activity

import com.hannesdorfmann.mosby.mvp.MvpActivity
import ru.wutiarn.edustor.android.EdustorApplication
import ru.wutiarn.edustor.android.presenter.InitSyncPresenter
import ru.wutiarn.edustor.android.view.InitScreenView

class InitSyncActivity : MvpActivity<InitScreenView, InitSyncPresenter>() {
    override fun createPresenter(): InitSyncPresenter {
        val appComponent = (application as EdustorApplication).appComponent
        return InitSyncPresenter(appComponent)
    }
}