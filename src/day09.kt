package day09

import java.io.File
import kotlin.math.abs

data class Point2D(val x: Long, val y: Long) {
	fun areaWith(other: Point2D): Long {
		return (abs(x - other.x) + 1) * (abs(y - other.y) + 1)
	}

	val consoleColor = "\u001B[38;2;${(x * 37) % 255};${(y * 57) % 255};${((x + y) * 17) % 255}m"

	override fun toString(): String {
		return "$consoleColor$x,$y\u001B[0m"
	}
}

data class Rectangle(val topLeft: Point2D, val topRight: Point2D, val bottomLeft: Point2D, val bottomRight: Point2D) {
	constructor(minX: Long, minY: Long, maxX: Long, maxY: Long) : this(
		Point2D(minX, minY),
		Point2D(maxX, minY),
		Point2D(minX, maxY),
		Point2D(maxX, maxY)
	)

	override fun toString(): String {
		return "Rectangle[TL=$topLeft, TR=$topRight, BL=$bottomLeft, BR=$bottomRight]"
	}

	fun allPoints(): Sequence<Point2D> {
		return sequenceOf(topLeft, topRight, bottomLeft, bottomRight)
	}

	fun allEdges(): Sequence<Connection> {
		return sequenceOf(
			Connection(this.topLeft, this.topRight),
			Connection(this.topRight, this.bottomRight),
			Connection(this.bottomRight, this.bottomLeft),
			Connection(this.bottomLeft, this.topLeft)
		)
	}
}

data class Connection(val from: Point2D, val to: Point2D) {
	override fun toString(): String {
		return "Connection between $from and $to: area=${from.areaWith(to)}"
	}

	fun toRectangle(): Rectangle {
		val minX = minOf(from.x, to.x)
		val maxX = maxOf(from.x, to.x)
		val minY = minOf(from.y, to.y)
		val maxY = maxOf(from.y, to.y)
		return Rectangle(minX, minY, maxX, maxY)
	}

	fun crossesPerimeter(perimeter: MutableList<Connection>): Boolean {
		return perimeter.any { perimeterConnection -> connectionsIntersect(this, perimeterConnection) }
	}
}

// Define arbitrary ordering for Point2D to use in sets
val pointComparator = Comparator<Point2D> { p1, p2 ->
	when {
		p1.x != p2.x -> p1.x - p2.x
		else -> p1.y - p2.y
	}.toInt()
}

fun getUniquePointCombination(tile1: Point2D, tile2: Point2D): Connection {
	return if (pointComparator.compare(tile1, tile2) < 0) {
		Connection(tile1, tile2)
	} else {
		Connection(tile2, tile1)
	}
}

private fun getMaxAreaConnection(allPoints: List<Point2D>, excludeConnections: Set<Connection>): Connection? {
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
			if (excludeConnections.contains(connection)) {
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
	return furthestPair
}

fun preProcessData(lines: List<String>): List<Point2D> {
	return lines.map {
		val coordinates = it.split(","); Point2D(
		coordinates[0].toLong(),
		coordinates[1].toLong()
	)
	}
}

fun part1(redTiles: List<Point2D>): Long {
	val biggestAreaPair = getMaxAreaConnection(redTiles, emptySet())
	return biggestAreaPair!!.from.areaWith(biggestAreaPair.to)
}

fun part2(redTiles: List<Point2D>): Long {
	val perimeter = mutableListOf<Connection>()
	for (pair in redTiles.windowed(2)) {
		perimeter.add(Connection(pair[0], pair[1]))
	}
	perimeter.add(Connection(redTiles.last(), redTiles.first()))
	//println(perimeter.joinToString("\n"))
	var biggestAreaPairInBounds: Connection? = null
	val alreadyTried = mutableSetOf<Connection>()
	while (true) {
		val nextBiggestPair = getMaxAreaConnection(redTiles, alreadyTried)
		if (nextBiggestPair == null) {
			break
		}
		//println(nextBiggestPair)
		alreadyTried.add(nextBiggestPair)
		val rectangle = nextBiggestPair.toRectangle()
		//println(rectangle)
		val allCornersInsidePerimeter = rectangle.allPoints().all { isPointInsidePerimeter(it, perimeter) }
		if (allCornersInsidePerimeter && rectangle.allEdges().none { it.crossesPerimeter(perimeter) }) {
			biggestAreaPairInBounds = nextBiggestPair
			break
		}
	}
	return biggestAreaPairInBounds!!.from.areaWith(biggestAreaPairInBounds.to)
}

enum class Orientation {
	COLLINEAR,
	LEFT,
	RIGHT
}

fun orientation(con: Connection, point: Point2D): Orientation {
	// cross product sign
	val value = (con.to.y - con.from.y) * (point.x - con.to.x) - (con.to.x - con.from.x) * (point.y - con.to.y)
	return when {
		value == 0L -> Orientation.COLLINEAR
		value > 0L -> Orientation.LEFT
		else -> Orientation.RIGHT
	}
}

fun connectionsIntersect(c1: Connection, c2: Connection): Boolean {
	val o1 = orientation(c1, c2.from)
	val o2 = orientation(c1, c2.to)
	val o3 = orientation(c2, c1.from)
	val o4 = orientation(c2, c1.to)

	// Intersection AND none of the orientations are collinear
	return (o1 != o2 && o3 != o4) && sequenceOf(o1, o2, o3, o4).none(Orientation.COLLINEAR::equals)
}

fun isPointOnConnection(point: Point2D, con: Connection): Boolean {
	return orientation(con, point) == Orientation.COLLINEAR
			&& point.x in minOf(con.from.x, con.to.x)..maxOf(con.from.x, con.to.x)
			&& point.y in minOf(con.from.y, con.to.y)..maxOf(con.from.y, con.to.y)
}

fun isPointInsidePerimeter(
	point: Point2D,
	perimeter: MutableList<Connection>
): Boolean {
	// Ray-casting algorithm: cast a horizontal ray to +infinity and count intersections
	var intersections = 0
	for (edge in perimeter) {
		if (isPointOnConnection(point, edge)) return true
		val a = edge.from
		val b = edge.to
		if ((a.y > point.y) != (b.y > point.y)) {
			val xIntersect = a.x + (b.x - a.x) * (point.y - a.y) / (b.y - a.y).toDouble()
			if (point.x < xIntersect) {
				intersections++
			}
		}
	}
	// inside if odd intersections
	return intersections % 2 == 1
}


fun main() {
	val input = File("inputs/day09.txt").readLines()
	val data = preProcessData(input)
	val answer1 = part1(data)
	println("Answer 1: $answer1") // 4735222687
	val answer2 = part2(data)
	println("Answer 2: $answer2") // 1569262188
}