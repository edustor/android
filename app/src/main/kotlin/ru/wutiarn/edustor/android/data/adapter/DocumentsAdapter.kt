package ru.wutiarn.edustor.android.data.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableSwipeableItemViewHolder
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import ru.wutiarn.edustor.android.R
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.data.models.Document
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.events.DocumentMovedEvent
import ru.wutiarn.edustor.android.events.DocumentRemovedEvent

class DocumentsAdapter(val context: Context, val appComponent: AppComponent) : RecyclerView.Adapter<DocumentsAdapter.DocumentViewHolder>() {

    var lesson: Lesson? = null
        set(value) {
            field = value; documents = lesson?.documents ?: mutableListOf()
        }

    private var documents: MutableList<Document> = mutableListOf()


    init {
        setHasStableIds(true)
    }

    val TAG: String = "DocumentsAdapter"
    override fun onBindViewHolder(holder: DocumentViewHolder, position: Int) {
        val document = documents[position]

        holder.document = document
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentViewHolder? {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.document_recycler_item, parent, false)

        return DocumentViewHolder(view, this)
    }

    override fun getItemCount(): Int {
        return documents.count()
    }


    fun onMoveItem(fromPosition: Int, toPosition: Int) {
        Log.d(TAG, "onMoveItem(fromPosition = $fromPosition, toPosition = $toPosition)")
        val document = documents[fromPosition]
        val after: Document?

        if (fromPosition > toPosition)
            after = if (toPosition > 0) documents[toPosition - 1] else null
        else
            after = documents[toPosition]

        appComponent.eventBus.post(DocumentMovedEvent(lesson!!, document, after, fromPosition, toPosition))

//        appComponent.lessonsApi.reorderDocuments(lesson?.id!!, document.id!!, after?.id)
//                .configureAsync().subscribe(
//                { appComponent.eventBus.post(RequestSnackbarEvent("Successfully moved")) },
//                { appComponent.eventBus.post(RequestSnackbarEvent("Move error: ${it.message}")) }
//        )
    }

    override fun getItemId(position: Int): Long {
        return documents[position].hashCode().toLong()
    }


    fun onRemoveItem(holder: DocumentViewHolder) {
        val document = holder.document!!
        val position = documents.indexOf(document)
        documents.remove(document)
//                        appComponent.documentsApi.delete(document.id!!)
//                                .configureAsync().subscribe(
//                                { appComponent.eventBus.post(RequestSnackbarEvent("Successfully removed: ${document.shortUUID}")) },
//                                { appComponent.eventBus.post(RequestSnackbarEvent("Error removing ${document.shortUUID}: ${it.message}")) }
//                        )
        appComponent.eventBus.post(DocumentRemovedEvent(document, position))
    }

    class DocumentViewHolder(val view: View, val adapter: DocumentsAdapter) : AbstractDraggableSwipeableItemViewHolder(view) {
        // Nullable only because of kotlin 1.0.3 doesn't support custom setters along with lateinit
        var document: Document? = null
            set(value: Document?) {
                field = value!!

                uuid.text = value.shortUUID
                timestamp.text = LocalDateTime.ofInstant(value.timestamp, ZoneId.systemDefault())
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

                value.uploadedTimestamp?.let {
                    uploadedTimestamp.text = LocalDateTime.ofInstant(value.uploadedTimestamp, ZoneId.systemDefault())
                            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                }

                val colorResource = if (value.isUploaded) R.color.documentUploaded else R.color.documentNotUploaded
                val color = ContextCompat.getColor(view.context, colorResource)
                isUploaded.setBackgroundColor(color)

            }
            get() = field

        private var uuid: TextView
        private var timestamp: TextView
        private var uploadedTimestamp: TextView
        private var isUploaded: View

        init {
            uuid = view.findViewById(R.id.uuid) as TextView
            timestamp = view.findViewById(R.id.scanned_timestamp) as TextView
            uploadedTimestamp = view.findViewById(R.id.uploaded_timestamp) as TextView
            isUploaded = view.findViewById(R.id.is_uploaded)
        }
    }

}