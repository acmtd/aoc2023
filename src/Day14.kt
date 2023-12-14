import java.io.File
import kotlin.time.measureTime

fun main() {
    fun rowsAndCols(pattern: String): Pair<List<String>, List<String>> {
        val rows = pattern.split("\n")

        val colCount = rows.first().length

        val cols = buildList {
            (0..<colCount).forEach { col ->
                add(rows.indices.map { row -> rows[row][col] }.joinToString(""))
            }
        }

        return Pair(rows, cols)
    }

    fun part1(input: String): Int {
        val (_, cols) = rowsAndCols(input)

        return cols.sumOf { col ->
            buildList {
                add(-1) // add the wall at the top
                addAll(col.indices.filter { col[it] == '#' })
                add(col.length) // add a wall at the bottom
            }.zipWithNext { first, second ->
                // find out which rolling rocks can hit each rock
                val movingRocks = col.indices.filter { it in first..second }.count { col[it] == 'O' }

                val baseScore = col.length - first - 1
                baseScore * movingRocks - (0..<movingRocks).sum()
            }.sum()
        }
    }

    val testInput = File("data", "Day14_test.txt").readText()
    val input = File("data", "Day14.txt").readText()

    check(part1(testInput) == 136)

    measureTime {
        part1(input).println() // 112046
    }.also { it.println() } // 15ms
}
