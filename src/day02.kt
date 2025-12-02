package day02

import java.io.File

data class IdRange(val start: Long, val end: Long) {
	fun getIds(): List<Long> {
		val ids = mutableListOf<Long>()
		for (id in start..end) {
			ids.add(id)
		}
		return ids
	}

	fun getInvalidIds(): List<Long> {
		val allIds = getIds()
		val invalidIds = mutableListOf<Long>()
		for (id in allIds) {
			val idString = id.toString()
			if (idString.startsWith("0") || isRepeatingSubstringOfLength(idString, idString.length / 2)) {
				invalidIds.add(id)
			}
		}
		return invalidIds
	}

	fun isRepeatingSubstringOfLength(s: String, length: Int): Boolean {
		if (length==0 || s.length % length != 0) {
			return false
		}
		val substring = s.take(length)
		var index = length
		while (index < s.length) {
			if (s.substring(index, index + length) != substring) {
				return false
			}
			index += length
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

	fun getInvalidIds2(): List<Long> {
		val allIds = getIds()
		val invalidIds = mutableListOf<Long>()
		for (id in allIds) {
			val idString = id.toString()
			if (idString.startsWith("0") || anySubstringRepeats(idString)) {
				invalidIds.add(id)
			}
		}
		return invalidIds
	}
}

fun preProcessData(input: String): List<IdRange> {
	return input.split(",").map { rangeString: String ->
		val (start, end) = rangeString.split("-")
		IdRange(start.toLong(), end.toLong())
	}
}

fun part1(ranges: List<IdRange>): Long {
	var invalidIdSum = 0L
	for (range in ranges) {
		val invalidIds = range.getInvalidIds()
		invalidIdSum += invalidIds.sum()
	}
	return invalidIdSum
}

fun part2(ranges: List<IdRange>): Long {
	var invalidIdSum = 0L
	for (range in ranges) {
		println("Processing range: ${range.start}-${range.end}")
		val invalidIds = range.getInvalidIds2()
		println(" - found ${invalidIds.size} invalid IDs")
		invalidIdSum += invalidIds.sum()
	}
	return invalidIdSum
}


fun main() {
	val input = File("inputs/day02.txt").readText()
	val data = preProcessData(input)
	val answer1 = part1(data)
	println("Answer 1: $answer1") // 8576933996
	val answer2 = part2(data)
	println("Answer 2: $answer2") // 25663320831
}