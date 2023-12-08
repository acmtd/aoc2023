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

    fun getStepCount(startNode: Node, endNodeName: String, directions: CharArray, nodeMap: Map<String, Node>): Int {
        var step = 0
        var curNode = startNode

        while (curNode.name != endNodeName) {
            curNode = nodeMap[nextNodeName(nextDirection(step, directions), curNode)]!!
            step++
        }

        return step
    }

    fun part1(blocks: List<String>): Int {
        val (directionsText, nodesText) = blocks

        val directions = directionsText.toCharArray()
        val nodeMap = nodesText.split("\n").map { it.makeNode() }.associateBy { it.name }

        nodeMap["AAA"]?.let { return getStepCount(it, "ZZZ", directions, nodeMap) }

        return 0
    }


    val testInput = File("data", "Day08_test.txt").readText().split("\n\n")
    check(part1(testInput) == 2)

    val input = File("data", "Day08.txt").readText().split("\n\n")

    measureTime {
        part1(input).println() // 22357
    }.also { it.println() } // 7ms
}