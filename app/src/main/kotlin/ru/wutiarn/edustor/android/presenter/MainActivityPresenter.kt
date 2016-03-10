package ru.wutiarn.edustor.android.presenter

import android.app.Activity
import android.os.Bundle
import com.google.zxing.integration.android.IntentIntegrator
import com.hannesdorfmann.mosby.mvp.MvpPresenter
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.events.RequestSnackbarEvent
import ru.wutiarn.edustor.android.fragment.LessonDetailsFragment
import ru.wutiarn.edustor.android.util.extension.configureAsync
import ru.wutiarn.edustor.android.view.MainActivityView
import rx.subscriptions.CompositeSubscription

/**
 * Created by wutiarn on 06.03.16.
 */
class MainActivityPresenter(val appComponent: AppComponent) : MvpPresenter<MainActivityView> {
    private var view: MainActivityView? = null
    var subscriptions: CompositeSubscription = CompositeSubscription()
    var currentScanRequestType: ScanRequestType? = null

    override fun attachView(p0: MainActivityView?) {
        view = p0
    }

    override fun detachView(p0: Boolean) {
        view = null
        subscriptions.clear()
    }

    fun showLessonInfo(uuid: String) {
        val documentInfoFragment = LessonDetailsFragment()
        val fragmentBundle = Bundle()
        fragmentBundle.putString("uuid", uuid)

        //        TODO: New activity call

    }

    fun activateUUID(uuid: String) {
        appComponent.documentsApi.activateUUID(uuid).configureAsync().subscribe({
            appComponent.eventBus.post(RequestSnackbarEvent("Done! ID: ${it.id}"))
        }, {
            appComponent.eventBus.post(RequestSnackbarEvent("Error: ${it.message}"))
        })
    }

    fun requestQrScan(activity: Activity, type: ScanRequestType) {
        currentScanRequestType = type
        IntentIntegrator(activity).initiateScan(IntentIntegrator.QR_CODE_TYPES)
    }

    fun processQrScanResult(result: String) {
        when (currentScanRequestType) {
            ScanRequestType.EXIST -> {
                showLessonInfo(result)
            }
            ScanRequestType.NEW -> {
                appComponent.eventBus.post(RequestSnackbarEvent("Activating $result"))

                activateUUID(result)

            }
        }
    }

    enum class ScanRequestType {
        EXIST, NEW
    }
}