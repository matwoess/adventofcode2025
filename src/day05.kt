package dayXX

import java.io.File

data class Range(val from: Long, val to: Long) {
	fun contains(value: Long): Boolean {
		return value in from..to
	}

	fun size(): Long = to - from + 1
}

fun preProcessData(lines: String): Pair<List<Range>, List<Long>> {
	val (freshSets, availableIngredients) = lines.split("\n\n")
	val freshIngredientsRanges: MutableList<Range> = mutableListOf()
	for (line in freshSets.lines()) {
		val (from, to) = line.split("-").map(String::toLong)
		freshIngredientsRanges.add(Range(from, to))
	}
	val allIngredients: MutableList<Long> = mutableListOf()
	for (line in availableIngredients.lines()) {
		allIngredients.add(line.toLong())
	}
	return Pair(freshIngredientsRanges, allIngredients)
}

fun part1(fresh: List<Range>, ingredients: List<Long>): Int {
	return ingredients.count { ingredient -> fresh.any { it.contains(ingredient) } }
}

fun part2(fresh: List<Range>): Long {
	val sortedRanges = fresh.sortedBy { it.from }
	val mergedRanges = mutableListOf<Range>()
	mergedRanges.add(sortedRanges[0])
	for (range in sortedRanges.drop(1)) {
		val currentLast = mergedRanges.last()
		if (currentLast.to >= range.from) {
			mergedRanges.removeLast()
			mergedRanges.add(Range(currentLast.from, maxOf(currentLast.to, range.to)))
		} else {
			mergedRanges.add(range)
		}
	}
	return mergedRanges.sumOf { it.size() }
}


fun main() {
	val input = File("inputs/day05.txt").readText()
	val (fresh, ingredients) = preProcessData(input)
	val answer1 = part1(fresh, ingredients)
	println("Answer 1: $answer1") // 865
	val answer2 = part2(fresh)
	println("Answer 2: $answer2") // 352556672963116
}