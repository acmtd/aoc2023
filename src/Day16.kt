import kotlinx.coroutines.*
import kotlin.time.measureTime

fun main() {
    data class Position(val row: Int, val col: Int) {}
    data class Direction(val deltaRow: Int, val deltaCol: Int) {}
    data class State(val pos: Position, val dir: Direction) {}

    fun makeGrid(input: List<String>): Array<CharArray> {
        val rows = input.size
        val cols = input.first().length

        val grid = Array(rows) { CharArray(cols) }

        for ((row, line) in input.withIndex()) {
            for ((col, character) in line.withIndex()) {
                grid[row][col] = character
            }
        }

        return grid
    }

    fun isOutOfBounds(pos: Position, grid: Array<CharArray>) =
            (pos.col < 0 || pos.row < 0 || pos.col >= grid.first().size || pos.row >= grid.size)

    fun nextStates(state: State, grid: Array<CharArray>): List<State> {
        return buildList {
            val item = grid[state.pos.row][state.pos.col]

            // continue in the same direction if empty space or a splitter
            // that runs parallel to our direction ("pointy" end)
            if (item == '.' || (item == '-' && state.dir.deltaRow == 0) || (item == '|' && state.dir.deltaCol == 0)) {
                add(state.dir)
            } else if (item == '-') {
                // this splitter creates two new beams, one going left, one going right
                add(Direction(0, -1))
                add(Direction(0, 1))
            } else if (item == '|') {
                // this splitter creates two new beams, one going up, one going down
                add(Direction(1, 0))
                add(Direction(-1, 0))
            } else if (item == '/') {
                // right becomes up, down becomes left, left becomes down, up becomes right
                add(Direction(-state.dir.deltaCol, -state.dir.deltaRow))
            } else if (item == '\\') {
                // right becomes down, down becomes right, left becomes up, up becomes left
                add(Direction(state.dir.deltaCol, state.dir.deltaRow))
            }
        }.map {
            State(Position(state.pos.row + it.deltaRow, state.pos.col + it.deltaCol), it)
        }
    }

    fun sendBeam(startState: State, grid: Array<CharArray>): Int {
        val queue = ArrayDeque<State>()
        queue.add(startState)

        val visited = hashSetOf<State>()

        while (!queue.isEmpty()) {
            val state = queue.removeFirst()

            if (visited.contains(state) || isOutOfBounds(state.pos, grid)) continue
            visited.add(state)

            queue.addAll(nextStates(state, grid))
        }

        return visited.map { it.pos }.distinct().count()
    }

    fun part1(input: List<String>): Int {
        val pos = Position(0, 0)
        val dir = Direction(0, 1)
        val state = State(pos, dir)
        return sendBeam(state, makeGrid(input))
    }

    suspend fun calculatePart2(input: List<String>): Int {
        // basically part 1 again but need to test all possible edge positions
        val grid = makeGrid(input)

        val startStates = buildSet {
            grid.indices.forEach { row ->
                grid.first().indices.forEach { col ->
                    if (row == 0 || row == grid.size - 1 || col == 0 || col == grid.first().size - 1) {
                        val pos = Position(row, col)

                        if (row == 0) {
                            add(State(pos, Direction(1, 0)))
                        } else if (col == 0) {
                            add(State(pos, Direction(0, 1)))
                        } else if (row == grid.size - 1) {
                            add(State(pos, Direction(-1, 0)))
                        } else {
                            add(State(pos, Direction(0, -1)))
                        }
                    }
                }
            }
        }

        return coroutineScope {
            startStates.map { state ->
                async {
                    sendBeam(state, grid)
                }
            }.awaitAll()
        }.max()
    }

    fun part2(input: List<String>): Int {
        return runBlocking { calculatePart2(input) }
    }

    val testInput = readInput("Day16_test")
    val input = readInput("Day16")

    check(part1(testInput) == 46)

    measureTime {
        part1(input).println() // 6994
    }.also { it.println() } // 12ms

    check(part2(testInput) == 51)

    measureTime {
        part2(input).println() // 7488
    }.also { it.println() }  // 476ms -> 406ms with coroutines
}
