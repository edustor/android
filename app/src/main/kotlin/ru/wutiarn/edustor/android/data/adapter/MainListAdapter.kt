package ru.wutiarn.edustor.android.data.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ru.wutiarn.edustor.android.R
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.data.models.Tag

class MainListAdapter(val appComponent: AppComponent, val listener: TagAdapterEventsListener) : RecyclerView.Adapter<MainListAdapter.TagViewHolder>() {
    var tags: List<Tag> = listOf()

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder? {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.tag_recycler_item, parent, false)

        return TagViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tags.count()
    }

    override fun getItemId(position: Int): Long {
        return tags[position].hashCode().toLong()
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val tag = tags[position]

        holder.name.text = tag.name

        holder.view.setOnClickListener {
            listener.onTagClick(tag)
        }
    }

    class TagViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById(R.id.name) as TextView
    }

    interface TagAdapterEventsListener {
        fun onTagClick(tag: Tag)
    }

}