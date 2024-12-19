fun main() {

    // memoization? we've heard of it ;)
    val cache = mutableMapOf<String, Long>()

    // recursively check the design to see if we can reconstruct it using the available towels patterns
    fun checkDesign(design: String, patterns: List<String>): Boolean {

        patterns.filter { it.length <= design.length }.forEach { pattern ->
            // can the design can be matched to a single towel pattern or a combination of patterns?
            if (design == pattern ||
                (design.startsWith(pattern) && checkDesign(design.substringAfter(pattern), patterns))
            ) {
                return true
            }
        }
        return false // no matches possible for this design
    }

    // Returns a list of all the designs that can be constructed using any available towel patterns
    fun checkDesigns(designs: List<String>, patterns: List<String>): List<String> {
        val validDesigns = mutableListOf<String>()

        designs.forEach { design ->
            if (checkDesign(design, patterns)) {
                validDesigns.add(design)
            }
        }
        return validDesigns
    }

    // Find the total number of ways a design can be constructed from any of the available towel patterns
    // We use memoization to cache the results, so this will actually finish before the heat death of the universe
    fun validCombos(design: String, patterns: List<String>): Long = cache.getOrPut(design) {
        var count = 0L

        patterns.filter { it.length <= design.length }.forEach { towel ->
            if (design == towel) count++
            else {
                if (design.startsWith(towel)) {
                    count += validCombos(design.substringAfter(towel), patterns)
                }
            }
        }
        count
    }

    // Read the input from the `src/Day19_input.txt` file.
//    val input = readInput("Day19_sample")
    val input = readInput("Day19_input")

    val patterns = input[0].split(", ").sortedByDescending { it.length }
    val designs = input.subList(2, input.size)

    // get all the designs that can be constructed using towels
    val validDesigns = checkDesigns(designs, patterns)
    validDesigns.size.println()

    // for all the valid designs, count how many different ways each one can be constructed
    // and output the sum of the counts
    validDesigns.sumOf { design -> validCombos(design, patterns) }.println()
}