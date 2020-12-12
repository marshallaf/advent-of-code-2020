package util

data class Position(var x: Int, var y: Int)

enum class Direction(val x: Int, val y: Int, val degrees: Int) {
    NORTH(0, 1, 0),
    WEST(-1, 0, 270),
    EAST(1, 0, 90),
    SOUTH(0, -1, 180);

    companion object {
        fun withDegrees(degrees: Int) = when (degrees) {
            0 -> NORTH
            90 -> EAST
            180 -> SOUTH
            270 -> WEST
            else -> throw IllegalArgumentException("unsupported degree value: $degrees")
        }
    }
}

