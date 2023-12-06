import kotlin.time.measureTime
fun main() {
    fun permutations(time: Long): List<Long> = (1..<time).map { speed -> speed * (time - speed) }

    fun part1(input: List<String>): Int {
        val times = input.first().removePrefix("Time:")
            .split(' ').filter { it.isNotBlank() }.map { it.toInt() }

        val records = input.last().removePrefix("Distance:")
            .split(' ').filter { it.isNotBlank() }.map { it.toInt() }

        return times.withIndex().map { (idx, time) -> permutations(time.toLong()).count { it > records[idx] } }
            .reduce(Int::times)
    }

    fun part2(input: List<String>): Int {
        val time = input.first().filter { it.isDigit() }.toLong()
        val record = input.last().filter { it.isDigit() }.toLong()

        return permutations(time).count { it > record }
    }

    val testInput = readInput("Day06_test")
    check(part1(testInput) == 288)

    val testInput2 = readInput("Day06_test")
    check(part2(testInput2) == 71503)

    val input = readInput("Day06")

    measureTime {
        part1(input).println() // 4403592
    }.also { it.println() } // 890us

    measureTime {
        part2(input).println() // 38017587
    }.also { it.println() } // 1.5s
}
