package day01

import java.io.File

data class Rotation(val direction: Char, val distance: Int)

class Dial {
	var value: Int = 50
	var totalRotationsOverZero: Int = 0

	fun rotate(rotation: Rotation): Int {
		val extraRotations = rotation.distance / 100
		if (extraRotations > 0) {
			this.totalRotationsOverZero += extraRotations
		}
		val remainingDistance = rotation.distance % 100
		if (value != 0) {
			if (rotation.direction == 'L' && remainingDistance >= value) {
				this.totalRotationsOverZero++
			} else if (rotation.direction == 'R' && remainingDistance + value >= 100) {
				this.totalRotationsOverZero++
			}
		}
		if (rotation.direction == 'L') {
			value = (value - remainingDistance + 100) % 100
		} else if (rotation.direction == 'R') {
			value = (value + remainingDistance) % 100
		}
		if (value !in 0..<100) {
			throw IllegalStateException("Dial value out of bounds: $value")
		}
		return value
	}
}

fun preProcessData(lines: List<String>): List<Rotation> {
	return lines.map { Rotation(it[0], it.substring(1).toInt()) }
}

fun part1(rotations: List<Rotation>): Int {
	val dial = Dial()
	var timesDialZero = 0
	for (rotation in rotations) {
		if (dial.rotate(rotation) == 0) {
			timesDialZero++
		}
	}
	return timesDialZero
}

fun part2(rotations: List<Rotation>): Int {
	val dial = Dial()
	for (rotation in rotations) {
		dial.rotate(rotation)
	}
	return dial.totalRotationsOverZero
}


fun main() {
	val input = File("inputs/day01.txt").readLines()
	val data = preProcessData(input)
	val answer1 = part1(data)
	println("Answer 1: $answer1") // 984
	val answer2 = part2(data)
	println("Answer 2: $answer2") // 5657
}