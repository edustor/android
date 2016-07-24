package ru.wutiarn.edustor.android.presenter

import android.content.Context
import android.util.Log
import com.hannesdorfmann.mosby.mvp.MvpPresenter
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.util.extension.configureAsync
import ru.wutiarn.edustor.android.view.InitScreenView

class InitSyncPresenter(val appComponent: AppComponent, val context: Context) : MvpPresenter<InitScreenView> {
    val TAG: String = InitSyncPresenter::class.java.simpleName

    var view: InitScreenView? = null

    init {

        appComponent.syncApi.fetch()
                .configureAsync()
                .subscribe {
                    Log.i(TAG, "Sync finished")
                }

//        val realmConfig = RealmConfiguration.Builder(context)
//                .inMemory()
//                .build()
//        Realm.setDefaultConfiguration(realmConfig)
//        val realm = Realm.getDefaultInstance()
//        realm.beginTransaction()
//        val user = realm.createObject(User::class.java)
//        user.email = "me@wutiarn.ru"
//        realm.commitTransaction()
//        println("Commit")
    }

    override fun detachView(retainInstance: Boolean) {
        this.view = null
    }

    override fun attachView(view: InitScreenView?) {
        this.view = view
    }
}