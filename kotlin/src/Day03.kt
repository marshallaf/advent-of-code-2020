import util.readLines
import java.io.File

fun main() {
    val file = File("res/day03/input.txt")
//    val file = File("res/day03/test-input-0.txt")
    challengeA(file)
    challengeB(file)
}

private fun challengeA(file: File) { // 262
    val slopeRight = 3
    val slopeDown = 1
    val numberOfTreesHit = traverseSlope(file, slopeRight, slopeDown)
    println("hit $numberOfTreesHit trees")
}

private fun challengeB(file: File) { // 2698900776
    val slopes = listOf(
        1 to 1,
        3 to 1,
        5 to 1,
        7 to 1,
        1 to 2
    )
    val product = slopes.map {
        val numberOfTreesHit = traverseSlope(file, it.first, it.second)
        println("for [${it.first}, ${it.second}], you hit $numberOfTreesHit trees.")
        numberOfTreesHit
    }.reduce { acc, i -> acc * i }
    println("the product of trees that you hit is $product")
}

private fun traverseSlope(file: File, slopeRight: Int, slopeDown: Int): Long {
    var index = 0
    var row = slopeDown
    val mapWidth = file.readLines { it.trim().length }.take(1).sum()
//    println("map width is $mapWidth")
    return file.readLines { parseTreePositions(it) }
        .mapIndexed { currentRow, positions ->
            if (currentRow != row) {
                return@mapIndexed 0
            } else {
                row += slopeDown
            }

            index = adjustIndex(index, mapWidth, slopeRight)
            if (positions.contains(index)) {
//                println("hit tree at position $index in currentRow $currentRow")
                1
            } else {
//                println("position $index in currentRow $currentRow was clear")
                0
            }
        }
        .sum().toLong()
}

private fun parseTreePositions(line: String): Set<Int> {
    val positions = mutableSetOf<Int>()
    for ((index, char) in line.withIndex()) {
        if (char == '#') {
            positions.add(index)
        }
    }
    return positions
}

private fun adjustIndex(startingIndex: Int, mapWidth: Int, slopeRight: Int): Int {
    var index = startingIndex + slopeRight
    index %= mapWidth
    return index
}