fun main() {

    fun parseValuesAndOperands(input: List<String>): Map<Long, List<Long>> {
        val valsAndOps = mutableMapOf<Long, List<Long>>()

        input.forEach { line ->
            val value = line.substringBefore(":").toLong()
            val opsString = line.substringAfter(": ")
            val opsList = opsString.split(" ").map { it.toLong() }
            valsAndOps[value] = opsList
        }
        return valsAndOps
    }

    fun <T> evaluate(operands: List<T>, operators: List<(T, T) -> T>, depth: Int = 0): List<T> {
        // If we only have one operand, we've reached the end of the list, so return it as the result.
        if (operands.size == 1) {
            return listOf(operands.first())
        }

        val results = mutableListOf<T>()

        // Try each operator between every pair of operands (working left to right)
        for (operator in operators) {
            // Combine the operands at index 0 and 1 with the operator.
            val combined = operator(operands[0], operands[1])

            // Create a new list with the combined result replacing the two operands.
            val newOperands = operands.toMutableList()
            newOperands[0] = combined
            newOperands.removeAt(1)

            // Recursively evaluate the new list of operands.
            results.addAll(evaluate(newOperands, operators, depth + 1))
        }

        return results
    }

    fun part1(input: List<String>): Long {
        var safeCount: Long = 0

        // Split each line of the input into a Target value and List of operands
        val valsAndOps = parseValuesAndOperands(input)

        valsAndOps.forEach { (key, value) ->
            // We create our "operators" as lambda's
            val operators = listOf<(Long, Long) -> Long>(
                { a, b -> a + b }, // Addition
                { a, b -> a * b }  // Multiplication
            )
            val results = evaluate(value, operators)
            if (results.contains(key)) {
                // if we found the answer in our permutations, add it to the tally
                safeCount += key
            }
        }
        return safeCount
    }

    fun part2(input: List<String>): Long {
        var safeCount: Long = 0

        // Split each line of the input into a Target value and List of operands
        val valsAndOps = parseValuesAndOperands(input)

        valsAndOps.forEach { key, value ->
            // We create our "operators" as lambda's
            val operators = listOf<(Long, Long) -> Long>(
                { a, b -> a + b }, // Addition
                { a, b -> a * b }, // Multiplication
                { a, b -> (a.toString() + b.toString()).toLong() } // Concatenation
            )
            val results = evaluate(value, operators)
            if (results.contains(key)) {
                // if we found the answer in our permutations, add it to the tally
                safeCount += key
            }
        }
        return safeCount
    }

    // Read the input from the `src/Day07_input.txt` file.
//    val input = readInput("Day07_sample")
    val input = readInput("Day07_input")

    part1(input).println()
    part2(input).println()

}

