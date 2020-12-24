import util.readLines
import java.io.File

fun main() {
//    val inputGroup = "test-input-0"
    val inputGroup = "input"
    val foods = File("res/day21/$inputGroup.txt").readLines { FoodItem.parse(it) }.toList()
    challengeA(foods)
}

private fun challengeA(foods: List<FoodItem>) { // challengeA: 2302; challengeB: smfz,vhkj,qzlmr,tvdvzd,lcb,lrqqqsg,dfzqlk,shp
    val allergens = foods.fold(setOf<String>()) { acc, foodItem ->
        acc.union(foodItem.allergenList)
    }
    val possiblesForAllergen = allergens.map { allergen ->
        var first = true
        val possibles = foods.fold(setOf<String>()) { possibleSoFar, food ->
            val newPossible: Set<String>
            if (food.allergenList.contains(allergen)) {
                // if it's the first time we've seen this allergen, add all of the ingredients
                if (first) {
                    newPossible = food.ingredientList.toSet()
                    first = false
                } else {
                    newPossible = possibleSoFar.intersect(food.ingredientList)
                }
            } else {
                newPossible = possibleSoFar
            }

            newPossible
        }
        Pair(allergen, possibles)
    }.toMap()

//    possiblesForAllergen.forEach {
//        println("${it.key}: ${it.value}")
//    }

    val allPossibles = possiblesForAllergen.keys.fold(setOf<String>()) { acc, allergen ->
        acc.union(possiblesForAllergen[allergen] ?: setOf())
    }

    val allIngredients = foods.fold(setOf<String>()) { acc, foodItem ->
        acc.union(foodItem.ingredientList)
    }

    val cantBeAllergens = allIngredients.subtract(allPossibles)
    println(cantBeAllergens)
    val appearances = foods.map { food ->
        food.ingredientList.intersect(cantBeAllergens).size
    }.sum()
    println("The ingredients that can't be allergens appear $appearances times.")

    val possiblesFiltered = allergens.map { Pair(it, mutableListOf<Set<String>>()) }.toMap()
    foods.forEach { food ->
        food.allergenList.forEach { allergen ->
            val possibleAllergensForThisFood = food.ingredientList.subtract(cantBeAllergens)
            possiblesFiltered[allergen]?.add(possibleAllergensForThisFood)
        }
    }
    println("possible ingredients for each allergen, separated by food")
    possiblesFiltered.forEach {
        println("${it.key}: ${it.value}")
    }

    val allergensToIngredients = mutableMapOf<String, String>()
    var toFind = allergens.size

    val removeImpossibles = possiblesFiltered.keys.map { allergen ->
        var first = true
        val filtered = possiblesFiltered[allergen]?.fold(setOf<String>()) { previousPossible, thisSet ->
            if (first) {
                first = false
                thisSet
            } else {
                previousPossible.intersect(thisSet)
            }
        } ?: setOf()
        allergen to filtered
    }.toMap()
    println("possible ingredients for each allergen")
    removeImpossibles.forEach {
        println("${it.key}: ${it.value}")
    }

    var leftOver = removeImpossibles
    var counter = 1
    while (toFind > 0) {
        val (foundIngredients, newLeftOver) = cycle(leftOver, counter)
        counter++
        leftOver = newLeftOver
        foundIngredients.forEach { (allergen, ingredient) ->
            allergensToIngredients[allergen] = ingredient
            toFind--
        }
    }

    println("found all ingredients to allergen")
    allergensToIngredients.forEach {
        println("${it.key}: ${it.value}")
    }

    val commaSeparated = allergensToIngredients.toList().sortedBy { it.first }.map { it.second }.joinToString(",")
    println("Ingredients, alphabetized by the allergen they contain:")
    println(commaSeparated)
}

private fun cycle(removeImpossibles: Map<String, Set<String>>, roundNumber: Int): Pair<Map<String, String>, Map<String, Set<String>>> {
    val allergensToIngredients = mutableMapOf<String, String>()
    val singles = removeImpossibles.filter { it.value.size == 1 }
    val allSingles = singles.values.fold(setOf<String>()) { acc, single ->
        acc.union(single)
    }

    val removeSingles = removeImpossibles.map { (allergen, possibleIngredients) ->
        val filteredIngredients = possibleIngredients.subtract(allSingles)
        allergen to filteredIngredients
    }.toMap().filter { it.value.isNotEmpty() }
    println("possible ingredients after round $roundNumber")
    removeSingles.forEach {
        println("${it.key}: ${it.value}")
    }
    singles.forEach { (allergen, ingredient) ->
        allergensToIngredients[allergen] = ingredient.first()
    }
    return Pair(allergensToIngredients, removeSingles)
}

private data class FoodItem(val ingredientList: Set<String>, val allergenList: Set<String>) {
    companion object {
        fun parse(serialized: String): FoodItem {
            val (ingredientsSerial, allergenSerial) = serialized.split(" (contains ")
            val ingredientList = ingredientsSerial.split(" ").toSet()
            val allergenList = allergenSerial.substringBefore(")").split(", ").toSet()
            return FoodItem(ingredientList, allergenList)
        }
    }
}
