package dayXX

import util.Direction
import util.Grid2D
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
	return problems.sumOf { it.applyOperation() }
}

fun part2(input: String): Long {
	val grid: Grid2D<Char> = Grid2D(input, elemDelimiter = "")
	val width = grid.getWidth()
	val mathProblems = mutableListOf<MathProblem>()
	val numbers = mutableListOf<Long>()
	println(grid)
	for (col in width - 1 downTo 0) {
		val topPosition = grid.getPositionAt(0, col)
		val num = grid.getDirectionalValueSequence(topPosition, Direction.S).joinToString("").trim()
		if (num.isEmpty()) continue
		var operator: Char
		if (!num.last().isDigit()) {
			operator = num.last()
			numbers.add(num.dropLast(1).trim().toLong())
			mathProblems.add(MathProblem(numbers.map { it }, operator))
			numbers.clear()
		}
		else {
			numbers.add(num.trim().toLong())
		}
	}
	return part1(mathProblems)
}


fun main() {
	val input = File("inputs/day06.txt").readText()
	val data = preProcessData(input.split("\n"))
	val answer1 = part1(data)
	println("Answer 1: $answer1") // 4805473544166
	val answer2 = part2(input)
	println("Answer 2: $answer2") // 8907730960817
}