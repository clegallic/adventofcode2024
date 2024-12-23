package d11

import readInput

class SolverBasicSolution(val stones: List<String>, val nbBlinks: Int) {

    fun solve() = (1..nbBlinks).fold(stones) { acc, _ -> blink(acc) }.size

    private fun blink(stones: List<String>) = stones
        .map {
            when {
                it == "0" -> listOf("1")
                it.length % 2 == 0 -> (it.length / 2).let { halfIndex ->
                    listOf(
                        it.take(halfIndex),
                        it.substring(halfIndex).toLong().toString()
                    )
                }

                else -> listOf((it.toLong() * 2024).toString())
            }
        }.flatten()
}

class SolverRecursiveSolution(val stones: List<String>, val nbBlinks: Int) {

    val nbStonesPerStoneAndBlink: MutableMap<Pair<String, Int>, Long> = mutableMapOf()

    fun solve() = stones.sumOf { blink(it, 0) }

    private fun blink(stone: String, blinkIndex: Int): Long =
        if (blinkIndex == nbBlinks) 1L
        else Pair(stone, blinkIndex).let {
            nbStonesPerStoneAndBlink.getOrElse(it) {
                val newBlinkIndex = blinkIndex + 1
                when {
                    stone == "0" -> blink("1", newBlinkIndex)
                    stone.length % 2 == 0 -> (stone.length / 2).let { halfIndex ->
                        blink(stone.take(halfIndex), newBlinkIndex) +
                                blink(stone.substring(halfIndex).toLong().toString(), newBlinkIndex)
                    }

                    else -> blink((stone.toLong() * 2024).toString(), newBlinkIndex)
                }.also { nbStones -> nbStonesPerStoneAndBlink[it] = nbStones }
            }
        }
}


fun part1(stones: List<String>) = SolverBasicSolution(stones, 25).solve()
fun part2(stones: List<String>) = SolverRecursiveSolution(stones, 75).solve()

fun main() {
    readInput(11, "input")[0].split(" ").let {
        println("Part 1 : ${part1(it)}")
        println("Part 2 : ${part2(it)}")
    }
}

