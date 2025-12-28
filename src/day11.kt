package day11

import java.io.File

data class Device(val name: String, val outputs: MutableList<Device> = mutableListOf()) {
	override fun toString(): String {
		return name + ": " + outputs.joinToString(" ") { it.name }
	}
}


fun preProcessData(lines: List<String>): List<Device> {
	val devices = mutableMapOf<String, Device>()
	for (line in lines) {
		val (deviceName, outputs) = line.split(": ")
		val outputNames = outputs.split(" ").map { it.trim() }
		val device = devices.computeIfAbsent(deviceName, { Device(it) })
		for (outputName in outputNames) {
			val outputDevice = devices.computeIfAbsent(outputName, { Device(it) })
			device.outputs.add(outputDevice)
		}
	}
	return devices.values.toList()
}

fun part1(devices: List<Device>): Int {
	println(devices.joinToString("\n") { it.toString() })
	val start = devices.find { it.name == "you" }!!
	val leaves = countValidLeavesRecursive(start)
	return leaves
}

fun countValidLeavesRecursive(device: Device): Int {
	if (device.name == "out") {
		return 1
	}
	var sum = 0
	for (nextDevice in device.outputs) {
		sum += countValidLeavesRecursive(nextDevice)
	}
	return sum
}

fun part2(devices: List<Device>): Int {
	return 0
}


fun main() {
	val input = File("inputs/day11.txt").readLines()
	val data = preProcessData(input)
	val answer1 = part1(data)
	println("Answer 1: $answer1") // 758
	val answer2 = part2(data)
	println("Answer 2: $answer2") // ANSWER
}