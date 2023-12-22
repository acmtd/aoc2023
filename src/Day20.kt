import java.util.*
import kotlin.collections.ArrayDeque

class Day20 {
    companion object {
        val moduleMap = hashMapOf<String, Module>()
    }

    enum class PulseType {
        LOW, HIGH, NONE
    }

    data class Module(val name: String, val type: Char, val destinations: List<String>) {
        // need to track most recent pulse for each input for conjunctions
        val lastPulses = hashMapOf<String, PulseType>()

        // need to track overall on/off state for flip-flops
        var flipFlopState = false

        fun initializeLastPulses(incomingModules: List<String>) {
            incomingModules.forEach { lastPulses[it] = PulseType.LOW }
        }

        fun receivePulse(pulse: Pulse): List<Pulse> {
            val newPulseType = when (type) {
                '&' -> {
                    lastPulses[pulse.origin.name] = pulse.pulseType
                    if (lastPulses.values.all { it == PulseType.HIGH }) PulseType.LOW else PulseType.HIGH
                }

                '%' -> {
                    if (pulse.pulseType == PulseType.HIGH) PulseType.NONE else {
                        flipFlopState = !flipFlopState
                        if (flipFlopState) PulseType.HIGH else PulseType.LOW
                    }
                }

                else -> {
                    pulse.pulseType
                }
            }

            if (newPulseType == PulseType.NONE) return listOf()

//            println(destinations)
            return destinations.map { moduleMap.getValue(it) }
                .map { Pulse(it, pulse.dest, newPulseType) }
        }
    }

    data class Pulse(val dest: Module, val origin: Module, val pulseType: PulseType) {
        override fun toString(): String {
            return "${origin.name} -${pulseType.toString().lowercase(Locale.getDefault())}-> ${dest.name}"
        }
    }

    fun toModule(str: String): Module {
        val (moduleWithType, destinations) = str.split(" -> ")

        val type = moduleWithType.first().takeIf { it == '&' || it == '%' } ?: ' '
        val name = moduleWithType.dropWhile { it == '&' || it == '%' }

        val destList = destinations.split(", ")

        return Module(name, type, destList)
    }

    fun initializeModuleMap(input: List<String>) {
        moduleMap.clear()
        // parse the module configuration
        input.map { toModule(it) }.forEach { m -> moduleMap[m.name] = m }

        moduleMap["button"] = Module("button", ' ', listOf("broadcaster"))

        // find any module names in the destinations that are not in the module map
        // and add them
        moduleMap.values.flatMap { it.destinations }.toSet()
            .filter { it !in moduleMap.keys }
            .forEach { moduleMap[it] = Module(it, ' ', listOf()) }

        // find all of the conjunction modules and initialize them
        // with knowledge of the modules that feed into them
        moduleMap.values.filter { it.type == '&' }
            .forEach { conjunction ->
                val incoming = moduleMap.values
                    .filter { otherModule -> conjunction.name in otherModule.destinations }
                    .map { otherModule -> otherModule.name }

                conjunction.initializeLastPulses(incoming)
            }
    }

    fun part1(input: List<String>, buttonPushCount: Int): Long {
        initializeModuleMap(input)

        val queue = ArrayDeque<Pulse>()

        val startPulse = Pulse(
            moduleMap.getValue("broadcaster"),
            moduleMap.getValue("button"),
            PulseType.LOW
        )

        var lowCount = 0.toLong()
        var highCount = 0.toLong()

        repeat(buttonPushCount) {
            queue.add(startPulse) // push the button, frank

            while (!queue.isEmpty()) {
                val pulse = queue.removeFirst()

                if (pulse.pulseType == PulseType.HIGH) highCount++ else lowCount++

                queue.addAll(pulse.dest.receivePulse(pulse))
            }
        }

        return (lowCount * highCount)
    }
}

fun main() {
    val day20 = Day20()

    val testInput = readInput("Day20_test")
    check(day20.part1(testInput, 1000) == 32000000.toLong())

    val testInput2 = readInput("Day20_test2")
    check(day20.part1(testInput2, 1000) == 11687500.toLong())

    val input = readInput("Day20")
    day20.part1(input, 1000).println() // 807069600
}