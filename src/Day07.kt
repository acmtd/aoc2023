fun main() {
    data class Hand(val cards: String, val bid: Int, val valueMap: Map<Int, Int>, val score: Long) {}

    fun valueMap(cards: String, cardValues: Map<Char, Int>): Map<Int, Int> = buildMap {
        cards.map { lbl ->
            val value = cardValues[lbl]!!
            if (containsKey(value)) {
                put(value, get(value)!! + 1)
            } else {
                put(value, 1)
            }
        }
    }

    fun toHand(data: String, cardValues: Map<Char, Int>): Hand {
        val (cards, bid) = data.split(" ")
        val valueMap = valueMap(cards, cardValues)

        var baseScore: Int

        if (cardValues.containsKey('J')) {
            val maxScoreNonJoker = valueMap.filter { it.key > 1 }.maxOfOrNull { it.value } ?: 0
            val jokerCount = valueMap[1] ?: 0

            baseScore = (maxScoreNonJoker + jokerCount) * 10
        } else {
            baseScore = valueMap.maxOf { it.value } * 10
        }

        // full house / two pair adjustment
        if (valueMap.filter { it.key > 1 && it.value > 1 }.count() > 1) baseScore += 5

        var tieScore = 0.toLong()

        cards.forEach { lbl ->
            tieScore = tieScore.times(100).plus(10 + cardValues[lbl]!!)
        }

        val score = "$baseScore$tieScore".toLong()
        return Hand(cards, bid.toInt(), valueMap, score)
    }

    fun calculate(input: List<String>, labels: String): Long {
        val cardValues = labels.withIndex().associate { (idx, lbl) -> lbl to 14 - idx }

        return input.map { toHand(it, cardValues) }.sortedBy { it.score }
            .mapIndexed { index, hand -> hand.bid * (1 + index) }
            .sumOf { it -> it.toLong() }
    }

    fun part1(input: List<String>): Long {
        return calculate(input, "AKQJT98765432")
    }

    fun part2(input: List<String>): Long {
        return calculate(input, "AKQJT98765432J")
    }

    val testInput = readInput("Day07_test")
    check(part1(testInput) == 6440.toLong())
    check(part2(testInput) == 5905.toLong())

    val input = readInput("Day07")
    part1(input).println() // 248105065
    part2(input).println() // 249515436
}
