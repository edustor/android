package ru.wutiarn.edustor.android.data.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.threeten.bp.format.DateTimeFormatter
import ru.wutiarn.edustor.android.R
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.data.models.Lesson

class LessonsAdapter(val appComponent: AppComponent, val listener: LessonsAdapterEventsListener) : RecyclerView.Adapter<LessonsAdapter.LessonViewHolder>() {
    var lessons: MutableList<Lesson> = mutableListOf()


    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder? {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.lesson_recycler_item, parent, false)

        return LessonViewHolder(view)
    }

    override fun getItemCount(): Int {
        return lessons.count()
    }

    override fun getItemId(position: Int): Long {
        return lessons[position].hashCode().toLong()
    }

    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {
        val lesson = lessons[position]

        holder.topic.text = lesson.topic ?: "No topic specified"
        holder.date.text = lesson.date.format(DateTimeFormatter.ISO_LOCAL_DATE)

        holder.view.setOnClickListener {
            listener.onLessonClick(lesson)
        }
    }

    class LessonViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        lateinit var topic: TextView
        lateinit var date: TextView

        init {
            topic = view.findViewById(R.id.topic) as TextView
            date = view.findViewById(R.id.date) as TextView
        }
    }

    interface LessonsAdapterEventsListener {
        fun onLessonClick(lesson: Lesson)
    }

}