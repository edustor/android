package ru.wutiarn.edustor.android.activity

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.zxing.integration.android.IntentIntegrator
import com.hannesdorfmann.mosby.mvp.MvpActivity
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.activity_base.*
import ru.wutiarn.edustor.android.Application
import ru.wutiarn.edustor.android.R
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.events.RequestSnackbarEvent
import ru.wutiarn.edustor.android.fragment.LessonsListFragment
import ru.wutiarn.edustor.android.presenter.MainActivityPresenter
import ru.wutiarn.edustor.android.view.MainActivityView

class MainActivity : MvpActivity<MainActivityView, MainActivityPresenter>(), MainActivityView {
    lateinit var appComponent: AppComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        val application = applicationContext as Application
        appComponent = application.appComponent

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        setSupportActionBar(toolbar)

        configureFabs()

        val lessonsListFragment = LessonsListFragment()

        supportFragmentManager.beginTransaction()
                .add(R.id.main_container, lessonsListFragment)
                .commit()
    }

    override fun createPresenter(): MainActivityPresenter {
        return MainActivityPresenter(applicationContext, appComponent)
    }

    override fun showLessonInfo(uuid: String) {
        val intent = Intent(this, LessonDetailsActivity::class.java)
        intent.putExtra("uuid", uuid)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        IntentIntegrator.parseActivityResult(requestCode, resultCode, data)?.contents?.let {
            presenter.processQrScanResult(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    @Subscribe fun onSnackbarShowRequest(event: RequestSnackbarEvent) {
        Snackbar.make(container, event.message, event.length).show()
    }

    fun configureFabs() {
        fab_scan_exists.visibility = View.VISIBLE
        fab_scan_new.visibility = View.VISIBLE
        fab_lessons.visibility = View.VISIBLE

        fab_scan_exists.setOnClickListener {
            presenter.requestQrScan(this, MainActivityPresenter.ScanRequestType.EXIST)
        }
        fab_scan_new.setOnClickListener {
            presenter.requestQrScan(this, MainActivityPresenter.ScanRequestType.NEW)
        }
        fab_lessons.setOnClickListener {
            val intent = Intent(baseContext, SubjectsListActivity::class.java)
            startActivity(intent)
        }
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
