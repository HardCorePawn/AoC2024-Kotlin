fun main() {

    /**
     * Calculates Monkey Exchange Market secret numbers
     * (Apologies to Phil Collins)
     * @param secretNum The current secret number
     * @returns the next Pseudorandom number in the sequence
     */
    fun pseuPseuPseudio(secretNum: Long): Long {
        val step1Mult = secretNum * 64
        val step1Mix = step1Mult xor secretNum
        val step1Prune = step1Mix % 16777216

        val step2Div = step1Prune.floorDiv(32)
        val step2Mix = step2Div xor step1Prune
        val step2Prune = step2Mix % 16777216

        val step3Mult = step2Prune * 2048
        val step3Mix = step3Mult xor step2Prune
        val step3Prune = step3Mix % 16777216

        return step3Prune
    }

    fun part1(input: List<String>): Long {
        var sum = 0L
        input.forEach { line ->
            var secNum = line.toLong()
            for (i in 0..<2000) {
                secNum = pseuPseuPseudio(secNum)
            }
            sum += secNum
        }
        return sum
    }

    fun getSequences(priceList: List<List<Int>>): List<Map<List<Int>, Int>> {
        val sequences = mutableListOf<MutableMap<List<Int>, Int>>()
        priceList.forEachIndexed { i, list ->
            sequences.add(mutableMapOf())
            list.forEachIndexed { j, price ->
                if (j >= 4) {
                    val sequence = listOf(
                        list[j - 3] - list[j - 4],
                        list[j - 2] - list[j - 3],
                        list[j - 1] - list[j - 2],
                        list[j] - list[j - 1]
                    )
                    if (!sequences[i].containsKey(sequence)) sequences[i][sequence] = price
                }
            }
        }
        return sequences
    }

    fun sequenceSums(sequences: List<Map<List<Int>, Int>>): Map<List<Int>, Int> {
        val sums = mutableMapOf<List<Int>, Int>()

        sequences.forEach { buyer ->
            buyer.forEach { (sequence, price) ->
                if (!sums.containsKey(sequence)) sums[sequence] = price
                else sums[sequence] = sums[sequence]!! + price
            }
        }

        return sums
    }

    fun part2(input: List<String>): Long {
        var sum = 0L
        val priceList = mutableListOf<MutableList<Int>>()
        input.forEachIndexed { index, line ->
            var secNum = line.toLong()
            priceList.add(mutableListOf())
            priceList[index].add((secNum % 10).toInt())
            for (i in 0..<2000) {
                secNum = pseuPseuPseudio(secNum)
                priceList[index].add((secNum % 10).toInt())
            }
            sum += secNum
        }
        val sequences = getSequences(priceList)
        val sums = sequenceSums(sequences)
        sum = sums.maxBy { it.value }.value.toLong()
        return sum
    }

    // Read the input from the `src/Day22_input.txt` file.
//    val input = readInput("Day22_test")
    val input = readInput("Day22")
    part1(input).println()
    part2(input).println()

}
