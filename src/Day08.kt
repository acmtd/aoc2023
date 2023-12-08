import java.io.File
import kotlin.time.measureTime

fun main() {
    data class Node(val name: String, val connections: Pair<String, String>)

    fun String.makeNode(): Node {
        val name = this.substringBefore(" =")
        val conn1 = this.substringAfter("(").substringBefore(",")
        val conn2 = this.substringAfter(", ").substringBefore(")")

        return Node(name, Pair(conn1, conn2))
    }

    fun nextNodeName(nextDirection: Char, curNode: Node) = when (nextDirection) {
        'R' -> curNode.connections.second
        'L' -> curNode.connections.first
        else -> error("this should not happen")
    }

    fun nextDirection(step: Int, directions: CharArray): Char = directions[step.mod(directions.size)]

    fun getStepCount(startNode: Node, nodeEndsWith: String, directions: CharArray, nodeMap: Map<String, Node>): Int {
        var step = 0
        var curNode = startNode

        while (!curNode.name.endsWith(nodeEndsWith)) {
            curNode = nodeMap[nextNodeName(nextDirection(step, directions), curNode)]!!
            step++
        }

        return step
    }

    fun calculate(blocks: List<String>, part1: Boolean): Long {
        val (directionsText, nodesText) = blocks

        val directions = directionsText.toCharArray()
        val nodeMap = nodesText.split("\n").map { it.makeNode() }.associateBy { it.name }

        if (part1) {
            nodeMap["AAA"]?.let { return getStepCount(it, "ZZZ", directions, nodeMap).toLong() } ?: return 0
        } else {
            // find distance for each individual start node to reach a Z node
            val distances = nodeMap.filter { it.key.endsWith("A") }.values
                .map { node -> getStepCount(node, "Z", directions, nodeMap) }

            // need to use least common multiple to find the shared distance that leads all start nodes to a Z at the same time
            return distances.map { it.toLong() }.reduce { acc, next -> lcm(acc, next) }
        }
    }

    fun part1(blocks: List<String>) = calculate(blocks, true)
    fun part2(blocks: List<String>) = calculate(blocks, false)

    val testInput = File("data", "Day08_test.txt").readText().split("\n\n")
    check(part1(testInput) == 2.toLong())

    val testInput2 = File("data", "Day08_test2.txt").readText().split("\n\n")
    check(part2(testInput2) == 6.toLong())

    val input = File("data", "Day08.txt").readText().split("\n\n")

    measureTime {
        part1(input).println() // 22357
    }.also { it.println() } // 6ms

    measureTime {
        part2(input).println() // 10371555451871
    }.also { it.println() } // 10ms
}