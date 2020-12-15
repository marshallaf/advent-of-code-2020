fun main() {
//    val input = listOf(0, 3, 6) // test
    val input = listOf(5, 2, 8, 16, 18, 0, 1) // actual
    advanceToTurnNumber(input, 2020) // challengeA -> 517
    advanceToTurnNumber(input, 30000000) // challengeB -> 1047739
}

private fun advanceToTurnNumber(input: List<Int>, goUntilTurn: Int) {
    val mostRecentTurns = mutableMapOf<Int, MostRecentTurns>()
    input.forEachIndexed { index, number ->
        val turn = index + 1
        mostRecentTurns[number] = MostRecentTurns(mostRecent = turn)
    }
    var turn = input.size + 1
    var mostRecentNumber = input.last()
    while (turn <= goUntilTurn) {
        val mostRecent = mostRecentTurns.getOrPut(mostRecentNumber) { MostRecentTurns(mostRecent = turn) }
        mostRecentNumber = if (mostRecent.isNew) {
            0
        } else {
            mostRecent.mostRecent - mostRecent.secondMostRecent
        }
        val newMostRecent = mostRecentTurns.getOrPut(mostRecentNumber) { MostRecentTurns() }
        newMostRecent.newMostRecent(turn)

        turn++
    }
    println("The number spoken on turn $goUntilTurn is $mostRecentNumber.")
}

private data class MostRecentTurns(var secondMostRecent: Int = -1, var mostRecent: Int = -1) {
    fun newMostRecent(newMostRecent: Int) {
        secondMostRecent = mostRecent
        mostRecent = newMostRecent
    }

    val isNew: Boolean
        get() = secondMostRecent == -1
}
