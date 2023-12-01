fun main() {
    fun convertToDigit(str: String): String {
        if (str.length == 1) return str

        if (str == "one") return "1"
        if (str == "two") return "2"
        if (str == "three") return "3"
        if (str == "four") return "4"
        if (str == "five") return "5"
        if (str == "six") return "6"
        if (str == "seven") return "7"
        if (str == "eight") return "8"
        return "9"
    }

    fun convertToDigits(str: String): String {
        return Regex("(?=(\\d|one|two|three|four|five|six|seven|eight|nine))")
            .findAll(str)
            .joinToString { convertToDigit(it.groups[1]?.value ?: "") }
    }

    fun part1(input: List<String>): Int {
        return input.map { it.filter { x -> x.isDigit() } }
            .sumOf { it.first().digitToInt() * 10 + it.last().digitToInt() }
    }

    fun part2(input: List<String>): Int {
        return input.map { convertToDigits(it) }
            .sumOf { it.first().digitToInt() * 10 + it.last().digitToInt() }
    }

    val testInput = readInput("Day01_test")
    check(part1(testInput) == 142)

    val testInput2 = readInput("Day01_test2")
    check(part2(testInput2) == 281)

    val input = readInput("Day01")
    part1(input).println() // 54081
    part2(input).println() // 54649
}
