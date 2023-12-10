fun main() {
    data class Position(val row: Int, val col: Int)

    data class State(val pos: Position,
                     val direction: Char, val symbol: Char) {

        fun getNextLegalStates(grid: Array<CharArray>): List<State> {
            return buildList {
                if (pos.row > 0) {
                    add(State(Position(pos.row - 1, pos.col), 'N', grid[pos.row - 1][pos.col]))
                }
                if (pos.col > 0) {
                    add(State(Position(pos.row, pos.col - 1), 'W', grid[pos.row][pos.col - 1]))
                }
                if (pos.row < grid.lastIndex) {
                    add(State(Position(pos.row + 1, pos.col), 'S', grid[pos.row + 1][pos.col]))
                }
                if (pos.col < grid.lastIndex) {
                    add(State(Position(pos.row, pos.col + 1), 'E', grid[pos.row][pos.col + 1]))
                }
            }.filter { pos -> pos.symbol.canBeReachedFrom(pos.direction) }
        }

        fun transformLocation(): State {
            return State(pos, this.newDirection(), this.symbol)
        }

        fun newDirection(): Char {
            // if it's a corner, flip the direction
            if (symbol == 'L') {
                if (this.direction == 'S') return 'E'
                return 'N'
            }

            if (symbol == 'F') {
                if (this.direction == 'N') return 'E'
                return 'S'
            }

            if (symbol == '7') {
                if (this.direction == 'E') return 'S'
                return 'E'
            }

            if (symbol == 'J') {
                if (this.direction == 'S') return 'W'
                return 'N'
            }

            return this.direction
        }
    }

    data class Route(val states: List<State>)

    fun makeGrid(input: List<String>): Pair<Array<CharArray>, State> {
        val boxSize = input.size

        // define a 2d array to hold all the data
        val grid = Array(boxSize) { CharArray(boxSize) }

        var startPos = State(Position(0, 0), '.', grid[0][0])

        for ((row, line) in input.withIndex()) {
            for ((col, character) in line.withIndex()) {
                grid[row][col] = character

                if (character == 'S') startPos = State(Position(row, col), '.', grid[row][col])
            }
        }

        return Pair(grid, startPos)
    }

    fun part1(input: List<String>): Int {
        val (grid, startState) = makeGrid(input)

        val routeList = ArrayDeque<Route>()
        val initialRoute = Route(mutableListOf(startState))

        routeList.add(initialRoute)

        while (routeList.isNotEmpty()) {
            val rte = routeList.removeFirst()
            val state = rte.states.last().transformLocation()

            val possibleMovesUnfiltered = state.getNextLegalStates(grid)

            val possibleMoves = possibleMovesUnfiltered
                .filter { s -> !rte.states.map { it.pos }.contains(s.pos) }

            // if there are no possible moves left, check if a move to the
            // starting position is possible
            if (possibleMoves.isEmpty()) {
                if (possibleMovesUnfiltered.any { it.pos == startState.pos }) {
                    println("Found the S again, pos is $state")
                    return rte.states.size / 2
                }
            }

            val nextRoutes = possibleMoves.map {
                buildList {
                    addAll(rte.states)
                    add(it)
                }
            }.map { Route(it) }

            routeList.addAll(nextRoutes)
        }

        return 0
    }

    val testInput = readInput("Day10_test")
    check(part1(testInput) == 4)

    val testInput2 = readInput("Day10_test2")
    check(part1(testInput2) == 8)

    val input = readInput("Day10")
    part1(input).println()
}

private fun Char.canBeReachedFrom(direction: Char): Boolean {
    if (this == '.') return false // cannot travel to a ground position

    if (this == 'S') return true
    
    if (direction == '.') return true

    if (this == '|') return (direction == 'N' || direction == 'S')
    if (this == '-') return (direction == 'E' || direction == 'W')

    if (this == 'L') return (direction == 'S' || direction == 'W')
    if (this == 'J') return (direction == 'S' || direction == 'E')
    if (this == 'F') return (direction == 'N' || direction == 'W')
    if (this == '7') return (direction == 'N' || direction == 'E')

    return false
}
