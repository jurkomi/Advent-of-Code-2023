fun main() {
    fun part1(input: List<String>): Int {
        return input.parseHandWithBids().getScore()
    }

    fun part2(input: List<String>): Int {
        return input.parseHandWithBids().getScore(isJokerRuleApplied = true)
    }

    val input = readInput("Day07")
    part1(input).println()
    part2(input).println()
}

private typealias HandWithBid = Pair<String, Int>

private const val JOKER = 'J'

private fun List<HandWithBid>.getScore(isJokerRuleApplied: Boolean = false): Int {
    val sortedHands = groupBy { it.getTypeStrength(isJokerRuleApplied) }
        .toSortedMap()
        .flatMap { (_, handsWithBids) ->
            handsWithBids.sortedByDescending { handWithBid ->
                handWithBid.first.map { it.mapCardToAlphabetChar(isJokerRuleApplied) }.toString()
            }
        }
    return sortedHands.foldRightIndexed(0) { index, hand, score ->
        val rank = index + 1
        score + rank * hand.second
    }
}

private fun HandWithBid.getTypeStrength(isJokerRuleApplied: Boolean): Int {
    var cardGroups = first.groupBy { it }

    if (isJokerRuleApplied && first.contains(JOKER) && first != "JJJJJ") {
        val largestGroup = cardGroups.filterNot { it.key == JOKER }.maxBy { it.value.size }
        cardGroups = first.replace(JOKER, largestGroup.key).groupBy { it }
    }

    return when (cardGroups.size) {
        1 -> 6
        2 -> if (cardGroups.values.maxByOrNull { it.size }?.size == 4) 5 else 4
        3 -> if (cardGroups.values.maxByOrNull { it.size }?.size == 3) 3 else 2
        4 -> 1
        else -> 0
    }
}

private val alphabet = listOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm')
private val regularCards = listOf('A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2')
private val cardsWithJoker =
    listOf('A', 'K', 'Q', 'T', '9', '8', '7', '6', '5', '4', '3', '2', JOKER)

private fun Char.mapCardToAlphabetChar(isJokerRuleApplied: Boolean): Char {
    val cards = if (isJokerRuleApplied) cardsWithJoker else regularCards
    val cardAlphabetMapping = cards.zip(alphabet)
    return cardAlphabetMapping.first { it.first == this }.second
}

private fun List<String>.parseHandWithBids(): List<HandWithBid> = map { line ->
    val (hand, bid) = line.split(" ")
    Pair(hand, bid.toInt())
}