package ru.wutiarn.edustor.android.view

import com.hannesdorfmann.mosby.mvp.MvpView

interface MainListActivityView : MvpView {
    fun onPageQRCodeScanned(result: String)
}
