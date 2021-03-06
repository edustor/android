package ru.wutiarn.edustor.android.presenter

import android.app.Activity
import com.google.zxing.integration.android.IntentIntegrator
import com.hannesdorfmann.mosby.mvp.MvpPresenter
import ru.wutiarn.edustor.android.view.MainListActivityView

class MainListActivityPresenter : MvpPresenter<MainListActivityView> {
    var view: MainListActivityView? = null


    override fun attachView(view: MainListActivityView) {
        this.view = view
    }

    override fun detachView(retainInstance: Boolean) {
        view = null
    }

    fun requestQrScan(activity: Activity) {
        IntentIntegrator(activity).initiateScan(IntentIntegrator.QR_CODE_TYPES)
    }

    fun processQrScanResult(result: String) {
        view?.onPageQRCodeScanned(result)
    }

}
