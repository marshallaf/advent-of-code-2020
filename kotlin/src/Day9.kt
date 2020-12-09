import util.readLines
import java.io.File
import kotlin.math.max
import kotlin.math.min

fun main() {
    val file = File("res/day9/input.txt")
//    val file = File("res/day9/test-input-0.txt")
    val numbers = file.readLines { it.toLong() }.toList()
    val offender = challengeA(numbers, 25)
    println("Challenge A: the offending number is $offender.")
    val summingSequenceExtremeSum = challengeB(numbers, offender)
    println("Challenge B: the sum of the largest and smallest members of the " +
            "sequence that sums to the offender is $summingSequenceExtremeSum.")
}

private fun challengeA(numbers: List<Long>, preambleSize: Int): Long { // 14144619
    val preamble = numbers.subList(0, preambleSize).toMutableList()
    for (index in preambleSize..numbers.lastIndex) {
        val nextNumber = numbers[index]
        if (!preamble.containsSummandsFor(nextNumber)) {
            return nextNumber
        }
        preamble.removeAt(0)
        preamble.add(nextNumber)
    }
    return -1
}

private fun List<Long>.containsSummandsFor(target: Long): Boolean {
    val opposites = mutableSetOf<Long>()
    forEach {
        if (opposites.contains(target - it)) {
            return true
        }
        opposites.add(it)
    }
    return false
}

private fun challengeB(numbers: List<Long>, target: Long): Long { // 1766397
    // this as a separate function ended up being redundant, but keeping it for organization+naming
    return numbers.findSummingSequence(target)
}

private fun List<Long>.findSummingSequence(target: Long): Long {
    for (startIndex in 0..lastIndex) {
        var sum = this[startIndex]
        var minMax = setMinMax(Pair(Long.MAX_VALUE, -1L), sum)
        var offset = 1
        while (sum < target) {
            val newValue = this[startIndex + offset]
            sum += newValue
            minMax = setMinMax(minMax, newValue)
            offset++
        }
        if (sum == target) {
//            println("largest and smallest are $minMax")
            return minMax.first + minMax.second
        }
    }
    return -1
}

private fun setMinMax(original: Pair<Long, Long>, newValue: Long): Pair<Long, Long> {
    val newMin = min(original.first, newValue)
    val newMax = max(original.second, newValue)
    return Pair(newMin, newMax)
}