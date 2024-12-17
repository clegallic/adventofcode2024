package d06

import readInput

data class Coord(val x: Int, val y: Int) {
    fun moveBy(direction: Direction) = Coord(this.x + direction.offsetx, this.y + direction.offsety)
    fun isOutOfBounds(bounds: Coord) = this.x < 0 || this.x > bounds.x - 1 || this.y < 0 || this.y > bounds.y - 1
}

enum class Direction(val offsetx: Int, val offsety: Int) {
    TOP(0, -1), LEFT(-1, 0), RIGHT(1, 0), BOTTOM(0, 1);
}

class Guard(var coord: Coord, var direction: Direction) {
    fun move() {
        coord = coord.moveBy(direction)
    }

    fun turnLeft() {
        direction = when (direction) {
            Direction.TOP -> Direction.RIGHT
            Direction.RIGHT -> Direction.BOTTOM
            Direction.BOTTOM -> Direction.LEFT
            Direction.LEFT -> Direction.TOP
        }
    }
}

data class LabPlace(val coord: Coord, val isObstacle: Boolean, val isGuard: Boolean)

class Lab(
    private val places: List<List<LabPlace>>,
    val guard: Guard,
    val visited: MutableSet<Pair<Coord, Direction>>,
    var loops: Int = 0
) {

    fun moveGuard() {
        val nextPlace = guard.coord.moveBy(guard.direction)
        if (places[nextPlace.y][nextPlace.x].isObstacle)
            guard.turnLeft()//.also { moveGuard() }
        else guard.move().also { visited.add(Pair(nextPlace, guard.direction)) }
    }

    fun guardWillLeave() = guard.coord.moveBy(guard.direction).isOutOfBounds(bounds())

    fun findLoops() {
        if (when (guard.direction) {
                Direction.TOP -> isLoop(
                    guard.coord.x + 1..<bounds().x,
                    guard.coord.y..guard.coord.y,
                    Direction.RIGHT
                )

                Direction.RIGHT -> isLoop(
                    guard.coord.x..guard.coord.x,
                    guard.coord.y + 1..<bounds().y,
                    Direction.BOTTOM
                )

                Direction.BOTTOM -> isLoop(
                    0..<guard.coord.x,
                    guard.coord.y..guard.coord.y,
                    Direction.LEFT
                )

                Direction.LEFT -> isLoop(
                    guard.coord.x..guard.coord.x,
                    0..<guard.coord.y,
                    Direction.TOP
                )
            }
        ) {
            loops++
                .also { println("LOOP ! : ${guard.coord} ${guard.direction} ") }
        }
    }

    private fun isLoop(xRange: IntRange, yRange: IntRange, direction: Direction) =
        visited.any { it.first.x in xRange && it.first.y in yRange && it.second == direction }

    private fun bounds() = Coord(places[0].size, places.size)
}

fun parseInput(input: List<String>) = input
    .mapIndexed { y, line -> line.mapIndexed { x, c -> LabPlace(Coord(x, y), c == '#', c == '^') } }
    .let {
        val guard = findGuard(it)
        Lab(it, guard, mutableSetOf(Pair(guard.coord, Direction.TOP)))
    }

fun findGuard(places: List<List<LabPlace>>) =
    Guard(places.flatten().first { place -> place.isGuard }.coord, Direction.TOP)

fun moveGuardUntilLeave(lab: Lab) {
    do {
        lab.moveGuard()
        lab.findLoops()
    } while (!lab.guardWillLeave())
}

fun part1(lab: Lab) =
    moveGuardUntilLeave(lab)
        .let {
            lab.visited.map { it.first }.toSet().size
        }

fun part2(lab: Lab) =
    moveGuardUntilLeave(lab)
        .let {
            lab.loops
        }

fun main() {
    val inputPart1 = readInput(6, "sample")
    println("Part 1 : ${part1(parseInput(inputPart1))}")
    val inputPart2 = readInput(6, "input")
    println("Part 2 : ${part2(parseInput(inputPart2))}")
}
