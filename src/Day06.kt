fun main() {

    fun findObjects(board: List<String>, objectToFind: String): MutableSet<Pair<Int, Int>> {
        val objectsFound = mutableSetOf<Pair<Int, Int>>()

        // iterate through board, line by line, identifying all obstacles and the starting position
        board.forEachIndexed { rowIndex, line ->
            line.forEachIndexed { columnIndex, currLoc ->
                if (currLoc.toString() == objectToFind) {
                    objectsFound.add(columnIndex to rowIndex)
                }
            }
        }
        return objectsFound
    }

    fun turnRight(currDir: Pair<Int, Int>, dirs: List<Pair<Int, Int>>): Pair<Int, Int> {
        // move to next direction in array (looping back around to 0 position as necessary)
        val index = (dirs.indexOf(currDir) + 1) % dirs.size
        return dirs[index]
    }

    fun nextPos(currPos: Pair<Int, Int>, dir: Pair<Int, Int>): Pair<Int, Int> {
        //use curr dir offset, to calculate next position
        return (currPos.first + dir.first) to (currPos.second + dir.second)
    }

    fun part1(input: List<String>): Int {

        // board size
        val rows = input.indices.last()
        val columns = input[0].indices.last()

        // movement direction offsets
        val dirs = listOf(
            0 to -1, // up
            1 to 0, // right
            0 to 1, // down
            -1 to 0 // left
        )
        var currDir = dirs[0] // assuming the guard is always moving up to start

        val obstacles = findObjects(input, "#")
        val initialPos = findObjects(input, "^") // assuming guard is always moving up to start

        var currPos = initialPos.first()

        val visitedPos = mutableSetOf(currPos) // unique locations visited on the walk

        // Stop when the guard moves off the board
        while (currPos.first in 0..columns && currPos.second in 0..rows) {
            visitedPos += currPos // add new visited location

            // use position and direction delta to calculate next move
            val next = nextPos(currPos, currDir)
            if (next in obstacles) {
                // next location is an obstacle, change direction
                currDir = turnRight(currDir, dirs)
            } else {
                // move to next location
                currPos = next
            }
        }

        //return the unique number of visited locations
        return visitedPos.size
    }

    fun part2(input: List<String>): Int {

        // board size
        val rows = input.indices.last()
        val columns = input[0].indices.last()

        val obstacles = findObjects(input, "#")
        val initialPos = findObjects(input, "^").first()

        // movement direction offsets
        val dirs = listOf(
            0 to -1, // up
            1 to 0, // right
            0 to 1, // down
            -1 to 0 // left
        )
        var currDir: Pair<Int, Int>

        // iterate through board, line by line, adding a new obstacle in each location,
        // then check to see if we have generated a loop
        var loopCount = 0
        for (testX in 0..rows) {
            for (testY in 0..columns) {
                val newObstacles = obstacles + (testX to testY)
                var currPos = initialPos
                currDir = dirs[0] // assuming the guard is always moving up to start

                //Need to track each visited location as: Pair(currLoc, Direction)
                // This will tell us if we are looping
                val visitedPos = mutableSetOf<Pair<Pair<Int, Int>, Pair<Int, Int>>>()

                // check until guard moves off board
                while (currPos.first in 0..columns && currPos.second in 0..rows) {

                    //have we been in this location, moving in the same direction before?
                    if (currPos to currDir in visitedPos) {
                        //If so, we found a loop, count it and move to next Y location
                        loopCount++
                        break
                    }

                    visitedPos += currPos to currDir // add new visited location

                    // use position and direction delta to calculate next move
                    val next = nextPos(currPos, currDir)
                    if (next in newObstacles) {
                        // next location is an obstacle, change direction
                        currDir = turnRight(currDir, dirs)
                    } else {
                        // move to next location
                        currPos = next
                    }
                }
            }
        }

        // return number of times we found a loop
        return loopCount
    }

    // Read the input from the `src/Day06_input.txt` file.
//    val input = readInput("Day06_sample")
    val input = readInput("Day06_input")

    part1(input).println()
    part2(input).println()
}

