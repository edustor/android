package ru.wutiarn.edustor.android.data.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import ru.wutiarn.edustor.android.R
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.data.models.Document
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.events.RequestSnackbarEvent
import rx.Subscription

class DocumentsAdapter(val context: Context, val appComponent: AppComponent) : RecyclerView.Adapter<DocumentsAdapter.DocumentViewHolder>() {

    var lesson: Lesson? = null
        set(value) {
            field = value
            activeDocumentsSubscription?.unsubscribe()
            value?.addChangeListener<Lesson> { onDocumentsChanged(it.documents) }
            onDocumentsChanged(value?.documents)
        }

    private var documents: MutableList<Document> = mutableListOf()

    private var lastUnfinishedMovement: Pair<String, String?>? = null
    var activeDocumentsSubscription: Subscription? = null
    val TAG: String = "DocumentsAdapter"


    init {
        setHasStableIds(true)
    }

    fun onDocumentsChanged(newDocs: List<Document>?) {
        documents = newDocs
                ?.sortedBy { it.index }
                ?.toMutableList() ?: mutableListOf()
        notifyDataSetChanged()
//        TODO: По непонятным причинам список документов перестает обновляться при добавлении в него новых документов
//        после удаления любого старого. Хотя событие долетает до этого метода корректно и список документов в нем верный
    }


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

        documents.remove(document)

        val targetIndex: Int

        if (after != null) {
            targetIndex = documents.indexOf(after) + 1
        } else {
            targetIndex = 0
        }

        documents.add(targetIndex, document)

        lastUnfinishedMovement = document.id to after?.id
    }

    fun onMovementFinished() {
        lastUnfinishedMovement?.let {
            appComponent.repo.lessons.reorderDocuments(lesson?.id!!, it.first, it.second)
                    .subscribe(
                            { appComponent.eventBus.post(RequestSnackbarEvent("Successfully moved")) },
                            { appComponent.eventBus.post(RequestSnackbarEvent("Move error: ${it.message}")) }
                    )
        }
    }

    override fun getItemId(position: Int): Long {
        return documents[position].id.hashCode().toLong()
    }


    fun onRemoveItem(holder: DocumentViewHolder) {
        val document = holder.document!!

        val shortUUID = document.shortUUID
        appComponent.repo.documents.delete(document.id)
                .subscribe(
                        { appComponent.eventBus.post(RequestSnackbarEvent("Successfully removed: $shortUUID")) },
                        { appComponent.eventBus.post(RequestSnackbarEvent("Error removing $shortUUID: ${it.message}")) }
                )
    }

    class DocumentViewHolder(val view: View, val adapter: DocumentsAdapter) : RecyclerView.ViewHolder(view) {
        // Nullable only because kotlin 1.0.3 doesn't support custom setters along with lateinit
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