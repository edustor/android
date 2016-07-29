package ru.wutiarn.edustor.android.data.models.util.sync

class SyncTask() {
    lateinit var method: String
    lateinit var params: Map<String, Any?>
    var index: Int? = null

    /**
     * NOTE: Should be instantly saved to realm after construction to prevent index duplication
     */
    constructor(method: String, params: Map<String, Any?>) : this() {
        this.method = method
        this.params = params
    }
}