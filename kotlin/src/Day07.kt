import util.readLines
import java.io.File

fun main() {
    val file = File("res/day07/input.txt")
//    val file = File("res/day07/test-input-1.txt")
    challengeA(file)
    challengeB(file)
}

private fun challengeA(file: File) { // 316
    val graph = mutableMapOf<String, MutableSet<String>>()
    file.readLines {
        it.replace(".", "")
            .replace(Regex("bags?"), "")
            .replace(Regex("\\d"), "")
            .split("contain", ",")
            .map { it.trim() }
    }.forEach { bags ->
        for (bagIndex in 1..bags.lastIndex) {
            val containerSet = graph.getOrPut(bags[bagIndex]) { mutableSetOf() }
            containerSet.add(bags[0])
        }
    }
    val validContainers = mutableSetOf<String>()
    val toVisit = mutableListOf<String>()
    graph["shiny gold"]?.also {
        validContainers.addAll(it)
        toVisit.addAll(it)
    }
    while (toVisit.isNotEmpty()) {
        val currentNode = toVisit.removeAt(0)
        val containers = graph[currentNode] ?: continue
        validContainers.addAll(containers)
        toVisit.addAll(containers)
    }
    println(validContainers.size)
}

private fun challengeB(file: File) { // 11310
    val graph = mutableMapOf<String, MutableMap<String, Int>>()
    file.readLines {
        it.replace(".", "")
            .replace(Regex("bags?"), "")
            .split("contain", ",")
            .map { it.trim() }
            .filterNot { it == "no other" }
    }.forEach { bags ->
        for (bagIndex in 1..bags.lastIndex) {
            val containerSet = graph.getOrPut(bags[0]) { mutableMapOf() }
            val amount = bags[bagIndex].substringBefore(' ').toInt()
            val bag = bags[bagIndex].substringAfter(' ')
            containerSet[bag] = amount
        }
    }
    var contained = 0
    val toVisit = mutableListOf<Pair<String, Int>>()
    graph["shiny gold"]?.also {
        contained += it.values.sum()
        toVisit.addAll(it.toList())
    }
    while (toVisit.isNotEmpty()) {
        val currentNode = toVisit.removeAt(0)
        val bagsInside = graph[currentNode.first]?.toList() ?: continue
        bagsInside.forEach {
            val toAdd = it.second * currentNode.second
            contained += toAdd
        }
        val bagsInsideMultiplied = bagsInside.map {
            Pair(it.first, it.second * currentNode.second)
        }
        toVisit.addAll(bagsInsideMultiplied)
    }
    println(contained)
}