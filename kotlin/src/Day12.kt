import util.Direction
import util.Position
import util.readLines
import java.io.File
import kotlin.math.absoluteValue

fun main() {
    val file = File("res/day12/input.txt")
//    val file = File("res/day12/test-input-0.txt")
    challengeA(file)
    challengeB(file)
}

private fun challengeA(file: File) { // 362
    var currentDirection = Direction.EAST
    val currentPosition = Position(0, 0)
    file.readLines { ShipDirective.parse(it) }
        .mapNotNull {
            when (it.action) {
                ShipAction.NORTH,
                ShipAction.SOUTH,
                ShipAction.EAST,
                ShipAction.WEST -> Movement(directionFromShipAction(it.action), it.value)
                ShipAction.FORWARD -> Movement(currentDirection, it.value)
                ShipAction.LEFT,
                ShipAction.RIGHT -> {
                    currentDirection = getNewShipFacingDirection(it, currentDirection)
                    null
                }
            }
        }.forEach {
            currentPosition.x += it.direction.x * it.distance
            currentPosition.y += it.direction.y * it.distance
        }
    println("final position is $currentPosition, and manhattan is ${currentPosition.x.absoluteValue + currentPosition.y.absoluteValue}")
}

private fun getNewShipFacingDirection(shipDirective: ShipDirective, startDirection: Direction): Direction {
    val degreesRight = normalizeToRightDegrees(shipDirective)
    val newDegrees = (startDirection.degrees + degreesRight) % 360
    return Direction.withDegrees(newDegrees)
}

private fun challengeB(file: File) { // 29895
    val shipPosition = Position(0, 0)
    val waypoint = Position(10, 1)
    file.readLines { ShipDirective.parse(it) }
        .forEach {
            when (it.action) {
                ShipAction.FORWARD -> moveShipWithWaypoint(it.value, shipPosition, waypoint)
                else -> moveWaypoint(it, waypoint)
            }
        }
    println("final position is $shipPosition, and manhattan is ${shipPosition.x.absoluteValue + shipPosition.y.absoluteValue}")
}

private fun moveShipWithWaypoint(distance: Int, shipPosition: Position, waypoint: Position) {
    shipPosition.x += waypoint.x * distance
    shipPosition.y += waypoint.y * distance
}

private fun moveWaypoint(directive: ShipDirective, position: Position) {
    when (directive.action) {
        ShipAction.LEFT,
        ShipAction.RIGHT -> {
            when (normalizeToRightDegrees(directive)) {
                90 -> {
                    val tempX = position.x
                    position.x = position.y
                    position.y = -tempX
                }
                180 -> {
                    position.x = -position.x
                    position.y = -position.y
                }
                270 -> {
                    val tempX = position.x
                    position.x = -position.y
                    position.y = tempX
                }
            }
            return
        }
        ShipAction.NORTH,
        ShipAction.SOUTH,
        ShipAction.EAST,
        ShipAction.WEST -> {
            val direction = directionFromShipAction(directive.action)
            position.x += direction.x * directive.value
            position.y += direction.y * directive.value
        }
        ShipAction.FORWARD -> throw IllegalArgumentException("the relative position of the waypoint doesn't change for ShipAction.FORWARD")
    }
}

private fun normalizeToRightDegrees(directive: ShipDirective): Int {
    return when (directive.action) {
        ShipAction.LEFT -> {
            when (directive.value) {
                90 -> 270
                270 -> 90
                180 -> 180
                else -> throw IllegalArgumentException("unexpected number for rotation degrees: ${directive.value}")
            }
        }
        ShipAction.RIGHT -> {
            when (directive.value) {
                90, 180, 270 -> directive.value
                else -> throw IllegalArgumentException("unexpected number for rotation degrees: ${directive.value}")
            }
        }
        else -> throw IllegalArgumentException("unexpected ShipAction for rotation: ShipAction.${directive.action.name}")
    }
}

private fun directionFromShipAction(action: ShipAction) = when (action) {
    ShipAction.NORTH -> Direction.NORTH
    ShipAction.SOUTH -> Direction.SOUTH
    ShipAction.EAST -> Direction.EAST
    ShipAction.WEST -> Direction.WEST
    else -> throw IllegalArgumentException("unsupported ShipAction to Direction: ShipAction.${action.name}")
}

private data class Movement(val direction: Direction, val distance: Int)

private enum class ShipAction {
    NORTH,
    SOUTH,
    EAST,
    WEST,
    FORWARD,
    LEFT,
    RIGHT
}

private data class ShipDirective(val action: ShipAction, val value: Int) {
    companion object {
        fun parse(serial: String): ShipDirective {
            val value = serial.substring(1).toInt()
            val action = when (val actionSerial = serial[0]) {
                'N' -> ShipAction.NORTH
                'S' -> ShipAction.SOUTH
                'E' -> ShipAction.EAST
                'W' -> ShipAction.WEST
                'F' -> ShipAction.FORWARD
                'L' -> ShipAction.LEFT
                'R' -> ShipAction.RIGHT
                else -> throw IllegalArgumentException("unexpected character for ship action: $actionSerial")
            }
            return ShipDirective(action, value)
        }
    }
}
