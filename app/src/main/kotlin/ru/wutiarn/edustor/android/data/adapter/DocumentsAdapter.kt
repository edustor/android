package ru.wutiarn.edustor.android.data.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ru.wutiarn.edustor.android.R
import ru.wutiarn.edustor.android.data.models.Document

/**
 * Created by wutiarn on 07.03.16.
 */
class DocumentsAdapter(var documents: MutableList<Document> = mutableListOf()) : RecyclerView.Adapter<DocumentViewHolder>() {

    override fun onBindViewHolder(holder: DocumentViewHolder, position: Int) {
        holder.uuid.text = documents[position].uuid
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentViewHolder? {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.document_recycler_item, parent, false)

        return DocumentViewHolder(view)
    }

    override fun getItemCount(): Int {
        return documents.count()
    }

}

class DocumentViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    lateinit var uuid: TextView

    init {
        uuid = view.findViewById(R.id.uuid) as TextView
    }
}