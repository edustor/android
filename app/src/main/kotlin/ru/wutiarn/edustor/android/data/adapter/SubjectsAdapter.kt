package ru.wutiarn.edustor.android.data.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ru.wutiarn.edustor.android.R
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.data.models.Subject

class SubjectsAdapter(val appComponent: AppComponent, val listener: SubjectsAdapterEventsListener) : RecyclerView.Adapter<SubjectsAdapter.SubjectViewHolder>() {
    var subjects: List<Subject> = listOf()

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder? {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.subject_recycler_item, parent, false)

        return SubjectViewHolder(view)
    }

    override fun getItemCount(): Int {
        return subjects.count()
    }

    override fun getItemId(position: Int): Long {
        return subjects[position].hashCode().toLong()
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val subject = subjects[position]

        holder.name.text = subject.name

        holder.view.setOnClickListener {
            listener.onSubjectClick(subject)
        }
    }

    class SubjectViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById(R.id.name) as TextView
    }

    interface SubjectsAdapterEventsListener {
        fun onSubjectClick(subject: Subject)
    }

}