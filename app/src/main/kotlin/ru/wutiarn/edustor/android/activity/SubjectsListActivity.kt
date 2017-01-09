package ru.wutiarn.edustor.android.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.zxing.integration.android.IntentIntegrator
import com.hannesdorfmann.mosby.mvp.MvpActivity
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.activity_base.*
import ru.wutiarn.edustor.android.EdustorApplication
import ru.wutiarn.edustor.android.R
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.events.RequestSnackbarEvent
import ru.wutiarn.edustor.android.fragment.SubjectsListFragment
import ru.wutiarn.edustor.android.presenter.SubjectListActivityPresenter
import ru.wutiarn.edustor.android.util.extension.EdustorURIParser
import ru.wutiarn.edustor.android.util.extension.assertActivityCanStart
import ru.wutiarn.edustor.android.util.extension.makeToast
import ru.wutiarn.edustor.android.util.extension.show
import ru.wutiarn.edustor.android.view.SubjectsListActivityView

class SubjectsListActivity : MvpActivity<SubjectsListActivityView, SubjectListActivityPresenter>(), SubjectsListActivityView {
    lateinit var appComponent: AppComponent

    override fun createPresenter(): SubjectListActivityPresenter {
        return SubjectListActivityPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val application = applicationContext as EdustorApplication
        appComponent = application.appComponent

        super.onCreate(savedInstanceState)

        if (!appComponent.assertActivityCanStart(this)) return

        setContentView(R.layout.activity_base)
        setSupportActionBar(toolbar)

        val fragment = SubjectsListFragment()
        supportFragmentManager.beginTransaction()
                .add(R.id.main_container, fragment)
                .commit()

        fab_scan_exists.visibility = View.VISIBLE
        fab_scan_exists.setOnClickListener {
            presenter.requestQrScan(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        IntentIntegrator.parseActivityResult(requestCode, resultCode, data)?.contents?.let {
            presenter.processQrScanResult(it)
        }
    }

    override fun onPageQRCodeScanned(result: String) {
        val (type, qrData) = EdustorURIParser.parse(result)

        if (type != EdustorURIParser.URIType.PAGE) {
            appComponent.eventBus.post(RequestSnackbarEvent("Error: incorrect QR code payload")); return
        }

        appComponent.repo.lessons.byQR(qrData).first().subscribe(
                {
                    if (!it.isValid) {
                        appComponent.context.makeToast("Unknown QR code: $qrData")
                        return@subscribe
                    }

                    val intent = Intent(this, LessonDetailsActivity::class.java)
                    intent.putExtra("id", it.id)
                    startActivity(intent)
                },
                {
                    appComponent.context.makeToast("Failed to open lesson by qr: $it")
                }
        )


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