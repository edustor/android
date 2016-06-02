package ru.wutiarn.edustor.android.activity

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.activity_base.*
import ru.wutiarn.edustor.android.EdustorApplication
import ru.wutiarn.edustor.android.R
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.events.RequestSnackbarEvent
import ru.wutiarn.edustor.android.fragment.LessonsListFragment

class LessonsListActivity : AppCompatActivity() {
    lateinit var appComponent: AppComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        val application = applicationContext as EdustorApplication
        appComponent = application.appComponent

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_base)
        setSupportActionBar(toolbar)

        val fragment = LessonsListFragment()
        fragment.arguments = intent.extras
        fragment.arguments.putBoolean("allowDatePick", true)

        supportFragmentManager.beginTransaction()
                .add(R.id.main_container, fragment)
                .commit()
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