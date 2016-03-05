package ru.wutiarn.edustor.android

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.dagger.component.DaggerAppComponent

/**
 * Created by wutiarn on 04.03.16.
 */
class Application : Application() {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)

        appComponent = DaggerAppComponent.create()
    }
}
