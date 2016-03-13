package ru.wutiarn.edustor.android.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_base.*
import ru.wutiarn.edustor.android.Application
import ru.wutiarn.edustor.android.R
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.fragment.SubjectsListFragment

/**
 * Created by wutiarn on 11.03.16.
 */
class SubjectsListActivity : AppCompatActivity() {
    lateinit var appComponent: AppComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        val application = applicationContext as Application
        appComponent = application.appComponent

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_base)
        setSupportActionBar(toolbar)

        val fragment = SubjectsListFragment()
        supportFragmentManager.beginTransaction()
                .add(R.id.main_container, fragment)
                .commit()
    }
}