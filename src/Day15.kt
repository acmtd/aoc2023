import java.io.File
import kotlin.time.measureTime

fun main() {
    class Lens(val label: String, var focalLength: Int)

    fun hash(input: String): Int {
        return input.map { it.code }
            .fold(0) { acc, next -> ((acc + next) * 17).rem(256) }
    }

    fun part1(input: String): Int {
        return input.split(",").sumOf { hash(it) }
    }

    fun part2(input: String): Int {
        val map = hashMapOf<Int, MutableList<Lens>>()

        input.split(",").forEach { instruction ->
            val (label, focalLength) = instruction.split("-", "=")
            val boxNumber = hash(label)

            if (focalLength.isEmpty()) {
                // go to the relevant box and remove the lens with the given label, IF present in the box
                map[boxNumber]?.let { lenses -> lenses.removeIf { it.label == label } }
            } else {
                val newLens = Lens(label, focalLength.toInt())

                if (map.containsKey(boxNumber)) {
                    map[boxNumber]?.let { lenses ->
                        lenses.find { it.label == label }?.let {
                            // if this label is in the box, update the focal length
                            it.focalLength = focalLength.toInt()
                        } ?: run {
                            // if this label not already in the box, add to the end of the box
                            lenses.add(newLens)
                        }
                    }
                } else {
                    map[boxNumber] = mutableListOf(newLens)
                }
            }
        }

        return map.flatMap { (box, lenses) ->
            lenses.mapIndexed { idx, lens -> (box + 1) * (idx + 1) * lens.focalLength }
        }.sum()
    }

    val testInput = File("data", "Day15_test.txt").readText()
    val input = File("data", "Day15.txt").readText()

    check(part1("HASH") == 52)
    check(part1(testInput) == 1320)

    measureTime {
        part1(input).println() // 507769
    }.also { it.println() } // 4ms

    check(part2(testInput) == 145)

    measureTime {
        part2(input).println() // 269747
    }.also { it.println() }  // 17ms
}
