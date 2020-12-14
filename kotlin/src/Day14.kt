import util.readLines
import java.io.File
import java.nio.ByteBuffer
import java.lang.Double as JDouble
import java.lang.Long as JLong

fun main() {
    val file = File("res/day14/input.txt")
//    val file = File("res/day14/test-input-0.txt")
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

private fun Long.toByteArray(): ByteArray {
    return ByteBuffer.allocate(Long.SIZE_BYTES)
        .putLong(this).array()
}

//private fun ByteArray.toLong() {
//
//}

private fun challengeB(file: File) { //

}