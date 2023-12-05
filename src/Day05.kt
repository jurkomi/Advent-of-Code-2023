fun main() {
    fun part1(input: List<String>): Long {
        val seedNumber = input.first().parseSeedNumbers()
        val almanac = input.parseAlmanac()
        return seedNumber.minOf { seed ->
            almanac.findLocationForSeed(seed)
        }
    }

    fun part2(input: List<String>): Long {
        val seedNumberRanges = input.first().parseSeedRanges()
        val almanac = input.parseAlmanac()
        val minLocationsOfSeedRanges = seedNumberRanges.map {
            it.minOf { seed -> almanac.findLocationForSeed(seed) }
        }
        return minLocationsOfSeedRanges.min()
    }

    val input = readInput("Day05")
    part1(input).println()
    part2(input).println()
}

private fun Almanac.findLocationForSeed(seedNumber: Long): Long {
    var currentProperty = PropertyType.SEED
    var currentSourceValue = seedNumber
    do {
        val evaluatedMapping = mappings.find { it.mappingType.sourceType == currentProperty }
            ?: throw java.lang.IllegalStateException("Could not find mapping for $currentProperty property type!")

        val destinationValue = evaluatedMapping.ranges.getDestinationValue(currentSourceValue)

        currentSourceValue = destinationValue
        currentProperty = evaluatedMapping.mappingType.destinationType
    } while (currentProperty != PropertyType.LOCATION)
    return currentSourceValue
}

private fun List<MappingRange>.getDestinationValue(sourceValue: Long): Long =
    firstOrNull { sourceValue in it.sourceRange }?.let { mappingRange ->
        mappingRange.destinationRange.first + sourceValue - mappingRange.sourceRange.first
    } ?: sourceValue

private fun String.parseSeedNumbers(): List<Long> =
    substringAfter(": ").split(" ").map { it.toLong() }

private fun String.parseSeedRanges(): List<LongRange> =
    parseSeedNumbers().chunked(2) { valuePair ->
        LongRange(valuePair[0], valuePair[0] + valuePair[1] - 1)
    }

private fun List<String>.parseAlmanac(): Almanac {
    val allMappings = mutableListOf<Pair<PropertyTypeMapping, MappingRange>>()
    var currentMapping: PropertyTypeMapping? = null
    forEachIndexed { index, line ->
        if (index == 0) return@forEachIndexed // ignore first line

        when {
            line.lastOrNull() == ':' -> {
                val (sourcePart, destinationPart, _) = line.split("-to-", " ")
                val currentSourceType = sourcePart.findPropertyTypeWithSameName()
                val currentDestinationType = destinationPart.findPropertyTypeWithSameName()
                currentMapping = PropertyTypeMapping(currentSourceType, currentDestinationType)
            }

            line.isNotEmpty() -> {
                currentMapping?.let { allMappings.add(it to line.parseMapRange()) }
                    ?: throw IllegalStateException("Property types were not mapped!")
            }

            else -> {
                // ignore empty line
            }
        }
    }

    val almanacMapping =
        allMappings.groupBy { it.first }.map { (propertyTypeMapping, mappingsList) ->
            AlmanacMapping(propertyTypeMapping, mappingsList.map { it.second })
        }
    return Almanac(almanacMapping)
}

private fun String.parseMapRange(): MappingRange {
    val (destinationStart, sourceStart, range) = split(" ").map { it.toLong() }
    return MappingRange(
        sourceRange = LongRange(sourceStart, sourceStart + range - 1),
        destinationRange = LongRange(destinationStart, destinationStart + range - 1)
    )
}

private fun String.findPropertyTypeWithSameName(ignoreCase: Boolean = true): PropertyType =
    PropertyType.entries.first { it.name.equals(this, ignoreCase) }

private data class Almanac(
    val mappings: List<AlmanacMapping>
)

private data class AlmanacMapping(
    val mappingType: PropertyTypeMapping,
    val ranges: List<MappingRange>
)

private data class MappingRange(
    val sourceRange: LongRange,
    val destinationRange: LongRange
)

private data class PropertyTypeMapping(
    val sourceType: PropertyType,
    val destinationType: PropertyType
)

private enum class PropertyType {
    SEED, SOIL, FERTILIZER, WATER, LIGHT, TEMPERATURE, HUMIDITY, LOCATION
}