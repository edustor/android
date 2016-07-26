package ru.wutiarn.edustor.android.presenter

import android.content.Context
import android.util.Log
import com.hannesdorfmann.mosby.mvp.MvpPresenter
import io.realm.Realm
import ru.wutiarn.edustor.android.activity.SubjectsListActivity
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.util.extension.configureAsync
import ru.wutiarn.edustor.android.util.extension.startActivity
import ru.wutiarn.edustor.android.view.InitScreenView

class InitSyncPresenter(val appComponent: AppComponent, val context: Context) : MvpPresenter<InitScreenView> {
    val TAG: String = InitSyncPresenter::class.java.simpleName

    var view: InitScreenView? = null

    init {

        appComponent.syncApi.fetch()
                .configureAsync()
                .subscribe { initData ->
                    val realm = Realm.getDefaultInstance()
                    realm.executeTransaction {
                        realm.deleteAll()

                        initData.lessons.forEach {
                            it.calculateDocumentIndexes()
                        }

                        realm.copyToRealm(initData.user)
                        realm.copyToRealm(initData.subjects)
                        realm.copyToRealmOrUpdate(initData.lessons)
                    }

                    Log.i(TAG, "Sync finished")
                    context.startActivity(SubjectsListActivity::class.java, true)
                }
    }

    override fun detachView(retainInstance: Boolean) {
        this.view = null
    }

    override fun attachView(view: InitScreenView?) {
        this.view = view
    }
}