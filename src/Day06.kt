import kotlin.math.sqrt
import kotlin.time.measureTime
fun main() {
    fun permutations(time: Long): List<Long> = (1..<time).map { speed -> speed * (time - speed) }

    fun quadraticWins(time: Long, record: Long): Int {
        // if (speed * (time - speed)) > record
        // ST - S^2 > record
        // -S^2 + ST - record > 0
        // ax^2 + bx + c = 0
        // a = -1, b = T, c = -record
        // -b +/- sqrt(b^2 - 4ac)/2a

        val a = -1
        val b = time.toDouble()
        val c = -record.toDouble()

        val root = sqrt(b*b - (4*a*c))

        return root.toInt()
    }

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

        return quadraticWins(time, record)
//        return permutations(time).count { it > record }
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
    }.also { it.println() } // 1.5s for bruteforce, 49us for quadratic
}
