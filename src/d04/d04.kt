package d04

import readInput

data class Letter(val content: Map<Coord, Char>, val bounds: Coord)

data class Coord(val x: Int, val y: Int) {
    fun moveBy(direction: Direction) = Coord(this.x + direction.offsetx, this.y + direction.offsety)
    fun isInBound(bounds: Coord) = this.x in 0..bounds.x && this.y in 0..bounds.y
}

enum class Direction(val offsetx: Int, val offsety: Int) {
    ANY(Int.MAX_VALUE, Int.MAX_VALUE),
    TOP_LEFT(-1, -1), TOP(0, -1), TOP_RIGHT(1, -1),
    LEFT(-1, 0), RIGHT(1, 0),
    BOTTOM_LEFT(-1, 1), BOTTOM(0, 1), BOTTOM_RIGHT(1, 1);

    operator fun component1() = offsetx
    operator fun component2() = offsety
}

const val XMAS = "XMAS"

fun parseInput(input: List<String>) = input
    .foldIndexed(mapOf<Coord, Char>())
    { y, result, line -> result + line.mapIndexed { x, char -> Coord(x, y) to char }.toMap() }
    .let { Letter(it, Coord(input[0].length - 1, input.size - 1)) }

fun adjacents(coord: Coord, bounds: Coord) = Direction.entries
    .map { Pair(coord.moveBy(it), it) }
    .filter { (newCoord, _) -> newCoord.isInBound(bounds) }

fun browseAdjacent(letter: Letter, coord: Coord, direction: Direction, char: Char) =
    adjacents(coord, letter.bounds)
        .filter { (coord, d) -> (direction == Direction.ANY || direction == d) && letter.content[coord] == char }
        .sumOf { (coord, d) -> foundXmas(letter, coord, d, char) }

fun foundXmas(letter: Letter, coord: Coord, direction: Direction, char: Char): Int =
    XMAS.indexOf(char).let {
        if (it < XMAS.length - 1) browseAdjacent(letter, coord, direction, XMAS[it + 1]) else 1
    }

fun part1(letter: Letter) = letter.content.entries
    .filter { (_, char) -> char == 'X' }
    .sumOf { (coord, char) -> foundXmas(letter, coord, Direction.ANY, char) }

fun isXmas(aCoord: Coord, letter: Letter) =
    listOf(
        aCoord.moveBy(Direction.TOP_LEFT), aCoord.moveBy(Direction.TOP_RIGHT),
        aCoord.moveBy(Direction.BOTTOM_LEFT), aCoord.moveBy(Direction.BOTTOM_RIGHT)
    )
        .filter { it.isInBound(letter.bounds) }
        .map { letter.content[it] }
        .takeIf {
            it.size == 4
                    && it.filter { c -> c == 'M' }.size == 2 && it.filter { c -> c == 'S' }.size == 2
                    && it[0] != it[3] && it[1] != it[2]
        } != null

fun part2(letter: Letter) = letter.content.entries
    .filter { (_, char) -> char == 'A' }
    .filter { (coord, _) -> isXmas(coord, letter) }
    .size

fun main() {
    val inputPart1 = readInput(4, "input")
    println("Part 1 : ${part1(parseInput(inputPart1))}")
    val inputPart2 = readInput(4, "input")
    println("Part 2 : ${part2(parseInput(inputPart2))}")
}
