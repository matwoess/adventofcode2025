package day04

import util.Direction
import util.Grid2D
import java.io.File


fun preProcessData(arrayString: String): Grid2D<Char> {
	return Grid2D(arrayString, elemDelimiter = "")
}

fun part1(grid: Grid2D<Char>): Int {
	println(grid)
	val aPositions = grid.getPositions()
		.filter { it.el == '@' }
		.toList()
	var validPositions = 0
	for (aPos in aPositions) {
		if (isValid(grid, aPos)) {
			validPositions++
		}
	}
	return validPositions
}

private fun isValid(
	grid: Grid2D<Char>,
	aPos: Grid2D.Position<Char>
): Boolean {
	val nw = grid.getAdjacentValue(aPos, Direction.NW)
	val n = grid.getAdjacentValue(aPos, Direction.N)
	val ne = grid.getAdjacentValue(aPos, Direction.NE)
	val e = grid.getAdjacentValue(aPos, Direction.E)
	val se = grid.getAdjacentValue(aPos, Direction.SE)
	val s = grid.getAdjacentValue(aPos, Direction.S)
	val sw = grid.getAdjacentValue(aPos, Direction.SW)
	val w = grid.getAdjacentValue(aPos, Direction.W)
	return listOf(nw, n, ne, e, se, s, sw, w).count { '@' == it } < 4
}

fun part2(grid: Grid2D<Char>): Int {
	var grid = grid
	var removedRolls = 0
	var cont = true
	while (cont) {
		val aPositions = grid.getPositions()
			.filter { it.el == '@' }
			.toList()
		val toRemove = mutableListOf<Grid2D.Position<Char>>()
		for (aPos in aPositions) {
			if (isValid(grid, aPos)) {
				toRemove.add(aPos)
			}
		}
		if (toRemove.isEmpty()) {
			cont = false
		} else {
			removedRolls+= toRemove.size
			val values = toRemove.map { '.' }
			grid = grid.copyWithModifications(toRemove, values)
		}
	}
	return removedRolls
}


fun main() {
	val input = File("inputs/day04.txt").readText()
	val data = preProcessData(input)
	val answer1 = part1(data)
	println("Answer 1: $answer1") // 1464
	val answer2 = part2(data)
	println("Answer 2: $answer2") // 8409
}