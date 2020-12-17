import util.readLines
import java.io.File
import kotlin.IllegalStateException

fun main() {
    val file = File("res/day17/input.txt")
//    val file = File("res/day17/test-input-0.txt")
    challengeA(file)
    challengeB(file)
}

private fun challengeA(file: File) {
    val lines = file.readLines { it }.toList()
    val pocketDimension = PocketDimension.parse(lines)
    println("Challenge A:")
    println("after 0 cycles, ${pocketDimension.numberActive()} are active.")
    for (cycle in 1..6) {
        pocketDimension.cycleAll()
        println("after $cycle cycles, ${pocketDimension.numberActive()} are active.")
    }
}

private fun challengeB(file: File) {
    val lines = file.readLines { it }.toList()
    val pocketDimension = HyperPocketDimension.parse(lines)
    println("Challenge B:")
    println("after 0 cycles, ${pocketDimension.numberActive()} are active.")
    for (cycle in 1..6) {
        pocketDimension.cycleAll()
        println("after $cycle cycles, ${pocketDimension.numberActive()} are active.")
    }
}

private data class PocketDimension(
    val generateNewState: (Boolean, Int) -> Boolean
) {

    val adjVertices: MutableMap<Cube, Set<Cube>> = mutableMapOf()
    val cubeStates = mutableMapOf<Cube, Boolean>()

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

    fun addVertex(newCube: Cube) {
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

    fun setCubeState(cube: Cube, state: Boolean): Boolean {
        val currentState = cubeStates[cube] ?: throw IllegalStateException("This cube isn't in the map of states: $cube")
        if (currentState != state) {
            toggleActive(cube)
            return true
        }
        return false
    }

    fun toggleActive(cube: Cube) {
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

    fun getAdjacentCubes(cube: Cube): Set<Cube> {
        val adjacentCubes = mutableSetOf<Cube>()
        for (x in -1..1) {
            for (y in -1..1) {
                z@ for (z in -1..1) {
                    if (x == 0 && y == 0 && z == 0) {
                        continue@z
                    }
                    adjacentCubes.add(Cube(cube.x + x, cube.y + y, cube.z + z))
                }
            }
        }
        return adjacentCubes
    }

    private fun getNewState(cube: Cube, tempStates: Map<Cube, Boolean>): Boolean {
        val currentState = tempStates[cube] ?: throw IllegalStateException("this cube isn't in the graph: $cube")
        if (!adjVertices.containsKey(cube)) {
            addVertex(cube)
        }
        val adjacent = adjVertices[cube] ?: throw IllegalStateException("this cube isn't in the graph: $cube")
        val adjacentActive = adjacent.sumBy { if (tempStates[it] == true) 1 else 0 }
        return generateNewState(currentState, adjacentActive)
    }

    companion object {
        fun parse(serialLines: List<String>): PocketDimension {
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
                        val cube = Cube(x, y, 0)
                        pocketDimension.addVertex(cube)
                        pocketDimension.toggleActive(cube)
                    }
                }
            }
            return pocketDimension
        }
    }
}

private data class Cube(val x: Int, val y: Int, val z: Int)

private data class HyperPocketDimension(
    val generateNewState: (Boolean, Int) -> Boolean
) {

    val adjVertices: MutableMap<HyperCube, Set<HyperCube>> = mutableMapOf()
    val cubeStates = mutableMapOf<HyperCube, Boolean>()

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

    fun addVertex(newCube: HyperCube) {
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

    fun setCubeState(cube: HyperCube, state: Boolean): Boolean {
        val currentState = cubeStates[cube] ?: throw IllegalStateException("This cube isn't in the map of states: $cube")
        if (currentState != state) {
            toggleActive(cube)
            return true
        }
        return false
    }

    fun toggleActive(cube: HyperCube) {
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

    fun getAdjacentCubes(cube: HyperCube): Set<HyperCube> {
        val adjacentCubes = mutableSetOf<HyperCube>()
        for (x in -1..1) {
            for (y in -1..1) {
                for (z in -1..1) {
                    w@ for (w in -1..1) {
                        if (x == 0 && y == 0 && z == 0 && w == 0) {
                            continue@w
                        }
                        adjacentCubes.add(HyperCube(cube.x + x, cube.y + y, cube.z + z, cube.w + w))
                    }
                }
            }
        }
        return adjacentCubes
    }

    private fun getNewState(cube: HyperCube, tempStates: Map<HyperCube, Boolean>): Boolean {
        val currentState = tempStates[cube] ?: throw IllegalStateException("this cube isn't in the graph: $cube")
        if (!adjVertices.containsKey(cube)) {
            addVertex(cube)
        }
        val adjacent = adjVertices[cube] ?: throw IllegalStateException("this cube isn't in the graph: $cube")
        val adjacentActive = adjacent.sumBy { if (tempStates[it] == true) 1 else 0 }
        return generateNewState(currentState, adjacentActive)
    }

    companion object {
        fun parse(serialLines: List<String>): HyperPocketDimension {
            val pocketDimension = HyperPocketDimension { isActive, activeAdjacent ->
                if (isActive) {
                    (2..3).contains(activeAdjacent)
                } else {
                    activeAdjacent == 3
                }
            }
            for ((y, line) in serialLines.withIndex()) {
                for ((x, char) in line.withIndex()) {
                    if (char == '#') {
                        val cube = HyperCube(x, y, 0, 0)
                        pocketDimension.addVertex(cube)
                        pocketDimension.toggleActive(cube)
                    }
                }
            }
            return pocketDimension
        }
    }
}

private data class HyperCube(val x: Int, val y: Int, val z: Int, val w: Int)
