package day03

import java.io.File

fun part1(banks: List<String>): Long {
	val numbers = mutableListOf<Long>()
	for (bank in banks) {
		val result = maxOrderedSubsequence(bank, 2)
		numbers.add(result.toLong())
	}
	return numbers.sum()
}


private fun maxOrderedSubsequence(bank: String, amountLeft: Int): String {
	val length = bank.length
	val sb = StringBuilder()
	var start = 0
	for (pos in 0 until amountLeft) {
		val end = length - (amountLeft - pos)
		var bestIndex = start
		for (i in start..end) {
			if (bank[i] > bank[bestIndex]) bestIndex = i
		}
		sb.append(bank[bestIndex])
		start = bestIndex + 1
	}
	return sb.toString()
}

fun part2(banks: List<String>): Long {
	val numbers = mutableListOf<Long>()
	for (bank in banks) {
		val result = maxOrderedSubsequence(bank, 12)
		numbers.add(result.toLong())
	}
	return numbers.sum()
}

fun main() {
	val data = File("inputs/day03.txt").readLines()
	val answer1 = part1(data)
	println("Answer 1: $answer1") // 17316
	val answer2 = part2(data)
	println("Answer 2: $answer2") // 171741365473332
}