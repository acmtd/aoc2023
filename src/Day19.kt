import java.io.File

fun main() {
    data class Rating(val category: Char, val rating: Int)
    data class Part(val ratings: List<Rating>) {
        fun score(): Int {
            return ratings.sumOf { it.rating }
        }
    }

    data class Condition(val category: Char, val test: String, val testValue: Int)
    data class Instruction(val condition: Condition?, val next: String)
    data class Workflow(val name: String, val steps: List<Instruction>) {
        fun examinePart(p: Part): String {
            steps.forEach { i ->
                i.condition?.let { c ->
                    val rating = p.ratings
                            .first { it.category == i.condition.category }
                            .rating

                    if (rating < c.testValue && "<" == c.test) return i.next
                    if (rating > c.testValue && ">" == c.test) return i.next

                } ?: return i.next
            }

            error("This should not happen")
        }
    }

    fun String.asCondition(): Condition {
        val category = this.first()
        val test = this.substring(1, 2)
        val rating = this.substring(2).toInt()

        return Condition(category, test, rating)
    }

    fun isAccepted(workflows: Map<String, Workflow>, p: Part): Boolean {
        var workflow = workflows.getValue("in")

        while (true) {
            val nextWorkflow = workflow.examinePart(p)

            if (nextWorkflow == "A") return true
            if (nextWorkflow == "R") return false

            workflow = workflows.getValue(nextWorkflow)
        }
    }

    fun part1(input: List<String>): Long {
        val (workflowData, ratingsData) = input

        val workflows = workflowData.split("\n")
                .map { line ->
                    val name = line.substringBefore("{")
                    val instructions = line.substringAfter("{").substringBefore("}")
                            .split(",")
                            .map { inst ->
                                val condNext = inst.split(":")

                                if (condNext.size == 1) {
                                    Instruction(null, condNext[0])
                                } else {
                                    Instruction(condNext[0].asCondition(), condNext[1])
                                }
                            }

                    Workflow(name, instructions)
                }.associateBy { it.name }

        val parts = ratingsData.split("\n").map {
            it.substring(1, it.length - 1).split(",")
                    .map { kv -> kv.split("=") }
                    .map { list -> Rating(list[0].first(), list[1].toInt()) }
        }.map { Part(it) }

        return parts.filter { p -> isAccepted(workflows, p) }.sumOf { it.score() }.toLong()
    }

    fun part2(input: List<String>): Long {
        return 0
    }

    val testInput = File("data", "Day19_test.txt").readText().split("\n\n")
    check(part1(testInput) == 19114.toLong())

    val input = File("data", "Day19.txt").readText().split("\n\n")
    part1(input).println() // 319295
}
