package ru.wutiarn.edustor.android.util.helpers

import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import ru.wutiarn.edustor.android.R
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

interface PullToRefreshHelper {
    var appComponent: AppComponent

    fun configureSwipeToRefresh(baseView: View) {
        val swipeRefreshLayout = baseView.findViewById(R.id.swipe_refresh_layout) as SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            appComponent.syncManager.requestSync(true, false)
            Observable.timer(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                    .subscribe { swipeRefreshLayout.isRefreshing = false }
        }
    }
}