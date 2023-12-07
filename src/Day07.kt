import kotlin.time.measureTime

fun main() {
    data class Hand(val cards: String, val bid: Int, val score: Long)

    fun toHand(data: String, values: Map<Char, Int>): Hand {
        val (cards, bid) = data.split(" ")

        // break down cards into their values and how many of each we have
        val cardMap: Map<Int, Int> = buildMap {
            cards.map {
                val value = values[it]!!
                if (containsKey(value)) {
                    put(value, get(value)!! + 1)
                } else {
                    put(value, 1)
                }
            }
        }

        val maxCardsOfOneType = cardMap.filter { it.key > 1 }.maxOfOrNull { it.value } ?: 0
        val jokerCount = cardMap[1] ?: 0

        // the base hand score runs from 50 (five of a kind) down to 10 (all different)
        // with a bonus added for the in between hands (full house, two pair)
        val bonus = cardMap.filter { it.key > 1 && it.value > 1 }.count().takeIf { it > 1 } ?: 0
        val baseScore = (maxCardsOfOneType + jokerCount) * 10 + bonus

        // the tie score consists of two digits for each card in the hand, concatenated
        val tieScore = cards.map { (values[it]!!.toString().padStart(2, '0')) }
            .joinToString("")

        // concatenate the base score and tie score and we get an overall score
        // that can be trivially sorted to put the cards in order
        return Hand(cards, bid.toInt(), "$baseScore$tieScore".toLong())
    }

    fun calculate(input: List<String>, labels: String): Long {
        val values = labels.withIndex().associate { (idx, lbl) -> lbl to 14 - idx }

        return input.map { toHand(it, values) }.sortedBy { it.score }
            .mapIndexed { index, hand -> hand.bid * (1 + index) }
            .sumOf { it.toLong() }
    }

    fun part1(input: List<String>): Long = calculate(input, "AKQJT98765432")
    fun part2(input: List<String>): Long = calculate(input, "AKQ_T98765432J")

    val testInput = readInput("Day07_test")
    check(part1(testInput) == 6440.toLong())
    check(part2(testInput) == 5905.toLong())

    val input = readInput("Day07")
    measureTime {
        part1(input).println() // 248105065
    }.also { it.println() } // 16ms

    measureTime {
        part2(input).println() // 249515436
    }.also { it.println() } // 5ms
}
