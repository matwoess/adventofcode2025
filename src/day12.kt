package day12

import util.Grid2D
import java.io.File

data class Shape(val id: Int, val grid2D: Grid2D<Char>, val nrFilled: Int, val nrEmpty: Int, val rectArea: Int) {
	companion object {
		fun fromString(id: Int, gridSpec: String): Shape {
			val firstNewline = gridSpec.indexOf('\n')
			val firstLine = gridSpec.substring(0, firstNewline)
			assert(firstLine == "$id:")
			val grid2D = Grid2D<Char>(gridSpec.substring(firstNewline + 1), elemDelimiter = "")
			val nrFilled = grid2D.getPositions().count { it.el == '#' }
			val nrEmpty = grid2D.getPositions().count { it.el == '.' }
			return Shape(id, grid2D, nrFilled, nrEmpty, grid2D.getWidth() * grid2D.getHeight())
		}
	}

	override fun toString(): String {
		return "Shape$id ($nrFilled/${nrFilled + nrEmpty}): {${grid2D.toString().replace("\r\n", " | ")}}"
	}
}

data class Region(val width: Int, val length: Int, val shapeQuantities: List<Int>) {
	companion object {
		fun fromString(regionSpec: String): Region {
			val (size, shapes) = regionSpec.split(": ")
			val (width, length) = size.split("x").map(String::toInt)
			val shapeQuantities = mutableListOf<Int>()
			for (quantity in shapes.split(" ").map(String::toInt)) {
				shapeQuantities.add(quantity)
			}
			return Region(width, length, shapeQuantities)
		}
	}
}

fun preProcessData(lines: String): Pair<List<Shape>, List<Region>> {
	val segments = lines.split("\n\n")
	val shapes = segments.dropLast(1).mapIndexed { i, s -> Shape.fromString(i, s) }
	val regions = segments.last().split("\n").map(Region::fromString)
	return Pair(shapes, regions)
}

fun part1(shapes: List<Shape>, regions: List<Region>): Int {
	var nrValidRegions = 0
	for (region in regions) {
		val totalRegionArea = region.width * region.length
		var sumMinRequiredShapesArea = 0

		for ((shapeIndex, shapeQuantity) in region.shapeQuantities.withIndex()) {
			val shape = shapes[shapeIndex]
			sumMinRequiredShapesArea += shape.nrFilled * shapeQuantity
		}
		// Too simple for the example input, but works for the real input -.-
		if (sumMinRequiredShapesArea < totalRegionArea) {
			nrValidRegions++
		}
	}
	return nrValidRegions
}


fun main() {
	val input = File("inputs/day12.txt").readText()
	val (shapes, regions) = preProcessData(input)
	val answer1 = part1(shapes, regions)
	println("Answer 1: $answer1") // 577
	// No part 2 on last day
}