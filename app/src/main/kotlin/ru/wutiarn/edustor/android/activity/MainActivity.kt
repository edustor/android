package ru.wutiarn.edustor.android.activity

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.zxing.integration.android.IntentIntegrator
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.android.synthetic.main.content_main.*
import ru.wutiarn.edustor.android.R
import ru.wutiarn.edustor.android.fragment.DocumentInfoFragment
import ru.wutiarn.edustor.android.view.MainActivityView

class MainActivity : AppCompatActivity(), MainActivityView {


    var currentSlidingPanelFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener {
            IntentIntegrator(this).initiateScan(IntentIntegrator.QR_CODE_TYPES)
        }

        configureSldingPanel()

        val documentInfoFragment = DocumentInfoFragment()
        val fragmentBundle = Bundle()
        fragmentBundle.putString("uuid", "18e69f5b-5a97-4ce7-9692-23ea18155be3")
        documentInfoFragment.arguments = fragmentBundle

        showSlidingPanelFragment(documentInfoFragment)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        IntentIntegrator.parseActivityResult(requestCode, resultCode, data)?.contents?.let {
            Snackbar.make(findViewById(R.id.container), "Found $it", Snackbar.LENGTH_LONG).show()
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
        detachSlidingPanelFrament()
        supportFragmentManager.beginTransaction()
                .add(R.id.sliding_panel_container, fragment)
                .commit()
        sliding_panel.isEnabled = true
        sliding_panel.panelState = SlidingUpPanelLayout.PanelState.ANCHORED
        currentSlidingPanelFragment = fragment
    }

    override fun detachSlidingPanelFrament() {
        sliding_panel.isEnabled = false
        currentSlidingPanelFragment?.let {
            supportFragmentManager.beginTransaction().detach(it).commit()
        }
    }

    fun configureSldingPanel() {
        sliding_panel.isEnabled = false
        sliding_panel.anchorPoint = 0.25f
        sliding_panel.setPanelSlideListener(object : SlidingUpPanelLayout.PanelSlideListener {
            override fun onPanelExpanded(p0: View?) {
            }

            override fun onPanelSlide(p0: View?, p1: Float) {
            }

            override fun onPanelCollapsed(p0: View?) {
                detachSlidingPanelFrament()
            }

            override fun onPanelHidden(p0: View?) {
            }

            override fun onPanelAnchored(p0: View?) {
            }

        })
    }
}
