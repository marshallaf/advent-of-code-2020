import util.readLines
import java.io.File

fun main() {
    val file = File("res/day13/input.txt")
//    val file = File("res/day13/test-input-0.txt")
    challengeA(file)
    challengeB3(file)
}

private fun challengeA(file: File) { // 205
    val lines = file.readLines { it }.toList()
    val timeStampAtBusStop = lines[0].toLong()
    val busNumbers = lines[1].split(',').filterNot { it == "x" }.map { it.toInt() }
    var nextBus = -1
    var minimumDeparture = Long.MAX_VALUE
    for (busNumber in busNumbers) {
        val busDeparture = nextDeparture(timeStampAtBusStop, busNumber)
        if (busDeparture < minimumDeparture) {
            nextBus = busNumber
            minimumDeparture = busDeparture
        }
    }
    println("The next bus you can take is $nextBus at $minimumDeparture. The product is ${nextBus * (minimumDeparture - timeStampAtBusStop)}")
}

fun nextDeparture(timeStamp: Long, busNumber: Int): Long {
    return timeStamp + (busNumber - timeStamp % busNumber)
}

private fun challengeB(file: File) { //
    val lines = file.readLines { it }.toList()
    val busNumbers = lines[1].split(',').map { if (it == "x") -1 else it.toInt() }
    val firstBus = busNumbers[0].toLong()
    var firstBusIteration = 0L
    var finalT = -1L
    mainloop@ while (finalT == -1L) {
        val t = firstBus * firstBusIteration
        if (firstBusIteration % 100000 == 0L) {
            println("iteration: $firstBusIteration, timestamp: $t")
        }
        firstBusIteration++
        busIteration@ for (busIndex in 1..busNumbers.lastIndex) {
            val currentBus = busNumbers[busIndex]
            if (currentBus == -1) {
                continue@busIteration
            }
            if ((t + busIndex) % currentBus != 0L) {
                continue@mainloop
            }
            if (busIndex == busNumbers.lastIndex) {
                finalT = t
            }
        }
    }
    println("The earliest timestamp in which the busses leave consecutively is $finalT.")
}

private fun challengeB2(file: File) { //
    val lines = file.readLines { it }.toList()
    val busNumbers = lines[1].split(',').map { if (it == "x") -1 else it.toInt() }
    val largestBusNumber = busNumbers.maxOf { it }.toLong()
    val largestBusIndex = busNumbers.indexOfFirst { it.toLong() == largestBusNumber }
    var largestBusIteration = 0L
    var finalT = -1L
    mainloop@ while (finalT == -1L) {
        val t = largestBusNumber * largestBusIteration - largestBusIndex
        if (largestBusIteration % 100000 == 0L) {
            println("iteration: $largestBusIteration, timestamp: $t")
        }
//        if (t > 1068781L) {
//            println()
//        }
        largestBusIteration++
        // before largest bus
        val beforeLargestBusIndex = largestBusIndex - 1
        busIterationBefore@ for (busIndex in beforeLargestBusIndex downTo 0) {
            val currentBus = busNumbers[busIndex]
            if (currentBus == -1) {
                continue@busIterationBefore
            }
            if ((t + busIndex) % currentBus != 0L) {
                continue@mainloop
            }
        }
        // after largest bus
        val afterLargestBusIndex = largestBusIndex + 1
        busIterationAfter@ for (busIndex in afterLargestBusIndex..busNumbers.lastIndex) {
            val currentBus = busNumbers[busIndex]
            if (currentBus == -1) {
                continue@busIterationAfter
            }
            if ((t + busIndex) % currentBus != 0L) {
                continue@mainloop
            }
            if (busIndex == busNumbers.lastIndex) {
                finalT = t
            }
        }
    }
    println("The earliest timestamp in which the busses leave consecutively is $finalT.")
}

// see https://en.wikipedia.org/wiki/Extended_Euclidean_algorithm
// and https://en.wikipedia.org/wiki/Chinese_remainder_theorem
private fun challengeB3(file: File) { // 803025030761664
    val lines = file.readLines { it }.toList()
    val modEquations = lines[1].split(',')
        .mapIndexedNotNull { index, busNumberSerial ->
            if (busNumberSerial == "x") {
                return@mapIndexedNotNull null
            }
            val busNumber = busNumberSerial.toLong()
            ModEquation(-index.toLong(), busNumber)
        }
    val bigN = modEquations.fold(1L) { acc, modEquation ->
        modEquation.n * acc
    }
    println("bigN: $bigN") // check
    val ys = modEquations.map {
        bigN / it.n
    }
    println(ys) // check
    val bezouts = ys.mapIndexed { index, y ->
        extendedEuclid(modEquations[index].n, y)
    }
    println(bezouts)
    val summands = bezouts.mapIndexed { index, bezout ->
        bezout.second * ys[index] * modEquations[index].a
    }
    println(summands)
    val sum = summands.sum()
    var answer = sum
    if (answer < 0) {
        while (answer < 0) {
            answer += bigN
        }
    } else {
        while (answer > bigN) {
            answer -= bigN
        }
    }
    println("The earliest timestamp in which the busses leave consecutively is $answer.")
}

private data class ModEquation(val a: Long, val n: Long)

private fun extendedEuclid(a: Long, b: Long): Pair<Long, Long> {
    var oldR = a
    var r = b
    var oldS = 1L
    var s = 0L
    var oldT = 0L
    var t = 1L

    while (r != 0L) {
        val quotient = oldR / r

        val newR = oldR - quotient * r
        oldR = r
        r = newR

        val newS = oldS - quotient * s
        oldS = s
        s = newS

        val newT = oldT - quotient * t
        oldT = t
        t = newT
    }

    if (oldR != 1L) throw IllegalStateException("$a and $b aren't coprime, something is wrong.")
    return Pair(oldS, oldT)
}