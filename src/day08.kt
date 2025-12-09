package day08

import java.io.File
import kotlin.math.pow
import kotlin.math.sqrt

data class Point3D(val x: Int, val y: Int, val z: Int) {
	fun distanceTo(other: Point3D): Double {
		return sqrt(
			(x - other.x).toDouble().pow(2)
					+ (y - other.y).toDouble().pow(2)
					+ (z - other.z).toDouble().pow(2)
		)
	}

	val consoleColor = "\u001B[38;2;${x % 256};${y % 256};${z % 256}m"

	override fun toString(): String {
		return "$consoleColor($x,$y,$z)\u001B[0m"
	}
}

data class Connection(val from: Point3D, val to: Point3D) {
	override fun toString(): String {
		return "Connection(from=$from, to=$to, distance=${from.distanceTo(to)})"
	}
}

// Define arbitrary ordering for Point3D to use in sets
val pointComparator = Comparator<Point3D> { p1, p2 ->
	when {
		p1.x != p2.x -> p1.x - p2.x
		p1.y != p2.y -> p1.y - p2.y
		else -> p1.z - p2.z
	}
}

fun getUniqueJBoxConnection(box1: Point3D, box2: Point3D): Connection {
	return if (pointComparator.compare(box1, box2) < 0) {
		Connection(box1, box2)
	} else {
		Connection(box2, box1)
	}
}

private fun getMinDistanceConnection(
	allPoints: List<Point3D>,
	existingConnections: MutableSet<Connection>
): Connection? {
	var minDistance: Double = Double.MAX_VALUE
	var closestPair: Connection? = null
	for (point1 in allPoints) {
		for (point2 in allPoints) {
			if (point1 == point2) {
				continue
			}
			val connection = getUniqueJBoxConnection(point1, point2)
			if (existingConnections.contains(connection)) {
				continue
			}
			val distance = point1.distanceTo(point2)
			if (distance < minDistance) {
				minDistance = distance
				closestPair = connection
			}
		}
	}
	return closestPair
}

fun preProcessData(lines: List<String>): List<Point3D> {
	return lines.map {
		val coordinates = it.split(","); Point3D(
		coordinates[0].toInt(),
		coordinates[1].toInt(),
		coordinates[2].toInt()
	)
	}
}

private fun extendCircuits(circuits: MutableList<MutableSet<Point3D>>, newConnection: Connection) {
	val existingCircuit = circuits.firstOrNull { newConnection.from in it || newConnection.to in it }
	if (existingCircuit != null) {
		existingCircuit.add(newConnection.from)
		existingCircuit.add(newConnection.to)
	} else {
		circuits.add(mutableSetOf(newConnection.from, newConnection.to))
	}
}

private fun mergeCircuits(circuits: MutableList<MutableSet<Point3D>>): MutableList<MutableSet<Point3D>> {
	val newCircuits = mutableListOf<MutableSet<Point3D>>()
	for (circuit in circuits) {
		val overlappingCircuit = newCircuits.firstOrNull { it.intersect(circuit).isNotEmpty() }
		if (overlappingCircuit != null) {
			overlappingCircuit.addAll(circuit)
		} else {
			newCircuits.add(circuit)
		}
	}
	return newCircuits
}

fun part1(jBoxes: List<Point3D>): Int {
	val connections = mutableSetOf<Connection>()
	var circuits = mutableListOf<MutableSet<Point3D>>()
	for (i in 0..<1_000) {
		val closestPair: Connection = getMinDistanceConnection(jBoxes, connections) ?: break
		connections.add(closestPair)
		extendCircuits(circuits, closestPair)
		circuits = mergeCircuits(circuits)
	}
	circuits.sortByDescending { it.size }
	return circuits.take(3).fold(1) { acc, set -> acc * set.size }
}

fun part2(jBoxes: List<Point3D>): Int {
	val connections = mutableSetOf<Connection>()
	var circuits = mutableListOf<MutableSet<Point3D>>()
	for (box in jBoxes) {
		circuits.add(mutableSetOf(box))
	}
	var lastConnection: Connection? = null
	while (true) {
		val closestPair: Connection = getMinDistanceConnection(jBoxes, connections) ?: break
		connections.add(closestPair)
		extendCircuits(circuits, closestPair)
		circuits = mergeCircuits(circuits)
		if (connections.size > 5 && circuits.size == 1) { // 5 is arbitrary to avoid premature ending
			lastConnection = closestPair
			break
		}
	}
	return lastConnection!!.from.x * lastConnection.to.x
}


fun main() {
	val input = File("inputs/day08.txt").readLines()
	val data = preProcessData(input)
	val answer1 = part1(data)
	println("Answer 1: $answer1") // 123420
	val answer2 = part2(data)
	println("Answer 2: $answer2") // 673096646
}