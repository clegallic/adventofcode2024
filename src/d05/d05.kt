package d05

import readInput

data class ParsedInput(val rules: Map<Int, List<Int>>, val updates: List<List<Int>>)

private const val RULE_SEP = "|"
private const val UPDATE_SEP = ","

fun isRule(line: String) = line.contains(RULE_SEP)

fun parseRule(line: String) = line.split(RULE_SEP).map { it.toInt() }.let { Pair(it.first(), it.last()) }

fun parseUpdate(line: String) = line.split(UPDATE_SEP).map { it.toInt() }

fun parseInput(input: List<String>) = input
    .partition { isRule(it) }
    .let { (rulesDefs, updatesDefs) ->
        val rules = rulesDefs.map { parseRule(it) }
            .groupBy { it.first }
            .mapValues { it.value.map { v -> v.second } }
        val updates = updatesDefs.map { parseUpdate(it) }
        ParsedInput(rules, updates)
    }

fun isPageNumberBeforeIndex(update: List<Int>, pageNumber: Int, index: Int) =
    update.indexOf(pageNumber).let { it == -1 || it > index }

fun pageNumberRespectsRules(update: List<Int>, pageNumberIndex: Int, rules: Map<Int, List<Int>>) =
    update[pageNumberIndex].let { pageNumber ->
        !rules.containsKey(pageNumber)
                || rules[pageNumber]!!.all { isPageNumberBeforeIndex(update, it, pageNumberIndex) }
    }

fun firstInvalidRuleIndex(update: List<Int>, rules: Map<Int, List<Int>>): Int {
    var pageNumberIndex = 0
    do {
        if (pageNumberRespectsRules(update, pageNumberIndex, rules)) pageNumberIndex++ else return pageNumberIndex
    } while (pageNumberIndex < update.size)
    return -1
}

fun middlePageNumber(update: List<Int>) = update[update.size / 2]

fun swapElements(list: List<Int>, index1: Int, index2: Int) = list[index1].let { itemAtIndex1 ->
    list.toMutableList().also {
        it[index1] = it[index2]
        it[index2] = itemAtIndex1
    }
}

fun fixUpdateOrdering(update: List<Int>, foundInvalidRuleIndex: Int, rules: Map<Int, List<Int>>): List<Int> {
    var swappedUpdate = update
    var invalidRuleIndex = foundInvalidRuleIndex
    do {
        swappedUpdate = swapElements(swappedUpdate, invalidRuleIndex, invalidRuleIndex - 1)
        invalidRuleIndex = firstInvalidRuleIndex(swappedUpdate, rules)
    } while (invalidRuleIndex != -1)
    return swappedUpdate
}

fun part1(input: ParsedInput) = input.updates
    .filter { firstInvalidRuleIndex(it, input.rules) == -1 }
    .sumOf { middlePageNumber(it) }

fun part2(input: ParsedInput) = input.updates
    .map { it to firstInvalidRuleIndex(it, input.rules) }
    .filter { (_, index) -> index != -1 }
    .map { (update, index) -> fixUpdateOrdering(update, index, input.rules) }
    .sumOf { middlePageNumber(it) }

fun main() {
    val inputPart1 = readInput(5, "sample")
    println("Part 1 : ${part1(parseInput(inputPart1))}")
    val inputPart2 = readInput(5, "input")
    println("Part 2 : ${part2(parseInput(inputPart2))}")
}
