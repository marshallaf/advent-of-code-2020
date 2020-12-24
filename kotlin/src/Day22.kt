import java.io.File

fun main() {
//    val inputGroup = "test-input-0"
    val inputGroup = "input"
    val serialized = File("res/day22/$inputGroup.txt").readText().split("\n\n")

    challengeA(serialized)
    challengeB(serialized)
}

private fun challengeA(serialized: List<String>) { // 33694
    val (player1, player2) = serialized.map { SpaceCardDeck.parse(it) }
    while (player1.cards.isNotEmpty() && player2.cards.isNotEmpty()) {
        playRoundRegular(player1, player2)
    }
    println(player1)
    println(player2)

    val player1Score = calculateScore(player1)
    val player2Score = calculateScore(player2)
    println("The result is Player 1: $player1Score, Player 2: $player2Score.")
}

private fun playRoundRegular(player1: SpaceCardDeck, player2: SpaceCardDeck) {
    val player1Card = player1.cards.removeFirst()
    val player2Card = player2.cards.removeFirst()
    if (player1Card > player2Card) {
        player1.cards.add(player1Card)
        player1.cards.add(player2Card)
    } else {
        player2.cards.add(player2Card)
        player2.cards.add(player1Card)
    }
//    println(player1)
//    println(player2)
}

private fun calculateScore(player: SpaceCardDeck): Int {
    var multiplier = 1
    return player.cards.reversed().fold(0) { acc, card ->
        val sum = acc + (card * multiplier)
        multiplier++
        sum
    }
}

private fun challengeB(serialized: List<String>) { // 31835
    val (player1, player2) = serialized.map { SpaceCardDeck.parse(it) }
    val winner = SpaceCardRecursiveGame(player1, player2).play()

    println(player1)
    println(player2)

    val player1Score = calculateScore(player1)
    val player2Score = calculateScore(player2)
    println("The winner is Player $winner. Scores: Player 1: $player1Score, Player 2: $player2Score.")
}

private data class SpaceCardRecursiveGame(val player1: SpaceCardDeck, val player2: SpaceCardDeck) {
    private val player1Combos = Trie()
    private val player2Combos = Trie()

    fun play(): Int {
        var roundNumber = 1
        while (player1.cards.isNotEmpty() && player2.cards.isNotEmpty()) {
            if (player1Combos.contains(player1.cards) || player2Combos.contains(player2.cards)) {
                // player1 wins automatically
                println("Player 1 wins automatically.")
                return 1
            }
            // keep track of previously-seen sequences
            player1Combos.addSequence(player1.cards)
            player2Combos.addSequence(player2.cards)

            playRound(roundNumber)
            roundNumber++
        }
        // winner is whoever still has cards
        return if (player1.cards.isNotEmpty()) 1 else 2
    }

    private fun playRound(roundNumber: Int) {
        // draw top card of deck as normal
        val player1Card = player1.cards.removeFirst()
        val player2Card = player2.cards.removeFirst()
        val winner = if (player1.cards.size >= player1Card && player2.cards.size >= player2Card) {
            // both players have enough, start a new sub-game
            val subPlayer1 = SpaceCardDeck(1, player1.cards.subList(0, player1Card).toMutableList())
            val subPlayer2 = SpaceCardDeck(2, player2.cards.subList(0, player2Card).toMutableList())
            val winnerId = SpaceCardRecursiveGame(subPlayer1, subPlayer2).play()
            if (winnerId == 1) player1 else player2
        } else {
            // the winner is the one with the higher card
            if (player1Card > player2Card) player1 else player2
        }
        val cardOrder =
            if (winner == player1) listOf(player1Card, player2Card) else listOf(player2Card, player1Card)
        winner.cards.addAll(cardOrder)
        println("end of round $roundNumber: $player1 -- $player2")
    }
}

private data class SpaceCardDeck(val player: Int, val cards: MutableList<Int>) {
    companion object {
        private val PLAYER = Regex("Player (\\d):")

        fun parse(serialized: String): SpaceCardDeck {
            var player = 0
            val cards = mutableListOf<Int>()
            serialized.split("\n").forEach {
                val match = PLAYER.matchEntire(it)
                if (match != null) {
                    player = match.groups[1]?.value?.toInt() ?: throw IllegalStateException("`$it` is weird.")
                    return@forEach
                }

                cards.add(it.toInt())
            }
            return SpaceCardDeck(player, cards)
        }
    }
}

private class Trie {
    private val topNode = TrieNode()

    fun addSequence(cards: List<Int>) {
        val list = cards.toMutableList()
        val key = list.removeFirst()
        topNode.add(key, list)
    }

    fun contains(cards: List<Int>): Boolean {
        val list = cards.toMutableList()
        val key = list.removeFirst()
        return topNode.contains(key, list)
    }

    private class TrieNode {
        private val map = mutableMapOf<Int, TrieNode>()
        var isTerminal = false

        fun add(key: Int, values: MutableList<Int>) {
            val keyNode = map.getOrPut(key) { TrieNode() }
            val nextKey = values.removeFirstOrNull()
            if (nextKey != null) {
                keyNode.add(nextKey, values)
            } else {
                keyNode.isTerminal = true
            }
        }

        fun contains(key: Int, values: MutableList<Int>): Boolean {
            val keyNode = map[key] ?: return false
            val nextKey = values.removeFirstOrNull()
            return if (nextKey != null) {
                keyNode.contains(nextKey, values)
            } else {
                keyNode.isTerminal
            }
        }
    }
}