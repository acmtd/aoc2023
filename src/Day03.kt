fun main() {
    fun isDigitAdjacentToSymbol(pos: Position, symbolPositions: List<Position>, length: Int): Boolean {
        // pos indicates start position, get all positions by adding the length
        val digitPositions = mutableListOf<Position>()

        for (i in 0..<length) {
            digitPositions.add(Position(pos.row, pos.col + i))
        }

        return symbolPositions.any { s ->
            digitPositions.any { it.isAdjacent(s) }
        }
    }

    fun part1(rows: List<String>): Int {
        val digitPositions = mutableMapOf<Position, Int>()
        val symbolPositions = mutableListOf<Position>()

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
                } else if (col == '.') {
                    // ignore dots
                    digitPos = null
                } else {
                    symbolPositions.add(pos)
                    digitPos = null
                }
            }
        }

        var result = 0

        digitPositions.forEach { (position, value) ->
            if (isDigitAdjacentToSymbol(position, symbolPositions, value.toString().length)) {
                println("Position  $position is adjacent")
                result += value
            } else {
                println("Position $position is not adjacent")
            }
        }

        return result
    }

    fun getGearRatio(gp: Position, digitPositions: Map<Position, Int>) : Int {
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

    fun part2(rows: List<String>): Int {
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
                } else if (col == '.') {
                    // ignore dots
                    digitPos = null
                } else {
                    symbolPositions.add(pos)
                    digitPos = null

                    // could also be a gear
                    if (col == '*') gearPositions.add(pos)
                }
            }
        }

        val result = gearPositions.sumOf { getGearRatio(it, digitPositions)}

        return result
    }

    val testInput = readInput("Day03_test")
    check(part1(testInput) == 4361)
    check(part2(testInput) == 467835)

    val input = readInput("Day03")
    part1(input).println() // 521601
    part2(input).println() // 80694070
}
