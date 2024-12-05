package d01

import readInput
import splitWhiteSpaces
import kotlin.math.abs

fun parseInput(input: List<String>) = input
    .map { it.splitWhiteSpaces() }.map { splitted -> Pair(splitted[0].toInt(), splitted[1].toInt()) }
    .unzip()

fun part1(l1: List<Int>, l2: List<Int>) = l2
    .sorted()
    .let { l1.sorted().mapIndexed { i, v -> abs(v - it[i]) }.sum() }

fun part2(l1: List<Int>, l2: List<Int>) = l1.sumOf { it * l2.count { l2v -> it == l2v } }

fun main() {
    val (l1, l2) = parseInput(readInput(1, "input"))
    println("Part 1 : ${part1(l1, l2)}")
    println("Part 2 : ${part2(l1, l2)}")
}
