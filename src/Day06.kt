fun main() {

    // Read the input from the `src/Day06_input.txt` file.
    val input = readInput("Day06_sample")
//    val input = readInput("Day06_input")

    val obstacles = mutableSetOf<Pair<Int, Int>>()
    lateinit var initialPos: Pair<Int, Int>

    // board size
    val rows = input.indices.last()
    val columns = input[0].indices.last()

    // movement direction offsets
    val dirs = listOf<Pair<Int, Int>>(
        0 to -1, // up
        1 to 0, // right
        0 to 1, // down
        -1 to 0 // left
    )

    var dir = dirs[0]

    // iterate through board, line by line, identifying all obstacles and the starting position
    input.forEachIndexed { rowIndex, line ->
        line.forEachIndexed { columnIndex, currLoc ->
            if (currLoc == '#') {
                obstacles.add(columnIndex to rowIndex)
            } else if (currLoc == '^') {
                initialPos = columnIndex to rowIndex
            }
        }
    }

    var currPos = initialPos

    fun turnRight() {
        // move to next direction in array (looping back to 0)
        val index = (dirs.indexOf(dir) + 1) % dirs.size
        dir = dirs[index]
    }

    fun nextPos(): Pair<Int, Int> {
        //use curr dir offset, to calculate next position
        return (currPos.first + dir.first) to (currPos.second + dir.second)
    }

    fun stepForward() {
        //update position
        currPos = nextPos()
    }

    val visitedPos = mutableSetOf(currPos)
    while (currPos.first in 0..columns && currPos.second in 0..rows) {
        visitedPos += currPos

        val next = nextPos()
        if (next in obstacles) {
            turnRight()
        } else {
            stepForward()
        }
    }
    println("Part 1 Unique locations: ${visitedPos.size}")

}

