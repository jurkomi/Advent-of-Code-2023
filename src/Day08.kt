fun main() {
    fun part1(input: List<String>): Int {
        return input.parsePathWithNodes().getStepCountToEnd(
            startDestination = "AAA",
            endDestinationSuffix = "ZZZ"
        )
    }

    fun part2(input: List<String>): Long {
        val pathWithNodes = input.parsePathWithNodes()
        val stepCounts =
            pathWithNodes.nodes.filter { node ->
                node.key.endsWith('A')
            }.keys.toList().map { startDestination ->
                pathWithNodes.getStepCountToEnd(
                    startDestination = startDestination,
                    endDestinationSuffix = "Z"
                )
            }
        return stepCounts.map { it.toLong() }.findLCM()
    }

    val input = readInput("Day08")
    part1(input).println()
    part2(input).println()
}

private fun List<String>.parsePathWithNodes(): PathWithNodes = PathWithNodes(
    path = first(),
    nodes = drop(2).parseNodes()
)

private fun List<String>.parseNodes(): Map<String, Pair<String, String>> = associate { line ->
    val (node, coordinatesPart) = line.split(" = ")
    val coordinates = coordinatesPart.substringAfter("(").substringBefore(")")
        .split(",").let { Pair(it[0].trim(), it[1].trim()) }
    node to coordinates
}

private fun PathWithNodes.getStepCountToEnd(
    startDestination: String,
    endDestinationSuffix: String
): Int {
    var currentDestination = startDestination
    var stepCount = 0
    while (!currentDestination.endsWith(endDestinationSuffix)) {
        currentDestination = getDestinationForStep(stepCount, currentDestination)
        stepCount++
    }
    return stepCount
}

private fun PathWithNodes.getDestinationForStep(
    stepCount: Int,
    currentDestination: String
): String {
    return when (path[stepCount.mod((path.length))]) {
        'L' -> nodes[currentDestination]?.first ?: throw IllegalStateException("")
        'R' -> nodes[currentDestination]?.second ?: throw IllegalStateException("")
        else -> throw IllegalStateException("")
    }
}

private data class PathWithNodes(
    val path: String,
    val nodes: Map<String, Pair<String, String>>
)