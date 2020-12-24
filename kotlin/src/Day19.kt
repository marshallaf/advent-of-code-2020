import util.readLines
import java.io.File

fun main() {
//    val inputGroup = "test-input-0"
//    val inputGroup = "test-input-1"
//    val inputGroup = "test-input-2"
    val inputGroup = "input"
    val rules = File("res/day19/$inputGroup-rules.txt").readLines { it }.toList()
    val messages = File("res/day19/$inputGroup-messages.txt").readLines { it }.toList()
    challengeA(rules, messages)
    challengeB(rules, messages)
}

private fun challengeA(rulesSerial: List<String>, messages: List<String>) { // 233
    val serialRuleMap = rulesSerial.map {
        val (key, value) = it.split(": ")
        Pair(key.toInt(), value)
    }.toMap()
    val ruleSet = MessageRuleSet(serialRuleMap)
    val rulePattern = ruleSet.deserializeRule(0)
    val rule = Regex(rulePattern)
    val matchingMessages = messages.map {
        if (rule.matches(it)) 1 else 0
    }.sum()
    println("rule 0's pattern is $rulePattern")
    println("there are $matchingMessages matching messages")
}

private fun challengeB(rulesSerial: List<String>, messages: List<String>) { // 413 is too high
    val serialRuleMap = rulesSerial.map {
        val (key, value) = it.split(": ")
        val keyAsInt = key.toInt()
        val newValue = when (keyAsInt) {
            8 -> "42+"
            11 -> "42+ 31+"
            else -> value
        }
        Pair(keyAsInt, newValue)
    }.toMap()
    val ruleSet = MessageRuleSet(serialRuleMap)
    val rulePattern = ruleSet.deserializeRule(0)
    val rule = Regex(rulePattern)
    val matchingMessages = messages.map {
        if (rule.matches(it)) 1 else 0
    }.sum()
    ruleSet.printRuleMap()
    println("rule 0's pattern is $rulePattern")
    println("there are $matchingMessages matching messages")
}

private class MessageRuleSet(private val serialRuleMap: Map<Int, String>) {
    private val deserialRuleMap = mutableMapOf<Int, String>()

    fun printRuleMap() {
        deserialRuleMap.keys.sorted().forEach { key ->
            println("$key: ${serialRuleMap[key]} ---> ${deserialRuleMap[key]}")
        }
    }

    fun deserializeRule(key: Int): String {
        val deserialized = deserialRuleMap[key]
        if (deserialized != null) {
            return deserialized
        }

        val serialized = serialRuleMap[key]?.split(" ") ?: throw IllegalArgumentException("key $key isn't in the map")
        var hasOr = false
        val deserializedRuleTokens = serialized.map {
            val asInt = it.toIntOrNull()

            when {
                it.startsWith("\"") -> it.replace("\"", "")
                it.contains("+") -> {
                    val number = it.replace("+", "").toInt()
                    "(${deserializeRule(number)})+"
                }
                asInt != null -> deserializeRule(asInt)
                it == "|" -> {
                    hasOr = true
                    it
                }
                else -> throw IllegalArgumentException("$it cannot be parsed.")
            }
        }
        val joined = deserializedRuleTokens.joinToString("")
        val rulePattern = if (hasOr) {
            "($joined)"
        } else {
            joined
        }
        deserialRuleMap[key] = rulePattern
        return rulePattern
    }
}