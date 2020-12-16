import util.readLines
import java.io.File

fun main() {
    val file = File("res/day16/input.txt")
//    val file = File("res/day16/test-input-1.txt")
    challengeA(file)
    challengeB(file)
}

private fun challengeA(file: File) { // 23115
    val lines = file.readLines { it }.toList()
    val rules = TicketRule2()
    val tickets = mutableListOf<List<Int>>()
    var section = 0
    lines.forEach {
        when {
            it.startsWith("your ticket") -> {
                section = 1
                return@forEach
            }
            it.startsWith("nearby tickets:") -> {
                section = 2
                return@forEach
            }
            it.isBlank() -> {
                return@forEach
            }
        }
        when (section) {
            0 -> rules.ranges.addAll(parseRule2(it))
            2 -> tickets.add(parseTicket(it))
        }
    }
    var errorRate = 0
    for (ticket in tickets) {
        for (value in ticket) {
            if (!rules.isValid(value)) {
                errorRate += value
            }
        }
    }
    println("error rate is $errorRate")
}

private fun challengeAo(file: File) {
    val lines = file.readLines { it }.toList()
    val rules = mutableListOf<TicketRule>()
    val tickets = mutableListOf<List<Int>>()
    var section = 0
    lines.forEach {
        when {
            it.startsWith("your ticket") -> {
                section = 1
                return@forEach
            }
            it.startsWith("nearby tickets:") -> {
                section = 2
                return@forEach
            }
            it.isBlank() -> {
                return@forEach
            }
        }
        when (section) {
            0 -> rules.add(parseRule(it))
            2 -> tickets.add(parseTicket(it))
        }
    }
    var errorRate = 0
    for (ticket in tickets) {
        if (rules.size != ticket.size) throw IllegalStateException("the rules and the ticket should be the same size?")
        for ((index, rule) in rules.withIndex()) {
            if (!rule.isValid(ticket[index])) {
                errorRate += ticket[index]
            }
        }
    }
    println("error rate is $errorRate")
}

private fun parseRule(serial: String): TicketRule {
    val minusTitle = serial.split(": ")
    val rangesSerial = minusTitle[1].split(" or ")
    val ranges = rangesSerial.map {
        val (startSerial, endSerial) = it.split("-")
        val start = startSerial.toInt()
        val end = endSerial.toInt()
        start..end
    }
    return TicketRule(minusTitle[0], ranges)
}

private fun parseRule2(serial: String): List<IntRange> {
    val minusTitle = serial.split(": ")
    val rangesSerial = minusTitle[1].split(" or ")
    val ranges = rangesSerial.map {
        val (startSerial, endSerial) = it.split("-")
        val start = startSerial.toInt()
        val end = endSerial.toInt()
        start..end
    }
    return ranges
}

private fun parseTicket(serial: String): List<Int> {
    return serial.split(",").map { it.toInt() }
}

private data class TicketRule(val name: String, val ranges: List<IntRange>) {
    fun isValid(number: Int): Boolean {
        return ranges.any { it.contains(number) }
    }
}

private data class TicketRule2(val ranges: MutableList<IntRange> = mutableListOf()) {
    fun isValid(number: Int): Boolean {
        return ranges.any { it.contains(number) }
    }
}

private fun challengeB(file: File) { // 239727793813
    val lines = file.readLines { it }.toList()
    val allRules = TicketRule2()
    val rules = mutableListOf<TicketRule>()
    val tickets = mutableListOf<List<Int>>()
    var section = 0
    lines.forEach {
        when {
            it.startsWith("your ticket") -> {
                section = 1
                return@forEach
            }
            it.startsWith("nearby tickets:") -> {
                section = 2
                return@forEach
            }
            it.isBlank() -> {
                return@forEach
            }
        }
        when (section) {
            0 -> {
                rules.add(parseRule(it))
                allRules.ranges.addAll(parseRule2(it))
            }
            1, 2 -> tickets.add(parseTicket(it))
        }
    }
    var errorRate = 0
    val validTickets = tickets.filter { ticket ->
        var isValid = true
        for (value in ticket) {
            if (!allRules.isValid(value)) {
                errorRate += value
                isValid = false
            }
        }
        isValid
    }
    println(errorRate)
    println(rules)
    println(validTickets)
    val fieldSetMap = mutableMapOf<String, MutableSet<Int>>()
    for (rule in rules) {
        valueIndex@ for (valueIndex in 0..validTickets[0].lastIndex) {
            for (ticket in validTickets) {
                val value = ticket[valueIndex]
                if (!rule.isValid(value)) {
                    continue@valueIndex
                }
            }
            val indexSet = fieldSetMap.getOrPut(rule.name) { mutableSetOf() }
            indexSet.add(valueIndex)
        }
    }
    fieldSetMap.forEach {
        println("${it.key}: ${it.value}")
    }
    val fieldMap = mutableMapOf<String, Int>()
    while (fieldSetMap.isNotEmpty()) {
        val singleFieldPossible = fieldSetMap.keys.find {
            fieldSetMap[it]?.size == 1
        } ?: throw IllegalStateException("uggghhh")
        val value = fieldSetMap[singleFieldPossible]?.first() ?: throw IllegalStateException("uuggghhh more")
        fieldMap[singleFieldPossible] = value
        fieldSetMap.remove(singleFieldPossible)
        fieldSetMap.forEach {
            it.value.remove(value)
        }
    }
    println(fieldMap)
    val fieldsWithDeparture = fieldMap.filterKeys { it.startsWith("departure") }
    println(fieldsWithDeparture)
    val product = fieldsWithDeparture.values.map { tickets[0][it].toLong() }.fold(1L) { acc, current -> acc * current }
    println(product)
}
