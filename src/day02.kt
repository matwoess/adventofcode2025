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
	return s.chunked(length).all(substring::equals)
}

fun anySubstringRepeats(s: String) = (s.length / 2 downTo 1).any { isRepeatingSubstringOfLength(s, it) }

fun part1(ranges: List<IdRange>): Long = ranges.sumOf { range ->
	range.getIds()
		.filter { isRepeatingSubstringOfLength(it.toString(), it.toString().length / 2) }
		.sum()
}

fun part2(ranges: List<IdRange>): Long = ranges.sumOf { range ->
	range.getIds()
		.filter { anySubstringRepeats(it.toString()) }
		.sum()
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