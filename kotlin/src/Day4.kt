import util.readLines
import java.io.File
import java.lang.IndexOutOfBoundsException

fun main() {
    val file = File("res/day4/input.txt")
//    val file = File("res/day4/test-input-mix.txt")
    val lines = file.readLines { it }.toList()
    val passportsSerialized = mutableListOf("")
    var index = 0
    lines.forEach {
        if (it.isBlank()) {
            passportsSerialized[index] = passportsSerialized[index].trim()
            index++
            passportsSerialized.add(index, "")
        } else {
            passportsSerialized[index] += "$it "
        }
    }
    challengeB(passportsSerialized)
}

private fun challengeA(passportsSerialized: List<String>) {
    countValidPassports(passportsSerialized) {
        it.containsAllFields
    }
}

private fun challengeB(passportsSerialized: List<String>) {
    countValidPassports(passportsSerialized) {
        it.isValid
    }
}

private fun countValidPassports(passportsSerialized: List<String>, validator: (Passport) -> Boolean) {
    val validPassports = passportsSerialized.filter {
        validator(Passport.parse(it))
    }.size
    println("there are $validPassports valid passports")
}

private const val LOGS = false

private data class Passport(
    val byr: String?,
    val iyr: String?,
    val eyr: String?,
    val hgt: String?,
    val hcl: String?,
    val ecl: String?,
    val pid: String?
) {
    val isValid by lazy {
        checkField(::isByrValid, "byr", byr)
                && checkField(::isIyrValid, "iyr", iyr)
                && checkField(::isEyrValid, "eyr", eyr)
                && checkField(::isHgtValid, "hgt", hgt)
                && checkField(::isHclValid, "hcl", hcl)
                && checkField(::isEclValid, "ecl", ecl)
                && checkField(::isPidValid, "pid", pid)
    }

    val containsAllFields = byr != null
            && iyr != null
            && eyr != null
            && hgt != null
            && hcl != null
            && ecl != null
            && pid != null

    private fun checkField(validator: (String?) -> Boolean, fieldName: String, fieldValue: String?): Boolean {
        val fieldValid = validator(fieldValue)
        if (LOGS && !fieldValid) {
            println("$this is invalid because of field $fieldName: [$fieldValue]")
        }
        return fieldValid
    }

    private fun isByrValid(byr: String?): Boolean {
        return isValidNumberInRange(byr, 1920..2002)
    }

    private fun isIyrValid(iyr: String?): Boolean {
        return isValidNumberInRange(iyr, 2010..2020)
    }

    private fun isEyrValid(eyr: String?): Boolean {
        return isValidNumberInRange(eyr, 2020..2030)
    }

    private fun isHgtValid(hgt: String?): Boolean {
        if (hgt == null) {
            return false
        }
        val regex = Regex("(\\d{2,3})(\\w{2})")
        val match = regex.matchEntire(hgt) ?: return false
        val unit = match.groups[2]?.value ?: return false
        return when (unit) {
            "cm" -> isValidNumberInRange(match.groups[1]?.value, 150..193)
            "in" -> isValidNumberInRange(match.groups[1]?.value, 59..76)
            else -> false
        }
    }

    private fun isHclValid(hcl: String?): Boolean {
        if (hcl == null) {
            return false
        }

        val regex = Regex("#[a-f0-9]{6}")
        return regex.matches(hcl)
    }

    private val eyeColors = listOf(
        "amb", "blu", "brn", "gry", "grn", "hzl", "oth"
    )

    private fun isEclValid(ecl: String?): Boolean {
        return ecl != null && eyeColors.contains(ecl)
    }

    private fun isPidValid(pid: String?): Boolean {
        if (pid == null) {
            return false
        }

        val regex = Regex("[0-9]{9}")
        return regex.matches(pid)
    }

    private fun isValidNumberInRange(value: String?, range: IntRange): Boolean {
        if (value == null) {
            return false
        }

        return try {
            val numeric = value.toInt()
            range.contains(numeric)
        } catch (_: NumberFormatException) {
            false
        }
    }

    companion object {
        fun parse(serialized: String): Passport {
            val parts = serialized.split(" ").filter { it.isNotBlank() }
            var byr: String? = null
            var iyr: String? = null
            var eyr: String? = null
            var hgt: String? = null
            var hcl: String? = null
            var ecl: String? = null
            var pid: String? = null
            parts.forEach {
                try {
                    val (key, value) = it.split(":")
                    when (key) {
                        "byr" -> byr = value
                        "iyr" -> iyr = value
                        "eyr" -> eyr = value
                        "hgt" -> hgt = value
                        "hcl" -> hcl = value
                        "ecl" -> ecl = value
                        "pid" -> pid = value
                    }
                } catch (_: IndexOutOfBoundsException) {
                    throw IllegalArgumentException("[$it] is malformed.")
                }
            }
            return Passport(byr, iyr, eyr, hgt, hcl, ecl, pid)
        }
    }
}