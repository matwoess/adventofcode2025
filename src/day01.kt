package day01

import java.io.File

data class Rotation(val direction: Char, val distance: Int)

class Dial {
	var value: Int = 50
	var totalRotationsOverZero: Int = 0

	fun rotate(rotation: Rotation): Int {
		val extraRotations = rotation.distance / 100
		this.totalRotationsOverZero += extraRotations
		val remainingDistance = rotation.distance % 100
		when (rotation.direction) {
			'L' -> {
				if (value != 0 && remainingDistance >= value) {
					this.totalRotationsOverZero++
				}
				value = (value - remainingDistance + 100) % 100
			}

			'R' -> {
				if (value != 0 && remainingDistance + value >= 100) {
					this.totalRotationsOverZero++
				}
				value = (value + remainingDistance) % 100
			}

			else -> throw IllegalArgumentException("Invalid rotation direction: ${rotation.direction}")
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
	return rotations.map { dial.rotate(it) }.count { it == 0 }
}

fun part2(rotations: List<Rotation>): Int {
	val dial = Dial()
	rotations.forEach(dial::rotate)
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