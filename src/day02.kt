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
			if (idString.startsWith("0")
				|| idString.take(idString.length / 2) == idString.substring(idString.length / 2, idString.length)
			) {
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
		println("Processing range: ${range.start}-${range.end}")
		val invalidIds = range.getInvalidIds()
		println(" - found ${invalidIds.size} invalid IDs")
		invalidIdSum += invalidIds.sum()
	}
	return invalidIdSum
}

fun part2(ranges: List<IdRange>): Long {
	return 0
}


fun main() {
	val input = File("inputs/day02.txt").readText()
	val data = preProcessData(input)
	val answer1 = part1(data)
	println("Answer 1: $answer1") // 8576933996
	val answer2 = part2(data)
	println("Answer 2: $answer2") // ANSWER
}