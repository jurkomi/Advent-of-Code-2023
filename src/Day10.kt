fun main() {
    fun part1(input: List<String>): Int {
        return input.parseMaze().getLoopStepCount() / 2
    }

    fun part2(input: List<String>): Int {
        return input.parseMaze().countInsideLoopParts()
    }

    val input = readInput("Day10")
    part1(input).println()
    part2(input).println()
}

private typealias Maze = List<MazePart>

private fun Maze.getLoopStepCount(): Int {
    val start = first { it is MazePart.Start }

    RelativePosition.values().forEach { relativePosition ->
        val next =
            firstOrNull { it.position == start.position.goToRelativePosition(relativePosition) }
        val stepCount = countStepsBackToStartFrom(start.position, next)
        if (stepCount != null) return stepCount
    }

    throw IllegalStateException("Loop not found.")
}

private fun Maze.getLoopMazeParts(): List<MazePart> {
    val start = first { it is MazePart.Start }

    RelativePosition.values().forEach { relativePosition ->
        val next =
            firstOrNull { it.position == start.position.goToRelativePosition(relativePosition) }
        val mazeParts = getMazePartsInLoop(start.position, next)
        if (mazeParts != null) return mazeParts
    }

    throw IllegalStateException("Loop not found.")
}

private fun Maze.countStepsBackToStartFrom(startPosition: Position, mazePart: MazePart?): Int? {
    var evaluatedMazePart = mazePart
    var currentPosition = startPosition
    var stepCount = 1
    while (evaluatedMazePart != null && evaluatedMazePart !is MazePart.Start && evaluatedMazePart.canGoFromPosition(
            currentPosition
        )
    ) {
        val newEvaluatedPosition = (evaluatedMazePart as MazePart.Pipe).go(currentPosition)
        currentPosition = evaluatedMazePart.position
        evaluatedMazePart = firstOrNull { it.position == newEvaluatedPosition }
        stepCount++
    }
    return if (evaluatedMazePart is MazePart.Start) stepCount else null
}

private fun Maze.getMazePartsInLoop(startPosition: Position, mazePart: MazePart?): List<MazePart>? {
    var evaluatedMazePart = mazePart
    var currentPosition = startPosition
    val loopMazeParts = mutableListOf<MazePart>()
    while (evaluatedMazePart != null && evaluatedMazePart !is MazePart.Start && evaluatedMazePart.canGoFromPosition(
            currentPosition
        )
    ) {
        val newEvaluatedPosition = (evaluatedMazePart as MazePart.Pipe).go(currentPosition)
        currentPosition = evaluatedMazePart.position
        loopMazeParts.add(evaluatedMazePart)
        evaluatedMazePart = firstOrNull { it.position == newEvaluatedPosition }
    }
    return if (evaluatedMazePart is MazePart.Start) {
        loopMazeParts.add(evaluatedMazePart)
        loopMazeParts
    } else null
}

private fun Maze.countInsideLoopParts(): Int {
    val mazeRows = groupBy { it.position.y }
    val loopPartsRows = getLoopMazeParts().groupBy { it.position.y }
    var insideLoopCount = 0

    mazeRows.forEach { (y, row) ->
        val loopPartRow = loopPartsRows[y] ?: return@forEach
        row.forEachIndexed { x, _ ->
            // ignore loop parts
            if (loopPartRow.firstOrNull { it.position.x == x } != null) return@forEachIndexed

            // count loop parts barriers before current maze part - it's inside if it is even number
            val loopPartsBefore = loopPartRow.filter { it.position.x < x }
            val shouldCountStart =
                if (loopPartsBefore.filterIsInstance<MazePart.Start>().isNotEmpty()) {
                    val startPosition = loopPartsBefore.first { it is MazePart.Start }.position
                    shouldCountStartPipe(startPosition.x, startPosition.y, loopPartsRows)
                } else false

            val loopBarriersCountBefore = loopPartsBefore
                .sortedBy { it.position.x }
                .map { it.char }
                .joinToString("")
                .filterNot { it == PipeType.HORIZONTAL.char } // omit horizontal pipes
                .replace("FJ", "|") // acts as single barrier
                .replace("L7", "|") // acts as single barrier
                .replace("S", if (shouldCountStart) "|" else "")
                .count()

            if (loopBarriersCountBefore % 2 == 1) insideLoopCount++
        }
    }

    return insideLoopCount
}

