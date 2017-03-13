package ru.wutiarn.edustor.android.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Switch
import com.google.zxing.integration.android.IntentIntegrator
import com.hannesdorfmann.mosby.mvp.MvpActivity
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.activity_base.*
import ru.wutiarn.edustor.android.EdustorApplication
import ru.wutiarn.edustor.android.R
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.events.RequestSnackbarEvent
import ru.wutiarn.edustor.android.fragment.MainListFragment
import ru.wutiarn.edustor.android.presenter.MainListActivityPresenter
import ru.wutiarn.edustor.android.util.extension.EdustorURIParser
import ru.wutiarn.edustor.android.util.extension.assertActivityCanStart
import ru.wutiarn.edustor.android.util.extension.makeToast
import ru.wutiarn.edustor.android.util.extension.show
import ru.wutiarn.edustor.android.view.MainListActivityView

class MainActivity : MvpActivity<MainListActivityView, MainListActivityPresenter>(), MainListActivityView {
    lateinit var appComponent: AppComponent
    lateinit var fragment: MainListFragment

    override fun createPresenter(): MainListActivityPresenter {
        return MainListActivityPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val application = applicationContext as EdustorApplication
        appComponent = application.appComponent

        super.onCreate(savedInstanceState)

        if (!appComponent.assertActivityCanStart(this)) return

        setContentView(R.layout.activity_base)
        setSupportActionBar(toolbar)

        fragment = MainListFragment()
        fragment.arguments = intent.extras
        supportFragmentManager.beginTransaction()
                .add(R.id.main_container, fragment)
                .commit()

        fab_scan_existed.visibility = View.VISIBLE
        fab_scan_existed.setOnClickListener {
            presenter.requestQrScan(this)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_lessons, menu)
        val switch = menu.findItem(R.id.menuSyncSwitchItem).actionView.findViewById(R.id.menuSyncSwitch) as Switch
        fragment.syncSwitch = switch
        return true
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

        val lesson = appComponent.repo.lessons.byQR(qrData)

        if (lesson == null) {
            appComponent.context.makeToast("Unknown QR code: $qrData")
            return
        }

        val intent = Intent(this, LessonDetailsActivity::class.java)
        intent.putExtra("id", lesson.id)
        startActivity(intent)
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