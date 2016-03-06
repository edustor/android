package ru.wutiarn.edustor.android.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.hannesdorfmann.mosby.mvp.lce.MvpLceFragment
import kotlinx.android.synthetic.main.fragment_document_info.*
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import ru.wutiarn.edustor.android.Application
import ru.wutiarn.edustor.android.R
import ru.wutiarn.edustor.android.data.models.Document
import ru.wutiarn.edustor.android.presenter.DocumentInfoPresenter
import ru.wutiarn.edustor.android.view.DocumentInfoView


class DocumentInfoFragment : MvpLceFragment<LinearLayout, Document, DocumentInfoView, DocumentInfoPresenter>(), DocumentInfoView {

    override fun createPresenter(): DocumentInfoPresenter? {
        val uuid = arguments.getString("uuid")
        val application = context.applicationContext as Application
        return DocumentInfoPresenter(application.appComponent, uuid)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData(false)
    }

    override fun setData(p0: Document) {
        uuid.text = p0.uuid
        is_uploaded.text = p0.isUploaded.toString()
        owner.text = p0.owner?.login
        subject.text = p0.lesson?.subject?.name
        lesson_date.text = p0.lesson?.date?.format(DateTimeFormatter.ISO_LOCAL_DATE)

        added_datetime.text = OffsetDateTime.ofInstant(p0.timestamp, ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        showContent()
    }

    override fun loadData(p0: Boolean) {
        showLoading(false)
        presenter.loadData()
    }

    override fun getErrorMessage(p0: Throwable, p1: Boolean): String? {
        return p0.message
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_document_info, container, false)
    }
}