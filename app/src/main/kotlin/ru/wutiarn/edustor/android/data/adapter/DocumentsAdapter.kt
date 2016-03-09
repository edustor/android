package ru.wutiarn.edustor.android.data.adapter

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionRemoveItem
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
import ru.wutiarn.edustor.android.events.RequestSnackbarEvent
import ru.wutiarn.edustor.android.util.extension.configureAsync

/**
 * Created by wutiarn on 07.03.16.
 */
class DocumentsAdapter(val appComponent: AppComponent) : RecyclerView.Adapter<DocumentsAdapter.DocumentViewHolder>(),
        DraggableItemAdapter<DocumentsAdapter.DocumentViewHolder>,
        SwipeableItemAdapter<DocumentsAdapter.DocumentViewHolder> {

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
        holder.uuid.text = document.shortUUID
        holder.timestamp.text = LocalDateTime.ofInstant(document.timestamp, ZoneId.systemDefault())
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        holder.isUploaded.setBackgroundColor(if (document.isUploaded) R.color.documentUploaded else R.color.documentNotUploaded)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentViewHolder? {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.document_recycler_item, parent, false)

        return DocumentViewHolder(view)
    }

    override fun getItemCount(): Int {
        return documents.count()
    }


    override fun onMoveItem(fromPosition: Int, toPosition: Int) {
        Log.d(TAG, "onMoveItem(fromPosition = $fromPosition, toPosition = $toPosition)")
        val document = documents[fromPosition]
        val after: Document?

        if (fromPosition > toPosition)
            after = if (toPosition > 0) documents[toPosition - 1] else null
        else
            after = documents[toPosition]

        appComponent.eventBus.post(DocumentMovedEvent(lesson!!, document, after, fromPosition, toPosition))

        appComponent.lessonsApi.reorderDocuments(lesson?.id!!, document.id!!, after?.id)
                .configureAsync().subscribe(
                { appComponent.eventBus.post(RequestSnackbarEvent("Successfully moved")) },
                { appComponent.eventBus.post(RequestSnackbarEvent("Move error: ${it.message}")) }
        )
    }

    override fun onGetItemDraggableRange(p0: DocumentViewHolder?, p1: Int): ItemDraggableRange? {
        return null
    }

    override fun onCheckCanStartDrag(p0: DocumentViewHolder?, p1: Int, p2: Int, p3: Int): Boolean {
        return true
    }

    override fun getItemId(position: Int): Long {
        return documents[position].hashCode().toLong()
    }

    override fun onGetSwipeReactionType(holder: DocumentViewHolder?, position: Int, x: Int, y: Int): Int {
        return SwipeableItemConstants.REACTION_CAN_SWIPE_BOTH_H
    }

    override fun onSetSwipeBackground(holder: DocumentViewHolder?, position: Int, type: Int) {

    }

    override fun onSwipeItem(holder: DocumentViewHolder?, position: Int, result: Int): SwipeResultAction? {
        when (result) {
            in arrayOf(SwipeableItemConstants.RESULT_SWIPED_LEFT, SwipeableItemConstants.RESULT_SWIPED_RIGHT) -> {
                return object : SwipeResultActionRemoveItem() {
                    override fun onPerformAction() {
                        val document = documents[position]
                        documents.removeAt(position)
                        appComponent.documentsApi.delete(document.id!!)
                                .configureAsync().subscribe(
                                { appComponent.eventBus.post(RequestSnackbarEvent("Successfully removed: ${document.shortUUID}")) },
                                { appComponent.eventBus.post(RequestSnackbarEvent("Error removing ${document.shortUUID}: ${it.message}")) }
                        )
                        appComponent.eventBus.post(DocumentRemovedEvent(document, position))
                    }
                }
            }
            else -> {
                return null
            }
        }
    }

    class DocumentViewHolder(val view: View) : AbstractDraggableSwipeableItemViewHolder(view) {

        lateinit var uuid: TextView
        lateinit var timestamp: TextView
        lateinit var isUploaded: View

        init {
            uuid = view.findViewById(R.id.uuid) as TextView
            timestamp = view.findViewById(R.id.uploaded_timestamp) as TextView
            isUploaded = view.findViewById(R.id.is_uploaded)
        }

        override fun getSwipeableContainerView(): View {
            return view
        }
    }

}