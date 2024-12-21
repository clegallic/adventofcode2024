package d10

import readInput

enum class Direction(val offsetx: Int, val offsety: Int) {
    TOP(0, -1), LEFT(-1, 0), RIGHT(1, 0), BOTTOM(0, 1);
}

data class Coord(val x: Int, val y: Int) {
    fun moveBy(direction: Direction) = Coord(this.x + direction.offsetx, this.y + direction.offsety)
}

data class MapPosition(val coord: Coord, val height: Int, val isTrailHead: Boolean, val isTrailEnd: Boolean) {
    constructor(coord: Coord, height: Int) : this(coord, height, height == 0, height == 9)
}

data class Trail(val head: MapPosition, val end: MapPosition)

data class TopoMap(
    val positions: Map<Coord, MapPosition>,
    val trailHeads: List<MapPosition>,
    val trails: MutableList<Trail> = mutableListOf<Trail>()
) {

    constructor(positions: Map<Coord, MapPosition>) : this(positions, positions.values.filter { it.isTrailHead })

    fun nextPossiblePositions(currentPosition: MapPosition) = Direction.entries
        .mapNotNull { positions[currentPosition.coord.moveBy(it)] }
        .filter { it.height == currentPosition.height + 1 }

    fun walk(trailHead: MapPosition, from: MapPosition, to: MapPosition) {
        if (from.height == 8 && to.isTrailEnd) {
            trails.add(Trail(trailHead, to))
        }
        nextPossiblePositions(to).let {
            if (it.isNotEmpty()) it.forEach { walk(trailHead, to, it) }
        }
    }

    fun findHikingTrails() = this.trailHeads
        .forEach { trailHead ->
            nextPossiblePositions(trailHead).forEach { walk(trailHead, trailHead, it) }
        }
}

fun parseInput(input: List<String>) = input
    .mapIndexed { y, line -> line.mapIndexed { x, c -> Coord(x, y).let { it to MapPosition(it, c.digitToInt()) } } }
    .flatten().toMap()
    .let { TopoMap(it) }

fun part1(topoMap: TopoMap) = topoMap.findHikingTrails().let { topoMap.trails.distinct().size }
fun part2(topoMap: TopoMap) = topoMap.findHikingTrails().let { topoMap.trails.size }

fun main() {
    readInput(10, "input").let {
        println("Part 1 : ${part1(parseInput(it))}")
        println("Part 2 : ${part2(parseInput(it))}")
    }
}
