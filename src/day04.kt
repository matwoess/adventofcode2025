package day04

import util.Grid2D
import java.io.File


fun preProcessData(arrayString: String): Grid2D<Char> {
	return Grid2D(arrayString, elemDelimiter = "")
}

private fun forkliftCanAccess(grid: Grid2D<Char>, pos: Grid2D.Position<Char>): Boolean =
	grid.getAllAdjacentValues(pos).count { '@' == it } < 4

fun part1(grid: Grid2D<Char>): Int =
	grid.getPositions()
		.filter { it.el == '@' }
		.count { forkliftCanAccess(grid, it) }

fun part2(grid: Grid2D<Char>): Int {
	var grid = grid
	var removedRolls = 0
	while (true) {
		val toRemove = grid.getPositions()
			.filter { it.el == '@' }
			.filter { forkliftCanAccess(grid, it) }
			.toList()
		if (toRemove.isEmpty()) {
			break
		}
		val values = toRemove.map { '.' }
		grid = grid.copyWithModifications(toRemove, values)
		removedRolls += toRemove.size
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