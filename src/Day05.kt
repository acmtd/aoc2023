import java.io.File

fun main() {
    fun createMap(data: String): Map<LongRange, Long> {
        val map = data.split("\n").drop(1).associate { line ->
            val digits = line.split(" ").filter { it.isNotEmpty() }.map { it.toLong() }

            val destRange = digits[1]..<digits[2] + digits[1]
            val sourceStart = digits[0]

            destRange to sourceStart
        }

        return map
    }

    fun mapLookup(num: Long, map: Map<LongRange, Long>): Long {
        val range = map.keys.firstOrNull { num in it } ?: return num
        val offset = num - range.first

        return map[range]!! + offset
    }

    fun getSeedsPart1(seeds: String): List<LongRange> {
        return seeds.substringAfter("seeds: ").split(" ")
                .filter { it.isNotEmpty() }.map { it.toLong() }.map { it..it }
    }

    fun getSeedsPart2(seeds: String): List<LongRange> {
        // these represent start numbers and numbers of seeds
        return seeds.substringAfter("seeds: ").split(" ")
                .filter { it.isNotEmpty() }.map { it.toLong() }.chunked(2).map { it[0]..<it[0] + it[1] }
    }

    fun calculate(data: String, part1: Boolean): Long {
        val blocks = data.split("\n\n")
        val seedRanges = if (part1) getSeedsPart1(blocks[0]) else getSeedsPart2(blocks[0])
        val listOfMaps = buildList<Map<LongRange, Long>> {
            blocks.drop(1).forEach { add(createMap(it)) }
        }

        return seedRanges.flatMap { range ->
            range.map { s -> listOfMaps.fold(s) { acc, next -> mapLookup(acc, next) } }
        }.min()

    }

    fun part1(data: String): Long {
        return calculate(data, true)
    }

    fun part2(data: String): Long {
        return calculate(data, false)
    }

    val testInput = File("data", "Day05_test.txt").readText()
    check(part1(testInput) == 35.toLong())
    check(part2(testInput) == 46.toLong())

    val input = File("data", "Day05.txt").readText()
    part1(input).println() // 462648396
    part2(input).println() // 
}