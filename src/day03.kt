package day03

import java.io.File


fun preProcessData(banks: List<String>): List<String> {
	return banks
}

fun part1(banks: List<String>): Long {
	val numbers = mutableListOf<Long>()
	for (bank in banks) {
		val indices = mutableListOf<Int>()
		val highestIndex = bank.indexOfFirst { it == bank.max() }
		indices.add(highestIndex)
		findLargestIndices(bank, indices, currIndex = highestIndex, amount = 1)
		indices.sort()
		val array = bank.toCharArray()
		val builder = StringBuilder()
		for (index in indices) {
			builder.append(array[index])
		}
		println(builder.toString())
		numbers.add(builder.toString().toLong())
	}
	return numbers.sum()
}

private fun findLargestIndices(bank: String, indices: MutableList<Int>, currIndex: Int, amount: Int) {
	if (amount == 0) return
	var newIndex = bank.withIndex()
		.filter { !indices.contains(it.index) }
		.maxByOrNull { it.value }?.index
	if (newIndex != null) {
		indices.add(newIndex)
	} else {
		newIndex = bank.withIndex()
			.filter { !indices.contains(it.index) }
			.maxByOrNull { it.value }?.index!!
		indices.add(newIndex)
	}
	findLargestIndices(bank, indices, newIndex, amount - 1)
}

fun part2(banks: List<String>): Long {
	val numbers = mutableListOf<Long>()
	for (bank in banks) {
		val indices = mutableListOf<Int>()
		val highestIndex = bank.indexOfFirst { it == bank.max() }
		indices.add(highestIndex)
		findLargestIndices(bank, indices, currIndex = highestIndex, amount = 11)
		indices.sort()
		val array = bank.toCharArray()
		val builder = StringBuilder()
		for (index in indices) {
			builder.append(array[index])
		}
		println(builder.toString())
		numbers.add(builder.toString().toLong())
	}
	return numbers.sum()
}


fun main() {
	val input = File("examples/day03.txt").readLines()
	val data = preProcessData(input)
	val answer1 = part1(data)
	println("Answer 1: $answer1") // 17316
	val answer2 = part2(data)
	println("Answer 2: $answer2") // ANSWER
}