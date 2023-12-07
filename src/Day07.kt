import kotlin.time.measureTime

fun main() {
    data class Hand(val cards: String, val bid: Int, val score: Long)

    fun toHand(data: String, values: Map<Char, Int>): Hand {
        val (cards, bid) = data.split(" ")

        // Map the numeric equivalent of each card (14=A, 2=2, 1=Joker) to the number of cards of each type
        val cardMap = buildMap {
            cards.groupingBy { it }.eachCount().forEach { (key, value) -> put(values[key]!!, value) }
        }

        val maxNonJokerCards = cardMap.filter { it.key > 1 }.maxOfOrNull { it.value } ?: 0
        val jokerCount = cardMap[1] ?: 0

        // The base hand score is the maximum number of cards of one type multiplied by ten
        // with a two point bonus added for the hands with more than one pair (full house, two pair).
        // Gives possible scores of 50 (5K), 40 (4K), 32 (FH), 30 (3K), 22 (2P), 20 (2K), 10 (HC)
        val bonus = cardMap.filter { it.key > 1 && it.value in (2..3) }.count().takeIf { it == 2 } ?: 0
        val baseScore = (maxNonJokerCards + jokerCount) * 10 + bonus

        // The tie-breaker score consists of two digits for each card in the hand, concatenated
        val tieScore = cards.map { (values[it]!!.toString().padStart(2, '0')) }.joinToString("")

        // Concatenate the base score and tie score and we get an overall numeric
        // score that can be trivially sorted to put the cards in order
        return Hand(cards, bid.toInt(), "$baseScore$tieScore".toLong())
    }

    fun calculate(input: List<String>, labels: String): Long {
        // map each card letter to a number from 14 (Ace) down to 1 (Joker)
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
    }.also { it.println() } // about 20ms

    measureTime {
        part2(input).println() // 249515436
    }.also { it.println() } // about 8ms
}
