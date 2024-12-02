import kotlin.math.abs

fun main() {
    fun part1(input: List<String>): Int {
        // split into two lists
        val (left, right) = input.map { line ->
            val first = line.substringBefore(" ").toInt()
            val second = line.substringAfterLast(" ").toInt()
            first to second
        }.unzip()

        //sort lists and calc, then sum differences
        val result = left.sorted().zip(right.sorted()).map { (first, second) ->
            abs(first - second)
        }.sum()

        return result
    }

    fun part2(input: List<String>): Int {

        // split into two lists
        val (left, right) = input.map { line ->
            val first = line.substringBefore(" ").toInt()
            val second = line.substringAfterLast(" ").toInt()
            first to second
        }.unzip()

        var similarity = 0
        // for each value in left column, multiply by the number of times it appears in right column
        // then add that product to running similarity total
        left.forEach { num ->
            similarity += (num * right.count{it == num})
        }

        return similarity
    }

    // Read the input from the `src/Day01_input.txt` file.
    val input = readInput("Day01_input")
    part1(input).println()
    part2(input).println()
}
