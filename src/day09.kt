package day09

import java.io.File
import kotlin.math.abs

data class Point2D(val x: Int, val y: Int) {
	fun areaWith(other: Point2D): Long {
		return (abs(x - other.x)+1).toLong() * (abs(y - other.y)+1).toLong()
	}

	val consoleColor = "\u001B[38;2;${(x * 37) % 255};${(y * 57) % 255};${((x + y) * 17) % 255}m"

	override fun toString(): String {
		return "$consoleColor$x,$y\u001B[0m"
	}
}

data class Connection(val from: Point2D, val to: Point2D) {
	override fun toString(): String {
		return "Connection between $from and $to: area=${from.areaWith(to)}"
	}
}

// Define arbitrary ordering for Point2D to use in sets
val pointComparator = Comparator<Point2D> { p1, p2 ->
	when {
		p1.x != p2.x -> p1.x - p2.x
		else -> p1.y - p2.y
	}
}

fun getUniquePointCombination(tile1: Point2D, tile2: Point2D): Connection {
	return if (pointComparator.compare(tile1, tile2) < 0) {
		Connection(tile1, tile2)
	} else {
		Connection(tile2, tile1)
	}
}

private fun getMaxAreaConnection(allPoints: List<Point2D>): Connection? {
	var maxArea = Long.MIN_VALUE
	var furthestPair: Connection? = null
	val allConnections = mutableSetOf<Connection>()
	for (point1 in allPoints) {
		for (point2 in allPoints) {
			if (point1 == point2) {
				continue
			}
			val connection = getUniquePointCombination(point1, point2)
			if (allConnections.contains(connection)) {
				continue
			}
			allConnections.add(connection)
			val area = connection.from.areaWith(connection.to)
			if (area > maxArea) {
				maxArea = area
				furthestPair = connection
			}
		}
	}
	println(allConnections.joinToString("\n"))
	return furthestPair
}

fun preProcessData(lines: List<String>): List<Point2D> {
	return lines.map {
		val coordinates = it.split(","); Point2D(
		coordinates[0].toInt(),
		coordinates[1].toInt()
	)
	}
}

fun part1(redTiles: List<Point2D>): Long {
	//printTiles(redTiles)
	val biggestAreaPair = getMaxAreaConnection(redTiles)
	println(biggestAreaPair)
	return biggestAreaPair!!.from.areaWith(biggestAreaPair.to)
}

fun part2(redTiles: List<Point2D>): Int {
	return 0
}

fun printTiles(allTiles: List<Point2D>) {
	val minX = allTiles.minOf { it.x }
	val minY = allTiles.minOf { it.y }
	val maxX = allTiles.maxOf { it.x }
	val maxY = allTiles.maxOf { it.y }
	println("($minX,$minY) to ($maxX,$maxY)")
	for (y in minY..maxY) {
		for (x in minX..maxX) {
			val tile = Point2D(x, y)
			if (allTiles.contains(tile)) {
				print("${tile.consoleColor}#\u001B[0m")
			} else {
				print(".")
			}
		}
		println()
	}
}


fun main() {
	val input = File("inputs/day09.txt").readLines()
	val data = preProcessData(input)
	val answer1 = part1(data)
	println("Answer 1: $answer1") // 4735222687
	val answer2 = part2(data)
	println("Answer 2: $answer2") // ANSWER
}