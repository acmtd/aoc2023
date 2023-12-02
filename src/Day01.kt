fun main() {
    val numberMap = mapOf(
        "one" to 1, "two" to 2, "three" to 3, "four" to 4, "five" to 5,
        "six" to 6, "seven" to 7, "eight" to 8, "nine" to 9
    )

    fun convertToDigit(str: String): Int {
        return numberMap[str] ?: str.toInt()
    }

    fun convertToDigits(str: String): Sequence<Int> {
        return Regex("(?=(\\d|one|two|three|four|five|six|seven|eight|nine))")
            .findAll(str)
            .mapNotNull { it.groups[1] }
            .map { convertToDigit(it.value) }
    }

    fun part1(input: List<String>): Int {
        return input.map { it.filter { x -> x.isDigit() } }
            .sumOf { it.first().digitToInt() * 10 + it.last().digitToInt() }
    }

    fun part2(input: List<String>): Int {
        return input.map { convertToDigits(it) }.sumOf { it.first() * 10 + it.last() }
    }

    val testInput = readInput("Day01_test")
    check(part1(testInput) == 142)

    val testInput2 = readInput("Day01_test2")
    check(part2(testInput2) == 281)

    val input = readInput("Day01")
    part1(input).println() // 54081
    part2(input).println() // 54649
}
