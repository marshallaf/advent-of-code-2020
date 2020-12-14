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
// and https://brilliant.org/wiki/chinese-remainder-theorem/
private fun challengeB3(file: File) { // 803025030761664
    val lines = file.readLines { it }.toList()
    val modEquations = lines[1].split(',')
        .mapIndexedNotNull { index, busNumberSerial ->
            if (busNumberSerial == "x") {
                return@mapIndexedNotNull null
            }
            val busNumber = busNumberSerial.toLong()
            // what we're ultimately looking for is the solution to a system of
            // equations with the pattern (t + busIndex) = 0 (mod busNumber)
            // we can solve for t by changing each to t = -busIndex (mod busNumber)
            // so we'll represent that as this ModEquation object
            ModEquation(-index.toLong(), busNumber)
        }
    // bigN is the product of all the moduli
    // the solution will be a congruence of something mod this product
    val bigN = modEquations.fold(1L) { acc, modEquation ->
        modEquation.n * acc
    }
    println("product of busNumbers (bigN): $bigN") // check
    // each of these bigN(i) is the product of all of the moduli except the one at i
    val bigNis = modEquations.map {
        bigN / it.n
    }
    println(bigNis)
    // since all the busNumbers (n's) are coprime, we know that bigN(i) is coprime with n(i)
    // therefore, their greatest common denominator is 1.
    // given this, we apply Bezout's identity: there exists u(i) and v(i) such that
    //      u(i) * n(i) + v(i) * bigN(i) = 1
    // using Euclid's Extended algo to find u(i) and v(i)
    val bezouts = bigNis.mapIndexed { index, bigNi ->
        extendedEuclid(modEquations[index].n, bigNi)
    }
    println(bezouts)
    // the key here is that we're looking for a solution for t ≡ a(i) (mod n(i)), for every i
    // so t and a(i) have the same remainder when divided by n(i)
    // this can be restated as t = k * n(i) + a(i)


    // We care about the modular inverse of bigN(i) mod n(i) [bigN(i)^-1 mod n(i)].
    // We're looking for the number [bigN(i) * v(i)] that results in the same remainder when divided by n
    // as [1 divided by n] (aka 1).
    // for each of these modular inverses v(i), multiply by bigN(i)
    // and then by a(i), which in our case is the negative time offset from t
    val summands = bezouts.mapIndexed { index, bezout ->
        bezout.second * bigNis[index] * modEquations[index].a
    }
    println(summands)
    val sum = summands.sum()
    // the value for sum satisfies the equation, but recall that any congruence of modulo bigN will work,
    // so find the lowest positive number that satisfies that congruence, that's our t
    var t = sum % bigN
    if (t < 0) {
        while (t < 0) {
            t += bigN
        }
    }
    println("The earliest timestamp in which the busses leave consecutively is $t.")
}

private data class ModEquation(val a: Long, val n: Long)

// https://math.stackexchange.com/questions/67969/linear-diophantine-equation-100x-23y-19/68021
// https://www.khanacademy.org/computing/computer-science/cryptography/modarithmetic/a/the-euclidean-algorithm
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