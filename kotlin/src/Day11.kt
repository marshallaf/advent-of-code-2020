import util.readLines
import java.io.File
import java.lang.IllegalStateException

fun main() {
    val file = File("res/day11/input.txt")
//    val file = File("res/day11/test-input-0.txt")
    challengeA(file)
    challengeB(file)
}

private fun challengeA(file: File) { // 2296
    val rowsSerialized = file.readLines { it }.toList()
    val layout = SeatingChart.parseLayoutForAdjacentSeats(rowsSerialized)
    layout.stabilize(false)
    println("The seating chart has stabilized with ${layout.numberOccupied()} occupied seats.")
}

private fun challengeB(file: File) { // 2089
    val rowsSerialized = file.readLines { it }.toList()
    val layout = SeatingChart.parseLayoutForVisibleSeats(rowsSerialized)
    layout.stabilize(false)
    println("The seating chart has stabilized with ${layout.numberOccupied()} occupied seats.")
}

private data class SeatingChart(
    val adjVertices: Map<Seat, Set<Seat>>,
    val maxRow: Int,
    val maxCol: Int,
    val shouldSwap: (Seat, Int) -> Boolean
) {

    fun numberOccupied() = adjVertices.keys.sumBy { if (it.occupied) 1 else 0 }

    fun cycleAll(): Int {
        var numSwaps = 0
        val tempVertices = deepCopyVertices()
        adjVertices.forEach {
            if (checkShouldSwap(it.key, tempVertices)) {
                numSwaps++
                it.key.occupied = !it.key.occupied
            }
        }
        return numSwaps
    }

    fun stabilize(printSteps: Boolean = false) {
        while (cycleAll() != 0) {
            if (printSteps) println(this)
        }
    }

    private fun deepCopyVertices(): Map<Seat, Set<Seat>> {
        return adjVertices.mapKeys {
            it.key.copy()
        }.mapValues {
            it.value.map { it.copy() }.toSet()
        }
    }

    private fun checkShouldSwap(seat: Seat, tempVertices: Map<Seat, Set<Seat>>): Boolean {
        val adjacent = tempVertices[seat] ?: throw IllegalStateException("this seat isn't in the graph.")
        val adjacentOccupied = adjacent.sumBy { if (it.occupied) 1 else 0 }
        return shouldSwap(seat, adjacentOccupied)
    }

    override fun toString(): String {
        val sb = StringBuilder("")
        for (row in 0..maxRow) {
            for (col in 0..maxCol) {
                val vertex = adjVertices.keys.find { it == Seat(Seat.Coord(row, col)) }
                val char = when {
                    vertex == null -> '.'
                    vertex.occupied -> '#'
                    else -> 'L'
                }
                sb.append(char)
            }
            sb.append('\n')
        }
        return sb.toString()
    }

    companion object {
        fun parseLayoutForAdjacentSeats(rows: List<String>): SeatingChart {
            val maxRow = rows.lastIndex
            val maxCol = rows[0].lastIndex
            val keeper = (0..maxRow).map { row ->
                (0..maxCol).map { col ->
                    Seat(Seat.Coord(row, col))
                }
            }
            val vertices = mutableMapOf<Seat, Set<Seat>>()
            for ((rowIndex, row) in rows.withIndex()) {
                for ((colIndex, column) in row.withIndex()) {
                    if (column == 'L') {
                        val node = keeper[rowIndex][colIndex]
                        val adjacent = mutableSetOf<Seat>()
                        for (adjRow in rowIndex-1..rowIndex+1) {
                            if (adjRow < 0 || adjRow > rows.lastIndex) continue
                            for (adjCol in colIndex-1..colIndex+1) {
                                if (adjCol == colIndex && adjRow == rowIndex) continue
                                if (adjCol < 0 || adjCol > row.lastIndex) continue
                                if (rows[adjRow][adjCol] == 'L') {
                                    adjacent.add(keeper[adjRow][adjCol])
                                }
                            }
                        }
                        vertices[node] = adjacent
                    }
                }
            }
            val shouldSwap = { seat: Seat, adjacentOccupied: Int ->
                if (seat.occupied) {
                    adjacentOccupied >= 4
                } else {
                    adjacentOccupied == 0
                }
            }
            return SeatingChart(vertices, maxRow, maxCol, shouldSwap)
        }

        fun parseLayoutForVisibleSeats(rows: List<String>): SeatingChart {
            val maxRow = rows.lastIndex
            val maxCol = rows[0].lastIndex
            val keeper = (0..maxRow).map { row ->
                (0..maxCol).map { col ->
                    Seat(Seat.Coord(row, col))
                }
            }
            val vertices = mutableMapOf<Seat, Set<Seat>>()
            for ((rowIndex, row) in rows.withIndex()) {
                for ((colIndex, column) in row.withIndex()) {
                    if (column == 'L') {
                        val node = keeper[rowIndex][colIndex]

                        val adjacent = mutableSetOf<Seat>()
                        for (drow in -1..1) {
                            for (dcol in -1..1) {
                                val coord = findInDirection(rows, Seat.Coord(rowIndex, colIndex), drow, dcol)
                                if (coord != null) {
                                    adjacent.add(keeper[coord.row][coord.column])
                                }
                            }
                        }

                        vertices[node] = adjacent
                    }
                }
            }
            val shouldSwap = { seat: Seat, adjacentOccupied: Int ->
                if (seat.occupied) {
                    adjacentOccupied >= 5
                } else {
                    adjacentOccupied == 0
                }
            }
            return SeatingChart(vertices, maxRow, maxCol, shouldSwap)
        }

        fun findInDirection(
            rows: List<String>,
            coord: Seat.Coord,
            drow: Int,
            dcol: Int
        ): Seat.Coord? {
            if (drow == 0 && dcol == 0) return null

            var currentRow = coord.row + drow
            var currentCol = coord.column + dcol
            while (currentRow >= 0 && currentRow <= rows.lastIndex && currentCol >= 0 && currentCol <= rows[0].lastIndex) {
                if (rows[currentRow][currentCol] == 'L') {
                    return Seat.Coord(currentRow, currentCol)
                }
                currentRow += drow
                currentCol += dcol
            }
            return null
        }
    }
}

private data class Seat(val coord: Coord, var occupied: Boolean = false) {

    override fun equals(other: Any?): Boolean {
        return other is Seat && coord == other.coord
    }

    data class Coord(val row: Int, val column: Int)
}
