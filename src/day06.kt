package dayXX

import java.io.File

data class MathProblem(val numbers: List<Long>, val operator: Char) {
	fun applyOperation(): Long {
		when (operator) {
			'+' -> return numbers.sum()
			'*' -> return numbers.reduce { acc, l -> acc * l }
			else -> throw IllegalArgumentException("Unknown operator: $operator")
		}
	}
}


fun preProcessData(lines: List<String>): List<MathProblem> {
	val numberLists = mutableListOf<MutableList<Long>>()
	val mathProblems = mutableListOf<MathProblem>()
	val whitespaceRegex = "\\s+".toRegex()
	for (line in lines) {
		val values = line.split(whitespaceRegex).map(String::trim).filter(String::isNotBlank)
		if (numberLists.isEmpty()) {
			for (i in values.indices) {
				numberLists.add(mutableListOf())
				numberLists[i].add(values[i].toLong())
			}
		} else if (values[0] == "+" || values[0] == "*") {
			for (i in values.indices) {
				mathProblems.add(MathProblem(numberLists[i], values[i].first()))
			}
			break
		} else {
			for (i in values.indices) {
				numberLists[i].add(values[i].toLong())
			}
		}
	}
	return mathProblems
}

fun part1(problems: List<MathProblem>): Long {
	println(problems)
	return problems.sumOf { it.applyOperation() }
}

fun part2(problems: List<MathProblem>): Int {
	return 0
}


fun main() {
	val input = File("inputs/day06.txt").readLines()
	val data = preProcessData(input)
	val answer1 = part1(data)
	println("Answer 1: $answer1") // ANSWER
	val answer2 = part2(data)
	println("Answer 2: $answer2") // ANSWER
}