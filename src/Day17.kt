import kotlin.time.measureTime

typealias Routing<T, U> = List<Pair<T, Map<T, U>>>

fun main() {
    data class Position(val row: Int, val col: Int) {
        fun left(): Position {
            return Position(row, col - 1)
        }

        fun right(): Position {
            return Position(row, col + 1)
        }

        fun down(): Position {
            return Position(row + 1, col);
        }

        fun up(): Position {
            return Position(row - 1, col)
        }
    }

    data class Node(val pos: Position, val heatArriving: Int)

    data class Graph<Node>(
        val vertices: Set<Node>,
        val edges: Map<Node, Set<Node>>,
        val weights: Map<Pair<Node, Node>, Int>
    )

    fun wouldBeMoreThanThreeInOneDirection(
        prevNodeMap: MutableMap<Node, Node?>,
        nextNode: Node,
        current: Node
    ): Boolean {
        // need to compare the positions of next with whatever was three nodes ago
        // N, 3 (current), 2 (prev), 1 (prev2)
        // if the delta row or delta col is 3, then this is not allowed
        val prev: Node = prevNodeMap[current] ?: return false
        val prev2: Node = prevNodeMap[prev] ?: return false
        val prev3: Node = prevNodeMap[prev2] ?: return false

        val previousNodes = listOf(current, prev, prev2, prev3)
        val allSameRow = previousNodes.all { it.pos.row == nextNode.pos.row }
        val allSameCol = previousNodes.all { it.pos.col == nextNode.pos.col }

        if (allSameCol || allSameRow) {
//            println("Must change direction due to $prev3 so rejecting $nextNode")
            return true
        }

        return false
    }

    fun dijkstra(graph: Graph<Node>, start: Node): Map<Node, Node?> {
        val S: MutableSet<Node> = mutableSetOf()

        val delta = graph.vertices.associateWith { Int.MAX_VALUE }.toMutableMap()
        delta[start] = 0

        val previous: MutableMap<Node, Node?> = graph.vertices.associateWith { null }.toMutableMap()

        while (S != graph.vertices) {
            val v: Node = delta
                .filter { !S.contains(it.key) }
                .minBy { it.value }
                .key

            graph.edges.getValue(v).minus(S).forEach { neighbor ->
                val newPath = delta.getValue(v) + graph.weights.getValue(Pair(v, neighbor))

                if (newPath < delta.getValue(neighbor)) {
                    // would this create more than 3 steps in the same direction?
                    if (!wouldBeMoreThanThreeInOneDirection(previous, neighbor, v)) {
                        delta[neighbor] = newPath
                        previous[neighbor] = v
                    }
                }
            }

            S.add(v)
        }

        return previous.toMap()
    }

    fun <Node> shortestPath(shortestPathTree: Map<Node, Node?>, start: Node, end: Node): List<Node> {
        fun pathTo(start: Node, end: Node): List<Node> {
            if (shortestPathTree[end] == null) return listOf(end)
            return listOf(pathTo(start, shortestPathTree[end]!!), listOf(end)).flatten()
        }

        return pathTo(start, end)
    }


    fun getNodesAndEdges(input: List<String>): Pair<Map<Position, Node>, Routing<Node, Node?>> {
        val nodeMap = buildMap {
            for ((row, line) in input.withIndex()) {
                for ((col, character) in line.withIndex()) {
                    val pos = Position(row, col)
                    put(pos, Node(Position(row, col), character.digitToInt()))
                }
            }
        }

        val nodes = nodeMap.values

        // for each node, get the set of positions it connects to
        val edges = buildMap {
            nodes.forEach { node ->
                put(node, listOf(node.pos.up(), node.pos.down(), node.pos.left(), node.pos.right())
                    .mapNotNull { pos -> nodeMap[pos] }
                    .toSet())
            }
        }

        // need to map each Pair<from, to> to a weight
        val weights = buildMap {
            edges.map { (nodeFrom, toSet) ->
                toSet.map { nodeTo ->
                    put(Pair(nodeFrom, nodeTo), nodeTo.heatArriving)
                }
            }
        }

        return nodeMap to nodes.map { it to dijkstra(Graph(nodes.toSet(), edges, weights), it) }
    }

    fun routeTo(origin: Node, destination: Node, routing: Routing<Node, Node?>) =
        shortestPath(routing.first { it.first == origin }.second, origin, destination)

    fun part1(input: List<String>): Int {
        println("Calculate routing map")
        val (nodeMap, routing) = getNodesAndEdges(input)

        println("figure out the path")
        val startNode = nodeMap.getValue(Position(0, 0))
        val endNode = nodeMap.getValue(Position(input.size - 1, input.size - 1))

        var routeTo = routeTo(startNode, endNode, routing)

//        println("list of nodes in route:")
//        routeTo.forEach { it.println() }

        input.indices.forEach { row ->
            input.indices.forEach { col ->
                val pos = Position(row, col)

                if (pos in routeTo.drop(1).map { it.pos }) print("*") else nodeMap[pos]?.let { print(it.heatArriving) }
            }
            println()
        }
        return routeTo.drop(1).sumOf { it.heatArriving }.also { it.println() }
    }


    val testInput = readInput("Day17_test")
    val input = readInput("Day17")

//    check(part1(testInput) == 102) // getting 110

    measureTime {
        part1(input).println() //
    }.also { it.println() } //
}
