fun main() {
    fun part1(input: List<String>): Long {
        return input.sumOf { line ->
            line.split(" ").map { it.toLong() }.findExtrapolatedValue()
        }
    }

    fun part2(input: List<String>): Long {
        return input.sumOf { line ->
            line.split(" ").reversed().map { it.toLong() }.findExtrapolatedValue()
        }
    }

    val input = readInput("Day09")
    part1(input).println()
    part2(input).println()
}

private fun List<Long>.findExtrapolatedValue(): Long {
    var evaluated = this
    val lastValues = mutableListOf(last())
    while (!evaluated.all { it == 0L }) {
        evaluated = evaluated.getDifferences()
        lastValues.add(evaluated.last())
    }
    return lastValues.sum()
}

private fun List<Long>.getDifferences(): List<Long> = mapIndexed { index, value ->
    val next = getOrNull(index + 1) ?: return@mapIndexed null
    next - value
}.filterNotNull()