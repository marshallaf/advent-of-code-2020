import util.readLines
import java.io.File
import java.lang.IllegalArgumentException

fun main() {
    val file = File("res/day5/input.txt")
//    val file = File("res/day5/test-input-0.txt")
    val lines = file.readLines { BoardingPass.parse(it) }
    challengeB(lines)
}

private fun challengeA(lines: Sequence<BoardingPass>) { // 970
    val maxId = lines.maxOf {
        it.seatId
    }
    println("The maximum seat id is $maxId.")
}

private fun challengeB(lines: Sequence<BoardingPass>) { // 587
    var lastId = -1
    val mySeatId = lines.sortedBy {
        it.seatId
    }.first {
        val isSeatNextToMine = lastId != -1 && lastId != it.seatId - 1
        lastId = it.seatId
        isSeatNextToMine
    }.seatId - 1
    println("My seat id is $mySeatId.")
}

private data class BoardingPass(val row: Int, val column: Int, val seatId: Int, val serialized: String) {
    companion object {
        fun parse(serialized: String): BoardingPass {
            val rowSection = serialized.substring(0, 7)
            val columnSection = serialized.substring(7)
            var rowRange = Pair(0, 127)
            for (char in rowSection) {
                rowRange = when (char) {
                    'F' -> rowRange.lowerHalf()
                    'B' -> rowRange.upperHalf()
                    else -> throw IllegalArgumentException("[$serialized] is malformed.")
                }
            }
            var columnRange = Pair(0, 7)
            for (char in columnSection) {
                columnRange = when (char) {
                    'L' -> columnRange.lowerHalf()
                    'R' -> columnRange.upperHalf()
                    else -> throw IllegalArgumentException("[$serialized] is malformed.")
                }
            }
            if (rowRange.first != rowRange.second) throw IllegalStateException("[$serialized] is goofy for rows: $rowRange.")
            if (columnRange.first != columnRange.second) throw IllegalStateException("[$serialized] is goofy for columns: $columnRange.")
            val row = rowRange.first
            val column = columnRange.first
            val seatId = row * 8 + column
            return BoardingPass(row, column, seatId, serialized)
        }
    }
}

private fun Pair<Int, Int>.lowerHalf(): Pair<Int, Int> {
    return Pair(first, (second - first) / 2 + first)
}

private fun Pair<Int, Int>.upperHalf(): Pair<Int, Int> {
    return Pair((second - first) / 2 + first + 1, second)
}