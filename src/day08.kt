package day08

import java.io.File
import kotlin.math.abs
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

val pointComparator = Comparator<Point3D> { p1, p2 ->
	when {
		p1.x != p2.x -> p1.x - p2.x
		p1.y != p2.y -> p1.y - p2.y
		else -> p1.z - p2.z
	}
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

fun part1(jBoxes: List<Point3D>): Int {
	val connections = mutableSetOf<Connection>()
	var circuits = mutableListOf<MutableSet<Point3D>>()
	for (i in 0..<1_000) {
		// find the pair of points closest to each other
		var minDistance: Double = Double.MAX_VALUE
		var closestPair: Connection? = null
		for (box1 in jBoxes) {
			for (box2 in jBoxes) {
				if (box1 == box2) {
					continue
				}
				val connection = getjBoxConnection(box1, box2)
				if (connections.contains(connection)) {
					continue
				}
				val distance = box1.distanceTo(box2)
				if (distance < minDistance) {
					minDistance = distance
					closestPair = connection
				}
			}
		}
		if (closestPair == null) {
			break
		}
		connections.add(closestPair)
		circuits.firstOrNull { closestPair.from in it || closestPair.to in it }?.apply {
			add(closestPair.from)
			add(closestPair.to)
		} ?: circuits.add(mutableSetOf(closestPair.from, closestPair.to))
		// merge circuits if any of the sets overlap
		val newCircuits = mutableListOf<MutableSet<Point3D>>()
		for (circuit in circuits) {
			val overlappingCircuit = newCircuits.firstOrNull { it.intersect(circuit).isNotEmpty() }
			if (overlappingCircuit != null) {
				overlappingCircuit.addAll(circuit)
			} else {
				newCircuits.add(circuit)
			}
		}
		circuits = newCircuits
	}
	circuits.sortByDescending { it.size }
	return circuits.take(3).fold(1) { acc, set -> acc * set.size }
}

fun getjBoxConnection(box1: Point3D, box2: Point3D): Connection {
	return if (pointComparator.compare(box1, box2) < 0) {
		Connection(box1, box2)
	} else {
		Connection(box2, box1)
	}
}

fun part2(jBoxes: List<Point3D>): Int {
	val connections = mutableSetOf<Connection>()
	var circuits = mutableListOf<MutableSet<Point3D>>()
	for (box in jBoxes) {
		circuits.add(mutableSetOf(box))
	}
	var lastConnection: Connection? = null
	for (i in 0..<5_000) {
		println("Iteration $i: ${connections.size} connections, ${circuits.size} circuits")
		// find the pair of points closest to each other
		var minDistance = Double.MAX_VALUE
		var closestPair: Connection? = null
		for (box1 in jBoxes) {
			for (box2 in jBoxes) {
				if (box1 == box2) {
					continue
				}
				val connection = getjBoxConnection(box1, box2)
				if (connections.contains(connection)) {
					continue
				}
				val distance = box1.distanceTo(box2)
				if (distance < minDistance) {
					minDistance = distance
					closestPair = connection
				}
			}
		}
		if (closestPair == null) {
			break
		}
		println(closestPair)
		connections.add(closestPair)
		circuits.firstOrNull { closestPair.from in it || closestPair.to in it }?.apply {
			add(closestPair.from)
			add(closestPair.to)
		} ?: circuits.add(mutableSetOf(closestPair.from, closestPair.to))
		// merge circuits if any of the sets overlap
		val newCircuits = mutableListOf<MutableSet<Point3D>>()
		for (circuit in circuits) {
			val overlappingCircuit = newCircuits.firstOrNull { it.intersect(circuit).isNotEmpty() }
			if (overlappingCircuit != null) {
				overlappingCircuit.addAll(circuit)
			} else {
				newCircuits.add(circuit)
			}
		}
		circuits = newCircuits
		if (connections.size > 10 && newCircuits.size == 1) {
			lastConnection = closestPair
			break
		}
	}
	println(lastConnection)
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