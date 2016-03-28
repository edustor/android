package ru.wutiarn.edustor.android.util

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

abstract class EndlessRecyclerViewScrollListener(val linearLayoutManager: LinearLayoutManager) : RecyclerView.OnScrollListener() {

    val visibleThreshold = 3
    var currentPage = 0
    var previousTotalCount = 0
    var loading = true

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        val firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition()
        val visibleCount = linearLayoutManager.childCount
        val totalCount = linearLayoutManager.itemCount

        if (totalCount < previousTotalCount) {
            this.currentPage = 0
            this.previousTotalCount = totalCount
            if (totalCount == 0) {
                loading = true
            }
        }

        if (loading && (totalCount > previousTotalCount)) {
            loading = false
            previousTotalCount = totalCount
        }

        if (!loading && (totalCount - visibleCount) <= (firstVisibleItem + visibleThreshold)) {
            currentPage++
            onLoadMore(currentPage)
            loading = true
        }

    }

    abstract fun onLoadMore(page: Int)
}