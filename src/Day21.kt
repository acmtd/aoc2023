import kotlin.math.abs

fun main() {
    data class Position(val row: Int, val col: Int)

    data class Grid(val rocks: Set<Position>, val plots: Set<Position>, val startPos: Position) {
        val nextPlotCache = hashMapOf<Position, List<Position>>()

        fun nextPlots(pos: Position): List<Position> {
            return nextPlotCache.getOrPut(pos) {
                buildList {
                    addAll(plots.filter { it.row == pos.row && abs(it.col - pos.col) == 1 })
                    addAll(plots.filter { it.col == pos.col && abs(it.row - pos.row) == 1 })
                }
            }
        }
    }

    data class State(val pos: Position, val steps: Int) {}

    fun makeGrid(input: List<String>): Grid {
        // define a 2d array to hold all the data
        var startPos: Position = Position(0, 0)

        val rocks = hashSetOf<Position>()
        val plots = hashSetOf<Position>()

        for ((row, line) in input.withIndex()) {
            for ((col, character) in line.withIndex()) {
                val pos = Position(row, col)
                if (character == '#') {
                    rocks.add(pos)
                } else {
                    plots.add(pos)
                    if (character == 'S') startPos = pos
                }
            }
        }

        return Grid(rocks, plots, startPos)
    }

    fun part1(input: List<String>, target: Int): Int {
        val grid = makeGrid(input)

        val queue = ArrayDeque<State>()

        queue.add(State(grid.startPos, 0))

        val endStates = hashSetOf<State>()
        val visited = hashSetOf<State>()

        while (!queue.isEmpty()) {
            val state = queue.removeFirst()

            if (state.steps == target) {
                endStates.add(state)
            } else {
                val nextPlots = grid.nextPlots(state.pos)
                        .map { State(it, state.steps + 1) }
                        .filter { it !in visited }
                        .also { visited.addAll(it) }

                queue.addAll(nextPlots)
            }
        }

        return endStates.count()
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    val testInput = readInput("Day21_test")
    check(part1(testInput, 6) == 16)

    val input = readInput("Day21")
    part1(input, 64).println() // 3605
}
