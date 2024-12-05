package d02

import readInput
import splitWhiteSpaces

fun parseInput(input: List<String>) = input
    .map { it.splitWhiteSpaces().map(String::toInt) }


fun isSafe( p: Pair<Int, Int>) = p.second - p.first in 1..3

fun reportIsSafe(report: List<Int>) = report.zipWithNext().all { isSafe(it) }

fun part1(input: List<List<Int>>) = input
    .map { if (it.last() > it.first()) it else it.reversed() }
    .filter { reportIsSafe(it) }
    .size

fun part2(input: List<List<Int>>) = input
    .map { if (it.last() > it.first()) it else it.reversed() }
    .filter { List(it.size) { index -> reportIsSafe(it.filterIndexed { fi, _ -> index != fi })}.any { isSafe -> isSafe } }
    .size

fun main() {
    val input = parseInput(readInput(2, "input"))
    println("Part 1 : ${part1(input)}")
    println("Part 2 : ${part2(input)}")
}
