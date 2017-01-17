package ru.wutiarn.edustor.android.view

import com.hannesdorfmann.mosby.mvp.MvpView

interface TagListActivityView : MvpView {
    fun onPageQRCodeScanned(result: String)
}
