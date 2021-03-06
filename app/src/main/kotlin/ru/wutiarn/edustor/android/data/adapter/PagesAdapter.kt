package ru.wutiarn.edustor.android.data.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.daimajia.swipe.SwipeLayout
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import ru.wutiarn.edustor.android.R
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.data.models.Page
import ru.wutiarn.edustor.android.events.RequestSnackbarEvent

class PagesAdapter(val context: Context, val appComponent: AppComponent) : RecyclerView.Adapter<PagesAdapter.PageViewHolder>() {

    var lesson: Lesson? = null
        set(value) {
            field = value
            pages = (value?.pages ?: emptyList<Page>())
                    .sortedBy(Page::index)
                    .toMutableList()
            notifyDataSetChanged()
        }

    private var pages: MutableList<Page> = mutableListOf()

    private var lastUnfinishedMovement: Pair<String, String?>? = null
    val TAG: String = "PagesAdapter"


    init {
        setHasStableIds(true)
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        val page = pages[position]

        holder.page = page
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder? {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.page_recycler_item, parent, false)

        return PageViewHolder(view, this)
    }

    override fun getItemCount(): Int {
        return pages.count()
    }


    fun onMoveItem(fromPosition: Int, toPosition: Int) {
        Log.d(TAG, "onMoveItem(fromPosition = $fromPosition, toPosition = $toPosition)")
        val page = pages[fromPosition]
        val after: Page?

        if (fromPosition > toPosition)
            after = if (toPosition > 0) pages[toPosition - 1] else null
        else
            after = pages[toPosition]

        pages.remove(page)

        val targetIndex: Int

        if (after != null) {
            targetIndex = pages.indexOf(after) + 1
        } else {
            targetIndex = 0
        }

        pages.add(targetIndex, page)

        lastUnfinishedMovement = page.id to after?.id
    }

    fun onMovementFinished() {
        lastUnfinishedMovement?.let {
            val lessonsRepo = appComponent.repo.lessons
            val lessonId = lesson!!.id
            lessonsRepo.reorderPages(lessonId, it.first, it.second)
            this.lesson = lessonsRepo.byId(lessonId)
            this.pages = lesson!!.pages
            notifyDataSetChanged()
            appComponent.eventBus.post(RequestSnackbarEvent("Successfully moved"))
        }
    }

    override fun getItemId(position: Int): Long {
        return pages[position].id.hashCode().toLong()
    }


    fun onRemoveItem(holder: PageViewHolder) {
        val page = holder.page!!

        val shortUUID = page.shortQR
        appComponent.repo.pages.delete(page.id)
        appComponent.eventBus.post(RequestSnackbarEvent("Successfully removed: $shortUUID"))

        this.pages.remove(page)
        this.notifyItemRemoved(holder.adapterPosition)
    }

    class PageViewHolder(val view: View, val adapter: PagesAdapter) : RecyclerView.ViewHolder(view) {
        // Nullable only because kotlin 1.0.3 doesn't support custom setters along with lateinit
        var page: Page? = null
            set(value) {
                field = value!!

                uuid.text = value.shortQR
                timestamp.text = LocalDateTime.ofInstant(value.timestamp, ZoneId.systemDefault())
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

                value.uploadedTimestamp?.let {
                    uploadedTimestamp.text = LocalDateTime.ofInstant(value.uploadedTimestamp, ZoneId.systemDefault())
                            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                }

                val colorResource = if (value.isUploaded) R.color.pageUploaded else R.color.pageNotUploaded
                val color = ContextCompat.getColor(view.context, colorResource)
                isUploaded.setBackgroundColor(color)

                recyclerItem.showMode = SwipeLayout.ShowMode.LayDown
                recyclerItem.isClickToClose = true

                deleteButton.setOnClickListener {
                    recyclerItem.close()
                    adapter.onRemoveItem(this)
                    Log.i("EdustorHelperCallback", "removed: $adapterPosition")
                }

            }
            get() = field

        private var uuid: TextView = view.findViewById(R.id.uuid) as TextView
        private var timestamp: TextView = view.findViewById(R.id.scanned_timestamp) as TextView
        private var uploadedTimestamp: TextView = view.findViewById(R.id.uploaded_timestamp) as TextView
        private var isUploaded: View = view.findViewById(R.id.is_uploaded)
        private var recyclerItem: SwipeLayout = view.findViewById(R.id.page_recycler_swipe_item) as SwipeLayout
        private var deleteButton: Button = view.findViewById(R.id.item_delete_btn) as Button

    }

}