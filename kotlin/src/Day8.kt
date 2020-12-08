import util.readLines
import java.io.File

fun main() {
    val file = File("res/day8/input.txt")
//    val file = File("res/day8/test-input-0.txt")
    challengeB(file)
}

private fun challengeA(file: File) { // 1217
    val instructions = file.readLines { Instruction.parse(it) }.toList()
    val endState = runToTermination(instructions)
    println("The accumulator is ${endState.accumulator}.")
}

private fun challengeB(file: File) { // 501
    val originalInstructions = file.readLines { Instruction.parse(it) }.toList()
    var lastSwappedIndex = -1
    while (true) {
        val secondToLastSwappedIndex = lastSwappedIndex
        var alreadySwapped = false
        val instructions = mutableListOf<Instruction>().apply {
            originalInstructions.forEachIndexed { index, instruction ->
                val swapped = swapInstruction(index, lastSwappedIndex, alreadySwapped, instruction)
                if (swapped == null) {
//                    println("not swapped, keeping ${instruction.name} at index $index")
                    add(instruction.copy())
                } else {
                    println("swapping ${swapped.name} into index $index")
                    lastSwappedIndex = index
                    alreadySwapped = true
                    add(swapped)
                }
            }
        }
        if (secondToLastSwappedIndex == lastSwappedIndex) {
            println("Reached the end of the list, no more swaps possible.")
            break
        }
        val endState = runToTermination(instructions)
        if (endState.completed) {
            println("The program completed after changing the jmp or nop at $lastSwappedIndex. The accumulator is ${endState.accumulator}.")
            break
        } else {
            println("The program looped after changing the jmp or nop at $lastSwappedIndex.")
        }
    }
}

private fun runToTermination(instructions: List<Instruction>): EndState {
    var currentIndex = 0
    var accumulator = 0
    var completed = false
    while (true) {
        val currentInstruction = instructions.getOrNull(currentIndex)
//        println("current instruction: ${currentInstruction?.name}, accumulator: $accumulator")
        if (currentInstruction == null) {
            println("the program completed")
            completed = true
            break
        } else if (currentInstruction.exec) {
            println("the program looped")
            break
        }
        when (currentInstruction.name) {
            "nop" -> currentIndex += 1
            "jmp" -> currentIndex += currentInstruction.amount
            "acc" -> {
                accumulator += currentInstruction.amount
                currentIndex += 1
            }
        }
        currentInstruction.exec = true
    }
    return EndState(accumulator, completed)
}

data class EndState(val accumulator: Int, val completed: Boolean)

private fun swapInstruction(currentIndex: Int, lastFoundIndex: Int, alreadySwapped: Boolean, instruction: Instruction): Instruction? {
    if (alreadySwapped || currentIndex <= lastFoundIndex || instruction.name == "acc") {
        return null
    }

    return when (instruction.name) {
        "jmp" -> Instruction("nop", instruction.amount, instruction.exec)
        "nop" -> Instruction("jmp", instruction.amount, instruction.exec)
        else -> null
    }
}

private data class Instruction(val name: String, val amount: Int, var exec: Boolean = false) {
    companion object {
        fun parse(serialized: String): Instruction {
            val (operand, amountSerial) = serialized.split(' ')
            val amount = amountSerial.replace("+", "").toInt()
            return Instruction(operand, amount)
        }
    }
}
