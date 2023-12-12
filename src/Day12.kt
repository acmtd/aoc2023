fun main() {
    tailrec fun permutations(items: List<String>): List<String> {
        val nextItems = items.flatMap {
            if (it.contains('?')) {
                listOf(it.replaceFirst('?', '.'), it.replaceFirst('?', '#'))
            } else {
                listOf(it)
            }
        }

        if (nextItems.none { it.contains('?') }) return nextItems

        return permutations(nextItems)
    }

    fun isMatch(str: String, start: Int, blockSize: Int): Boolean {
        if (start + blockSize >= str.length) return false
        if (str.substring(0, start).contains('#')) return false

        val candidate = str.substring(start, (start + blockSize + 2).coerceAtMost(str.length))

        return (candidate.first() != '#' && candidate.last() != '#'
                && candidate.substring(1, 1 + blockSize).all { ch -> ch == '?' || ch == '#' })
    }

    fun newStringsForBlockSize(
        prefix: String,
        str: String,
        blockSize: Int,
        extraLogging: Boolean
    ): List<Pair<String, String>> {
        if (extraLogging) println("\nO \u001b[33m$prefix\u001b[0m$str $blockSize")

        if (str.length < blockSize) return listOf()

        val strings = str.indices
            .filter { startIndex -> isMatch(str, startIndex, blockSize) }
            .map {
                val firstPart = str.substring(0, it + 1).replace('?', '.') + "#".repeat(blockSize)
                val secondPart = str.substring((it + blockSize + 1).coerceAtMost(str.length))
                Pair(prefix + firstPart, secondPart)
            }
            .filter { it.second != str }

        if (extraLogging) strings.forEach { println("N \u001b[33m${it.first}\u001b[0m${it.second}") }

        return strings
    }

    fun isIllegal(perm: Pair<String, String>): Boolean {
        return (perm.second.contains('#'))
    }

    fun slidingWindowCalc(code: String, checksum: String, extraLogging: Boolean): Long {
        val blockSizes = checksum.split(",").map { it.toInt() }

        // pad the code with a dot before and after so we can always look for $###."
        var permutations = listOf(Pair("", ".$code."))

        blockSizes.forEach { size ->
            if (extraLogging) println("\nDo mapping for block size $size")
            permutations = permutations.flatMap { (prefix, code) ->
                newStringsForBlockSize(prefix, code, size, extraLogging)
            }
        //            println("Possible start values for next block: ${permutations.distinct()}")
        }

        // remove any permutations that have a # left in the second part

        val illegalPerms = permutations.filter { isIllegal(it) }
        if (extraLogging) {
            illegalPerms.map { it.first + it.second }.map { it.replace("?", ".") }
                .forEach { println("I $it") }
        }

        val legalPerms = permutations.filter { it !in illegalPerms }

        //        println("final list of permutations: $permutations")
        if (extraLogging) println("${legalPerms.size} permutations for $code $checksum")

        if (extraLogging) legalPerms.map { it.first + it.second }.map { it.replace("?", ".") }
            .forEach { println("F $it") }

        print("*")
        return legalPerms.count().toLong()
    }

    fun bruteForceCalculate(code: String, checksum: String): Int {
        val permutations = permutations(listOf(code))

        val pattern = Regex("\\.*" + checksum.split(",").joinToString("\\.+") { "#{$it}" } + "\\.*")

        return permutations.count { pattern.matches(it) }
    }

    //
    fun part1(input: List<String>): Int {
        return input.sumOf { it ->
            val (code, checksum) = it.split(" ")

            val bf = bruteForceCalculate(code, checksum)
            val sw = slidingWindowCalc(code, checksum, false)

            if (bf.toLong() != sw) {
                println("For $code, $checksum, BF=$bf, SW=$sw")
            }

            bf
        }
    }

    //
    fun part2(input: List<String>): Long {
        return input.sumOf { it ->
            val (code, checksum) = it.split(" ")

            val newChecksum = "$checksum,$checksum,$checksum,$checksum,$checksum"
            val newCode = "$code?$code?$code?$code?$code"

            val replaceMultipleDots = newCode.replace(Regex("\\.\\."), ".")

            slidingWindowCalc(replaceMultipleDots, newChecksum, false)
        //            bruteForceCalculate(replaceMultipleDots, newChecksum)
        }
    }

    val testInput = readInput("Day12_test")
    check(part1(testInput) == 21)
    check(part2(testInput) == 525152.toLong())

    val input = readInput("Day12")
    part1(input).println() // 7221
//    part2(input).println() // 
}