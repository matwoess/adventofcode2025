package day10

import java.io.File
import kotlin.math.abs
import kotlin.math.roundToInt

const val EPSILON = 1e-6 // Tolerance for floating-point comparisons
private val MACHINE_REGEX = """\[([.#]+)] ([(\d,) ]+) \{([\d,]+)}""".toRegex()

class Machine(
	val targetLights: Int,
	val buttons: IntArray,
	val joltages: IntArray,
) {
	fun buttonCombinationsOfSize(k: Int): List<List<Int>> {
		return buttons.toList().combinations(k)
	}
}

fun preProcessData(lines: List<String>): List<Machine> {
	val machines = mutableListOf<Machine>()
	for (line in lines) {
		val matchResult = MACHINE_REGEX.matchEntire(line)
		if (matchResult != null) {
			val (lightDiagramStr, buttonWiringsStr, joltageRequirementsStr) = matchResult.destructured
			var lightDiagram = 0
			lightDiagramStr.forEachIndexed { index, ch ->
				if (ch == '#') {
					lightDiagram = lightDiagram or (1 shl index)
				}
			}
			val buttonWirings = buttonWiringsStr.split(") (").map {
				val wiringStr = it.trim('(', ')', ' ')
				var buttonWiring = 0
				val numbers = wiringStr.split(",").map(String::toInt)
				for (num in numbers) {
					buttonWiring = buttonWiring or (1 shl num)
				}
				buttonWiring
			}.toIntArray()
			val joltageRequirements = joltageRequirementsStr.split(",").map { it.trim().toInt() }.toIntArray()
			val machine = Machine(lightDiagram, buttonWirings, joltageRequirements)
			machines.add(machine)
		} else {
			throw IllegalArgumentException("Line does not match expected format: $line")
		}
	}
	return machines
}

// Non-repeat combinations of k elements from the list
fun <T> List<T>.combinations(k: Int): List<List<T>> {
	if (k == 0) return listOf(emptyList())
	if (isEmpty()) return emptyList()

	val head = this.first()
	val tail = this.drop(1)

	// Combinations that include head + combinations that don't
	return tail.combinations(k - 1).map { listOf(head) + it } + tail.combinations(k)
}

private fun getLowestButtonSequenceSize(machine: Machine): Int {
	for (k in 1..machine.buttons.size) {
		val combinations = machine.buttonCombinationsOfSize(k)
		for (combination in combinations) {
			var indicators = 0
			for (button in combination) {
				indicators = indicators xor button
			}
			if (indicators == machine.targetLights) {
				return k
			}
		}
	}
	return Integer.MIN_VALUE
}

fun part1(machines: List<Machine>): Int {
	return machines.sumOf { getLowestButtonSequenceSize(it) }
}

fun getMinButtonPresses(machine: Machine): Int {
	// Simplify equation system using Gauss-Jordan elimination.
	val mat = buildEquationMatrix(machine)
	val (nRows, nCols) = Pair(machine.joltages.size, machine.buttons.size + 1)
	val (pivotColumns, freeVariables) = gaussJordanElimination(mat, nRows, nCols)
	// Get upper bounds of free variables (the maximum number of times each button can be pressed).
	val upperBounds = freeVariables.map { col ->
		val buttonWiring = machine.buttons[col]
		machine.joltages
			.filterIndexed { r, _ -> (buttonWiring and (1 shl r)) != 0 }
			.min()
	}
	// Brute-force all combinations of free variables.
	var currentBest = Integer.MAX_VALUE
	outer@ for (freeCombination in variableCombinations(upperBounds)) {
		if (freeCombination.sum() >= currentBest) {
			continue@outer
		}
		val solution = IntArray(nCols - 1)
		for (x in pivotColumns) {
			val r = pivotColumns.indexOf(x)
			var b = mat[r][nCols - 1]
			for ((j, c) in freeVariables.withIndex()) {
				b -= mat[r][c] * freeCombination[j]
			}
			val rounded = b.roundToInt()
			if (rounded < 0 || abs(b - rounded) > EPSILON) {
				continue@outer
			}
			solution[x] = rounded
		}
		for (x in freeVariables) {
			solution[x] = freeCombination[freeVariables.indexOf(x)]
		}
		val totalSteps = solution.sum()
		if (totalSteps < currentBest) {
			currentBest = totalSteps
		}
	}
	check(currentBest != Integer.MAX_VALUE) { "No solution found for machine: $machine" }
	return currentBest
}

