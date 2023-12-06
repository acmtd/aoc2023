import java.io.File
import kotlin.time.measureTime

fun main() {
    fun String.digits(): List<Long> = this.split(" ").filter { it.isNotEmpty() }.map { it.toLong() }
    fun String.mapData(): List<List<Long>> = this.split("\n").drop(1).map { it.digits() }

    fun lookup(num: Long, map: Map<LongRange, Long>): Long {
        val range = map.keys.firstOrNull { num in it } ?: return num
        return map[range]!! + num - range.first
    }

    fun Long.traverseMap(mapList: List<Map<LongRange, Long>>) =
            mapList.fold(this) { acc, next -> lookup(acc, next) }

    fun createReverseMap(data: String): Map<LongRange, Long> =
            data.mapData().associate { (source, dest, len) ->
                (source..<len + source) to dest
            }

    fun createMap(data: String): Map<LongRange, Long> =
            data.mapData().associate { (source, dest, len) ->
                (dest..<len + dest) to source
            }

    fun seedDigits(blocks: List<String>) = blocks[0].removePrefix("seeds: ").split(" ")
            .filter { it.isNotEmpty() }.map { it.toLong() }

    fun part1(blocks: List<String>): Long {
        val mapList = buildList {
            blocks.drop(1).forEach { add(createMap(it)) }
        }

        return seedDigits(blocks).minOf { it.traverseMap(mapList) }
    }

    fun part2(blocks: List<String>): Long {
        val mapList = buildList {
            blocks.drop(1).forEach { add(createReverseMap(it)) }
        }.reversed()

        val seedRanges = seedDigits(blocks).chunked(2)
                .map { (start, len) -> start..<len + start }

        for (loc in 0..Long.MAX_VALUE) {
            val seed = loc.traverseMap(mapList)
            seedRanges.firstOrNull { seed in it } ?: continue
            return loc
        }
        return 0
    }

    val testInput = File("data", "Day05_test.txt").readText().split("\n\n")
    check(part1(testInput) == 35.toLong())
    check(part2(testInput) == 46.toLong())

    val input = File("data", "Day05.txt").readText().split("\n\n")

    measureTime {
        part1(input).println() // 462648396
    }.also { it.println() } // 2.8ms

    measureTime {
        part2(input).println() // 2520479
    }.also { it.println() } // 826ms
}