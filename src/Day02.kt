fun main() {
    val colors = listOf("red", "green", "blue")

    fun colorValue(data: String, color: String): Int {
        val find = Regex("(\\d+) $color").find(data) ?: return 0
        return find.groups[1]!!.value.toInt()
    }

    fun moves(data: String): List<Int> {
        return colors.map { colorValue(data, it) }
    }

    fun illegalMove(rgb: List<Int>, rgbMax: List<Int>): Boolean {
        for ((index, value) in rgb.withIndex()) {
            if (value > rgbMax[index]) return true
        }

        return false
    }

    fun minimumCubesForGame(data: String): Int {
        val items = data.split(";", ":")
        val moves = items.drop(1).map { x -> moves(x) }

        val redMin = moves.maxOf { it[0] }
        val greenMin = moves.maxOf { it[1] }
        val blueMin = moves.maxOf { it[2] }

        return redMin * greenMin * blueMin
    }

    fun legalGameNumbers(data: String, rgbMax: List<Int>): Int {
        val items = data.split(";", ":")
        val moves = items.drop(1).map { x -> moves(x) }

        if (moves.any { m -> illegalMove(m, rgbMax) }) return 0

        return items.first().replace(Regex("\\D"), "").toInt()
    }

    fun part1(games: List<String>): Int {
        return games.sumOf { legalGameNumbers(it, listOf(12, 13, 14)) }
    }

    fun part2(games: List<String>): Int {
        return games.sumOf { minimumCubesForGame(it) }
    }

    val testInput = readInput("Day02_test")
    check(part1(testInput) == 8)
    check(part2(testInput) == 2286)

    val input = readInput("Day02")
    part1(input).println() // 2913
    part2(input).println() // 55593
}
