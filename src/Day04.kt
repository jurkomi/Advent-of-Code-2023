import kotlin.math.pow

fun main() {
    fun part1(input: List<String>): Int {
        return input.map { line ->
            line.parseScratchcard()
        }.sumOf { scratchcard ->
            scratchcard.points
        }
    }

    fun part2(input: List<String>): Int {
        val cardsBag = input.mapIndexed { index, line ->
            (index + 1) to line.parseScratchcard()
        }.toMap()
        cardsBag.asIterable().forEach { entry ->
            val evaluatedCard = entry.value
            val score = evaluatedCard.correctNumbersCount
            val cardId = entry.key
            for (i in (cardId + 1)..(cardId + score)) {
                val wonCard = cardsBag[i] ?: return@forEach
                wonCard.count += evaluatedCard.count
            }
        }
        return cardsBag.values.sumOf { it.count }
    }

    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}

private fun String.parseScratchcard(): Scratchcard {
    val input = substringAfter(": ")
    val (winningNumbers, guessedNumbers) = input.split(" | ").map { it.parseScratchcardNumbers() }
    return Scratchcard(winningNumbers, guessedNumbers, 1)
}

private fun String.parseScratchcardNumbers(): List<Int> =
    split(" ").filterNot { it.isEmpty() }.map { it.toInt() }

data class Scratchcard(
    val winningNumbers: List<Int>,
    val guessedNumbers: List<Int>,
    var count: Int
) {
    val correctNumbersCount: Int = guessedNumbers.count { winningNumbers.contains(it) }

    val points: Int
        get() = if (correctNumbersCount == 0) 0 else (2f).pow(correctNumbersCount - 1).toInt()
}