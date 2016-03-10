package ru.wutiarn.edustor.android.activity

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.zxing.integration.android.IntentIntegrator
import com.hannesdorfmann.mosby.mvp.MvpActivity
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import ru.wutiarn.edustor.android.Application
import ru.wutiarn.edustor.android.R
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.events.RequestSnackbarEvent
import ru.wutiarn.edustor.android.fragment.LessonsListFragment
import ru.wutiarn.edustor.android.presenter.MainActivityPresenter
import ru.wutiarn.edustor.android.view.MainActivityView

class MainActivity : MvpActivity<MainActivityView, MainActivityPresenter>(), MainActivityView {
    lateinit var appComponent: AppComponent
    var currentSlidingPanelFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val application = applicationContext as Application
        appComponent = application.appComponent

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        configureFabs()
        configureSlidingPanel()

        //        val fragmentArguments = Bundle()
        //        fragmentArguments.putString("id", "current")
        //
        //        val lessonFragment = LessonDetailsFragment()
        //        lessonFragment.arguments = fragmentArguments

        val lessonsListFragment = LessonsListFragment()

        supportFragmentManager.beginTransaction()
                .add(R.id.main_container, lessonsListFragment)
                .commit()
    }

    override fun createPresenter(): MainActivityPresenter {
        return MainActivityPresenter(appComponent)
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


    override fun showSlidingPanelFragment(fragment: Fragment) {
        detachSlidingPanelFragment()
        supportFragmentManager.beginTransaction()
                .add(R.id.sliding_panel_container, fragment)
                .commitAllowingStateLoss()
        sliding_panel.panelState = SlidingUpPanelLayout.PanelState.ANCHORED
        currentSlidingPanelFragment = fragment
        setFabsShown(false)
    }

    override fun detachSlidingPanelFragment() {
        sliding_panel.panelState = SlidingUpPanelLayout.PanelState.HIDDEN
        currentSlidingPanelFragment?.let {
            supportFragmentManager.beginTransaction().detach(it).commitAllowingStateLoss()
            currentSlidingPanelFragment = null
        }
        setFabsShown(true)
    }

    @Subscribe fun onSnackbarShowRequest(event: RequestSnackbarEvent) {
        Snackbar.make(container, event.message, event.length).show()
    }

    override fun isBottomPanelOpened(): Boolean {
        return currentSlidingPanelFragment != null
    }

    fun configureFabs() {
        scan_exists.setOnClickListener {
            presenter.requestQrScan(this, MainActivityPresenter.ScanRequestType.EXIST)
        }
        scan_new.setOnClickListener {
            presenter.requestQrScan(this, MainActivityPresenter.ScanRequestType.NEW)
        }
    }

    fun configureSlidingPanel() {
        sliding_panel.anchorPoint = 0.25f
        sliding_panel.setPanelSlideListener(object : SlidingUpPanelLayout.PanelSlideListener {
            override fun onPanelExpanded(p0: View?) {
            }

            override fun onPanelSlide(p0: View?, p1: Float) {
            }

            override fun onPanelCollapsed(p0: View?) {
                detachSlidingPanelFragment()
            }

            override fun onPanelHidden(p0: View?) {
            }

            override fun onPanelAnchored(p0: View?) {
            }

        })

        main_container.setOnClickListener {
            detachSlidingPanelFragment()
        }
    }

    fun setFabsShown(shown: Boolean) {
        if (shown) {
            scan_exists.show()
        } else {
            scan_exists.hide()
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
