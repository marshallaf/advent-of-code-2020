import util.readLines
import java.io.File
import kotlin.math.pow

fun main() {
    val file = File("res/day14/input.txt")
//    val file = File("res/day14/test-input-1.txt")
    challengeA(file)
    challengeB(file)
}

private fun challengeA(file: File) { // 14839536808842
    val lines = file.readLines {
        val (key, value) = it.split(" = ")
        Pair(key, value)
    }.toList()
    val memRegex = Regex("mem\\[(\\d+)]")
    val memory = mutableMapOf<Int, Long>()
    val mask = mutableListOf<ToSet>()
    lines.forEach {
        if (it.first.startsWith("mask")) {
            mask.clear()
            it.second.forEachIndexed { index, char ->
                if (char != 'X') {
                    mask.add(ToSet(index, char))
                }
            }
        } else if (it.first.startsWith("mem")) {
            val initialValue = it.second.toLong().toString(2).padStart(36, '0').toCharArray()
            mask.forEach { toSet ->
                initialValue[toSet.index] = toSet.value
            }
            val finalValue = initialValue.joinToString(separator = "").toLong(2)

            val memIndex = memRegex.matchEntire(it.first)?.groupValues?.get(1)?.toInt() ?: throw IllegalStateException("something is wrong")
            memory[memIndex] = finalValue
        }
    }
    val sumOfAll = memory.values.sum()
    println("sum: $sumOfAll")
}

private data class ToSet(val index: Int, val value: Char)

private fun challengeB(file: File) { // 4215284199669
    val lines = file.readLines {
        val (key, value) = it.split(" = ")
        Pair(key, value)
    }.toList()
    val memRegex = Regex("mem\\[(\\d+)]")
    val memory = mutableMapOf<Long, Long>()
    val maskFloatingIndices = mutableListOf<Int>()
    var mask1s: Long = 0
    lines.forEach {
        println("parsing [$it]")
        if (it.first.startsWith("mask")) {
            maskFloatingIndices.clear()
            it.second.forEachIndexed { index, char ->
                if (char == 'X') {
                    maskFloatingIndices.add(it.second.lastIndex - index)
                }
            }
            mask1s = it.second.replace('X', '0').toLong(2)
        } else if (it.first.startsWith("mem")) {
            val toWrite = it.second.toLong()//.toString(2).padStart(36, '0').toCharArray()
            val startMemIndex = memRegex.matchEntire(it.first)?.groupValues?.get(1)?.toLong() ?: throw IllegalStateException("something is wrong")
            val after1s = startMemIndex or mask1s
            val numberOfFloating = maskFloatingIndices.size
            val numberOfPermutations = 2.0.pow(numberOfFloating).toInt()
            val permutations = MutableList(numberOfPermutations) { after1s }
            for (floatingIndex in 0 until numberOfFloating) {
                var setValue = 0
                for (permIndex in 0 until numberOfPermutations) {
                    if (permIndex % 2.0.pow(floatingIndex).toInt() == 0) {
                        setValue = if (setValue == 0) 1 else 0
                    }
                    val indexToSet = maskFloatingIndices[maskFloatingIndices.lastIndex - floatingIndex]
                    permutations[permIndex] = permutations[permIndex].setBit(indexToSet, setValue)
                }
            }
            for (permutation in permutations) {
                memory[permutation] = toWrite
            }
        }
    }
    val sumOfAll = memory.values.sum()
    println("sum: $sumOfAll")
}

private fun Long.setBit(rightIndex: Int, setValue: Int): Long {
    val toSet = 1L shl rightIndex
    val mask = setValue.toLong() shl rightIndex
    val intermediate = this and toSet.inv()
    val other = mask and toSet
    return intermediate or other
}

private fun Long.binaryPrint(): String {
    return this.toString(2).padStart(36, '0')
}