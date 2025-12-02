package day02

import java.io.File

data class IdRange(val start: Long, val end: Long) {
	fun getIds(): List<Long> = (start..end).toList()
}

fun isRepeatingSubstringOfLength(s: String, length: Int): Boolean {
	if (length == 0 || s.length % length != 0) {
		return false
	}
	val substring = s.take(length)
	for (index in length until s.length step length) {
		if (s.substring(index, index + length) != substring) {
			return false
		}
	}
	return true
}

fun anySubstringRepeats(s: String): Boolean {
	for (length in s.length / 2 downTo 1) {
		if (isRepeatingSubstringOfLength(s, length)) {
			return true
		}
	}
	return false
}

fun part1(ranges: List<IdRange>): Long {
	var totalInvalidIdSum = 0L
	for (range in ranges) {
		for (id in range.getIds()) {
			val idString = id.toString()
			if (isRepeatingSubstringOfLength(idString, idString.length / 2)) {
				totalInvalidIdSum += id
			}
		}
	}
	return totalInvalidIdSum
}

fun part2(ranges: List<IdRange>): Long {
	var totalInvalidIdSum = 0L
	for (range in ranges) {
		for (id in range.getIds()) {
			val idString = id.toString()
			if (anySubstringRepeats(idString)) {
				totalInvalidIdSum += id
			}
		}
	}
	return totalInvalidIdSum
}


fun preProcessData(input: String): List<IdRange> {
	return input.split(",").map { rangeString: String ->
		val (start, end) = rangeString.split("-")
		IdRange(start.toLong(), end.toLong())
	}
}

fun main() {
	val input = File("inputs/day02.txt").readText()
	val data = preProcessData(input)
	val answer1 = part1(data)
	println("Answer 1: $answer1") // 8576933996
	val answer2 = part2(data)
	println("Answer 2: $answer2") // 25663320831
}