package ru.wutiarn.edustor.android.presenter

import android.app.Activity
import com.google.zxing.integration.android.IntentIntegrator
import com.hannesdorfmann.mosby.mvp.MvpPresenter
import ru.wutiarn.edustor.android.view.LessonDetailsActivityView

/**
 * Created by wutiarn on 06.03.16.
 */
class LessonDetailsActivityPresenter() : MvpPresenter<LessonDetailsActivityView> {
    private var view: LessonDetailsActivityView? = null

    override fun attachView(p0: LessonDetailsActivityView?) {
        view = p0
    }

    override fun detachView(p0: Boolean) {
        view = null
    }

    fun requestQrScan(activity: Activity) {
        IntentIntegrator(activity).initiateScan(IntentIntegrator.QR_CODE_TYPES)
    }

    fun processQrScanResult(result: String) {

    }
}