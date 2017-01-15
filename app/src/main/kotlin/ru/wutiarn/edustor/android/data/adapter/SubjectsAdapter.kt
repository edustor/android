package ru.wutiarn.edustor.android.data.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ru.wutiarn.edustor.android.R
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.data.models.Tag

class SubjectsAdapter(val appComponent: AppComponent, val listener: SubjectsAdapterEventsListener) : RecyclerView.Adapter<SubjectsAdapter.SubjectViewHolder>() {
    var tags: List<Tag> = listOf()

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder? {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.subject_recycler_item, parent, false)

        return SubjectViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tags.count()
    }

    override fun getItemId(position: Int): Long {
        return tags[position].hashCode().toLong()
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val subject = tags[position]

        holder.name.text = subject.name

        holder.view.setOnClickListener {
            listener.onSubjectClick(subject)
        }
    }

    class SubjectViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById(R.id.name) as TextView
    }

    interface SubjectsAdapterEventsListener {
        fun onSubjectClick(tag: Tag)
    }

}