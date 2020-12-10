import util.readLines
import java.io.File

fun main() {
    val file = File("res/day10/input.txt")
//    val file = File("res/day10/test-input-0.txt")
//    val file = File("res/day10/test-input-1.txt")
    challengeB(file)
}

private fun challengeA(file: File) { // 2432
    val adapters = file.readLines { it.toInt() }.toList().sorted().toMutableList()
    adapters.add(0, 0)
    adapters.add(adapters[adapters.lastIndex] + 3)
    val map = mutableMapOf<Int, Int>()
    for (index in 1..adapters.lastIndex) {
        val difference = adapters[index] - adapters[index - 1]
        val newValue = map.getOrPut(difference) { 0 }
        map[difference] = newValue + 1
    }
    val jolt1 = map.getOrDefault(1, 0)
    val jolt3 = map.getOrDefault(3, 0)
    println("there are $jolt1 differences of 1-jolt and $jolt3 differences of 3-jolt, resulting in ${jolt1 * jolt3}.")
}

private fun challengeB(file: File) { // 453551299002368
    val adapters = file.readLines { it.toInt() }.toList().sorted().toMutableList()
    adapters.add(0, 0)
    val device = adapters[adapters.lastIndex] + 3
    adapters.add(device)
    val graph = Graph(adapters)
    println("starting countpaths")
    val paths = graph.countPaths(0, device)
    println("found $paths paths.")
}

private class Graph(fromList: List<Int>) {
    val vertices: Map<Int, MutableSet<Int>> = fromList.map {
        it to mutableSetOf<Int>()
    }.toMap()

    init {
        for ((index, value) in fromList.withIndex()) {
            for (optionIndex in index+1..index+3) {
                if (optionIndex > fromList.lastIndex) {
                    break
                }
                val option = fromList[optionIndex]
                if (option - value in 1..3) {
//                    println("connecting $value to $option")
                    vertices[value]?.add(option)
                }
            }
        }
        val edges = vertices.map {
            it.value.size
        }.sum()
        println("this graph has ${vertices.size} vertices and $edges edges")
        println(vertices)
    }

    fun countPaths(start: Int, end: Int, pathsFrom: MutableMap<Int, Long> = mutableMapOf()): Long {
        if (start == end) return 1
        val previous = pathsFrom[start]
        if (previous != null) {
            return previous
        }

        var paths = 0L
        for (vertex in vertices[start] ?: emptySet()) {
            paths += countPaths(vertex, end, pathsFrom)
        }
        println("found $paths from $start. completed search for ${pathsFrom.size} vertices.")
        pathsFrom[start] = paths
        return paths
    }
}

