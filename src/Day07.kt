import kotlin.time.measureTime

fun main() {
    data class Hand(val cards: String, val bid: Int, val score: Long) {}

    fun toHand(data: String, cardValues: Map<Char, Int>): Hand {
        val (cards, bid) = data.split(" ")

        // break down cards into their values and how many of each we have
        val cardMap = buildMap<Int, Int> {
            cards.map {
                val value = cardValues[it]!!
                if (containsKey(value)) {
                    put(value, get(value)!! + 1)
                } else {
                    put(value, 1)
                }
            }
        }

        // the base hand score runs from 50 (five of a kind) down to 10 (all different)
        // with 35/25 being used for the in between hands (full house, two pair)
        var baseScore: Int

        if (cardValues.containsKey('J')) {
            val maxCardsWithoutJoker = cardMap.filter { it.key > 1 }.maxOfOrNull { it.value } ?: 0
            val jokerCount = cardMap[1] ?: 0

            baseScore = (maxCardsWithoutJoker + jokerCount) * 10
        } else {
            baseScore = cardMap.maxOf { it.value } * 10
        }

        // full house / two pair adjustment
        if (cardMap.filter { it.key > 1 && it.value > 1 }.count() > 1) baseScore += 5

        // the tie score consists of two digits for each card in the hand, concatenated
        // adding ten to each card value ensures we have two digits
        val tieScore = cards.map { lbl -> (cardValues[lbl]!! + 10).toString() }.joinToString("")

        // concatenate the base score and tie score and we get an overall score
        // that can be trivially sorted to put the cards in order
        return Hand(cards, bid.toInt(), "$baseScore$tieScore".toLong())
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
        return calculate(input, "AKQ_T98765432J")
    }

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
