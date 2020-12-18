import util.readLines
import java.io.File
import java.io.StringReader

fun main() {
    val file = File("res/day18/input.txt")
//    val file = File("res/day18/test-input-0.txt")
    challengeA(file)
    challengeB(file)
}

private fun challengeA(file: File) { // 69490582260
    val equations = file.readLines { it.reader().toMathComponent() }.toList()
//    equations.forEach {
//        println("$it = ${(it as EquationComponent).evaluate()}")
//    }
    val sum = equations.sumByDouble { (it as EquationComponent).evaluate().toDouble() }.toLong()
    println("sum: $sum")
}

private fun challengeB(file: File) { // 362464596624526
    val equations = file.readLines { it.reader().toMathComponent() }.toList()
//    equations.forEach {
//        println("$it = ${(it as EquationComponent).evaluate2()}")
//    }
    val sum = equations.sumByDouble { (it as EquationComponent).evaluate2().toDouble() }.toLong()
    println("sum: $sum")
}

private fun StringReader.toMathComponent(): MathComponent {
    val tokens = mutableListOf<MathComponent>()
    do {
        mark(0)
        val currentRead = this.read()
        when (currentRead.toChar()) {
            in '0'..'9' -> {
                reset()
                tokens.add(parseNumberComponent(this))
            }
            '(' -> tokens.add(this.toMathComponent())
            ')' -> return EquationComponent(tokens)
            in setOf('+', '*') -> tokens.add(SymbolComponent(currentRead.toChar()))
        }
    } while (currentRead != -1)
    return EquationComponent(tokens)
}

private fun parseNumberComponent(reader: StringReader): NumberComponent {
    var currentNumber = 0L
    do {
        reader.mark(0)
        when (val currentChar = reader.read().toChar()) {
            in '0'..'9' -> currentNumber = Character.getNumericValue(currentChar) + (currentNumber * 10)
            else -> {
                reader.reset()
                return NumberComponent(currentNumber)
            }
        }
    } while (true)
}

interface Evaluatable {
    fun evaluate(): Long
    fun evaluate2(): Long
}
private sealed class MathComponent
private data class NumberComponent(val number: Long) : MathComponent(), Evaluatable {
    override fun toString(): String = number.toString()
    override fun evaluate(): Long = number
    override fun evaluate2(): Long = number
}
private data class SymbolComponent(val symbol: Char) : MathComponent() {
    override fun toString(): String = symbol.toString()
}
private data class EquationComponent(val list: List<MathComponent>) : MathComponent(), Evaluatable {
    override fun toString(): String = "(${list.joinToString("")})"

    override fun evaluate(): Long {
        var result = 0L
        var lastSymbol: Char? = null
        for (component in list) {
            when (component) {
                is Evaluatable -> when (lastSymbol) {
                    null -> result = component.evaluate()
                    '+' -> result += component.evaluate()
                    '*' -> result *= component.evaluate()
                    else -> throw IllegalStateException("$lastSymbol isn't valid.")
                }
                is SymbolComponent -> lastSymbol = component.symbol
            }
        }
        return result
    }

    override fun evaluate2(): Long {
        val evaluated = list.map {
            when (it) {
                is Evaluatable -> NumberComponent(it.evaluate2())
                else -> it
            }
        }

        var previousNumber: Long? = null
        var previousSymbol: Char? = null
        val plussed = mutableListOf<MathComponent>()
        for (index in evaluated.indices) {
            val current = evaluated[index]
            when (current) {
                is Evaluatable -> when {
                    previousNumber == null -> previousNumber = current.evaluate2()
                    previousSymbol == '+' -> {
                        val sum = previousNumber + current.evaluate2()
                        previousNumber = sum
                        previousSymbol = null
                    }
                }
                is SymbolComponent -> when {
                    current.symbol == '+' -> previousSymbol = current.symbol
                    current.symbol == '*' && previousNumber != null -> {
                        plussed.add(NumberComponent(previousNumber))
                        plussed.add(current)
                        previousNumber = null
                    }
                    else -> {
                        plussed.add(current)
                        previousNumber = null
                    }
                }
            }
        }
        if (previousNumber != null) {
            plussed.add(NumberComponent(previousNumber))
        }

        var result = 0L
        var lastSymbol: Char? = null
        for (component in plussed) {
            when (component) {
                is Evaluatable -> when (lastSymbol) {
                    null -> result = component.evaluate2()
                    '*' -> result *= component.evaluate2()
                    else -> throw IllegalStateException("$lastSymbol isn't valid.")
                }
                is SymbolComponent -> lastSymbol = component.symbol
            }
        }
        return result
    }
}