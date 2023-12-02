fun main() {
    fun part1(input: List<String>): Int {
        return input.map { gameEntry ->
            gameEntry.parseGame()
        }.filter { game ->
            game.isPossible
        }.sumOf { game ->
            game.id
        }
    }

    fun part2(input: List<String>): Int {
        return input.map { gameEntry ->
            gameEntry.parseGame()
        }.sumOf { game ->
            var power = 1
            CubeType.values().forEach { cubeType ->
                power *= game.sets.maxOf { set -> set.cubeCountMap[cubeType] ?: 0 }
            }
            power
        }
    }

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}

private fun String.parseGame(): Game {
    val (gamePart, setsPart) = split(':')
    val gameId = gamePart.substringAfter("Game ").toInt()
    val sets = setsPart.split(';').map { it.parseSet() }
    return Game(gameId, sets)
}

private fun String.parseSet(): Set {
    val cubeCountMap = mutableMapOf<CubeType, Int>()
    val setParts = split(',')
    CubeType.values().forEach { cubeType ->
        setParts.firstOrNull { it.contains(cubeType.colorKey) }?.let { part ->
            val count = part.filter { it.isDigit() }.toInt()
            cubeCountMap[cubeType] = count
        }
    }
    return Set(cubeCountMap)
}

private data class Game(
    val id: Int,
    val sets: List<Set>
) {
    val isPossible: Boolean
        get() = sets.all { it.isPossible }
}

private data class Set(
    val cubeCountMap: Map<CubeType, Int>
) {
    val isPossible: Boolean
        get() = cubeCountMap.all { (cube, count) ->
            count <= cube.maxCount
        }
}

private enum class CubeType(val colorKey: String, val maxCount: Int) {
    RED("red", 12),
    GREEN("green", 13),
    BLUE("blue", 14)
}