package day07

import util.Direction
import util.Grid2D
import java.io.File

val SPLIT_DIRECTIONS = arrayOf(Direction.W, Direction.E)

fun preProcessData(input: String) = Grid2D<Char>(input, elemDelimiter = "")

fun part1(grid: Grid2D<Char>): Int {
	val startPoint = grid.getPositions().first { it.el == 'S' }
	val uniqueSplitPoints = mutableSetOf<Grid2D.Position<Char>>()
	val uniqueBeamStarts = mutableSetOf<Grid2D.Position<Char>>()
	val beams = ArrayDeque<Grid2D.Position<Char>>()
	beams.add(startPoint)
	while (beams.isNotEmpty()) {
		val beam = beams.removeFirst()
		val splitPoint = getNextSplitPoint(grid, beam) ?: continue
		uniqueSplitPoints.add(splitPoint)
		for (dir in SPLIT_DIRECTIONS) {
			val beamPos = grid.getAdjacentPosition(splitPoint, dir)
			if (beamPos != null && !uniqueBeamStarts.contains(beamPos)) {
				beams.add(beamPos)
				uniqueBeamStarts.add(beamPos)
			}
		}
	}
	return uniqueSplitPoints.size
}

fun part2(grid: Grid2D<Char>): Long {
	val startPoint = grid.getPositions().first { it.el == 'S' }
	val subTimelineCache = mutableMapOf<Grid2D.Position<Char>, Long>()
	val totalTimelineCount = calculateTimeVariantsCount(grid, startPoint, subTimelineCache)
	return totalTimelineCount
}

// Search for next beam splitter '^' from the given position going downwards
private fun getNextSplitPoint(grid: Grid2D<Char>, fromPos: Grid2D.Position<Char>): Grid2D.Position<Char>? =
	grid.getDirectionalPositionSequence(fromPos, Direction.S).dropWhile { it.el != '^' }.firstOrNull()

fun calculateTimeVariantsCount(
	grid: Grid2D<Char>,
	pos: Grid2D.Position<Char>,
	subTimelineCache: MutableMap<Grid2D.Position<Char>, Long>
): Long {
	var timelineCount = 0L
	val splitPoint = getNextSplitPoint(grid, pos) ?: return 1
	for (dir in SPLIT_DIRECTIONS) {
		val beamPos = grid.getAdjacentPosition(splitPoint, dir)
		if (beamPos != null) {
			if (subTimelineCache.contains(beamPos)) {
				timelineCount += subTimelineCache[beamPos]!!
			} else {
				val pathCount = calculateTimeVariantsCount(grid, beamPos, subTimelineCache)
				subTimelineCache[beamPos] = pathCount
				timelineCount += pathCount
			}
		}
	}
	return timelineCount
}


fun main() {
	val input = File("inputs/day07.txt").readText()
	val data = preProcessData(input)
	val answer1 = part1(data)
	println("Answer 1: $answer1") // 1602
	val answer2 = part2(data)
	println("Answer 2: $answer2") // 135656430050438
}