fun main() {
    fun part1(input: List<String>): Int {
        return input.sumOf { line ->
            getCalibrationValueFromDigits(
                firstDigit = line.firstOrNull { it.isDigit() },
                lastDigit = line.lastOrNull { it.isDigit() }
            )
        }
    }

    fun part2(input: List<String>): Int {
        return input.sumOf { line ->
            getCalibrationValueFromDigits(
                firstDigit = line.findAnyOf(transformations.keys)?.second?.let { transformations[it] },
                lastDigit = line.findLastAnyOf(transformations.keys)?.second?.let { transformations[it] }
            )
        }
    }

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}

private fun getCalibrationValueFromDigits(firstDigit: Char?, lastDigit: Char?): Int =
    "$firstDigit$lastDigit".toIntOrNull() ?: 0

private val transformations = mapOf(
    "one" to '1',
    "two" to '2',
    "three" to '3',
    "four" to '4',
    "five" to '5',
    "six" to '6',
    "seven" to '7',
    "eight" to '8',
    "nine" to '9',
    "1" to '1',
    "2" to '2',
    "3" to '3',
    "4" to '4',
    "5" to '5',
    "6" to '6',
    "7" to '7',
    "8" to '8',
    "9" to '9',
    "0" to '0'
)
