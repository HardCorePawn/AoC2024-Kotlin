fun main() {

    // pos = (x,y) position of Robot
    // move = (x,y) movement vector of Robot
    class Robot(var pos: Pair<Int, Int>, var move: Pair<Int, Int>) {

        // update the Robot's (x,y) position using it's (x,y) movement vector
        fun move(maxX: Int, maxY: Int) {
            var newX = pos.first + move.first
            var newY = pos.second + move.second

            if (newX < 0) {
                newX += maxX
            } else if (newX >= maxX) {
                newX -= maxX
            }
            if (newY < 0) {
                newY += maxY
            } else if (newY >= maxY) {
                newY -= maxY
            }

            this.pos = pos.copy(first = newX, second = newY)
        }
    }

    // Use a regex to parse each robots (x,y) position and (x,y) movement vector
    fun parseInput(input: List<String>): List<Robot> {
        val robots = mutableListOf<Robot>()

        input.forEach { line ->
            val botRegex = Regex("p=(\\d+),(\\d+).v=(-?\\d+),(-?\\d+)")
            val match = botRegex.find(line)

            val pos = match!!.groupValues[1].toInt() to match.groupValues[2].toInt()
            val move = match.groupValues[3].toInt() to match.groupValues[4].toInt()

            robots.add(Robot(pos, move))
        }

        return robots
    }

    // generates a map of the board of current robot locations
    fun displayBoard(robots: List<Robot>, maxX: Int, maxY: Int) {
        val board = mutableMapOf<Pair<Int, Int>, Char>()

        // fill the board with '.'s
        for (y in 0..<maxY) {
            for (x in 0..<maxX) {
                board[x to y] = '.'
            }
        }

        // mark each robot location with a '#'
        robots.forEach { robot ->
            board[robot.pos] = '#'
        }

        // print the board
        for (y in 0..<maxY) {
            for (x in 0..<maxX) {
                print(board[x to y])
            }
            println()
        }

    }

    fun part1(robots: List<Robot>): Int {

        // set max board size
        val maxX = 101 //11
        val maxY = 103 //7

        // find the middle row and column so we can ignore them
        val midX = (maxX - 1) / 2
        val midY = (maxY - 1) / 2

        // move each robot 100 times
        robots.forEach { robot ->
            repeat(100) { robot.move(maxX, maxY) }
        }

        // create the quadrant lists
        val firstQuad = robots.filter { it.pos.first < midX && it.pos.second < midY }
        val secondQuad = robots.filter { it.pos.first > midX && it.pos.second < midY }
        val thirdQuad = robots.filter { it.pos.first < midX && it.pos.second > midY }
        val fourthQuad = robots.filter { it.pos.first > midX && it.pos.second > midY }

        // return the total safety factor by multiplying all the quadrant counts
        return firstQuad.size * secondQuad.size * thirdQuad.size * fourthQuad.size
    }

    fun part2(robots: List<Robot>): Int {

        // max board size
        val maxX = 101 //11
        val maxY = 103 //7

        var seconds = 0

        while (true) {
            // move each robot
            robots.forEach { robot ->
                robot.move(maxX, maxY)
            }
            // time passes...
            seconds++

            // We continue until there are no positions occupied by multiple robots
            // This should mean the robots have moved into tree position
            if (robots.size == robots.distinctBy { it.pos }.size) break
        }

        // output the pretty picture
        displayBoard(robots, maxX, maxY)

        // return the number of seconds it took for the robots to align
        return seconds
    }

    // Read the input from the `src/Day14_input.txt` file.
//    val input = readInput("Day14_sample")
    val input = readInput("Day14_input")

    var robots = parseInput(input)
    part1(robots).println()

    robots = parseInput(input)
    part2(robots).println()
}
