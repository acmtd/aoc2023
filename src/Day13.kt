import java.io.File
import kotlin.math.min

fun main() {
    fun getRowsAndCols(pattern: String): Pair<List<String>, List<String>> {
        val rows = pattern.split("\n")

        val colCount = rows.first().length

        val cols = buildList {
            (0..<colCount).forEach { col ->
                add(rows.indices.map { row -> rows[row][col] }.joinToString(""))
            }
        }

        return Pair(rows, cols)
    }

    fun symmetryScore(items: List<String>, a: Int, b: Int): Int {
        val maxOffset = min(a, items.size - b - 1)

        val symmetry = (0..maxOffset).all { offset ->
            val aVal = items[a - offset]
            val bVal = items[b + offset]

            aVal == bVal
        }

        if (symmetry) {
            println("Symmetry found between $a and $b")
            return a + 1
        }

        return 0
    }

    fun getSymmetryCount(items: List<String>): Int {
        // remember that reflection occurs BETWEEN two columns
        return items.indices.windowed(2)
                .map { (a, b) -> symmetryScore(items, a, b) }
                .firstOrNull { it > 0 } ?: 0
    }

    fun symmetryCountForPattern(pattern: Pair<List<String>, List<String>>): Int {
        val rowSymmetry = getSymmetryCount(pattern.first)
        val colSymmetry = getSymmetryCount(pattern.second)

        val score = rowSymmetry * 100 + colSymmetry
        return score
    }

    fun part1(patterns: List<String>): Int {
        return patterns.map { pattern -> getRowsAndCols(pattern) }
                .sumOf { symmetryCountForPattern(it) }
                .also { it.println() }
    }

    val testInput = File("data", "Day13_test.txt").readText().split("\n\n")
    val input = File("data", "Day13.txt").readText().split("\n\n")

    part1(testInput) // 405
    part1(input) // 35691
}
