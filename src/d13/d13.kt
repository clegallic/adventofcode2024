package d13

import readInput

fun Double.isWhole() = this.rem(1) == 0.0

data class PrizeLocation(val x: Long, val y: Long) {
    companion object {
        private val BUTTON_INPUT_FORMAT = "Prize: X=(\\d+), Y=(\\d+)".toRegex()
        fun fromString(input: String) =
            BUTTON_INPUT_FORMAT.matchEntire(input)
                .let { PrizeLocation(it?.groupValues[1]!!.toLong(), it.groupValues[2].toLong()) }
    }
}

data class ButtonConfiguration(val offsetX: Long, val offsetY: Long) {
    companion object {
        private val BUTTON_INPUT_FORMAT = "Button [A|B]: X\\+(\\d+), Y\\+(\\d+)".toRegex()
        fun fromString(input: String) =
            BUTTON_INPUT_FORMAT.matchEntire(input)
                .let { ButtonConfiguration(it?.groupValues[1]!!.toLong(), it.groupValues[2].toLong()) }
    }
}

data class ClawMachine(
    val aButton: ButtonConfiguration,
    val bButton: ButtonConfiguration,
    val prizeLocation: PrizeLocation
) {
    fun cost(toAdd: Long = 0) = Pair(
        (bButton.offsetY.toDouble() * (prizeLocation.x + toAdd) - bButton.offsetX * (prizeLocation.y + toAdd)) /
                (aButton.offsetX * bButton.offsetY - bButton.offsetX * aButton.offsetY),
        (aButton.offsetX.toDouble() * (prizeLocation.y + toAdd) - aButton.offsetY * (prizeLocation.x + toAdd)) /
                (aButton.offsetX * bButton.offsetY - bButton.offsetX * aButton.offsetY)
    ).let { (a, b) -> if (a.isWhole() && b.isWhole()) a.toLong() * 3 + b.toLong() else 0 }
}

fun parseInput(input: List<String>): List<ClawMachine> = input
    .windowed(3, 3)
    .map {
        ClawMachine(
            ButtonConfiguration.fromString(it[0]),
            ButtonConfiguration.fromString(it[1]),
            PrizeLocation.fromString(it[2])
        )
    }

fun part1(machines: List<ClawMachine>) = machines.sumOf { it.cost() }
fun part2(machines: List<ClawMachine>) = machines.sumOf { it.cost(10000000000000) }

fun main() {
    parseInput(readInput(13, "input")).let {
        println("Part 1 : ${part1(it)}")
        println("Part 2 : ${part2(it)}")
    }
}