private fun buildEquationMatrix(machine: Machine): Array<DoubleArray> {
	val mat = Array(machine.joltages.size) { DoubleArray(machine.buttons.size + 1) }
	for ((r, joltage) in machine.joltages.withIndex()) {
		for ((c, button) in machine.buttons.withIndex()) {
			val isToggling = button and (1 shl r) != 0
			mat[r][c] = if (isToggling) 1.0 else 0.0
		}
		mat[r][machine.buttons.size] = joltage.toDouble()
	}
	return mat
}

// Solve 𝐴𝑥 = b for x
// e.g. [.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}
// 0*x0 + 0*x1 + 0*x2 + 0*x3 + 1*x4 + 1*x5 = 3
// 0*x0 + 1*x1 + 0*x2 + 0*x3 + 0*x4 + 1*x5 = 5
// 0*x0 + 0*x1 + 1*x2 + 1*x3 + 1*x4 + 0*x5 = 4
// 1*x0 + 1*x1 + 0*x2 + 1*x3 + 0*x4 + 0*x5 = 7
private fun gaussJordanElimination(
	mat: Array<DoubleArray>,
	nRows: Int,
	nCols: Int
): Pair<MutableList<Int>, MutableList<Int>> {
	val pivotColumns = mutableListOf<Int>()
	val freeVariables = mutableListOf<Int>()
	var pivotRow = 0
	for (col in 0 until nCols - 1) {
		// Remaining columns are free variables
		if (pivotRow >= nRows) {
			freeVariables.add(col)
			continue
		}
		// Find the pivot row for this column.
		var newPivot = pivotRow
		for (r in pivotRow + 1 until nRows) {
			if (abs(mat[r][col]) > abs(mat[newPivot][col])) {
				newPivot = r
			}
		}
		// If there is no pivot in this column it is a free variable.
		if (abs(mat[newPivot][col]) < EPSILON) {
			freeVariables.add(col)
			continue
		} else {
			pivotColumns.add(col)
		}
		// Swap the pivot row with the current row if necessary.
		if (newPivot != pivotRow) {
			swapRows(mat, newPivot, pivotRow)
		}
		// Normalize pivot row to 1
		if (abs(mat[pivotRow][col] - 1.0) > EPSILON) {
			val factor = 1 / mat[pivotRow][col]
			for (c in 0 until nCols) {
				mat[pivotRow][c] *= factor
			}
		}

		// Eliminate all other rows in this column.
		for (r in 0 until nRows) {
			if (r == pivotRow) continue
			val factor = mat[r][col] / mat[pivotRow][col]
			for (c in 0 until nCols) {
				mat[r][c] -= factor * mat[pivotRow][c]
			}
		}
		pivotRow++
	}
	return Pair(pivotColumns, freeVariables)
}

private fun swapRows(mat: Array<DoubleArray>, r1: Int, r2: Int) {
	val row1 = mat[r1]
	val row2 = mat[r2]
	mat[r2] = row1
	mat[r1] = row2
}

/**
 * Yields every possible combination for x variables and their upper bounds.
 * The length of the upperBounds list is its amount of variables.
 */
fun variableCombinations(upperBounds: List<Int>): Sequence<List<Int>> = sequence {
	val current = IntArray(upperBounds.size)
	while (true) {
		yield(current.toList())
		var i = upperBounds.lastIndex
		while (i >= 0 && current[i] == upperBounds[i]) {
			current[i] = 0
			i--
		}
		if (i < 0) break
		current[i]++
	}
}

fun part2(machines: List<Machine>): Int {
	return machines.sumOf(::getMinButtonPresses)
}


fun main() {
	val input = File("inputs/day10.txt").readLines()
	val data = preProcessData(input)
	val answer1 = part1(data)
	println("Answer 1: $answer1") // 375
	val answer2 = part2(data)
	println("Answer 2: $answer2") // 15377
}