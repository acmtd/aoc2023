import java.io.File

fun main() {
    fun createReverseMap(data: String): Map<LongRange, Long> {
        return data.split("\n").drop(1).associate { line ->
            val digits = line.split(" ").filter { it.isNotEmpty() }.map { it.toLong() }

            val sourceRange = digits[0]..<digits[2] + digits[0]
            val destStart = digits[1]

            sourceRange to destStart
        }
    }

    fun createMap(data: String): Map<LongRange, Long> {
        return data.split("\n").drop(1).associate { line ->
            val digits = line.split(" ").filter { it.isNotEmpty() }.map { it.toLong() }

            val destRange = digits[1]..<digits[2] + digits[1]
            val sourceStart = digits[0]

            destRange to sourceStart
        }
    }

    fun mapLookup(num: Long, map: Map<LongRange, Long>): Long {
        val range = map.keys.firstOrNull { num in it } ?: return num
        return map[range]!! + num - range.first
    }

    fun part1(data: String): Long {
        val blocks = data.split("\n\n")

        val listOfMaps = buildList<Map<LongRange, Long>> {
            blocks.drop(1).forEach { add(createMap(it)) }
        }

        return blocks[0].removePrefix("seeds: ").split(" ")
            .filter { it.isNotEmpty() }.map { it.toLong() }
            .minOf { r -> listOfMaps.fold(r) { acc, next -> mapLookup(acc, next) } }
    }

    fun part2(data: String): Long {
        val blocks = data.split("\n\n")

        val listOfMaps = buildList<Map<LongRange, Long>> {
            blocks.drop(1).forEach { add(createReverseMap(it)) }
        }.reversed()

        val seedRanges = blocks[0].removePrefix("seeds: ").split(" ")
            .filter { it.isNotEmpty() }.map { it.toLong() }.chunked(2).map { it[0]..<it[0] + it[1] }

        var loc: Long = 0

        while (true) {
            loc++
            val seed = listOfMaps.fold(loc) { acc, next -> mapLookup(acc, next) }
            val result = seedRanges.firstOrNull { seed in it }

            if (result != null) return loc
        }
    }

    val testInput = File("data", "Day05_test.txt").readText()
    println("tests")
    check(part1(testInput) == 35.toLong())
    check(part2(testInput) == 46.toLong())

    val input = File("data", "Day05.txt").readText()
    println("part 1")
    part1(input).println() // 462648396

    println("part 2")
    part2(input).println() // 2520479
}