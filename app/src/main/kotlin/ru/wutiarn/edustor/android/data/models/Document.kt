package ru.wutiarn.edustor.android.data.models

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import org.threeten.bp.Instant
import ru.wutiarn.edustor.android.R

/**
 * Created by wutiarn on 04.03.16.
 */
data class Document(
        var owner: User? = null,
        var uuid: String? = null,
        var isUploaded: Boolean = false,
        var timestamp: Instant = Instant.now(),
        var id: String? = null
) : IFlexible<DocumentViewHolder> {
    override fun setSelected(p0: Boolean) {
    }

    override fun setSelectable(p0: Boolean) {
    }

    override fun isHidden(): Boolean {
        return false
    }

    override fun isEnabled(): Boolean {
        return true
    }

    override fun setDraggable(p0: Boolean) {

    }

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: DocumentViewHolder, position: Int, list: MutableList<Any?>) {
        holder.uuid.text = this.uuid
    }

    override fun setSwipeable(p0: Boolean) {
    }

    override fun setEnabled(p0: Boolean) {

    }

    override fun isSelectable(): Boolean {
        return false
    }

    override fun isDraggable(): Boolean {
        return false
    }

    override fun setHidden(p0: Boolean) {

    }

    override fun createViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>, inflater: LayoutInflater, parent: ViewGroup?): DocumentViewHolder? {
        val view = inflater
                .inflate(R.layout.document_recycler_item, parent, false)
        return DocumentViewHolder(view)
    }

    override fun isSwipeable(): Boolean {
        return false
    }

    override fun getLayoutRes(): Int {
        return R.layout.document_recycler_item
    }

    override fun isSelected(): Boolean {
        return false
    }

}

class DocumentViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    lateinit var uuid: TextView

    init {
        uuid = view.findViewById(R.id.uuid) as TextView
    }
}