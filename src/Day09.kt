fun main() {
    fun part1(input: List<String>): Int =
            input.map { it.toDigits() }.sumOf { nextValue(it) }

    fun part2(input: List<String>): Int =
            input.map { it.toDigits() }.sumOf { prevValue(it) }

    val testInput = readInput("Day09_test")
    check(part1(testInput) == 114)
    check(part2(testInput) == 2)

    val input = readInput("Day09")
    part1(input).println() // 1789635132
    part2(input).println() // 913
}

fun getSequences(list: List<Int>): MutableList<List<Int>> {
    val lists = mutableListOf<List<Int>>()

    var thisList = list

    while (true) {
        val deltas = thisList.zipWithNext { a, b -> (b - a) }

        lists.add(deltas)
        if (deltas.distinct().size == 1) break

        thisList = deltas
    }

    return lists
}

fun nextValue(list: List<Int>): Int {
    val lists = getSequences(list)

    // to find the next value, take the last value of each of the sublists and sum it,
    // then add it to the last value of the original list
    return list.last() + lists.sumOf { it.last() }
}

fun prevValue(list: List<Int>): Int {
    val lists = getSequences(list)

    var firstValue = lists.last().first()

    lists.reversed().drop(1).forEach {
        firstValue = it.first() - firstValue
    }

    // to find the next value, take the last value of each of the sublists and sum it,
    // then add it to the last value of the original list
    return list.first() - firstValue
}

private fun String.toDigits(): List<Int> {
    return this.split(" ").filter { it.isNotBlank() }.map { it.toInt() }
}
