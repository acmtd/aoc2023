fun main() {
    fun Char.canBeReachedFrom(direction: Char): Boolean {
        return when (this) {
            '.' -> false
            'S' -> true
            '|' -> (direction == 'N' || direction == 'S')
            '-' -> (direction == 'E' || direction == 'W')
            'L' -> (direction == 'S' || direction == 'W')
            'J' -> (direction == 'S' || direction == 'E')
            'F' -> (direction == 'N' || direction == 'W')
            '7' -> (direction == 'N' || direction == 'E')
            else -> false
        }
    }

    data class Position(val row: Int, val col: Int)

    data class State(val pos: Position, val direction: Char, val symbol: Char) {
        fun getNextLegalStates(grid: Array<CharArray>): List<State> {
            return buildList {
                if (pos.row > 0)
                    add(State(Position(pos.row - 1, pos.col), 'N', grid[pos.row - 1][pos.col]))
                if (pos.col > 0)
                    add(State(Position(pos.row, pos.col - 1), 'W', grid[pos.row][pos.col - 1]))
                if (pos.row < grid.lastIndex)
                    add(State(Position(pos.row + 1, pos.col), 'S', grid[pos.row + 1][pos.col]))
                if (pos.col < grid.first().lastIndex)
                    add(State(Position(pos.row, pos.col + 1), 'E', grid[pos.row][pos.col + 1]))
            }.filter { pos -> pos.symbol.canBeReachedFrom(pos.direction) }
        }

        fun transformLocation() = State(pos, this.newDirection(), this.symbol)
        fun newDirection() =
            when (symbol) {
                'L' -> if (direction == 'S') 'E' else 'N'
                'F' -> if (direction == 'N') 'E' else 'S'
                '7' -> if (direction == 'E') 'S' else 'E'
                'J' -> if (direction == 'S') 'W' else 'N'
                else -> direction
            }
    }

    data class Route(val states: List<State>)

    fun visualize(grid: Array<CharArray>, polygon: List<Position>, inside: List<Position>, outside: List<Position>) {
        for ((row, line) in grid.withIndex()) {
            for ((col, symbol) in line.withIndex()) {
                when (Position(row, col)) {
                    in polygon -> print(symbol)
                    in inside -> print("I")
                    in outside -> print("O")
                    else -> print(grid[row][col])
                }
            }

            println()
        }
    }

    fun makeGrid(input: List<String>): Pair<Array<CharArray>, State> {
        val rowCount = input.size

        // define a 2d array to hold all the data
        val grid = Array(rowCount) { CharArray(input.first().length) }

        var startPos = State(Position(0, 0), '.', grid[0][0])

        for ((row, line) in input.withIndex()) {
            for ((col, character) in line.withIndex()) {
                grid[row][col] = character

                if (character == 'S') startPos = State(Position(row, col), '.', grid[row][col])
            }
        }

        return Pair(grid, startPos)
    }

    fun calculateRoute(grid: Array<CharArray>, startState: State): Route? {
        val routeList = ArrayDeque<Route>()
        val initialRoute = Route(mutableListOf(startState))

        routeList.add(initialRoute)

        while (routeList.isNotEmpty()) {
            val rte = routeList.removeFirst()
            val state = rte.states.last().transformLocation()

            val possibleMovesUnfiltered = state.getNextLegalStates(grid)

            val possibleMoves = possibleMovesUnfiltered
                .filter { s -> !rte.states.map { it.pos }.contains(s.pos) }

            // if there are no possible moves left, check if a move to the starting position is possible
            if (possibleMoves.isEmpty() && possibleMovesUnfiltered.any { it.pos == startState.pos })
                return rte

            val nextRoutes = possibleMoves.map {
                buildList {
                    addAll(rte.states)
                    add(it)
                }
            }.map { Route(it) }

            routeList.addAll(nextRoutes)
        }

        println("Could not find route!")
        return null
    }

    fun part1(input: List<String>): Int {
        val (grid, startState) = makeGrid(input)

        calculateRoute(grid, startState)?.let { return it.states.size / 2 } ?: return 0
    }

    fun part2(input: List<String>): Int {
        val (grid, startState) = makeGrid(input)

        calculateRoute(grid, startState)?.let { rte ->
            // these are the coordinates of the outside of our polygon
            val polygonPoints = rte.states.map { s -> s.pos }

            println("Calculating inside/outside polygon for ${polygonPoints.size} point polygon")

            val insidePositions = mutableListOf<Position>()
            val outsidePositions = mutableListOf<Position>()

            // draw a line from outside the polygon to the point, work out
            // how many times it intersects the polygon: odd=inside polygon, even=outside
            //
            // I'm still trying to convince myself why J and L should be included
            // but S and F should not, other than it makes the test case work
            // and produces the right output for the full input data
            for ((row, line) in grid.withIndex()) {
                var intersections = 0

                for ((col, symbol) in line.withIndex()) {
                    val pos = Position(row, col)

                    if (pos in polygonPoints) {
                        // this is a polygon point, if vertical bar or J/L then increment intersections
                        if (symbol in listOf('|', 'J', 'L')) intersections++
                    } else {
                        if (intersections % 2 == 1) {
                            insidePositions.add(pos)
                        } else {
                            outsidePositions.add(pos)
                        }
                    }
                }
            }

            visualize(grid, polygonPoints, insidePositions, outsidePositions)
            return insidePositions.size
        } ?: return 0
    }

    val testInput = readInput("Day10_test")
    check(part1(testInput) == 4)

    val testInput2 = readInput("Day10_test2")
    check(part1(testInput2) == 8)

    val testInput3 = readInput("Day10_test3")
    check(part2(testInput3) == 4)

    val testInput4 = readInput("Day10_test4")
    check(part2(testInput4) == 8)

    val testInput5 = readInput("Day10_test5")
    check(part2(testInput5) == 10)

    val input = readInput("Day10")
    part1(input).println() // 6754
    part2(input).println() // 567
}
