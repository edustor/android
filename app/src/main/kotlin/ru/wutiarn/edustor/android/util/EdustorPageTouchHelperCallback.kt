package ru.wutiarn.edustor.android.util

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import ru.wutiarn.edustor.android.data.adapter.PagesAdapter

class EdustorPageTouchHelperCallback(val context: Context) : ItemTouchHelper.Callback() {

    override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.END
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
        return true
    }

    override fun onMoved(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, fromPos: Int, target: RecyclerView.ViewHolder?, toPos: Int, x: Int, y: Int) {
        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y)
        Log.i("EdustorHelperCallback", "Moved: $fromPos $toPos")
        val pageViewHolder = viewHolder as PagesAdapter.PageViewHolder
        pageViewHolder.adapter.onMoveItem(fromPos, toPos)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
        val pageViewHolder = viewHolder as PagesAdapter.PageViewHolder
        pageViewHolder.adapter.onRemoveItem(viewHolder)
        Log.i("EdustorHelperCallback", "removed: ${viewHolder.adapterPosition}")
    }

    override fun clearView(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?) {
        super.clearView(recyclerView, viewHolder)
        val pageViewHolder = viewHolder as PagesAdapter.PageViewHolder
        pageViewHolder.adapter.onMovementFinished()
    }
}