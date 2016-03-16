package ru.wutiarn.edustor.android.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_base.*
import ru.wutiarn.edustor.android.Application
import ru.wutiarn.edustor.android.R
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.fragment.LessonsListFragment

/**
 * Created by wutiarn on 11.03.16.
 */
class LessonsListActivity : AppCompatActivity() {
    lateinit var appComponent: AppComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        val application = applicationContext as Application
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
}