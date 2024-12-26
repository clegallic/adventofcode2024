package d12

import readInput
import java.util.Optional

data class Coord(val x: Int, val y: Int) {
    fun adjacents() = listOf(Coord(x - 1, y), Coord(x + 1, y), Coord(x, y - 1), Coord(x, y + 1))
    fun vertices() = listOf(Coord(x - 1, y - 1), Coord(x + 1, y + 1), Coord(x - 1, y + 1), Coord(x + 1, y - 1))
    fun isInBounds(bounds: Coord) = x in 0..bounds.x && y in 0..bounds.y
    fun inCorners() = listOf(cornerTopLeft(), cornerTopRight(), cornerBottomLeft(), cornerBottomRight())
    private fun cornerTopLeft() = listOf(Coord(x - 1, y), Coord(x, y - 1))
    private fun cornerBottomLeft() = listOf(Coord(x - 1, y), Coord(x, y + 1))
    private fun cornerTopRight() = listOf(Coord(x, y - 1), Coord(x + 1, y))
    private fun cornerBottomRight() = listOf(Coord(x, y + 1), Coord(x + 1, y))
}

data class Region(val plantType: Char, val positions: MutableList<Coord>) {
    fun area() = positions.size

    fun perimeter() = positions.sumOf {
        4 - it.adjacents().filter { positions.contains(it) }.size
    }
}

data class MapPosition(val coord: Coord, val plantType: Char, var region: Optional<Region>) {}

data class GardenMap(val positions: Map<Coord, MapPosition>, val bounds: Coord) {

    val regions: MutableList<Region> = mutableListOf()

    fun findRegions() = positions.values.forEach { position ->
        if (position.region.isEmpty) {
            position.region = Optional.of(Region(position.plantType, mutableListOf(position.coord)))
            regions.add(position.region.get())
            attachToRegion(position)
        }
    }.let { regions }

    fun sides(region: Region) =
        region.positions.sumOf { p ->
            countInCorners(p, region.plantType) + countOutCorners(p, region.plantType)
        }

    private fun attachToRegion(position: MapPosition): Unit = position.coord.adjacents()
        .filter { it.isInBounds(bounds) }
        .mapNotNull { positions[it] }
        .filter { it.region.isEmpty && it.plantType == position.plantType }
        .forEach {
            if (it.region.isEmpty) {
                position.region.get().positions.add(it.coord)
                it.region = position.region
                attachToRegion(it)
            }
        }

    private fun countInCorners(p: Coord, plantType: Char) = p.inCorners().sumOf { corners ->
        if (corners.all { positions[it]?.plantType != plantType }) 1 else 0.toInt()
    }

    private fun countOutCorners(p: Coord, plantType: Char) = p.vertices().sumOf { vertice ->
        when (positions[vertice]?.plantType) {
            plantType -> 0.toInt()
            else -> if (positions[Coord(vertice.x, p.y)]?.plantType == plantType
                && positions[Coord(p.x, vertice.y)]?.plantType == plantType
            ) 1 else 0
        }
    }
}

fun parseInput(input: List<String>) = input
    .mapIndexed { y, line ->
        line.mapIndexed { x, c ->
            Coord(x, y).let {
                it to MapPosition(it, c, Optional.empty<Region>())
            }
        }
    }
    .flatten().toMap()
    .let { GardenMap(it, Coord(input[0].length - 1, input.size - 1)) }

fun part1(gardenMap: GardenMap) =
    gardenMap.findRegions()
        .sumOf { it.perimeter() * it.area() }

fun part2(gardenMap: GardenMap) =
    gardenMap.findRegions()
        .sumOf { gardenMap.sides(it) * it.area() }

fun main() {
    parseInput(readInput(12, "input")).let {
        println("Part 1 : ${part1(it)}")
        println("Part 2 : ${part2(it)}")
    }
}

