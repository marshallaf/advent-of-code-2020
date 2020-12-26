fun main() {
    val testInput = listOf(3, 8, 9, 1, 2, 5, 4, 6, 7)
    val realInput = listOf(9, 5, 2, 4, 3, 8, 7, 1, 6)
//    val input = testInput
    val input = realInput
    challengeA(input)
}

private fun challengeA(input: List<Int>) {
    val game = CupGameA(input)
    for (moveNumber in 1..100) {
        game.executeMove(moveNumber)
    }
    game.printFinal()
}

private class CupGameA(input: List<Int>) {
    private var currentCupIndex = 0
    private val cups = input.toMutableList()
    private val largestLabel = cups.maxOf { it }

    private fun nextIndexClockwise(startIndex: Int, incrementBy: Int = 1): Int {
        return (startIndex + incrementBy) % cups.size
    }

    private fun removeRangeClockwise(range: IntRange): List<Int> {
        if (range.last <= cups.lastIndex) {
            return cups.removeRange(range)
        }
        val firstRange = range.first..cups.lastIndex
        val secondRange = 0..(range.last % cups.size)
        val firstSubList = cups.removeRange(firstRange)
        val secondSubList = cups.removeRange(secondRange)
        return firstSubList + secondSubList
    }

    private fun removeNextClockwise(startIndex: Int, amount: Int): List<Int> {
        return removeRangeClockwise(startIndex until startIndex + amount)
    }

    private fun findDestinationCupIndex(currentCup: Int): Int {
        var destinationCupIndex: Int = -1
        var subtraction = 1
        var starting = currentCup
        while (destinationCupIndex == -1) {
            val cupToFind = starting - subtraction
            destinationCupIndex = cups.indexOfFirst { it == cupToFind }
            if (cupToFind < 1) {
                starting = largestLabel
                subtraction = 0
            } else {
                subtraction++
            }
        }
        return destinationCupIndex
    }

    fun executeMove(moveNumber: Int) {
        val currentCup = cups[currentCupIndex]
        println("-- move $moveNumber --")
        println("cups: ${cups.joinToString(" ") { if (it == currentCup) "($it)" else "$it" }}")
        val pickedUp = removeNextClockwise(nextIndexClockwise(currentCupIndex), 3)
        println("pick up: ${pickedUp.joinToString(", ")}")
        val destinationCupIndex = findDestinationCupIndex(currentCup)
        val destinationCup = cups[destinationCupIndex]
        println("destination: $destinationCup")
        cups.addAll(destinationCupIndex + 1, pickedUp)
        currentCupIndex = cups.indexOfFirst { it == currentCup }
        currentCupIndex = nextIndexClockwise(currentCupIndex)
        println()
    }

    fun printFinal() {
        val currentCup = cups[currentCupIndex]
        println("-- final --")
        println("cups: ${cups.joinToString(" ") { if (it == currentCup) "($it)" else "$it" }}")
        println("-- sequence after cup 1 --")
        val cup1Index = cups.indexOfFirst { it == 1 }
        var index = nextIndexClockwise(cup1Index)
        var sequence = ""
        while (index != cup1Index) {
            sequence += cups[index]
            index = nextIndexClockwise(index)
        }
        println(sequence)
    }
}

private fun <E> MutableList<E>.removeRange(range: IntRange): List<E> {
    val removed = mutableListOf<E>()
    for (index in range.reversed()) {
        removed.add(this.removeAt(index))
    }
    return removed.reversed()
}