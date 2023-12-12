fun main() {
    tailrec fun permutations(items: List<String>): List<String> {
        val nextItems = items.flatMap {
            if (it.contains('?')) {
                listOf(it.replaceFirst('?', '.'), it.replaceFirst('?', '#'))
            } else {
                listOf(it)
            }
        }

        if (nextItems.none { it.contains('?') }) return nextItems

        return permutations(nextItems)
    }

    fun bruteForceCalculate(code: String, checksum: String): Int {
        val permutations = permutations(listOf(code))

        val pattern = Regex("\\.*" + checksum.split(",")
                .joinToString("\\.+") { "#{$it}" } + "\\.*")

        return permutations.count { pattern.matches(it) }
    }

    fun part1(input: List<String>): Int {
        return input.sumOf { it ->
            val (code, checksum) = it.split(" ")

            bruteForceCalculate(code, checksum)
        }
    }

//    fun part2(input: List<String>): Int {
//        return input.sumOf { it ->
//            val (code, checksum) = it.split(" ")
//
//            val newChecksum = "$checksum,$checksum,$checksum,$checksum,$checksum"
//            val newCode = "$code?$code?$code?$code?$code"
//            bruteForceCalculate(newCode, newChecksum)
//        }
//    }

    val testInput = readInput("Day12_test")
    check(part1(testInput) == 21)
//    check(part2(testInput) == 525152)

    val input = readInput("Day12")
    part1(input).println() // 7221
//    part2(input).println() // 
}
