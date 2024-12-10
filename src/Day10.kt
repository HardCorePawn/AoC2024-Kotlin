fun main() {

    // finds every (x,y) location for a given height
    fun mapHeights(board: List<String>, height: Int): Set<Pair<Int, Int>> {
        val heightList = mutableSetOf<Pair<Int, Int>>()

        board.forEachIndexed { rowIndex, s ->
            s.forEachIndexed { colIndex, c ->
                if (c.digitToInt() == height) {
                    heightList.add(colIndex to rowIndex)
                }
            }
        }

        return heightList
    }

    // finds every reachable unique (x,y) location at the next height
    fun findNextSteps(
        currHeightList: Set<Pair<Int, Int>>,
        heightMap: Map<Int, Set<Pair<Int, Int>>>,
        currHeight: Int
    ): MutableSet<Pair<Int, Int>> {

        val nextSteps = mutableSetOf<Pair<Int, Int>>()

        // movement direction offsets
        val dirs = listOf(
            0 to -1, // up
            1 to 0, // right
            0 to 1, // down
            -1 to 0 // left
        )

        currHeightList.forEach { currLoc ->
            dirs.forEach { offset ->
                val nextPos = currLoc.first + offset.first to currLoc.second + offset.second
                if (heightMap[currHeight]!!.contains(nextPos)) {
                    nextSteps.add(nextPos)
                }
            }
        }

        return nextSteps
    }

    // finds each (x,y) location that can be reached from previous steps
    // Note: non-unique locations are included
    fun findPaths(
        currHeightList: List<Pair<Int, Int>>,
        heightMap: Map<Int, Set<Pair<Int, Int>>>,
        currHeight: Int
    ): MutableList<Pair<Int, Int>> {

        val nextSteps = mutableListOf<Pair<Int, Int>>()

        // movement direction offsets
        val dirs = listOf(
            0 to -1, // up
            1 to 0, // right
            0 to 1, // down
            -1 to 0 // left
        )

        currHeightList.forEach { currLoc ->
            dirs.forEach { offset ->
                val nextPos = currLoc.first + offset.first to currLoc.second + offset.second
                if (heightMap[currHeight]!!.contains(nextPos)) {
                    nextSteps.add(nextPos)
                }
            }
        }

        return nextSteps
    }

    fun part1(input: List<String>): Int {
        val heights = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)

        // map unique locations at each height
        val heightMap = mutableMapOf<Int, Set<Pair<Int, Int>>>()
        heights.forEach {
            heightMap[it] = mapHeights(input, it)
        }

        val trailHeadPeaks = mutableMapOf<Pair<Int, Int>, MutableSet<Pair<Int, Int>>>()

        var nextSteps: Set<Pair<Int, Int>>

        // for each trailhead, find each unique peak that can be reached
        heightMap[0]!!.forEach { trailHead ->
            var currHeight = 0
            nextSteps = setOf(trailHead)
            while (++currHeight <= 9) {
                nextSteps = findNextSteps(nextSteps, heightMap, currHeight)
            }
            trailHeadPeaks[trailHead] = nextSteps.toMutableSet()
        }

        var score = 0
        trailHeadPeaks.forEach { (_, value) ->
            score += value.size
        }
        return score
    }

    fun part2(input: List<String>): Int {
        val heights = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)

        // map unique locations at each height
        val heightMap = mutableMapOf<Int, Set<Pair<Int, Int>>>()
        heights.forEach {
            heightMap[it] = mapHeights(input, it)
        }

        val trailHeadPaths = mutableMapOf<Pair<Int, Int>, MutableList<Pair<Int, Int>>>()

        var nextSteps: List<Pair<Int, Int>>

        // for each trailhead, find all the unique paths to reachable peaks
        heightMap[0]!!.forEach { trailHead ->
            var currHeight = 0
            nextSteps = listOf(trailHead)
            while (++currHeight <= 9) {
                nextSteps = findPaths(nextSteps, heightMap, currHeight)
            }
            trailHeadPaths[trailHead] = nextSteps.toMutableList()
        }

        var rating = 0
        trailHeadPaths.forEach { (_, value) ->
            rating += value.size
        }
        return rating
    }

    // Read the input from the `src/Day10_input.txt` file.
//    val input = readInput("Day10_sample")
    val input = readInput("Day10_input")
    part1(input).println()
    part2(input).println()

}
