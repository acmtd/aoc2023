import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.math.absoluteValue

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("data", "$name.txt")
    .readLines()

fun readText(name: String) = File("data", "$name.txt")
    .readText()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)


fun lcm(n1: Long, n2: Long): Long {
    var gcd = 1.toLong()
    var i = 1.toLong()

    while (i <= n1 && i <= n2) {
        // Checks if i is factor of both integers
        if (n1 % i == 0.toLong() && n2 % i == 0.toLong())
            gcd = i
        ++i
    }

    return n1 * n2 / gcd
}

data class Position(val row: Int, val col: Int) {
    fun isAdjacent(otherPos: Position) =
        ((row - otherPos.row).absoluteValue <= 1) && ((col - otherPos.col).absoluteValue <= 1)
}