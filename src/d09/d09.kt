package d09

import readInput

interface Block {
    val size: Int
}

data class File(val id: Int, override val size: Int) : Block
data class Free(override val size: Int) : Block

class DiskMap(var blocks: List<Block>) {

    fun spreadBlock(blocks: List<Block>, blockToSpread: File): List<Block> {
        var updatedBlocks = blocks.toMutableList()
        val firstFreeIndex = updatedBlocks.indexOfFirst { it is Free }
        if (firstFreeIndex != -1) {
            val freeBlock = updatedBlocks[firstFreeIndex]
            if (freeBlock.size >= blockToSpread.size) {
                updatedBlocks[firstFreeIndex] = File(blockToSpread.id, blockToSpread.size)
                if (freeBlock.size > blockToSpread.size) {
                    updatedBlocks.add(firstFreeIndex + 1, Free(freeBlock.size - blockToSpread.size))
                }
            } else {
                updatedBlocks[firstFreeIndex] = File(blockToSpread.id, freeBlock.size)
                updatedBlocks = spreadBlock(
                    updatedBlocks,
                    File(blockToSpread.id, blockToSpread.size - freeBlock.size)
                ).toMutableList()
            }
            return updatedBlocks
        } else
            return updatedBlocks.plus(blockToSpread)
    }

    fun compactPart1() {
        do {
            val lastBlock = blocks.last()
            blocks = blocks.dropLast(1).toMutableList()
            if (lastBlock is File) blocks = spreadBlock(blocks, lastBlock)
        } while (blocks.any { it is Free })
    }

    fun compactPart2() {
        var blockIndex = blocks.size - 1
        var previousBlockId = Int.MAX_VALUE
        do {
            val block = blocks[blockIndex]
            if (block is File && block.id < previousBlockId) {
                val firstFreeIndex = blocks.indexOfFirst { it is Free && it.size >= block.size }
                if (firstFreeIndex != -1 && firstFreeIndex < blockIndex) {
                    val freeBlock = blocks[firstFreeIndex] as Free
                    val updatedBlocks = blocks.toMutableList()
                    updatedBlocks[blockIndex] = Free(block.size)
                    updatedBlocks[firstFreeIndex] = File(block.id, block.size)
                    if (freeBlock.size > block.size)
                        updatedBlocks.add(firstFreeIndex + 1, Free(freeBlock.size - block.size))
                    blocks = updatedBlocks
                }
                previousBlockId = block.id
            }
            blockIndex--
        } while (blockIndex > 0)
    }

    fun checksum() = blocks
        .flatMap { block -> List(block.size) { if (block is File) block.id.toLong() else 0 } }
        .foldIndexed(0L) { index: Int, acc: Long, id: Long -> acc + (index * id) }

    fun display() = println(blocks.joinToString("") {
        when (it) {
            is File -> "${it.id}".repeat(it.size)
            is Free -> ".".repeat(it.size)
            else -> throw IllegalArgumentException()
        }
    })

    companion object {
        fun parseInput(input: String) = input
            .mapIndexed { index, c ->
                if (index % 2 == 0)
                    File(index / 2, c.digitToInt())
                else
                    Free(c.digitToInt())
            }
            .filter { it != Free(0) }
            .let { DiskMap(it) }
    }
}

fun part1(input: String) = DiskMap.parseInput(input)
    .let {
        it.compactPart1()
        it.checksum()
    }

fun part2(input: String) = DiskMap.parseInput(input)
    .let {
        it.compactPart2()
        it.checksum()
    }

fun main() {
    val input = readInput(9, "input")
    println("Part 1 : ${part1(input[0])}")
    println("Part 2 : ${part2(input[0])}")
}
