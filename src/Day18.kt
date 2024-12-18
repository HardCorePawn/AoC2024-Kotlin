import java.util.*

fun main() {

    data class Point(val x: Int, val y: Int, var c: Char)

    // Sets up the initial board, returns it as a List of Point
    fun createBoard(input: List<String>, maxX: Int, maxY: Int, maxBytes: Int): List<Point> {
        val board = mutableListOf<Point>()

        // fill the board with empty space
        for (y in 0..maxY) {
            for (x in 0..maxX) {
                board.add(Point(x, y, '.'))
            }
        }

        // Take maxBytes number of entries from the input list,
        // and update the Points at those co-ords as being blocked
        input.forEachIndexed { index, line ->
            val coords = line.split(",").map { it.toInt() }
            if (index < maxBytes) board.find { it.x == coords[0] && it.y == coords[1] }.also { it!!.c = '#' }
        }

        return board
    }

    // For Debug purposes only
    fun outputBoard(board: List<Point>, maxX: Int, maxY: Int) {
        for (y in 0..maxY) {
            for (x in 0..maxX) {
                val point = board.find { it.x == x && it.y == y }
                print(point!!.c)
            }
            println()
        }
    }

    // Classic Dijkstra's Algorithm that allows for possible Loops
    // Returns the distance from given start Point to given end Point
    // If no valid path exists, returns null
    fun dijkstraWithLoops(graph: Map<Point, List<Pair<Point, Int>>>, start: Point, end: Point): Int? {
        val distances = mutableMapOf<Point, Int>().withDefault { Int.MAX_VALUE }
        val priorityQueue = PriorityQueue<Pair<Point, Int>>(compareBy { it.second })

        // need to know if we've visited this node before to prevent infinite looping (and help with multiple pathing)
        val visited = mutableSetOf<Pair<Point, Int>>()

        // we start searching from the given start Point
        priorityQueue.add(start to 0)
        distances[start] = 0

        while (priorityQueue.isNotEmpty()) {
            val (node, currentDist) = priorityQueue.poll()
            if (visited.add(node to currentDist)) {
                graph[node]?.forEach { (adjacent, weight) ->
                    val totalDist = currentDist + weight
                    // if new dist <= existing distance to this adjacent node, we've found a (new) shortest path
                    if (totalDist <= distances.getValue(adjacent)) {
                        distances[adjacent] = totalDist
                        priorityQueue.add(adjacent to totalDist)
                    }
                }
            }
        }

        return distances[end]
    }

    // build the graph for the current board layout, assume all edges have a weight of 1
    fun buildGraph(board: List<Point>): Map<Point, List<Pair<Point, Int>>> {

        val graph = mutableMapOf<Point, List<Pair<Point, Int>>>()

        // only interested in non-blocked spaces on the board
        val spaces = board.filter { it.c == '.' }

        // for each empty space, get the neighbours and add to graph
        spaces.forEach { point ->
            val neighbours = mutableListOf<Pair<Point, Int>>()
            Direction.entries.forEach { dir ->
                val neighbour = spaces.find { it.x == point.x + dir.pair.first && it.y == point.y + dir.pair.second }
                if (neighbour != null) {
                    neighbours.add(neighbour to 1)
                }
            }
            graph[point] = neighbours
        }

        return graph
    }

    fun part1(board: List<Point>, maxX: Int, maxY: Int) {

        val graph = buildGraph(board)

        // start and end are top/left and bottom/right, respectively
        val start = graph.keys.find { it.x == 0 && it.y == 0 }!!
        val end = graph.keys.find { it.x == maxX && it.y == maxY }!!

        // use a simple version of Dijkstra's Algorithm to calculate the shortest path from Start to End
        val dist = dijkstraWithLoops(graph, start, end)

        // we want the lowest score from given start point to given end point
        println("Lowest Score from $start to $end: $dist")
    }

    fun part2(board: MutableList<Point>, input: List<String>, maxX: Int, maxY: Int, maxBytes: Int) {

        val graph = buildGraph(board).toMutableMap()
        val start = graph.keys.find { it.x == 0 && it.y == 0 }!!
        val end = graph.keys.find { it.x == maxX && it.y == maxY }!!

        var currByte = maxBytes
        var coords = listOf<Int>()

        // While our Dijkstra's Algorithm can find a path from start to end, keep "dropping bytes" from the input list
        // We achieve this is an efficient way by simply removing the location from the node graph
        while (dijkstraWithLoops(graph, start, end) != null) {
            coords = input[currByte++].split(",").map { it.toInt() }
            graph.remove(Point(coords[0], coords[1], '.'))
        }

        // Output Co-ord's that finally blocked the path from start to end
        println("No Path after: ${coords[0]},${coords[1]}")
    }

    // Part 1 Example
//    val maxX = 6
//    val maxY = 6
//    val maxBytes = 12
//    val input = readInput("Day18_sample")

    // Part 1 Input
    val maxX = 70
    val maxY = 70
    val maxBytes = 1024
    val input = readInput("Day18_input")

    val board = createBoard(input, maxX, maxY, maxBytes)

    // DEBUG
    //outputBoard(board, maxX, maxY)

    part1(board, maxX, maxY)
    part2(board.toMutableList(), input, maxX, maxY, maxBytes)
}