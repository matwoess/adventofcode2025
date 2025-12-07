package day06

import util.Direction
import util.Grid2D
import java.io.File

data class MathProblem(val numbers: List<Long>, val operator: Char) {
	fun applyOperation(): Long {
		return when (operator) {
			'+' -> numbers.sum()
			'*' -> numbers.reduce { acc, l -> acc * l }
			else -> throw IllegalArgumentException("Unknown operator: $operator")
		}
	}
}

fun part1(input: String): Long {
	val lines = input.lines()
	val mathProblems = mutableListOf<MathProblem>()
	val whitespaceRegex = "\\s+".toRegex()
	val numberLists = mutableListOf<MutableList<Long>>()
	// Init number lists with amount of columns
	repeat(lines[0].split(whitespaceRegex).size) {
		numberLists.add(mutableListOf())
	}
	// Populate number lists (exclude last line with operators)
	for (line in lines.dropLast(1)) {
		val values = line.split(whitespaceRegex).map(String::trim).filter(String::isNotBlank)
		for (i in values.indices) {
			numberLists[i].add(values[i].toLong())
		}
	}
	// Finalize math problem creation (last line contains operators)
	val operators = lines.last().split(whitespaceRegex).map(String::trim).filter(String::isNotBlank)
	for (i in operators.indices) {
		mathProblems.add(MathProblem(numberLists[i], operators[i].first())) // first == string to char
	}
	// Calculate final result
	return mathProblems.sumOf { it.applyOperation() }
}

fun part2(input: String): Long {
	val grid: Grid2D<Char> = Grid2D(input, elemDelimiter = "")
	val width = grid.getWidth()
	val mathProblems = mutableListOf<MathProblem>()
	val numbers = mutableListOf<Long>()
	// start reading from last column top to bottom, then move left
	for (col in width - 1 downTo 0) {
		val num = grid.getDirectionalValueSequence(0, col, Direction.S).joinToString("").trim()
		if (num.isEmpty()) continue
		if (!num.last().isDigit()) { // operator at the end
			val operator = num.last()
			numbers.add(num.dropLast(1).trim().toLong())
			mathProblems.add(MathProblem(numbers.toList(), operator))
			numbers.clear()
		} else {
			numbers.add(num.trim().toLong())
		}
	}
	// Calculate final result
	return mathProblems.sumOf { it.applyOperation() }
}


fun main() {
	val input = File("inputs/day06.txt").readText()
	val answer1 = part1(input)
	println("Answer 1: $answer1") // 4805473544166
	val answer2 = part2(input)
	println("Answer 2: $answer2") // 8907730960817
}