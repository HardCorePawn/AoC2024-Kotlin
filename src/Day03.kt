fun main() {

    fun part1(input: List<String>): Int {
        var safeCount = 0

        // find the valid mul(xxx,xxx) subStrings, use groups to get Ints
        val regex = Regex("mul\\(([0-9]{1,3}),([0-9]{1,3})\\)")
        input.forEach { line ->
            val matchResults = regex.findAll(line).toList()
            for (match in matchResults) {
                // multiply the Ints together and add the product to the running sum
                safeCount += match.groupValues[1].toInt() * match.groupValues[2].toInt()
            }
        }

        // return the sum of all the products
        return safeCount
    }

    fun part2(input: List<String>): Int {
        var safeCount = 0

        // remove all the newlines, so we have 1 (very) long String
        var bigLine = ""
        input.forEach { line ->
            bigLine += line
        }

        // remove all the don't()...do() sections of text
        val excludeRegex = Regex("don't\\(\\).*?do\\(\\)")
        val matches = excludeRegex.splitToSequence(bigLine)

        // construct 1 line with all "enabled" multiplications
        var includedText = ""
        matches.forEach { match ->
            if (match != matches.last()) {
                includedText += match
            } else {
                // make sure to exclude anything after the final don't()
                val finalRegex = Regex("^.*?don't\\(\\)")
                val finalMatch = finalRegex.find(match)
                includedText += finalMatch?.value
            }
            // retrieve the sum of all the multiplications using part1()
            safeCount = part1(listOf(includedText))
        }

        return safeCount
    }

    // Read the input from the `src/Day03_input.txt` file.
//    val input = readInput("Day03_sample")
    val input = readInput("Day03_input")
    part1(input).println()
    part2(input).println()
}
