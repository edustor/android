package ru.wutiarn.edustor.android.data.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableSwipeableItemViewHolder
import ru.wutiarn.edustor.android.R
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.events.RequestSnackbarEvent

/**
 * Created by wutiarn on 07.03.16.
 */
class LessonsAdapter(val appComponent: AppComponent) : RecyclerView.Adapter<LessonsAdapter.LessonViewHolder>() {
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

        holder.name.text = lesson.subject?.name
        holder.topic.text = lesson.topic ?: "No topic specified"

        holder.view.setOnClickListener {
            appComponent.eventBus.post(RequestSnackbarEvent("Opening lesson ${lesson.subject?.name}"))
        }
    }

    class LessonViewHolder(val view: View) : AbstractDraggableSwipeableItemViewHolder(view) {

        lateinit var name: TextView
        lateinit var topic: TextView

        init {
            name = view.findViewById(R.id.name) as TextView
            topic = view.findViewById(R.id.topic) as TextView
        }

        override fun getSwipeableContainerView(): View {
            return view
        }
    }

}