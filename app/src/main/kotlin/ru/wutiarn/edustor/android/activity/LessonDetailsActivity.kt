package ru.wutiarn.edustor.android.activity

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
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
import ru.wutiarn.edustor.android.presenter.LessonPresenter
import ru.wutiarn.edustor.android.util.extension.assertSynced
import ru.wutiarn.edustor.android.view.LessonDetailsActivityView

class LessonDetailsActivity : MvpActivity<LessonDetailsActivityView, LessonDetailsActivityPresenter>(), LessonDetailsActivityView {
    lateinit var lessonDetailsFragment: LessonDetailsFragment
    lateinit var appComponent: AppComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        val application = applicationContext as EdustorApplication
        appComponent = application.appComponent

        super.onCreate(savedInstanceState)

        if (!appComponent.assertSynced()) return

        setContentView(R.layout.activity_base)
        setSupportActionBar(toolbar)

        fab_scan_exists.visibility = View.GONE

        lessonDetailsFragment = LessonDetailsFragment()
        val lessonBundle = Bundle()

        val uuid = intent.getStringExtra("uuid")
        lessonBundle.putString("uuid", uuid)

        val id = intent.getStringExtra("id")
        lessonBundle.putString("id", id)
        lessonDetailsFragment.arguments = lessonBundle

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

    override val fragmentPresenter: LessonPresenter?
        get() = lessonDetailsFragment.presenter

    @Subscribe fun onSnackbarShowRequest(event: RequestSnackbarEvent) {
        Snackbar.make(container, event.message, event.length).show()
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