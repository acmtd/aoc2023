import java.io.File

fun main() {
    fun String.digits(): List<Long> = this.split(" ").filter { it.isNotEmpty() }.map { it.toLong() }
    fun String.mapData(): List<List<Long>> = this.split("\n").drop(1).map { it.digits()}

    fun createReverseMap(data: String): Map<LongRange, Long> {
        return data.mapData().associate { (source, dest, len) ->
            (source..<len + source) to dest
        }
    }

    fun createMap(data: String): Map<LongRange, Long> {
        return data.mapData().associate { (source, dest, len) ->
            (dest..<len + dest) to source
        }
    }

    fun mapLookup(num: Long, map: Map<LongRange, Long>): Long {
        val range = map.keys.firstOrNull { num in it } ?: return num
        return map[range]!! + num - range.first
    }

    fun part1(data: String): Long {
        val blocks = data.split("\n\n")

        val listOfMaps = buildList {
            blocks.drop(1).forEach { add(createMap(it)) }
        }

        return blocks[0].removePrefix("seeds: ").split(" ")
            .filter { it.isNotEmpty() }.map { it.toLong() }
            .minOf { r -> listOfMaps.fold(r) { acc, next -> mapLookup(acc, next) } }
    }

    fun part2(data: String): Long {
        val blocks = data.split("\n\n")

        val listOfMaps = buildList {
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
    check(part1(testInput) == 35.toLong())
    check(part2(testInput) == 46.toLong())

    val input = File("data", "Day05.txt").readText()
    part1(input).println() // 462648396
    part2(input).println() // 2520479
}