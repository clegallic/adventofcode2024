package d07

import readInput

enum class Operator {
    ADD, MULTIPLY, CONCATENATE
}

data class Equation(val result: Long, val values: List<Long>)

fun parseInput(input: List<String>) = input
    .map {
        it.split(":")
            .let { l -> Equation(l[0].toLong(), l[1].trim().split(" ").map { v -> v.toLong() }) }
    }

fun evaluate(acc: Long, next: Long, operator: Operator) =
    when (operator) {
        Operator.ADD -> acc + next
        Operator.MULTIPLY -> acc * next
        Operator.CONCATENATE -> "$acc$next".toLong()
    }

fun evaluateAllOperators(acc: Long, next: Long, operators: List<Operator>) =
    operators.map { evaluate(acc, next, it) }

fun evaluateValues(values: List<Long>, operators: List<Operator>) =
    values.drop(1)
        .fold(listOf(values[0])) { acc: List<Long>, v: Long -> acc.flatMap { evaluateAllOperators(it, v, operators) } }

fun isOk(equation: Equation, operators: List<Operator>) =
    evaluateValues(equation.values, operators).any { it == equation.result }

fun part1(input: List<Equation>) =
    input.filter { isOk(it, listOf(Operator.ADD, Operator.MULTIPLY)) }.sumOf { it.result }

fun part2(input: List<Equation>) = input.filter { isOk(it, Operator.entries) }.sumOf { it.result }

fun main() {
    val inputPart1 = readInput(7, "input")
    println("Part 1 : ${part1(parseInput(inputPart1))}")
    val inputPart2 = readInput(7, "input")
    println("Part 1 : ${part2(parseInput(inputPart2))}")
}
