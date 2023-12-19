import kotlin.math.abs
import kotlin.time.measureTime

fun main() {
    data class Instruction(val dir: Char, val amount: Int, val color: String)
    data class Position(val x: Long, val y: Long) {
        fun next(dir: Char, amount: Int = 1): Position {
            return when (dir) {
                'R' -> Position(x + amount, y)
                'L' -> Position(x - amount, y)
                'U' -> Position(x, y - amount)
                'D' -> Position(x, y + amount)
                else -> this
            }
        }
    }

    fun visualize(yRange: LongRange, xRange: LongRange, positions: List<Position>, allInsidePoints: List<Position>) {
        val buffer = StringBuilder()

        yRange.forEach { y ->
            xRange.forEach { x ->
                when (Position(x, y)) {
                    in positions -> buffer.append("#")
                    in allInsidePoints -> buffer.append(".")
                    else -> buffer.append(" ")
                }
            }
            buffer.append("\n")
        }

        writeOutput("Day18_output.txt", buffer.toString())
    }

    fun shoelaceArea(positions: List<Position>): Long {
        val n = positions.size
        var area = 0.toLong()
        for (i in 0 until n - 1) {
            area += positions[i].x * positions[i + 1].y - positions[i + 1].x * positions[i].y
        }
        return abs(area + positions[n - 1].x * positions[0].y - positions[0].x * positions[n - 1].y) / 2
    }

    fun boundaryPoints(positions: List<Position>): Long {
        return positions.zipWithNext { a, b ->
            abs(a.x - b.x) + abs(a.y - b.y)
        }.sum()
    }

    fun calculateShoelacePick(instructions: List<Instruction>): Long {
        var position = Position(0, 0)

        val positions = buildList {
            add(position)
            instructions.forEach { inst ->
                position = position.next(inst.dir, inst.amount)
                add(position)
            }
        }

        val boundaryPoints = boundaryPoints(positions)
        val area = shoelaceArea(positions)

        val interiorPoints = area - (boundaryPoints / 2) + 1 // pick theorem

        return boundaryPoints + interiorPoints
    }

    fun instructionsPart1(input: List<String>): List<Instruction> {
        return input.map {
            val items = it.split(" ")
            Instruction(items[0].first(), items[1].toInt(), items[2])
        }
    }

    fun instructionsPart2(input: List<String>): List<Instruction> {
        return input.map {
            val instruction = it.split(" ")[2]

            // the color is actually an encoded instruction!
            // first 5 chars is a hex string, sixth maps to a direction
            val amount = instruction.substring(2..6).toInt(radix = 16)
            val direction = "RDLU"[instruction.substring(7, 8).toInt()]

            Instruction(direction, amount, "")
        }
    }

    fun calculateBruteForce(instructions: List<Instruction>, visualize: Boolean = false): Int {
        var position = Position(0, 0)

        val positions = buildList {
            instructions.forEach { inst ->
                repeat(inst.amount) {
                    position = position.next(inst.dir)
                    add(position)
                }
            }
        }

        val yRange = positions.minOf { it.y }..positions.maxOf { it.y }

        val allInsidePoints = yRange.flatMap { y ->
            var polygonCrossings = 0
            val insidePoints = hashSetOf<Position>()

            val xRangeThisRow = positions.filter { it.y == y }.minOf { it.x }..
                    positions.filter { it.y == y }.maxOf { it.x }

            xRangeThisRow.forEach { x ->
                val pos = Position(x, y)
                if (pos in positions) {
                    if (Position(x - 1, y) !in positions && Position(x + 1, y) !in positions) {
                        // simplest case, a single hash
                        polygonCrossings++
                    } else {
                        // need to know if this is an L or J (as in day 10)
                        // L: above (x, y-1) and to the right (x+1, y) are hashes
                        // J: above (x, y-1) and to the left (x-1, y) are hashes
                        if (Position(x, y - 1) in positions &&
                                (Position(x - 1, y) in positions || Position(x + 1, y) in positions)) polygonCrossings++
                    }
                } else {
                    if (polygonCrossings.mod(2) == 1) {
                        // inside the polygon
                        insidePoints.add(pos)
                    }
                }
            }

            insidePoints
        }

        if (visualize)
            visualize(yRange, positions.minOf { it.x }..positions.maxOf { it.x }, positions, allInsidePoints)

        return (positions.size + allInsidePoints.size)
    }

    val testInput = readInput("Day18_test")
    val input = readInput("Day18")

    // part 1 by two methods
    check(calculateBruteForce(instructionsPart1(testInput)) == 62)
    check(calculateShoelacePick(instructionsPart1(testInput)) == 62.toLong())

    measureTime {
        calculateBruteForce(instructionsPart1(input)).println() // 40761
    }.also { it.println() } // 173ms without visualizing

    measureTime {
        calculateShoelacePick(instructionsPart1(input)).println() // 40761
    }.also { it.println() } // 1.3ms

    // part 2 by the only method that's computationally available
    check(calculateShoelacePick(instructionsPart2(testInput)) == 952408144115)

    measureTime {
        calculateShoelacePick(instructionsPart2(input)).println() // 106920098354636
    }.also { it.println() }  // 1.7ms
}