package model

enum class FieldEvent {OPEN, MARK, UNMARK, EXPLOSION, RESTART}

data class Field(val line: Int, val column: Int) {
    private val neighbourFields = ArrayList<Field>()
    private val callbacks = ArrayList<(Field, FieldEvent) -> Unit>()

    var marked: Boolean = false
    var opened: Boolean = false
    var mineField: Boolean = false

    val unmarked: Boolean get() = !marked
    val closed: Boolean get() = !opened
    val safe: Boolean get() = !mineField
    val completed: Boolean get() = safe && opened || mineField && marked
    val minedNeighbourFields: Int get() = neighbourFields.filter { it.mineField }.size
    val safeNeighborhood: Boolean get() = neighbourFields.map { it.safe }.reduce { result, safe -> result && safe }

    fun addNeighbour(neighbour: Field) {
        neighbourFields.add(neighbour)
    }

    fun onEvent(callback: (Field, FieldEvent) -> Unit) {
        callbacks.add(callback)
    }

    fun open() {
        if(closed) {
            opened = true
            if(mineField) {
                callbacks.forEach { it(this, FieldEvent.EXPLOSION) }
            } else {
                callbacks.forEach { it(this, FieldEvent.OPEN) }
                neighbourFields.filter {it.closed && it.safe && safeNeighborhood}.forEach {it.open()}
            }
        }
    }

    fun changeMark() {
        if(closed) {
            marked = !marked
            val event = if(marked) FieldEvent.MARK else FieldEvent.UNMARK
            callbacks.forEach { it(this, event) }
        }
    }

    fun mineIt() {
        mineField = true
    }

    fun restart() {
        opened = false
        mineField = false
        marked = false

        callbacks.forEach { it(this, FieldEvent.RESTART) }
    }
}