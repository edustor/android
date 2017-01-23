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
import ru.wutiarn.edustor.android.fragment.LessonDetailsFragment
import ru.wutiarn.edustor.android.presenter.LessonDetailsActivityPresenter
import ru.wutiarn.edustor.android.presenter.LessonDetailsPresenter
import ru.wutiarn.edustor.android.util.extension.assertActivityCanStart
import ru.wutiarn.edustor.android.util.extension.show
import ru.wutiarn.edustor.android.view.LessonDetailsActivityView

class LessonDetailsActivity : MvpActivity<LessonDetailsActivityView, LessonDetailsActivityPresenter>(), LessonDetailsActivityView {
    lateinit var lessonDetailsFragment: LessonDetailsFragment
    lateinit var appComponent: AppComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        val application = applicationContext as EdustorApplication
        appComponent = application.appComponent

        super.onCreate(savedInstanceState)

        if (!appComponent.assertActivityCanStart(this)) return

        setContentView(R.layout.activity_base)
        setSupportActionBar(toolbar)

        fab_scan_existed.visibility = View.GONE

        lessonDetailsFragment = LessonDetailsFragment()

        lessonDetailsFragment.arguments = intent.extras

        fab_scan_new.visibility = View.VISIBLE
        fab_scan_new.setOnClickListener {
            presenter.requestQrScan(this)
        }

        supportFragmentManager.beginTransaction()
                .add(R.id.main_container, lessonDetailsFragment)
                .commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        IntentIntegrator.parseActivityResult(requestCode, resultCode, data)?.contents?.let {
            presenter.processQrScanResult(it)
        }
    }

    override fun createPresenter(): LessonDetailsActivityPresenter {
        return LessonDetailsActivityPresenter()
    }

    override val fragmentPresenter: LessonDetailsPresenter?
        get() = lessonDetailsFragment.presenter

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