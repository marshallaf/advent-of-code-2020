import util.readLines
import java.io.File

fun main() {
    val file = File("res/day1/input.txt")
    val lines = file.readLines { it.toInt() }.toList()
    challengeB(lines)
}

private fun challengeA(lines: List<Int>) {
    val sorted = lines.sorted()
    for ((index1, number1) in sorted.withIndex()) {
        val numberToMatch = 2020 - number1
        val indexOfMatch = sorted.binarySearch(numberToMatch)
        if (indexOfMatch >= 0 && indexOfMatch != index1) {
            val number2 = sorted[indexOfMatch]
            println("[$number1, $number2], product: ${number1 * number2}")
            break
        }
    }
}

private fun challengeB(lines: List<Int>) {
    val sorted = lines.sorted()
    outer@ for ((index1, number1) in sorted.withIndex()) {
        // ignore any that are too large, assuming zero isn't valid
        val maximum = 2020 - number1 - 1
        val indexOfMaximum = sorted.binarySearch(maximum)
        val topOfRange = if (indexOfMaximum >= 0) indexOfMaximum + 1 else -indexOfMaximum
        for ((index2, number2) in sorted.subList(0, topOfRange).withIndex()) {
            if (index1 == index2) continue

            val numberToMatch = 2020 - number1 - number2
            val indexOfMatch = sorted.binarySearch(numberToMatch, toIndex = topOfRange)
            if (indexOfMatch >= 0 && indexOfMatch != index1 && indexOfMatch != index2) {
                val number3 = sorted[indexOfMatch]
                println("[$number1, $number2, $number3], product: ${number1 * number2 * number3}")
                break@outer
            }
        }
    }
}