private fun shouldCountStartPipe(
    startPipeX: Int,
    startPipeY: Int,
    loopPartsRows: Map<Int, List<MazePart>>
): Boolean {
    val west = loopPartsRows[startPipeY]?.firstOrNull { it.position.x == startPipeX - 1 }
    val east = loopPartsRows[startPipeY]?.firstOrNull { it.position.x == startPipeX + 1 }

    // count if S is vertical barrier
    if (east == null && west == null) return true

    // handle L7 and FJ cases on west
    if (west != null) {
        val shouldActAsSingleBarrier = when (west.char) {
            PipeType.NORTH_EAST.char -> {
                // don't count in case of L7 sequence - acts as single barrier
                val south = loopPartsRows[startPipeY + 1]?.firstOrNull {
                    it.position.x == startPipeX
                }?.char
                south == PipeType.VERTICAL.char
                        && south == PipeType.NORTH_WEST.char
                        && south == PipeType.NORTH_EAST.char
            }

            PipeType.SOUTH_EAST.char -> {
                // don't count in case of FJ sequence - acts as single barrier
                val north = loopPartsRows[startPipeY - 1]?.firstOrNull {
                    it.position.x == startPipeX
                }?.char
                north == PipeType.VERTICAL.char
                        && north == PipeType.SOUTH_WEST.char
                        && north == PipeType.SOUTH_EAST.char
            }

            else -> false
        }

        if (shouldActAsSingleBarrier) return false
    }

    // handle L7 and FJ cases on east
    if (east != null) {
        val shouldActAsSingleBarrier = when (east.char) {
            PipeType.NORTH_WEST.char -> {
                // don't count in case of FJ sequence - acts as single barrier
                val south = loopPartsRows[startPipeY + 1]?.firstOrNull {
                    it.position.x == startPipeX
                }?.char
                south == PipeType.VERTICAL.char
                        && south == PipeType.NORTH_WEST.char
                        && south == PipeType.NORTH_EAST.char
            }

            PipeType.SOUTH_WEST.char -> {
                // don't count in case of L7 sequence - acts as single barrier
                val north = loopPartsRows[startPipeY - 1]?.firstOrNull {
                    it.position.x == startPipeX
                }?.char
                north == PipeType.VERTICAL.char
                        && north == PipeType.SOUTH_WEST.char
                        && north == PipeType.SOUTH_EAST.char
            }

            else -> false
        }

        if (shouldActAsSingleBarrier) return false
    }

    // count if it is not FJ, L7 or | case
    return true
}

private fun List<String>.parseMaze(): Maze =
    flatMapIndexed { y, line ->
        line.mapIndexed { x, char ->
            char.toMazePart(Position(x, y))
        }
    }

private fun Char.toMazePart(position: Position): MazePart {
    return when (this) {
        'S' -> MazePart.Start(position)
        '.' -> MazePart.Ground(position)
        else -> {
            val pipeType = PipeType.values().firstOrNull { it.char == this }
                ?: throw IllegalStateException("Unknown character $this.")
            MazePart.Pipe(pipeType, position)
        }
    }
}

private sealed class MazePart(
    open val char: Char,
    open val position: Position
) {
    fun canGoFromPosition(startPosition: Position): Boolean {
        return when (this) {
            is Ground -> false
            is Pipe -> {
                val relativeStartPosition = position.getRelativePositionTo(startPosition)
                canGo(relativeStartPosition)
            }

            is Start -> true
        }
    }

    data class Pipe(
        val pipeType: PipeType,
        override val position: Position
    ) : MazePart(pipeType.char, position) {
        private val connectingRelativePositions =
            listOf(pipeType.oneSideRelative, pipeType.secondSideRelative)

        fun go(startPosition: Position): Position {
            val relativeStartPosition = position.getRelativePositionTo(startPosition)
            return if (canGo(relativeStartPosition)) {
                val relativePositionToGoTo =
                    connectingRelativePositions.filterNot { it == relativeStartPosition }.first()
                position.goToRelativePosition(relativePositionToGoTo)
            } else throw IllegalStateException("Can't go!")
        }

        fun canGo(startPosition: RelativePosition?): Boolean {
            if (startPosition == null) return false
            return connectingRelativePositions.contains(startPosition)
        }
    }

    data class Start(override val position: Position) : MazePart('S', position)

    data class Ground(override val position: Position) : MazePart('.', position)
}

private enum class PipeType(
    val char: Char,
    val oneSideRelative: RelativePosition,
    val secondSideRelative: RelativePosition
) {
    VERTICAL(
        '|',
        RelativePosition.NORTH,
        RelativePosition.SOUTH
    ),

    HORIZONTAL(
        '-',
        RelativePosition.EAST,
        RelativePosition.WEST
    ),

    NORTH_EAST(
        'L',
        RelativePosition.NORTH,
        RelativePosition.EAST
    ),

    NORTH_WEST(
        'J',
        RelativePosition.NORTH,
        RelativePosition.WEST
    ),

    SOUTH_WEST(
        '7',
        RelativePosition.SOUTH,
        RelativePosition.WEST
    ),

    SOUTH_EAST(
        'F',
        RelativePosition.SOUTH,
        RelativePosition.EAST
    )
}

private data class Position(
    val x: Int,
    val y: Int
) {
    fun getRelativePositionTo(current: Position): RelativePosition? = when {
        current.x == x && current.y == y - 1 -> RelativePosition.NORTH
        current.x == x && current.y == y + 1 -> RelativePosition.SOUTH
        current.y == y && current.x == x + 1 -> RelativePosition.EAST
        current.y == y && current.x == x - 1 -> RelativePosition.WEST
        else -> null
    }

    fun goToRelativePosition(relativePosition: RelativePosition): Position =
        when (relativePosition) {
            RelativePosition.EAST -> copy(x = x + 1)
            RelativePosition.NORTH -> copy(y = y - 1)
            RelativePosition.WEST -> copy(x = x - 1)
            RelativePosition.SOUTH -> copy(y = y + 1)
        }
}

private enum class RelativePosition {
    EAST, NORTH, WEST, SOUTH
}