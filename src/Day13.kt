import java.io.File
import kotlin.math.min

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

    fun smudgeSymmetryScore(items: List<String>, a: Int, b: Int): Int {
        val maxOffset = min(a, items.size - b - 1)

        val range = (0..maxOffset)
        val rangeLength = range.count()

        val exactMatchCount = (0..maxOffset).count { offset ->
            val aVal = items[a - offset]
            val bVal = items[b + offset]

            aVal == bVal
        }

        // all but one of the items need to match for the
        // possibility of a smudge symmetry
        if (exactMatchCount != rangeLength - 1) return 0

        val smudgeMatch =
                (0..maxOffset).any { offset ->
                    val aVal = items[a - offset]
                    val bVal = items[b + offset]

                    aVal.indices.count { aVal[it] == bVal[it] } == aVal.length - 1
                }

        if (smudgeMatch) return a + 1

        return 0
    }

    fun symmetryScore(items: List<String>, a: Int, b: Int, part2: Boolean): Int {
        if (part2) return smudgeSymmetryScore(items, a, b)

        val maxOffset = min(a, items.size - b - 1)

        val symmetry = (0..maxOffset).all { offset ->
            val aVal = items[a - offset]
            val bVal = items[b + offset]

            aVal == bVal
        }

        if (symmetry) return a + 1

        return 0
    }

    fun symmetryCountForRowOrCol(items: List<String>, part2: Boolean): Int {
        // remember that reflection occurs BETWEEN two columns
        return items.indices.windowed(2)
                .map { (a, b) -> symmetryScore(items, a, b, part2) }
                .firstOrNull { it > 0 } ?: 0
    }

    fun symmetryCountForPattern(pattern: Pair<List<String>, List<String>>, part2: Boolean): Int {
        val rowSymmetry = symmetryCountForRowOrCol(pattern.first, part2)
        val colSymmetry = symmetryCountForRowOrCol(pattern.second, part2)

        return rowSymmetry * 100 + colSymmetry
    }

    fun part1(patterns: List<String>): Int {
        return patterns.map { rowsAndCols(it) }
                .sumOf { symmetryCountForPattern(it, part2 = false) }
    }

    fun part2(patterns: List<String>): Int {
        return patterns.map { rowsAndCols(it) }
                .sumOf { symmetryCountForPattern(it, part2 = true) }
    }

    val testInput = File("data", "Day13_test.txt").readText().split("\n\n")
    val input = File("data", "Day13.txt").readText().split("\n\n")

    check(part1(testInput) == 405)
    println(part1(input)) // 35691

    check(part2(testInput) == 400)
    println(part2(input)) // 39037
}
