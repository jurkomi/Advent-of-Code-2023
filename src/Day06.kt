import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sqrt

fun main() {
    fun part1(input: List<String>): Int {
        return input.parseRaces().map { it.numberOfWaysToWinRace }.multiply()
    }

    fun part2(input: List<String>): Int {
        val race = Race(input[0].getAllDigitsAsLong(), input[1].getAllDigitsAsLong())
        return race.numberOfWaysToWinRace
    }

    val input = readInput("Day06")
    part1(input).println()
    part2(input).println()
}

private fun List<String>.parseRaces(): List<Race> {
    val times = get(0).parseValues()
    val distances = get(1).parseValues()
    return times.mapIndexed { index, time ->
        Race(time, distances[index])
    }
}

private fun String.parseValues(): List<Long> =
    substringAfter(": ").trim().split(" ").filterNot { it.isEmpty() }.map { it.toLong() }

private data class Race(
    val time: Long,
    val distanceRecord: Long,
) {
    val numberOfWaysToWinRace: Int
        get() = (maxChargeTimeToBeatRecord - minChargeTimeToBeatRecord + 1).toInt()

    private val maxChargeTimeToBeatRecord: Long
        get() {
            val maxCharge = floor(
                (-time - sqrt(time.toDouble().pow(2) - 4 * distanceRecord)) / (-2)
            ).toLong()
            return if (getDistanceForChargeTime(maxCharge) == distanceRecord) {
                maxCharge - 1L
            } else {
                maxCharge
            }
        }

    private val minChargeTimeToBeatRecord: Long
        get() {
            val minCharge = ceil(
                (-time + sqrt(time.toDouble().pow(2) - 4 * distanceRecord)) / (-2)
            ).toLong()
            return if (getDistanceForChargeTime(minCharge) == distanceRecord) {
                minCharge + 1L
            } else {
                minCharge
            }
        }

    private fun getDistanceForChargeTime(chargeTime: Long): Long = chargeTime * (time - chargeTime)
}