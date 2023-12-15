import java.io.File
import kotlin.time.measureTime

fun main() {
    fun flipRowsCols(rows: List<String>): List<String> {
        return buildList {
            rows.indices.forEach { col ->
                add(rows.indices.map { row -> rows[row][col] }.joinToString(""))
            }
        }
    }

    fun rows(pattern: String): List<String> = pattern.split("\n")

    fun gridScore(input: String): Int {
        val cols = flipRowsCols(rows(input))

        return cols.sumOf { col -> col.indices.filter { col[it] == 'O' }.sumOf { cols.size - it } }
    }

    fun translate(input: String, direction: Char): String {
        val rows = rows(input)

        val items = if (direction == 'N' || direction == 'S') flipRowsCols(rows) else rows

        val translated = items.map { item ->
            buildList {
                add(-1) // add the wall at the top
                addAll(items.indices.filter { item[it] == '#' })
                add(item.length) // add a wall at the bottom
            }.zipWithNext { first, second ->
                // our goal here is to output a substring showing how this section now looks
                // it should still start and end with the "#" obviously, with the rest
                // containing the smushed up rocks and the "." sign
                val movingRocks = item.indices.filter { it in first..second }.count { item[it] == 'O' }

                if (direction == 'N' || direction == 'W')
                    "#" + "O".repeat(movingRocks) + ".".repeat(second - first - movingRocks - 1)
                else
                    "#" + ".".repeat(second - first - movingRocks - 1) + "O".repeat(movingRocks)
            }
                .joinToString("")
                .removePrefix("#")
        }

        val result = if (direction == 'E' || direction == 'W') translated.joinToString("\n")
        else flipRowsCols(translated).joinToString("\n")

        return result
    }

    fun part1(input: String): Int {
        return gridScore(translate(input, 'N'))
    }

    fun part2(input: String): Int {
        val directions = "NWSE"

        val gridCache = hashMapOf<String, Int>()
        val scores = mutableListOf<Int>()

        var grid = input
        var cycle = 0

        while (true) {
            // complete a cycle
            cycle++
            directions.forEach { dir -> grid = translate(grid, dir) }

            scores.add(gridScore(grid))

            val previousCycle = gridCache.getOrPut(grid) { cycle }

            if (previousCycle < cycle) {
                // everything prior to previousCycle is spent getting the grid into a periodic cycle
                val cycleLength = cycle - previousCycle
                val offset = (1000000000 - previousCycle).mod(cycleLength)

                return scores[previousCycle + offset - 1]
            }
        }
    }

    val testInput = File("data", "Day14_test.txt").readText()
    val input = File("data", "Day14.txt").readText()

    check(part1(testInput) == 136)

    measureTime {
        part1(input).println() // 112046
    }.also { it.println() } // 20ms

    check(part2(testInput) == 64)

    measureTime {
        part2(input).println() // 104619
    }.also { it.println() } // 1.2s
}
