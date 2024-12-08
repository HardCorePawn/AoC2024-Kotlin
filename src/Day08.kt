fun main() {

    //Traverse the board and create a Map containing the co-ords of each antenna type (char symbol)
    fun mapAntennas(board: List<String>): Map<Char, List<Pair<Int, Int>>> {
        val antennaMap = mutableMapOf<Char, MutableList<Pair<Int, Int>>>()

        board.forEachIndexed { rowIndex, s ->
            s.forEachIndexed { columnIndex, c ->
                if (c.isLetterOrDigit()) {
                    if (antennaMap[c] != null) {
                        antennaMap[c]!!.add(columnIndex to rowIndex)
                    } else {
                        antennaMap[c] = mutableListOf(columnIndex to rowIndex)
                    }
                }
            }
        }

        return antennaMap
    }

    //Creates a list of all the unique pairs of antennas (of a given type)
    fun mapAntennaPairs(antennaLocations: List<Pair<Int, Int>>): List<Pair<Pair<Int, Int>, Pair<Int, Int>>> {
        val antennaPairs = mutableListOf<Pair<Pair<Int, Int>, Pair<Int, Int>>>()

        for (i in antennaLocations.indices) {
            for (j in i + 1..antennaLocations.indices.last) {
                antennaPairs.add(antennaLocations[i] to antennaLocations[j])
            }
        }

        return antennaPairs
    }

    //Calculates the (x,y) delta between a pair of antennas
    fun calculatePairDelta(antennaPair: Pair<Pair<Int, Int>, Pair<Int, Int>>): Pair<Int, Int> {
        return (antennaPair.second.first - antennaPair.first.first) to (antennaPair.second.second - antennaPair.first.second)
    }

    //Inverts an (x,y) delta to (-x,-y)
    fun invertDelta(delta: Pair<Int, Int>): Pair<Int, Int> {
        return (delta.first * -1 to delta.second * -1)
    }

    //Checks if adding a given (x,y) delta to a given co-ord pair will remain within the bounds of the board
    fun withinBounds(pair: Pair<Int, Int>, delta: Pair<Int, Int>, maxRows: Int, maxColumns: Int): Boolean {
        return ((pair.first + delta.first in 0..maxRows)
                && (pair.second + delta.second in 0..maxColumns))
    }

    //calculates an antinode by combining a given (x,y) co-ord with a (x,y) delta
    fun calculateAntiNode(pair: Pair<Int, Int>, delta: Pair<Int, Int>): Pair<Int, Int> {
        return (pair.first + delta.first to pair.second + delta.second)
    }

    fun part1(input: List<String>): Int {
        // set of unique antiNode co-ords
        val antiNodes = mutableSetOf<Pair<Int, Int>>()

        // find all the antenna locations
        val antennaMap = mapAntennas(input)

        // dimensions of board
        val maxRows = input.indices.last
        val maxColumns = input[0].indices.last

        // foreach antenna type
        antennaMap.forEach { (_, value) ->
            // create all the unique pairs of antennas
            val pairs = mapAntennaPairs(value)
            pairs.forEach { pair ->
                // foreach unique pair, calculate delta and reverse delta
                val delta = calculatePairDelta(pair)
                val inverseDelta = invertDelta(delta)
                // if the antinode from second antenna is within bounds, add it to set of antinodes
                if (withinBounds(pair.second, delta, maxRows, maxColumns)) {
                    antiNodes.add(calculateAntiNode(pair.second, delta))
                }
                // if the antinode from first antenna is within bounds, add it to set of antinodes
                if (withinBounds(pair.first, inverseDelta, maxRows, maxColumns)) {
                    antiNodes.add(calculateAntiNode(pair.first, inverseDelta))
                }
            }
        }

        // return number of unique antinodes
        return antiNodes.size
    }

    fun part2(input: List<String>): Int {
        // set of unique antiNode co-ords
        val antiNodes = mutableSetOf<Pair<Int, Int>>()

        // find all the antenna locations
        val antennaMap = mapAntennas(input)

        val maxRows = input.indices.last
        val maxColumns = input[0].indices.last

        // foreach antenna type
        antennaMap.forEach { (_, value) ->
            // create all the unique pairs of antennas
            val pairs = mapAntennaPairs(value)
            pairs.forEach { pair ->
                val delta = calculatePairDelta(pair)
                val inverseDelta = invertDelta(delta)

                //starting from first antenna
                var nodeLoc = pair.first
                while (withinBounds(nodeLoc, delta, maxRows, maxColumns)) {
                    // while we are still within bounds of the board, create an antinode using appropriate delta
                    // and at it to the list
                    nodeLoc = calculateAntiNode(nodeLoc, delta)
                    antiNodes.add(nodeLoc)
                }
                //starting from second antenna
                nodeLoc = pair.second
                while (withinBounds(nodeLoc, inverseDelta, maxRows, maxColumns)) {
                    // while we are still within bounds of the board, create an antinode using appropriate delta
                    // and at it to the list
                    nodeLoc = calculateAntiNode(nodeLoc, inverseDelta)
                    antiNodes.add(nodeLoc)
                }
            }
        }

        // return number of unique antinodes
        return antiNodes.size
    }

    // Read the input from the `src/Day08_input.txt` file.
//    val input = readInput("Day08_sample")
    val input = readInput("Day08_input")

    part1(input).println()
    part2(input).println()

}