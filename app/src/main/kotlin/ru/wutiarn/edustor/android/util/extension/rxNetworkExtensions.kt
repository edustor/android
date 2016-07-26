package ru.wutiarn.edustor.android.util.extension

import android.util.Log
import com.hannesdorfmann.mosby.mvp.lce.MvpLceView
import rx.Observable
import rx.Single
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

fun <T> Observable<T>.linkToLCEView(view: MvpLceView<T>?, resultCallback: ((T) -> Unit)? = null): Subscription {
    return this.subscribe(
            { resultCallback?.invoke(it); view?.setData(it) },
            { Log.e("O.linkToLCEView", "Exception occured", it); view?.showError(it, false) }
    )
}

fun <T> Observable<T>.configureAsync(): Observable<T> {
    return this.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}

fun <T> Single<T>.configureAsync(): Single<T> {
    return this.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}