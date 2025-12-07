package day07

import util.Direction
import util.Grid2D
import java.io.File


fun preProcessData(input: String) = Grid2D<Char>(input, elemDelimiter = "")

fun part1(grid: Grid2D<Char>): Int {
	val startPoint = grid.getPositions().first { it.el == 'S' }
	val beams = ArrayDeque<Grid2D.Position<Char>>()
	beams.add(startPoint)
	val previousSplits = mutableSetOf<Grid2D.Position<Char>>()
	val previousBeamStarts = mutableSetOf<Grid2D.Position<Char>>()
	while (beams.isNotEmpty()) {
		val beam = beams.removeFirst()
		val splitPoint = grid.getDirectionalPositionSequence(beam, Direction.S).dropWhile { it.el != '^' }.firstOrNull()
		if (splitPoint != null) {
			previousSplits.add(splitPoint)
			val leftBeam = grid.getAdjacentPosition(splitPoint, Direction.W)
			val rightBeam = grid.getAdjacentPosition(splitPoint, Direction.E)
			if (leftBeam != null && previousBeamStarts.contains(leftBeam).not()) {
				beams.add(leftBeam)
				previousBeamStarts.add(leftBeam)
			}
			if (rightBeam != null && previousBeamStarts.contains(rightBeam).not()) {
				beams.add(rightBeam)
				previousBeamStarts.add(rightBeam)
			}
		}
	}
	return previousSplits.size
}

fun part2(grid: Grid2D<Char>): Int {
	val startPoint = grid.getPositions().first { it.el == 'S' }
	val visitedSet = mutableSetOf<Grid2D.Position<Char>>()
	visitedSet.add(startPoint)
	val timelineCount = dfs(grid, startPoint, visitedSet)
	return timelineCount
}

private fun getNextSplitPoint(grid: Grid2D<Char>, fromPos: Grid2D.Position<Char>): Grid2D.Position<Char>? =
	grid.getDirectionalPositionSequence(fromPos, Direction.S).dropWhile { it.el != '^' }.firstOrNull()

fun dfs(grid: Grid2D<Char>, pos: Grid2D.Position<Char>, visited: MutableSet<Grid2D.Position<Char>>) : Int {
	var timelineCount = 0
	val splitPoint = getNextSplitPoint(grid, pos) ?: return 1
	visited.add(splitPoint)
	val leftBeam = grid.getAdjacentPosition(splitPoint, Direction.W)
	val rightBeam = grid.getAdjacentPosition(splitPoint, Direction.E)
	if (leftBeam != null && !visited.contains(leftBeam)) {
		timelineCount += dfs(grid, leftBeam, visited)
	}
	if (rightBeam != null && !visited.contains(rightBeam)) {
		timelineCount += dfs(grid, rightBeam, visited)
	}
	return timelineCount
}


fun main() {
	val input = File("examples/day07.txt").readText()
	val data = preProcessData(input)
	val answer1 = part1(data)
	println("Answer 1: $answer1") // ANSWER
	val answer2 = part2(data)
	println("Answer 2: $answer2") // ANSWER
}