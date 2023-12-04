fun main() {
    fun isDigitAdjacentToSymbol(pos: Position, symbolPositions: List<Position>, length: Int): Boolean {
        // pos indicates start position, get all positions by adding the length
        val digitPositions = buildList {
            for (i in 0..<length) {
                add(Position(pos.row, pos.col + i))
            }
        }
        return symbolPositions.any { s -> digitPositions.any { it.isAdjacent(s) } }
    }

    fun buildDataStructure(rows: List<String>, part2: Boolean): Pair<MutableMap<Position, Int>, MutableList<Position>> {
        val digitPositions = mutableMapOf<Position, Int>()
        val symbolPositions = mutableListOf<Position>()
        val gearPositions = mutableListOf<Position>()

        rows.forEachIndexed { rowIdx, row ->
            var digitPos: Position? = null

            row.forEachIndexed { colIdx, col ->
                val pos = Position(rowIdx, colIdx)

                if (col.isDigit()) {
                    if (digitPos == null) {
                        digitPositions[pos] = col.digitToInt()
                        digitPos = pos
                    } else {
                        digitPositions[digitPos!!] = (digitPositions[digitPos]?.times(10) ?: 0) + col.digitToInt()
                    }
                } else {
                    digitPos = null

                    if (col != '.') {
                        symbolPositions.add(pos)

                        // could also be a gear
                        if (col == '*') gearPositions.add(pos)
                    }
                }
            }
        }

        if (part2) {
            return Pair(digitPositions, gearPositions)
        }

        return Pair(digitPositions, symbolPositions)
    }

    fun getGearRatio(gp: Position, digitPositions: Map<Position, Int>): Int {
        val listOfParts = mutableListOf<Int>()

        digitPositions.forEach { (dp, value) ->
            if (isDigitAdjacentToSymbol(dp, listOf(gp), value.toString().length)) {
                listOfParts.add(value)
            }

            if (listOfParts.size > 2) return 0
        }

        if (listOfParts.size < 2) return 0

        return listOfParts[0] * listOfParts[1]
    }

    fun part1(rows: List<String>): Int {
        val (digitPositions, symbolPositions) = buildDataStructure(rows, false)

        return digitPositions.filter { (position, value) ->
            isDigitAdjacentToSymbol(
                position,
                symbolPositions,
                value.toString().length
            )
        }
            .map { (_, value) -> value }
            .sum()
    }

    fun part2(rows: List<String>): Int {
        val (digitPositions, gearPositions) = buildDataStructure(rows, true)

        return gearPositions.sumOf { getGearRatio(it, digitPositions) }
    }

    val testInput = readInput("Day03_test")
    check(part1(testInput) == 4361)
    check(part2(testInput) == 467835)

    val input = readInput("Day03")
    part1(input).println() // 521601
    part2(input).println() // 80694070
}
