package ru.wutiarn.edustor.android.util.extension

import com.hannesdorfmann.mosby.mvp.lce.MvpLceView
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by wutiarn on 06.03.16.
 */
fun <T> Observable<T>.linkToLCEView(view: MvpLceView<T>?) {
    this.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                    { view?.setData(it) },
                    { view?.showError(it, false) }
            )
}