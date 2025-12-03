package day03

import java.io.File


fun preProcessData(banks: List<String>): List<String> {
	return banks
}

fun part1(banks: List<String>): Int {
	val numbers = mutableListOf<Int>()
	for (bank in banks) {
		val indices = mutableListOf<Int>()
		var highestIndex = bank.indexOfFirst { it == bank.max() }
		indices.add(highestIndex)
		val rightString = bank.substring(highestIndex + 1)
		if (rightString.isNotEmpty()) {
			val rightIndex = rightString.indexOfFirst { it == rightString.max() }
			indices.add(highestIndex+1 + rightIndex)
		}
		else {
			val leftString = bank.substring(0, highestIndex)
			val leftIndex = leftString.indexOfFirst { it == leftString.max() }
			indices.add(leftIndex)
		}
		indices.sort()
		val array = bank.toCharArray()
		val builder = StringBuilder()
		for (index in indices) {
			builder.append(array[index])
		}
		println(builder.toString())
		numbers.add(builder.toString().toInt())
	}
	return numbers.sum()
}

	fun part2(data: List<String>): Int {
		return 0
	}


	fun main() {
		val input = File("inputs/day03.txt").readLines()
		val data = preProcessData(input)
		val answer1 = part1(data)
		println("Answer 1: $answer1") // ANSWER
		val answer2 = part2(data)
		println("Answer 2: $answer2") // ANSWER
	}