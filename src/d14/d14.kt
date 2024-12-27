package d14

import readInput

const val BOUND_X = 101
const val BOUND_Y = 103

data class Coord(val x: Int, val y: Int)

data class Velocity(val x: Int, val y: Int)

data class Robot(var coord: Coord, val velocity: Velocity) {

    fun move() {
        val newX = coord.x + velocity.x
        val newY = coord.y + velocity.y
        this.coord = Coord(teleport(newX, BOUND_X), teleport(newY, BOUND_Y))
    }

    private fun teleport(v: Int, bound: Int) = when {
        v < 0 -> v + bound
        v >= bound -> v - bound
        else -> v
    }
}

fun display(robots: List<Robot>) = repeat(BOUND_Y) { y ->
    repeat(BOUND_X) { x ->
        print(if (robots.any { it.coord == Coord(x, y) }) 'X' else '.')
    }
    println()
}

fun part1(robots: List<Robot>) =
    repeat(100) { robots.forEach { it.move() } }
        .let {
            robots
                .filter { it.coord.x != (BOUND_X - 1) / 2 && it.coord.y != (BOUND_Y - 1) / 2 }
                .partition { it.coord.x < (BOUND_X - 1) / 2 }
                .let { (p1, p2) ->
                    p1.partition { it.coord.y < (BOUND_Y - 1) / 2 }.let { (p11, p12) -> p11.size * p12.size } *
                            p2.partition { it.coord.y < (BOUND_Y - 1) / 2 }.let { (p21, p22) -> p21.size * p22.size }
                }
        }

fun lineGreaterThan(coords: List<Coord>, minLength: Int) =
    coords.fold(Pair(0, 0)) { (c, max), v ->
        if (coords.any { r -> r.y == v.y - 1 }) Pair(c + 1, c + 1) else Pair(0, max)
    }.let { a -> a.second > minLength }

fun part2(robots: List<Robot>): Int {
    var seconds = 0
    do {
        robots.forEach { it.move() }
        val lineFound = robots
            .groupBy { it.coord.x }
            .any { lineGreaterThan(it.value.map { it.coord }, 5) }
        seconds++
    } while (!lineFound)
    display(robots)
    return seconds
}

fun parseInput(input: List<String>): List<Robot> = input
    .map { "p=([\\d-]+),([\\d-]+) v=([\\d-]+),([\\d-]+)".toRegex().matchEntire(it)!!.destructured }
    .map { (px, py, vx, vy) ->
        Robot(Coord(px.toInt(), py.toInt()), Velocity(vx.toInt(), vy.toInt()))
    }

fun main() {
    parseInput(readInput(14, "input")).let {
        println("Part 1 : ${part1(it.map { it.copy() })}")
        println("Part 2 : ${part2(it.map { it.copy() })}")
    }
}

