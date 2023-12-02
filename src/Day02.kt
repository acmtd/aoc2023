fun main() {
    val colors = listOf("red", "green", "blue")

    fun colorCount(data: String, color: String): Int {
        val find = Regex("(\\d+) $color").find(data) ?: return 0
        return find.groups[1]!!.value.toInt()
    }

    fun illegalBag(rgb: List<Int>, rgbMax: List<Int>): Boolean {
        for ((index, value) in rgb.withIndex()) {
            if (value > rgbMax[index]) return true
        }

        return false
    }

    fun String.cubeCombos(): List<Int> {
        return colors.map { colorCount(this, it) }
    }

    fun bagsOfCubes(data: String): List<List<Int>> {
        return data.substringAfter(":").split(";").map { it.cubeCombos() }
    }

    fun minimumCubesProduct(data: String): Int {
        val bags = bagsOfCubes(data)

        return listOf(bags.maxOf { it[0] },
            bags.maxOf { it[1] },
            bags.maxOf { it[2] })
            .reduce(Int::times)
    }

    fun legalGameNumbers(data: String, rgbMax: List<Int>): Int {
        if (bagsOfCubes(data).any { m -> illegalBag(m, rgbMax) }) return 0

        return data.substringBefore(":").substringAfter("Game ").toInt()
    }

    fun part1(games: List<String>): Int {
        return games.sumOf { legalGameNumbers(it, listOf(12, 13, 14)) }
    }

    fun part2(games: List<String>): Int {
        return games.sumOf { minimumCubesProduct(it) }
    }

    val testInput = readInput("Day02_test")
    check(part1(testInput) == 8)
    check(part2(testInput) == 2286)

    val input = readInput("Day02")
    part1(input).println() // 2913
    part2(input).println() // 55593
}
