import util.readLines
import java.io.File

fun main() {
    val inputFile = File("res/day2/test-input-0.txt")
    challengeB(inputFile)
}

fun challengeA(inputFile: File) {
    val validPasswords = inputFile.readLines { PasswordA.parse(it) }.filter { it.isValid() }.count()
    println(validPasswords)
}

fun challengeB(inputFile: File) {
    val validPasswords = inputFile.readLines { PasswordB.parse(it) }.filter { it.isValid() }.count()
    println(validPasswords)
}

data class PasswordA(val letter: Char, val range: IntRange, val password: String) {
    fun isValid(): Boolean {
        return range.contains(password.toList().count { it == letter })
    }

    companion object {
        fun parse(serialized: String): PasswordA {
            val (min, max, serialLetter, password) = serialized.split(' ', '-')
            val range = IntRange(min.toInt(), max.toInt())
            val letter = serialLetter[0]
            return PasswordA(letter, range, password)
        }
    }
}

data class PasswordB(val letter: Char, val position1: Int, val position2: Int, val password: String) {
    fun isValid(): Boolean {
        return (password[position1] == letter) xor (password[position2] == letter)
    }

    companion object {
        fun parse(serialized: String): PasswordB {
            val (serialPosition1, serialPosition2, serialLetter, password) = serialized.split(' ', '-')
            val position1 = serialPosition1.toInt() - 1
            val position2 = serialPosition2.toInt() - 1
            val letter = serialLetter[0]
            return PasswordB(letter, position1, position2, password)
        }
    }
}