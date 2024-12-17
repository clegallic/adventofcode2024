package d08

import readInput
import kotlin.math.abs

fun <T> List<T>.cartesianProduct(): List<Pair<T, T>> =
    flatMap { t -> this.filter { it != t }.map { u -> t to u } }

data class Coord(val x: Int, val y: Int) {
    fun distance(other: Coord): Int {
        return abs(other.x - x) + abs(other.y - y)
    }
}

data class Antenna(val symbol: Char)

data class MapPlace(
    val coord: Coord,
    val antenna: Antenna? = null,
    val antiNodes: MutableList<Char> = mutableListOf()
) {

    fun hasAntenna() = antenna != null

    companion object {
        fun parse(coord: Coord, c: Char) =
            if (c == '.') MapPlace(coord) else MapPlace(coord, Antenna(c))
    }
}

data class AntennaMap(val places: Map<Coord, MapPlace>, val bound: Coord) {

    fun findAntiNodes(partOne: Boolean = true) = places.values
        .filter { it.hasAntenna() }
        .groupBy { it.antenna }.values
        .also { if (!partOne) findResonants(it.toList()) }
        .flatMap { it.cartesianProduct() }
        .forEach {
            findAntiNode(it.first, it.second, partOne)
        }

    fun countAntiNodes() = places.values.filter { it.antiNodes.isNotEmpty() }.size

    private fun antiNode(c1: Coord, c2: Coord) = Coord(2 * c2.x - c1.x, 2 * c2.y - c1.y)

    private fun findAntiNode(a1: MapPlace, a2: MapPlace, partOne: Boolean = true) {
        val antiNodes = mutableListOf<Coord>()
        var current = a1.coord;
        var next = a2.coord;
        var isIn: Boolean
        do {
            val antiNode = antiNode(current, next)
            isIn = antiNode.x in 0..bound.x && antiNode.y in 0..bound.y
            if (isIn) {
                antiNodes.add(antiNode)
                current = next; next = antiNode
            }
        } while (isIn && !partOne)
        antiNodes.forEach { places[it]!!.antiNodes.add(a1.antenna!!.symbol) }
    }

    private fun findResonants(antennasPerFrequency: List<List<MapPlace>>) = antennasPerFrequency.forEach { sameAntennas ->
        sameAntennas.forEach { antenna ->
            sameAntennas.asSequence()
                .filter { it != antenna }
                .map { it to it.coord.distance(antenna.coord) }
                .groupBy { it.second }
                .filter { it.value.size > 1 }
                .flatMap { it.value }.toList()
                .forEach { places[it.first.coord]!!.antiNodes.add('T') }
                .also { places[antenna.coord]!!.antiNodes.add('T') }
        }
    }

    fun print() = bound.let {
        for (y in 0..it.y) {
            for (x in 0..it.x)
                print(if (places[Coord(x, y)]!!.antiNodes.isNotEmpty()) '#' else '.')
            println()
        }
    }

    companion object {
        fun parseInput(input: List<String>) = AntennaMap(
            mutableMapOf<Coord, MapPlace>().let {
                input.forEachIndexed { y, line ->
                    line.forEachIndexed { x, c ->
                        Coord(x, y).let { coord ->
                            it[coord] = MapPlace.parse(coord, c)
                        }
                    }
                }
                it
            },
            Coord(input[0].length - 1, input.size - 1)
        )
    }
}

fun part1(input: AntennaMap) = input.findAntiNodes()
    .let { input.countAntiNodes() }

fun part2(input: AntennaMap) = input.findAntiNodes(false)
    .let { input.countAntiNodes() }

fun main() {
    val input = readInput(8, "input")
    println("Part 1 : ${part1(AntennaMap.parseInput(input))}")
    println("Part 2 : ${part2(AntennaMap.parseInput(input))}")
}
