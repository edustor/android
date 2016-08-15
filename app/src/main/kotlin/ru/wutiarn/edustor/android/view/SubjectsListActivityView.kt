package ru.wutiarn.edustor.android.view

import com.hannesdorfmann.mosby.mvp.MvpView

interface SubjectsListActivityView : MvpView {
    fun onDocumentQRCodeScanned(result: String)
}
