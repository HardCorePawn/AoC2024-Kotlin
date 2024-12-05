import java.util.*

fun main() {

    fun parseRules(input: List<String>): MutableMap<Int, MutableMap<String, MutableList<Int>>> {

        //MutableMap<Int, MutableMap<String, MutableList<Int>>>
        val bigMap = mutableMapOf<Int, MutableMap<String, MutableList<Int>>>()

        input.forEach { line ->
            if (line.contains("|")) {
                val numbers = line.split("|")
                val num1 = numbers[0].toInt()
                val num2 = numbers[1].toInt()
                if (bigMap.containsKey(num1)) {
                    if (bigMap[num1]!!.containsKey("After")) {
                        bigMap[num1]!!["After"]!!.add(num2)
                    } else {
                        bigMap[num1]!!["After"] = mutableListOf(num2)
                    }
                } else {
                    bigMap[num1] = mutableMapOf("After" to mutableListOf(num2), "Before" to mutableListOf())
                }
                if (bigMap.containsKey(num2)) {
                    if (bigMap[num2]!!.containsKey("Before")) {
                        bigMap[num2]!!["Before"]!!.add(num1)
                    } else {
                        bigMap[num2]!!["Before"] = mutableListOf(num1)
                    }
                } else {
                    bigMap[num2] = mutableMapOf("Before" to mutableListOf(num1), "After" to mutableListOf())
                }
            }
        }

        return bigMap
    }

    fun parseUpdates(input: List<String>): List<List<Int>> {
        val updates: MutableList<MutableList<Int>> = mutableListOf()

        input.forEach { line ->
            if (line.contains(",")) {
                val pageStrings = line.split(",")
                val pageNums: MutableList<Int> = mutableListOf()
                pageStrings.forEach {
                    pageNums.add(it.toInt())
                }
                updates.add(pageNums)
            }
        }

        return updates
    }

    fun validateUpdates(
        orderRules: MutableMap<Int, MutableMap<String, MutableList<Int>>>,
        updates: List<List<Int>>
    ): List<List<Int>> {

        val validatedUpdates: MutableList<List<Int>> = mutableListOf()

        updates.forEach { line ->
            var validated = true
            for (x in line.indices) {
                for (y in 0..x) {
                    if (orderRules[line[x]]!!["After"]!!.contains(line[y])) {
                        // invalidUpdate
                        validated = false
                        break
                    }
                }
                if (!validated) break
                for (y in x..line.indices.last) {
                    if (orderRules[line[x]]!!["Before"]!!.contains(line[y])) {
                        // invalidUpdate
                        validated = false
                        break
                    }
                }
                if (!validated) break
            }
            if (validated) validatedUpdates.add(line)
        }

        return validatedUpdates
    }

    fun findBadUpdates(
        orderRules: MutableMap<Int, MutableMap<String, MutableList<Int>>>,
        updates: List<List<Int>>
    ): List<List<Int>> {

        val badUpdates: MutableList<List<Int>> = mutableListOf()

        updates.forEach { line ->
            var validated = true
            for (x in line.indices) {
                for (y in 0..x) {
                    if (orderRules[line[x]]!!["After"]!!.contains(line[y])) {
                        // invalidUpdate
                        validated = false
                        break
                    }
                }
                if (!validated) break
                for (y in x..line.indices.last) {
                    if (orderRules[line[x]]!!["Before"]!!.contains(line[y])) {
                        // invalidUpdate
                        validated = false
                        break
                    }
                }
                if (!validated) break
            }
            if (!validated) badUpdates.add(line)
        }

        return badUpdates
    }

    fun correctUpdates(
        orderRules: MutableMap<Int, MutableMap<String, MutableList<Int>>>,
        badUpdates: List<List<Int>>
    ): List<List<Int>> {

        badUpdates.forEach { update ->
            for (x in update.indices.first..update.indices.last - 1) {
                //if next number in list is not in "After" rules of current number, swap them
                if (!orderRules[update[x]]!!["After"]!!.contains(update[x + 1])) {
                    Collections.swap(update, x, x + 1)
                    for (y in x downTo 1) {
                        // check the newly moved number against previous numbers in the list
                        if (orderRules[update[y]]!!["After"]!!.contains(update[y - 1])) {
                            Collections.swap(update, y, y - 1)
                        }
                    }
                }
            }
        }

        return badUpdates
    }

    fun part1(input: List<String>): Int {
        var safeCount = 0

        val orderRules = parseRules(input)
        val updates = parseUpdates(input)

        val validatedUpdates = validateUpdates(orderRules, updates)

        // calculate sum of middle values
        validatedUpdates.forEach { update ->
            safeCount += update[update.indices.last / 2]
        }
        return safeCount
    }

    fun part2(input: List<String>): Int {
        var safeCount = 0

        val orderRules = parseRules(input)
        val updates = parseUpdates(input)

        val badUpdates = findBadUpdates(orderRules, updates)

        val correctedUpdates = correctUpdates(orderRules, badUpdates)

        // calculate sum of middle values
        correctedUpdates.forEach { update ->
            safeCount += update[update.indices.last / 2]
        }

        return safeCount
    }

    // Read the input from the `src/Day03_input.txt` file.
//    val input = readInput("Day05_sample")
    val input = readInput("Day05_input")
    part1(input).println()
    part2(input).println()
}

