import util.readLines
import java.io.File
import kotlin.IllegalStateException
import kotlin.math.pow

fun main() {
    val file = File("res/day17/input.txt")
//    val file = File("res/day17/test-input-0.txt")
    challengeA(file)
    challengeB(file)
}

private fun challengeA(file: File) { // 380
    val lines = file.readLines { it }.toList()
    val pocketDimension = PocketDimension.parse(lines, 3)
    println("Challenge A:")
    println("after 0 cycles, ${pocketDimension.numberActive()} are active.")
    for (cycle in 1..6) {
        pocketDimension.cycleAll()
        println("after $cycle cycles, ${pocketDimension.numberActive()} are active.")
    }
}

private fun challengeB(file: File) { // 2332
    val lines = file.readLines { it }.toList()
    val pocketDimension = PocketDimension.parse(lines, 4)
    println("Challenge B:")
    println("after 0 cycles, ${pocketDimension.numberActive()} are active.")
    for (cycle in 1..6) {
        pocketDimension.cycleAll()
        println("after $cycle cycles, ${pocketDimension.numberActive()} are active.")
    }
}

private data class Cuboid(val coords: List<Int>) {
    val dimension = coords.size
}

private data class PocketDimension(
    val generateNewState: (Boolean, Int) -> Boolean
) {

    val adjVertices: MutableMap<Cuboid, Set<Cuboid>> = mutableMapOf()
    val cubeStates = mutableMapOf<Cuboid, Boolean>()

    fun numberActive() = adjVertices.keys.sumBy { if (cubeStates[it] == true) 1 else 0 }

    fun cycleAll(): Int {
        var numSwaps = 0
        val tempStates = cubeStates.toMutableMap()
        tempStates.keys.forEach { cube ->
            val newState = getNewState(cube, tempStates)
            val swapped = setCubeState(cube, newState)
            if (swapped) {
                numSwaps++
            }
        }
        return numSwaps
    }

    fun addVertex(newCube: Cuboid) {
        if (adjVertices.containsKey(newCube)) {
            return
        }
        if (!cubeStates.keys.contains(newCube)) {
            cubeStates[newCube] = false
        }
        val adjacents = getAdjacentCubes(newCube)
        adjVertices[newCube] = adjacents
        for (adjacentCube in adjacents) {
            if (!cubeStates.keys.contains(adjacentCube)) {
                cubeStates[adjacentCube] = false
            }
        }
    }

    fun setCubeState(cube: Cuboid, state: Boolean): Boolean {
        val currentState = cubeStates[cube] ?: throw IllegalStateException("This cube isn't in the map of states: $cube")
        if (currentState != state) {
            toggleActive(cube)
            return true
        }
        return false
    }

    fun toggleActive(cube: Cuboid) {
        if (cubeStates[cube] == false) {
            if (!adjVertices.containsKey(cube)) {
                addVertex(cube)
                cubeStates[cube] = true
            } else {
                cubeStates[cube] = true
            }
        } else if (cubeStates[cube] == true) {
            cubeStates[cube] = false
        } else {
            throw IllegalStateException("This cube isn't in the map of states: $cube")
        }
    }

    fun getAdjacentCubes(cube: Cuboid): Set<Cuboid> {
        val adjacentCubes = mutableSetOf<Cuboid>()

        var count = 0
        val currentSummands = MutableList(cube.dimension) { -1 }
        val divisors = (0 until cube.dimension).map {
            3.0.pow(it).toInt()
        }
        val totalNeeded = 3.0.pow(cube.dimension).toInt()
        while (count < totalNeeded) {
            if (!currentSummands.all { it == 0 }) {
                val adjacentCoords = cube.coords.mapIndexed { index, coord ->
                    coord + currentSummands[index]
                }
                adjacentCubes.add(Cuboid(adjacentCoords))
            }
            count++
            for ((index, divisor) in divisors.withIndex()) {
                if (count % divisor == 0) {
                    currentSummands[index] += 1
                    if (currentSummands[index] == 2) {
                        currentSummands[index] = -1
                    }
                }
            }
        }
        return adjacentCubes
    }

    private fun getNewState(cube: Cuboid, tempStates: Map<Cuboid, Boolean>): Boolean {
        val currentState = tempStates[cube] ?: throw IllegalStateException("this cube isn't in the graph: $cube")
        if (!adjVertices.containsKey(cube)) {
            addVertex(cube)
        }
        val adjacent = adjVertices[cube] ?: throw IllegalStateException("this cube isn't in the graph: $cube")
        val adjacentActive = adjacent.sumBy { if (tempStates[it] == true) 1 else 0 }
        return generateNewState(currentState, adjacentActive)
    }

    companion object {
        fun parse(serialLines: List<String>, dimensions: Int): PocketDimension {
            val pocketDimension = PocketDimension { isActive, activeAdjacent ->
                if (isActive) {
                    (2..3).contains(activeAdjacent)
                } else {
                    activeAdjacent == 3
                }
            }
            for ((y, line) in serialLines.withIndex()) {
                for ((x, char) in line.withIndex()) {
                    if (char == '#') {
                        val coords = MutableList(dimensions) { 0 }
                        coords[0] = x
                        coords[1] = y
                        val cube = Cuboid(coords)
                        pocketDimension.addVertex(cube)
                        pocketDimension.toggleActive(cube)
                    }
                }
            }
            return pocketDimension
        }
    }
}
