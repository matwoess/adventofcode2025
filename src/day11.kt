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
	return countValidPathsRecursive(start, leaf, maxDepth = Int.MAX_VALUE)
}

fun countValidPathsRecursive(device: Device, leafDevice: Device, maxDepth: Int, currDepth: Int = 0): Int {
	if (device == leafDevice) return 1
	if (currDepth > maxDepth) return 0
	var sum = 0
	for (nextDevice in device.outputs) {
		sum += countValidPathsRecursive(nextDevice, leafDevice, maxDepth, currDepth + 1)
	}
	return sum
}

fun part2(devices: List<Device>): String {
	val svr = devices.find { it.name == "svr" } ?: return "-1"
	val dac = devices.find { it.name == "dac" } ?: return "-1"
	val fft = devices.find { it.name == "fft" } ?: return "-1"
	val out = devices.find { it.name == "out" } ?: return "-1"
	val depthSvrToFft = getDeviceDepthByBFS(svr, fft)
	val depthFftToDac = getDeviceDepthByBFS(fft, dac)
	val depthDacToOut = getDeviceDepthByBFS(dac, out)
	val extraDepthTolerancy = 3 // magic number to allow some (not too much) extra depth for alternative paths
	val srvToFft = countValidPathsRecursive(svr, fft, maxDepth = depthSvrToFft + extraDepthTolerancy)
	val fftToDac = countValidPathsRecursive(fft, dac, maxDepth = depthFftToDac + extraDepthTolerancy)
	val dacToOut = countValidPathsRecursive(dac, out, maxDepth = depthDacToOut + extraDepthTolerancy)
	return "${srvToFft.toLong() * fftToDac * dacToOut} (srv-fft:$srvToFft * fft-dac:$fftToDac * dac-out:$dacToOut)"
}

fun getDeviceDepthByBFS(start: Device, targetNode: Device): Int {
	val queue = ArrayDeque(listOf(Pair(start, 0)))
	val visited = mutableSetOf<Device>()
	visited.add(start)
	while (queue.isNotEmpty()) {
		val (current, depth) = queue.removeFirst()
		if (current == targetNode) {
			return depth
		}
		visited.add(current)
		for (next in current.outputs) {
			if (next !in visited) {
				queue.addLast(Pair(next, depth + 1))
			}
		}
	}
	throw IllegalStateException("No path from $start to $targetNode found")
}

fun main() {
	val input = File("inputs/day11.txt").readLines()
	val data = preProcessData(input)
	val answer1 = part1(data)
	println("Answer 1: $answer1") // 758
	val answer2 = part2(data)
	println("Answer 2: $answer2") // 490695961032000
}