package day11

import java.io.File

data class Device(val name: String, val outputs: MutableList<Device> = mutableListOf()) {
	override fun toString(): String {
		return name + ": " + outputs.joinToString(" ") { it.name }
	}

	override fun equals(other: Any?): Boolean {
		return other is Device && other.name == this.name
	}

	override fun hashCode(): Int {
		return name.hashCode()
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
	val start = devices.find { it.name == "you" } ?: return -1
	val leaf = devices.find { it.name == "out" } ?: return -1
	return countValidPathsRecursive(start, leaf)
}

fun countValidPathsRecursive(
	device: Device,
	leafDevice: Device,
	maxDepth: Int = Int.MAX_VALUE,
	currDepth: Int = 0
): Int {
	if (device == leafDevice) {
		return 1
	}
	if (currDepth > maxDepth) {
		return 0
	}
	var sum = 0
	for (nextDevice in device.outputs) {
		sum += countValidPathsRecursive(nextDevice, leafDevice, maxDepth, currDepth + 1)
	}
	return sum
}

fun part2(devices: List<Device>): Long {
	println(devices.joinToString("\n") { it.toString() })
	val svr = devices.find { it.name == "svr" } ?: return -1
	val dac = devices.find { it.name == "dac" } ?: return -1
	val fft = devices.find { it.name == "fft" } ?: return -1
	val out = devices.find { it.name == "out" } ?: return -1
	val depthSvrToFft = bfs(svr, fft)
	println("Depth svr to fft: $depthSvrToFft")
	val depthFftToDac = bfs(fft, dac)
	println("Depth fft to dac: $depthFftToDac")
	val depthDacToOut = bfs(dac, out)
	println("Depth dac to out: $depthDacToOut")

	val dacToOut = countValidPathsRecursive(dac, out, maxDepth = depthDacToOut + 2)
	val fftToDac = countValidPathsRecursive(fft, dac, maxDepth = depthFftToDac + 3)
	val srvToFft = countValidPathsRecursive(svr, fft, maxDepth = depthSvrToFft + 2)
	println("Valid paths svr to fft: $srvToFft")
	println("Valid paths fft to dac: $fftToDac")
	println("Valid paths dac to out: $dacToOut")
	// Paths: svr -> fft -> dac -> out
	val totalValidPaths = (srvToFft.toLong() * fftToDac * dacToOut)
	return totalValidPaths
}

fun bfs(start: Device, toNode: Device): Int {
	val queue = ArrayDeque(listOf(Pair(start, 0)))
	val visited = mutableSetOf<Device>()
	visited.add(start)
	while (queue.isNotEmpty()) {
		val (current, depth) = queue.removeFirst()
		if (current == toNode) {
			println("Found target node ${current.name} with a depth of $depth")
			return depth
		}
		visited.add(current)
		for (next in current.outputs) {
			if (next !in visited) {
				queue.addLast(Pair(next, depth + 1))
			}
		}
	}
	return -1
}

//fun findPathsToOut(device: Device, foundDac: Boolean, foundFft: Boolean): Int {
//	var foundDac = foundDac
//	if (!foundDac && device.name == "dac") {
//		foundDac = true
//	}
//	var foundFft = foundFft
//	if (!foundFft && device.name == "fft") {
//		foundFft = true
//	}
//	//println(currentPath.joinToString(",") { it.name })
//	if (device.name == "out") {
//		if (foundDac && foundFft) {
//			return 1
//		} else {
//			return 0
//		}
//	}
//	var sum = 0
//	for (nextDevice in device.outputs) {
//		sum += findPathsToOut(nextDevice, foundDac, foundFft)
//	}
//	return sum
//}


fun main() {
	val input = File("inputs/day11.txt").readLines()
	val data = preProcessData(input)
	val answer1 = part1(data)
	println("Answer 1: $answer1") // 758
	val answer2 = part2(data)
	println("Answer 2: $answer2") // 490695961032000
}