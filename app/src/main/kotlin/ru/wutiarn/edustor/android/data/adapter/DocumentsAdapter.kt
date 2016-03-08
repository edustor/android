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
import com.squareup.otto.Bus
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import ru.wutiarn.edustor.android.R
import ru.wutiarn.edustor.android.data.models.Document
import ru.wutiarn.edustor.android.events.DocumentRemovedEvent

/**
 * Created by wutiarn on 07.03.16.
 */
class DocumentsAdapter(var documents: MutableList<Document> = mutableListOf(), val bus: Bus) : RecyclerView.Adapter<DocumentViewHolder>(),
        DraggableItemAdapter<DocumentViewHolder>,
        SwipeableItemAdapter<DocumentViewHolder> {

    init {
        setHasStableIds(true)
    }

    val TAG: String = "DocumentsAdapter"
    override fun onBindViewHolder(holder: DocumentViewHolder, position: Int) {
        val document = documents[position]
        holder.uuid.text = document.uuid?.split("-")?.last()
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
        notifyItemMoved(fromPosition, toPosition)
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
        return SwipeableItemConstants.REACTION_CAN_SWIPE_LEFT
    }

    override fun onSetSwipeBackground(holder: DocumentViewHolder?, position: Int, type: Int) {

    }

    override fun onSwipeItem(holder: DocumentViewHolder?, position: Int, result: Int): SwipeResultAction? {

        when (result) {
            SwipeableItemConstants.RESULT_SWIPED_LEFT -> {
                return object : SwipeResultActionRemoveItem() {
                    override fun onSlideAnimationEnd() {
                        bus.post(DocumentRemovedEvent(documents[position]))
                    }
                }
            }
            else -> {
                return null
            }
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