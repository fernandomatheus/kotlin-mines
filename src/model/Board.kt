package model

import java.util.*

enum class BoardEvent {VICTORY, DEFEAT}

class Board(val numLines: Int, val numColumns: Int, private val numMines: Int) {
    private val fields = ArrayList<ArrayList<Field>>()
    private val callbacks = ArrayList<(BoardEvent) -> Unit>()

    init {
        craeteFields()
        associateNeighbors()
        sortMines()
    }

    private fun craeteFields() {
        for(line in 0 until numLines) {
            fields.add(ArrayList())
            for(column in 0 until numColumns) {
                val newField = Field(line, column)
                fields[line].add(newField)
            }
        }
    }

    private fun associateNeighbors() {
        forEachField { associateNeighbors(it) }
    }

    private fun associateNeighbors(field: Field) {
        val (line, column) = field
        val lines = arrayOf(line - 1, line, line + 1)
        val columns = arrayOf(column - 1, column, column + 1)

        lines.forEach { l ->
            columns.forEach { c ->
                val current = fields.getOrNull(l)?.getOrNull(c)
                current?.takeIf { field != it }?.let { field.addNeighbour(it) }
            }
        }
    }

    private fun sortMines() {
        val generator = Random()

        var lineSorted = -1
        var columnSorted = -1
        var currentMines = 0

        while(currentMines < this.numMines) {
            lineSorted = generator.nextInt(numLines)
            columnSorted = generator.nextInt(numColumns)

            val fieldSorted = fields[lineSorted][columnSorted]
            if(fieldSorted.safe) {
                fieldSorted.mineIt()
                currentMines++
            }
        }
    }

    fun forEachField(callback: (Field) -> Unit) {
        fields.forEach { line -> line.forEach { callback } }
    }
}