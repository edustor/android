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
import ru.wutiarn.edustor.android.data.models.MainListEntity
import ru.wutiarn.edustor.android.data.models.Tag

class MainListEntityAdapter(val appComponent: AppComponent, val listener: MainListEntityAdapterEventsListener) : RecyclerView.Adapter<MainListEntityAdapter.MainListEntityHolder>() {
    var entities: List<MainListEntity> = emptyList()

    init {
        setHasStableIds(true)
    }


    enum class ViewType {
        TAG,
        LESSON
    }

    override fun getItemViewType(position: Int): Int {
        return when(entities[position]) {
            is Tag -> ViewType.TAG.ordinal
            is Lesson -> ViewType.LESSON.ordinal
            else -> throw IllegalStateException("Unknown MainListEntity subtype")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainListEntityHolder? {
        when (viewType) {
            ViewType.TAG.ordinal -> {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.tag_recycler_item, parent, false)
                return TagViewHolder(view)
            }
            ViewType.LESSON.ordinal -> {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.lesson_recycler_item, parent, false)

                return LessonViewHolder(view)
            }
            else -> throw IllegalStateException("Unknown ViewType")
        }
    }

    override fun getItemCount(): Int {
        return entities.count()
    }

    override fun getItemId(position: Int): Long {
        return entities[position].hashCode().toLong()
    }

    override fun onBindViewHolder(holder: MainListEntityHolder?, position: Int) {
        when(holder) {
            is TagViewHolder -> {
                val tag = entities[position] as Tag
                holder.name.text = tag.name
                holder.view.setOnClickListener {
                    listener.onTagClick(tag)
                }
            }
            is LessonViewHolder -> {
                val lesson = entities[position] as Lesson

                holder.topic.text = lesson.topic ?: "No topic specified"
                holder.date.text = lesson.date.format(DateTimeFormatter.ISO_LOCAL_DATE)

                holder.view.setOnClickListener {
                    listener.onLessonClick(lesson)
                }
            }
        }
    }

    open class MainListEntityHolder(val view: View) : RecyclerView.ViewHolder(view)

    class TagViewHolder(view: View) : MainListEntityHolder(view) {
        var name: TextView = view.findViewById(R.id.name) as TextView
    }

    class LessonViewHolder(view: View) : MainListEntityHolder(view) {
        var topic: TextView = view.findViewById(R.id.topic) as TextView
        var date: TextView = view.findViewById(R.id.date) as TextView

    }

    interface MainListEntityAdapterEventsListener {
        fun onTagClick(tag: Tag)
        fun onLessonClick(lesson: Lesson)
    }
}