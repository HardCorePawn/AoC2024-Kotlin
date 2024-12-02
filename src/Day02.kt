fun main() {

    fun checkLevels(levels: List<Int>): Boolean {

        var prev = -1
        var increasing = false
        var second = false

        levels.forEach { curr ->

            //Not the first level?
            if (prev != -1) {
                // is this the second number?
                if (second) {
                    // reset second flag number flag
                    second = false

                    // is the level decreasing or increasing?
                    if (curr > prev) {
                        increasing = true
                    }
                }
                if (increasing) {
                    if ((curr - prev < 1) || (curr - prev > 3)) {
                        return false
                    }
                } else {
                    if ((prev - curr < 1) || (prev - curr > 3)) {
                        return false
                    }
                }
                prev = curr
            } else {
                // first level, remember it for comparison
                prev = curr
                // raise second number flag
                second = true
            }
        }
        // all levels passed
        return true
    }

    fun problemDampener(levels: List<Int>): Boolean {
        for (index in levels.indices) {
            val mutableLevels = levels.toMutableList()
            mutableLevels.removeAt(index)
            if (checkLevels(mutableLevels)) return true
        }
        return false
    }

    fun part1(input: List<String>): Int {
        var safeCount = 0

        input.forEach {

            //read in levels
            val levels = stringToList(it)

            if (checkLevels(levels)) {
                safeCount++
            }
        }
        return safeCount
    }

    fun part2(input: List<String>): Int {
        var safeCount = 0

        input.forEach {

            //read in levels
            val levels = stringToList(it)

            if (checkLevels(levels)) {
                safeCount++
            } else if (problemDampener(levels)) {
                safeCount++
            }
        }
        return safeCount
    }

    // Read the input from the `src/Day02_input.txt` file.
    val input = readInput("Day02_input")
    part1(input).println()
    part2(input).println()
}
