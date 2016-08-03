package ru.wutiarn.edustor.android.activity

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.widget.Switch
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.activity_base.*
import ru.wutiarn.edustor.android.EdustorApplication
import ru.wutiarn.edustor.android.R
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.events.RequestSnackbarEvent
import ru.wutiarn.edustor.android.fragment.LessonsListFragment
import ru.wutiarn.edustor.android.util.extension.assertActivityCanStart

class LessonsListActivity : AppCompatActivity() {
    lateinit var appComponent: AppComponent
    lateinit var fragment: LessonsListFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        val application = applicationContext as EdustorApplication
        appComponent = application.appComponent

        super.onCreate(savedInstanceState)

        if (!appComponent.assertActivityCanStart(this)) return

        setContentView(R.layout.activity_base)
        setSupportActionBar(toolbar)

        fragment = LessonsListFragment()
        fragment.arguments = intent.extras
        fragment.arguments.putBoolean("allowDatePick", true)

        supportFragmentManager.beginTransaction()
                .add(R.id.main_container, fragment)
                .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_lessons, menu)
        val switch = menu.findItem(R.id.menuSyncSwitchItem).actionView.findViewById(R.id.menuSyncSwitch) as Switch
        fragment.setSyncSwitch(switch)
        return true
    }

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