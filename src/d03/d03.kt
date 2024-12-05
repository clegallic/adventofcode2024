package d03

import readInput

fun parseInput(list: List<String>) = list.joinToString("")

fun extractMuls(text: String) =
    """mul\((\d+),(\d+)\)""".toRegex()
        .findAll(text)
        .map { Pair(it.groupValues[1].toInt(), it.groupValues[2].toInt()) }
        .toList()

fun keepDo(text: String) =
    """(?:^|do\(\))(.*?)(?:${'$'}|don't\(\))""".toRegex()
        .findAll(text)
        .map { it.groupValues[1] }.toList().joinToString("")

fun part1(input: String) = extractMuls(input).sumOf { mul -> mul.first * mul.second }

fun part2(input: String) = extractMuls(keepDo(input)).sumOf { mul -> mul.first * mul.second }

fun main() {
    val inputPart1 = readInput(3, "input")
    println("Part 1 : ${part1(parseInput(inputPart1))}")
    val inputPart2 = readInput(3, "input")
    println("Part 2 : ${part2(parseInput(inputPart2))}")
}
