package day10

import java.io.File

class LightDiagram(val target: Int, val length: Int) {
	var indicators: Int = 0

	fun applyButton(button: Button) {
		indicators = indicators xor button.wiring
	}

	override fun toString(): String {
		val sb = StringBuilder()
		for (i in 0 until length) {
			if ((target and (1 shl i)) != 0) {
				sb.append('#')
			} else {
				sb.append('.')
			}
		}
		return sb.toString()
	}

	companion object {
		fun fromString(str: String): LightDiagram {
			val chars = str.toCharArray()
			var number = 0
			for (char in chars.withIndex()) {
				if (char.value == '.') {
					continue
				} else if (char.value == '#') {
					number += (1 shl char.index)
				} else throw IllegalArgumentException("Invalid character in wiring string: $char")
			}
			return LightDiagram(number, chars.size)
		}
	}
}

data class Button(val wiring: Int) {

	override fun toString(): String {
		return Integer.toBinaryString(wiring)
			.reversed()
			.replace('0', '.')
			.replace('1', '#')
	}

	companion object {
		fun fromString(str: String): Button {
			val numbers = str.split(",").map { it.trim().toInt() }
			var number = 0
			for (num in numbers) {
				number = number or (1 shl num)
			}
			return Button(number)
		}
	}
}

data class JoltageRequirements(val requiredJoltage: List<Int>) {
	override fun toString(): String {
		return "{" + requiredJoltage.joinToString(",") + "}"
	}

	companion object {
		fun fromString(str: String): JoltageRequirements {
			return JoltageRequirements(str.split(",").map { it.trim().toInt() })
		}
	}
}

data class Machine(
	val lightDiagram: LightDiagram,
	val buttons: List<Button>,
	val joltageRequirements: JoltageRequirements
) {
	fun buttonCombinationsOfSize(k: Int): List<List<Button>> {
		return buttons.combinations(k)
	}
}

// Non-repeat combinations of k elements from the list
fun <T> List<T>.combinations(k: Int): List<List<T>> {
	if (k == 0) return listOf(emptyList())
	if (isEmpty()) return emptyList()

	val head = this.first()
	val tail = this.drop(1)

	// Combinations that include head + combinations that don't
	return tail.combinations(k - 1).map { listOf(head) + it } + tail.combinations(k)
}

val machineInfoRegex = """\[([.#]+)] ([(\d,) ]+) \{([\d,]+)}""".toRegex()

fun preProcessData(lines: List<String>): List<Machine> {
	val machines = mutableListOf<Machine>()
	for (line in lines) {
		val matchResult = machineInfoRegex.find(line)
		if (matchResult != null) {
			val (lightDiagramStr, buttonWiringsStr, joltageRequirementsStr) = matchResult.destructured
			// Further processing can be done here if needed
			val lightDiagram = LightDiagram.fromString(lightDiagramStr)
			val buttonWirings = buttonWiringsStr.split(") (").map {
				val wiringStr = it.trim('(', ')', ' ')
				Button.fromString(wiringStr)
			}
			val joltageRequirements = JoltageRequirements.fromString(joltageRequirementsStr)
			val machine = Machine(lightDiagram, buttonWirings, joltageRequirements)
			machines.add(machine)
		} else {
			throw IllegalArgumentException("Line does not match expected format: $line")
		}
	}
	return machines
}

fun part1(machines: List<Machine>): Int {
	return machines.sumOf { getLowestButtonSequenceSize(it) }
}

private fun getLowestButtonSequenceSize(machine: Machine): Int {
	for (k in 1..machine.buttons.size) {
		val combinations = machine.buttonCombinationsOfSize(k)
		for (combination in combinations) {
			val testDiagram = LightDiagram(machine.lightDiagram.target, machine.lightDiagram.length)
			for (button in combination) {
				testDiagram.applyButton(button)
			}
			if (testDiagram.indicators == machine.lightDiagram.target) {
				println("Found solution in $k steps for machine: $machine with buttons: $combination")
				return k
			}
		}
	}
	return Integer.MIN_VALUE
}

fun part2(data: List<Machine>): Int {
	return 0
}


fun main() {
	val input = File("inputs/day10.txt").readLines()
	val data = preProcessData(input)
	val answer1 = part1(data)
	println("Answer 1: $answer1") // 375
	val answer2 = part2(data)
	println("Answer 2: $answer2") // ANSWER
}