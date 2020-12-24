import util.readLines
import java.io.File

fun main() {
    val inputGroup = "test-input-0"
//    val inputGroup = "input"
    val tiles = File("res/day20/$inputGroup.txt")
        .readText()
        .split("\n\n")
        .map { tileSerial ->
            val (title, grid) = tileSerial.split(":\n")
            val gridList = grid.split("\n")
            val tileId = title.replace("Tile ", "").toInt()
            SatelliteImageTile.parse(tileId, gridList)
        }
    challengeA(tiles)
    challengeB(tiles)
}

private fun challengeA(tiles: List<SatelliteImageTile>) { //
    tiles.map { SatelliteImageSides.parse(it) }
        .forEach {
            println(it)
        }
}

private fun challengeB(tiles: List<SatelliteImageTile>) { //
}

private data class SatelliteImageTile(val tileId: Int, val grid: List<List<Int>>) {

    init {
        if (grid.size != grid[0].size) throw IllegalStateException("the grid must be a square")
    }

    val size = grid.size

    fun printTile() {
        println("Tile ID $tileId")
        grid.forEach {
            println(it.joinToString(""))
        }
    }

    companion object {
        fun parse(tileId: Int, serialized: List<String>): SatelliteImageTile {
            val grid = serialized.map { rowString ->
                rowString.map {
                    when (it) {
                        '.' -> 0
                        '#' -> 1
                        else -> throw IllegalArgumentException("$it is not a valid character.")
                    }
                }
            }
            return SatelliteImageTile(tileId, grid)
        }
    }
}

private data class SatelliteImageSides(val tileId: Int, val top: Int, val right: Int, val bottom: Int, val left: Int) {
    val sidesAsList = listOf(top, right, bottom, left)

    companion object {
        fun parse(tile: SatelliteImageTile): SatelliteImageSides {
            val lastIndex = tile.size - 1
            val top = tile.grid[0].reversed().intsToBits()
            val right = tile.grid.reversed().map { it[lastIndex] }.intsToBits()
            val bottom = tile.grid[lastIndex].intsToBits()
            val left = tile.grid.map { it[0] }.intsToBits()
            return SatelliteImageSides(tile.tileId, top, right, bottom, left)
        }
    }
}

private fun List<Int>.intsToBits(): Int {
    var result = 0
    forEach {
        result = result shl 1
        when (it) {
            1 -> result = result or 1
            0 -> { /** no op **/ }
            else -> throw IllegalArgumentException("found $it, only 0 or 1 are acceptable")
        }
    }
    return result
}

private fun reversedBits(number: Int): Int {
    var toReverse = number
    var reversed = 0
    var bitCount = 0
    while (bitCount < 10) {
        reversed = reversed shl 1
        if ((toReverse and 1) == 1) {
            reversed = reversed or 1
        }
        toReverse = toReverse shr 1
        bitCount++
    }
    return reversed
}