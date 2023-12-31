import kotlin.math.pow

fun main() {
    fun String.getNumbers(): List<Int> {
        return this.trim().split(Regex("\\s+")).map { it.toInt() }
    }

    fun parse(data: String): Pair<Int, Int> {
        val cardNumber = data.substringBefore(":").substringAfter("Card ").trim().toInt()

        val winningNumbers =
                data.substringAfter(":").substringBefore("|").getNumbers()

        val ourNumbers = data.substringAfter("|").getNumbers()

        val matchingNumbers = ourNumbers.filter { winningNumbers.contains(it) }.size

        return Pair(cardNumber, matchingNumbers)
    }

    fun part1Score(matchingNumbers: Int): Int {
        return (2.0).pow(matchingNumbers - 1).toInt()
    }

    fun part1(cards: List<String>): Int {
        return cards.sumOf { part1Score(parse(it).second) }
    }

    fun part2(cards: List<String>): Int {
        val resultMap = cards.associate {
            parse(it).let { (cardNum, matchingNums) -> cardNum to matchingNums }
        }

        var cardsProcessed = 0

        // add all the initial cards onto a stack
        val cardsToProcess = ArrayDeque(resultMap.keys)

        while (!cardsToProcess.isEmpty()) {
            val cardNumber = cardsToProcess.removeFirst()
            val cardsToAdd = resultMap[cardNumber] ?: 0
            cardsProcessed++

            if (cardsToAdd > 0) {
                val range =(cardNumber + 1)..(cardNumber + cardsToAdd)
                range.forEach { cardsToProcess.addLast(it) }
            }
        }

        return cardsProcessed
    }

    val testInput = readInput("Day04_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 30)

    val input = readInput("Day04")
    part1(input).println() // 21138
    part2(input).println() // 7185540
}
