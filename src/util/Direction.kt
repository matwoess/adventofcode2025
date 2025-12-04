package util

enum class Direction {
	N, S, W, E, NW, NE, SW, SE;

	fun directionalOffset(): Pair<Int, Int> = when (this) {
		N -> Pair(0, -1)
		S -> Pair(0, 1)
		W -> Pair(-1, 0)
		E -> Pair(1, 0)
		NW -> Pair(-1, -1)
		NE -> Pair(1, -1)
		SW -> Pair(-1, 1)
		SE -> Pair(1, 1)

	}
}