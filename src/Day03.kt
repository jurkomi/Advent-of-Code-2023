fun main() {
    fun part1(input: List<String>): Int {
        val engineParts = input.parseEngineParts()
        val partNumbers = engineParts.filterIsInstance<EnginePart.Number>()
        return partNumbers.filter { partNumber ->
            partNumber.hasAdjacentSymbolInEngine(engineParts)
        }.sumOf { it.value }
    }

    fun part2(input: List<String>): Int {
        val engineParts = input.parseEngineParts()
        val possibleGearSymbols =
            engineParts.filterIsInstance<EnginePart.Symbol>().filter { it.value == '*' }
        val gearRatios = possibleGearSymbols.map { possibleGearSymbol ->
            possibleGearSymbol.getAdjacentNumbersInEngine(engineParts)
        }.filter { adjacentNumbers ->
            adjacentNumbers.size == 2
        }.map { gearNumbers ->
            gearNumbers[0].value * gearNumbers[1].value
        }
        return gearRatios.sum()
    }

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}

private fun List<String>.parseEngineParts(): List<EnginePart> {
    val engineParts = mutableListOf<EnginePart>()
    forEachIndexed { yIndex, line ->
        val numberBuilder = StringBuilder()
        for (xIndex in 0..line.length) {
            val char = line.getOrNull(xIndex)

            if (char?.isDigit() == true) {
                numberBuilder.append(char)
            } else {
                if (numberBuilder.isNotEmpty()) {
                    val number = numberBuilder.toString()
                    engineParts.add(
                        EnginePart.Number(
                            value = number.toInt(),
                            xRange = (xIndex - number.length)..<xIndex,
                            yIndex = yIndex
                        )
                    )
                    numberBuilder.clear()
                }

                if (char != null && char != '.') {
                    engineParts.add(
                        EnginePart.Symbol(
                            value = char,
                            xIndex = xIndex,
                            yIndex = yIndex
                        )
                    )
                }
            }
        }
    }
    return engineParts
}

private sealed interface EnginePart {
    data class Number(
        val value: Int,
        val xRange: IntRange,
        val yIndex: Int
    ) : EnginePart {
        fun hasAdjacentSymbolInEngine(engineParts: List<EnginePart>): Boolean {
            val adjacentSymbolXRange = (xRange.first - 1)..(xRange.last + 1)
            val adjacentSymbolYRange = (yIndex - 1)..(yIndex + 1)
            return engineParts.filterIsInstance<Symbol>().any { symbol ->
                adjacentSymbolYRange.contains(symbol.yIndex) && adjacentSymbolXRange.contains(symbol.xIndex)
            }
        }
    }

    data class Symbol(
        val value: Char,
        val xIndex: Int,
        val yIndex: Int
    ) : EnginePart {
        fun getAdjacentNumbersInEngine(engineParts: List<EnginePart>): List<Number> {
            val adjacentNumberXRange = (xIndex - 1)..(xIndex + 1)
            val adjacentNumberYRange = (yIndex - 1)..(yIndex + 1)
            return engineParts.filterIsInstance<Number>().filter { partNumber ->
                adjacentNumberYRange.contains(partNumber.yIndex) &&
                        partNumber.xRange.any { adjacentNumberXRange.contains(it) }
            }
        }
    }
}