package ru.wutiarn.edustor.android

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.dagger.component.DaggerAppComponent
import ru.wutiarn.edustor.android.dagger.module.LocalStorageModule

class EdustorApplication : Application() {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)

        val sharedPreferences = getSharedPreferences("ru.wutiarn.edustor", MODE_PRIVATE)
        val localStorageModule = LocalStorageModule(sharedPreferences, this)

        appComponent = DaggerAppComponent.builder()
                .localStorageModule(localStorageModule)
                .build()
    }
}
