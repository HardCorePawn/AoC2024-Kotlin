import kotlin.math.absoluteValue
import kotlin.math.sign

fun main() {

    var robots = 0

    /**
     * Create a map representing a numeric Keypad
     * @return A Map of numeric keypad characters to their position in the keypad grid
     */
    fun getKeypad(): Map<Char, Point> {
        return mapOf(
            '7' to Point(0, 0), '8' to Point(1, 0), '9' to Point(2, 0),
            '4' to Point(0, 1), '5' to Point(1, 1), '6' to Point(2, 1),
            '1' to Point(0, 2), '2' to Point(1, 2), '3' to Point(2, 2),
            '-' to Point(0, 3), '0' to Point(1, 3), 'A' to Point(2, 3)
        )
    }

    /**
     * Create a map representing a directional Keypad
     * @return A Map of directional keypad characters to their position in the keypad grid
     */
    fun getDirPad(): Map<Char, Point> {
        return mapOf(
            '-' to Point(0, 0), '^' to Point(1, 0), 'A' to Point(2, 0),
            '<' to Point(0, 1), 'v' to Point(1, 1), '>' to Point(2, 1)
        )
    }

    //memoization
    // We cache the shortest number of moves (at a specific level in the robot chain) required
    // to move from one key to another on a keypad to prevent extra calculations
    var cache = mutableMapOf<Pair<String, Int>, Long>()

    /**
     * Recursively finds the shortest number of moves (at a given level in the robot chain) are required
     * to move from one key the keypad to another
     * @param start The key character we start at
     * @param end The key character we want to move to
     * @param level The level in the robot chain (0 = Numeric Keypad)
     * @returns The shortest number of moves required to get from start to end @ level
     */
    fun findShortestPath(start: Char, end: Char, level: Int): Long {
        val paths = mutableListOf<List<Char>>()
        // base level = number pad, other directional pad
        val pad = if (level == 0) getKeypad() else getDirPad()

        if (start == end) return 1 // we don't need to move, just push the button
        val key = "$start$end" to level

        // cache lookup
        if (cache[key] != null) return cache[key]!!

        val startPos = pad.getValue(start)
        val endPos = pad.getValue(end)
        val invalid = pad.getValue('-')

        // (x,y) deltas to get from start key to end key
        val (xDelta, yDelta) = endPos - startPos

        // make sure we don't move through the hole in the keypad
        if (Point(startPos.x + xDelta, startPos.y) != invalid) paths += buildList<Char> {
            // not going to hit the hole moving sideways first
            add('A') // a robot path always starts from A
            for (i in 1..xDelta.absoluteValue) {
                if (xDelta.sign < 0) {
                    add('<') // negative, move left
                } else {
                    add('>') // positive, move right
                }
            }
            for (i in 1..yDelta.absoluteValue) {
                if (yDelta.sign < 0) {
                    add('^') // negative, move up
                } else {
                    add('v') // positive, move down
                }
            }
            add('A') // we need to end on A
        }
        if (Point(startPos.x, startPos.y + yDelta) != invalid) paths += buildList<Char> {
            // not going to hit the hold moving vertically first
            add('A') // a robot path always start from A
            for (i in 1..yDelta.absoluteValue) {
                if (yDelta.sign < 0) {
                    add('^') // negative, move up
                } else {
                    add('v') // positive, move down
                }
            }
            for (i in 1..xDelta.absoluteValue) {
                if (xDelta.sign < 0) {
                    add('<') // negative, move left
                } else {
                    add('>') // positive, move right
                }
            }
            add('A') // we need to end on A
        }

        val shortestPath = paths.minOf { path ->
            // If this is the last robot, the paths generated are for the human operator,
            // we don't need the leading 'A', so we strip it from the path size
            if (level == robots) {
                path.size.toLong() - 1
            } else {
                // recursively search through the paths of next robot in the chain
                path.zipWithNext().sumOf { (start, end) ->
                    findShortestPath(start, end, level + 1)
                }
            }
        }

        // add our newly calculated shortest Path to the cache
        cache[key] = shortestPath
        return shortestPath
    }

    fun part1(input: List<String>): Long {
        // only 3 robots in Part 1 (but the levels are 0 indexed ;)
        robots = 2

        //sum of complexities
        var sum = 0L

        // For each line of input, starting at 'A' on numeric pad, find the shortest path
        input.forEach { line ->
            var currKey = 'A'
            var totalLen = 0L
            line.forEach { nextKey ->
                val pathLen = findShortestPath(currKey, nextKey, 0)
                totalLen += pathLen
                currKey = nextKey
            }
            // complexity for each code is: shortestPath for human operator * numeric part of code
            val complexity = totalLen * line.substringBefore('A').toInt()
            sum += complexity
        }
        return sum
    }

    fun part2(input: List<String>): Long {
        //re-initalise the cache as we have more links in the chain
        cache = mutableMapOf()

        // only 26 robots in Part 2 (but the levels are 0 indexed ;)
        robots = 25

        //sum of complexities
        var sum = 0L

        // For each line of input, starting at 'A' on numeric pad, find the shortest path
        input.forEach { line ->
            var currKey = 'A'
            var totalLen = 0L
            line.forEach { nextKey ->
                val pathLen = findShortestPath(currKey, nextKey, 0)
                totalLen += pathLen
                currKey = nextKey
            }
            // complexity for each code is: shortestPath for human operator * numeric part of code
            val complexity = totalLen * line.substringBefore('A').toInt()
            sum += complexity
        }
        return sum
    }

    // Read the input from the `src/Day21_input.txt` file.
//    val input = readInput("Day21_test")
    val input = readInput("Day21")

    part1(input).println()
    part2(input).println()
}
