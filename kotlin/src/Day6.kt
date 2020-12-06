import util.readLines
import java.io.File

fun main() {
    val file = File("res/day6/input.txt")
//    val file = File("res/day6/test-input-0.txt")
    challengeB(file)
}

private fun challengeA(file: File) { // 6457
    val lines = file.readLines { if (it.isBlank()) " " else it }
        .joinToString(separator = "")
        .split(' ')
        .map {
            it.toSet().size
        }
        .sum()
    println(lines)
}

private fun challengeB(file: File) { // 3260
    val lines = file.readLines { if (it.isBlank()) " " else it }
        .joinToString(separator = "/")
        .split(' ')
        .map { it.split('/').filterNot { it.isBlank() } }
        .map { groupAnswers ->
            groupAnswers.map { it.toList() }
                .reduce { acc, personAnswers ->
                    val removeList = mutableListOf<Char>()
                    acc.forEach { char ->
                        if (!personAnswers.contains(char)) {
                            removeList.add(char)
                        }
                    }
                    val updated = acc.toMutableList()
                    updated.apply {
                        removeAll(removeList)
                    }
                }.size
        }.sum()
    println(lines)
}