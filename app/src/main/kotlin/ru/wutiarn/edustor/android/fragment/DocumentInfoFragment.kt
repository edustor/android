package ru.wutiarn.edustor.android.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.hannesdorfmann.mosby.mvp.lce.MvpLceFragment
import ru.wutiarn.edustor.android.Application
import ru.wutiarn.edustor.android.R
import ru.wutiarn.edustor.android.data.models.Document
import ru.wutiarn.edustor.android.presenter.DocumentInfoPresenter
import ru.wutiarn.edustor.android.view.DocumentInfoView


class DocumentInfoFragment : MvpLceFragment<LinearLayout, Document, DocumentInfoView, DocumentInfoPresenter>(), DocumentInfoView {
    lateinit var uuidTextView: TextView

    override fun createPresenter(): DocumentInfoPresenter? {
        val uuid = arguments.getString("uuid")
        val application = context.applicationContext as Application
        return DocumentInfoPresenter(application.appComponent, uuid)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uuidTextView = view.findViewById(R.id.uuid) as TextView
        loadData(false)
    }

    override fun setData(p0: Document) {
        uuidTextView.text = p0.id
    }

    override fun loadData(p0: Boolean) {
        presenter.loadData()
    }

    override fun getErrorMessage(p0: Throwable?, p1: Boolean): String? {
        throw UnsupportedOperationException()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_document_info, container, false)
    }
}