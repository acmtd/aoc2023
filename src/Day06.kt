import kotlin.math.sqrt
import kotlin.math.floor
import kotlin.math.ceil
import kotlin.time.measureTime

fun main() {
    fun permutations(time: Long): List<Long> = (1..<time).map { speed -> speed * (time - speed) }

    fun quadraticWins(time: Long, record: Long): Int {
        // apply quadratic equation solution:
        // secondsToHold * (totalTime - secondsToHold) > record
        // -seconds^2 + (seconds*totalTime) > record
        // fits into quadratic, solve for seconds as x:
        // ax^2 +bx + c > 0
        // where a=-1, b = totalTime, c = -record
        val a = -1
        val b = time.toDouble()
        val c = -record.toDouble()

        val root = sqrt(b * b - (4 * a * c))

        val minVal = (-b + root) / (a * 2.0)
        val maxVal = (-b - root) / (a * 2.0)

        val minValRounded = floor(minVal + 1.0)
        val maxValRounded = ceil(maxVal - 1.0)

        return maxValRounded.toInt() - minValRounded.toInt() + 1
    }

    fun part1(input: List<String>): Int {
        val times = input.first().removePrefix("Time:")
                .split(' ').filter { it.isNotBlank() }.map { it.toInt() }

        val records = input.last().removePrefix("Distance:")
                .split(' ').filter { it.isNotBlank() }.map { it.toInt() }

        return times.withIndex().map { (idx, time) -> quadraticWins(time.toLong(), records[idx].toLong()) }
                .reduce(Int::times)

//        return times.withIndex().map { (idx, time) -> permutations(time.toLong()).count { it > records[idx] } }
//                .reduce(Int::times)
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
    }.also { it.println() } // 890us for bruteforce, 480us for quadratic (most time spent parsing)

    measureTime {
        part2(input).println() // 38017587
    }.also { it.println() } // 1.5s for bruteforce, 49us for quadratic
}
