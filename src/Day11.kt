fun main() {
    data class Position(val row: Int, val col: Int) {}

    fun makeGrid(input: List<String>): Pair<Array<CharArray>, List<Position>> {
        val rows = input.size
        val cols = input.first().length

        val grid = Array(rows) { CharArray(cols) }
        val galaxyPositions = mutableListOf<Position>()

        for ((row, line) in input.withIndex()) {
            for ((col, character) in line.withIndex()) {
                grid[row][col] = character

                if (character == '#') galaxyPositions.add(Position(row, col))
            }
        }

        return Pair(grid, galaxyPositions)
    }

    fun emptyRowIndices(grid: Array<CharArray>): List<Int> =
            grid.mapIndexedNotNull { index, chars -> if (chars.all { it == '.' }) index else null }

    fun emptyColIndices(grid: Array<CharArray>): List<Int> =
            (0..<grid.first().size).mapNotNull { index -> if (grid.all { it[index] == '.' }) index else null }

    fun calculate(input: List<String>, expansionCoefficient: Int): Long {
        val (grid, galaxyPositions) = makeGrid(input)

        val pairs = buildList {
            for ((idx1, p1) in galaxyPositions.withIndex()) {
                for ((idx2, p2) in galaxyPositions.withIndex()) {
                    if (idx2 > idx1) add(Pair(p1, p2))
                }
            }
        }

        val emptyRows = emptyRowIndices(grid)
        val emptyCols = emptyColIndices(grid)

        return pairs.sumOf { (g1, g2) ->
            val rowRange = if (g1.row > g2.row) g2.row..g1.row else g1.row..g2.row
            val colRange = if (g1.col > g2.col) g2.col..g1.col else g1.col..g2.col

            val rowDiff = rowRange.count() - 1
            val colDiff = colRange.count() - 1

            // expansion coefficent is how many rows a single row turns into.
            // Subtract 1 to get the number of ADDED rows
            val rowsAdded = (expansionCoefficient - 1) * emptyRows.count { it in rowRange }
            val colsAdded = (expansionCoefficient - 1) * emptyCols.count { it in colRange }

            (rowDiff + colDiff + rowsAdded + colsAdded).toLong()
        }
    }

    val testInput = readInput("Day11_test")
    check(calculate(testInput, 2) == 374.toLong())
    check(calculate(testInput, 10) == 1030.toLong())
    check(calculate(testInput, 100) == 8410.toLong())

    val input = readInput("Day11")
    calculate(input, 2).println() // Part 1: 9274989
    calculate(input, 1000000).println() // Part 2: 357134560737
}
