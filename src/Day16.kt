import java.util.*

enum class Direction(val pair: Pair<Int, Int>) {
    UP(0 to -1),
    DOWN(0 to 1),
    LEFT(-1 to 0),
    RIGHT(1 to 0)
}

fun main() {

    // Time to give up on the Pair<Int, Int> ;)
    data class Point(val x: Int, val y: Int, val c: Char)

    // create a list of Points from the input strings
    fun parseInput(input: List<String>): List<Point> {
        val parsed = mutableListOf<Point>()

        input.forEachIndexed { row, line ->
            line.forEachIndexed { col, c ->
                parsed.add(Point(col, row, c))
            }
        }

        return parsed
    }

    // Gets a Map containing all the points and their parent nodes on shortest paths
    // Returns a Set of the Points on any of the shortest paths from start to end Point
    fun getParents(allPoints: MutableMap<Point, MutableList<Point>>, start: Point, end: Point): MutableSet<Point> {
        val uniquePoints = mutableSetOf<Point>()
        uniquePoints.add(end)

        // if we are back at the start, we're done tracing all the paths
        if (end == start) {
            uniquePoints.add(start)
            return uniquePoints
        }

        // beginning at the end point, recursively find all the parent nodes that form part of any shortest path
        allPoints[end]!!.forEach { parent ->
            uniquePoints.add(parent)
            uniquePoints.addAll(getParents(allPoints, start, parent))
        }

        return uniquePoints
    }

    // Implementation of Dijkstra's Algorithm for finding the shortest paths between a given start point and all other
    // points in the given graph.
    //
    // Notes:
    // Algorithm has been modified slightly, so that each point tracks all parent nodes which are on any shortest
    // path back to the start point. This enables us to retrace *all* the shortest paths from a given end point back
    // to the start (which was required for Part 2)
    //
    // There is also a dirty "hack" to modify the edge weight on the fly when we're "turning" through a crossroad
    // section, as it was not possible to account for these situations in the graph creation
    fun dijkstraWithLoopsAndPaths(graph: Map<Point, List<Pair<Point, Int>>>, start: Point) {
        val distances = mutableMapOf<Point, Int>().withDefault { Int.MAX_VALUE }
        val priorityQueue = PriorityQueue<Pair<Point, Int>>(compareBy { it.second })

        // need to know if we've visited this node before to prevent infinite looping (and help with multiple pathing)
        val visited = mutableSetOf<Pair<Point, Int>>()

        // track the parents, so we can identify actual path points, not just the total distance on the path
        val paths = mutableMapOf<Point, MutableList<Point>>()

        // we start searching from the 'S' point
        priorityQueue.add(start to 0)
        distances[start] = 0

        while (priorityQueue.isNotEmpty()) {
            val (node, currentDist) = priorityQueue.poll()
            if (visited.add(node to currentDist)) {
                graph[node]?.forEach { (adjacent, weight) ->
                    var adjustedWeight = weight
                    if (graph[node]?.size == 4) {
                        // funky handling of edge weight required when traversing a crossroad
                        // because I couldn't figure out how to include it in the actual graph :P
                        graph[node]?.forEach { (previous, _) ->
                            // find the adjacent node of the crossroad that we entered from
                            if (visited.find { it.first == previous } != null) {
                                if (previous.x != adjacent.x && previous.y != adjacent.y) {
                                    // if both x AND y co-ords of previous node and curr adjacent node are different,
                                    // we have "turned a corner", so adjust the weight of the edge accordingly
                                    adjustedWeight += 1000
                                }
                            }
                        }
                    }
                    val totalDist = currentDist + adjustedWeight
                    // if new dist <= existing distance to this adjacent node, we've found a (new) shortest path
                    if (totalDist <= distances.getValue(adjacent)) {
                        distances[adjacent] = totalDist
                        priorityQueue.add(adjacent to totalDist)

                        // Add node as a parent of adjacent node, so we can retrace our short paths
                        if (paths[adjacent] == null) {
                            paths[adjacent] = mutableListOf()
                        }
                        paths[adjacent]!!.add(node)
                    }
                }
            }
        }

        // we want the lowest score from 'S' point to 'E' point
        val end = graph.keys.find { it.c == 'E' }!!
        println("Lowest Score from ${start.c} to ${end.c}: ${distances[end]}")

        // This is Part 2 ;)
        // retrieve all the points that are on any of the shortest paths from 'S' to 'E'
        val uniqueShortestPoints = getParents(paths, start, end)

        println("Unique Tiles on Lowest Score Paths: ${uniqueShortestPoints.size}")
        // end of Part 2

    }

    // are we at a T-junction?
    fun isTJunction(point: Point, spaces: List<Point>): Boolean {
        var count = 0
        Direction.entries.forEach { dir ->
            val neighbour = spaces.find { it.x == point.x + dir.pair.first && it.y == point.y + dir.pair.second }
            if (neighbour != null) {
                count++
            }
        }
        // if we have 3 adjacent nodes, we're in a T Junction
        return count == 3
    }

    // could we continue in current direction or would we hit a wall?
    fun oneMoreStep(point: Point, dir: Direction, spaces: List<Point>): Boolean {
        val nextStep = spaces.find { it.x == point.x + dir.pair.first && it.y == point.y + dir.pair.second }
        return nextStep != null
    }

    // check to see if there was a wall behind us
    // Used when in a T Junction to figure out if we're crossing the T, or have turned
    fun onePreviousStep(point: Point, dir: Direction, spaces: List<Point>): Boolean {
        val previousDir = when (dir) {
            Direction.UP -> Direction.DOWN
            Direction.DOWN -> Direction.UP
            Direction.RIGHT -> Direction.LEFT
            Direction.LEFT -> Direction.RIGHT
        }
        val previousStep =
            spaces.find { it.x == point.x + previousDir.pair.first && it.y == point.y + previousDir.pair.second }
        return previousStep != null
    }

    // Builds a directed, weighted graph of all the points in the maze
    //
    // Note: I couldn't work out how to handle crossroads during graph creation, so added a hack in the
    // Dijkstra's Algorithm code (see dijkstraWithLoopsAndPaths() function) to adjust the edge weight on the fly
    // if "turning" at a crossroad
    fun buildGraph(board: List<Point>): Map<Point, List<Pair<Point, Int>>> {

        val graph = mutableMapOf<Point, List<Pair<Point, Int>>>()

        // only interested in non-wall points
        val spaces = board.filter { it.c == '.' || it.c == 'S' || it.c == 'E' }

        // for each point in the maze, get the neighbours, calculate the movement cost to that neighbour
        spaces.forEach { point ->
            val neighbours = mutableListOf<Pair<Point, Int>>()
            val isTee = isTJunction(point, spaces)
            Direction.entries.forEach { dir ->
                val neighbour = spaces.find { it.x == point.x + dir.pair.first && it.y == point.y + dir.pair.second }
                if (neighbour != null) {
                    if (point.c == 'S' && dir == Direction.UP) {
                        // assuming we always start lower left, if we're going UP, it involves the 90degree turn,
                        // so add the turn penalty of 1000 to the move
                        neighbours.add(neighbour to 1001)
                    } else if (neighbour.c != 'E' && !oneMoreStep(neighbour, dir, spaces)) {
                        // if we haven't found the End point, and we won't be able to move in the same direction
                        // next turn, we will need to turn,
                        // so add the turn penalty of 1000 to the move
                        neighbours.add(neighbour to 1001)
                    } else if (isTee && !onePreviousStep(point, dir, spaces)) {
                        // if we're at a T Junction, and we've not travelling across the top of the T, we've turned
                        // so add the turn penalty of 1000 to the move
                        neighbours.add(neighbour to 1001)
                    } else {
                        // otherwise, it's just a simple step (or crossroad which we'll "hack" later),
                        // add move weight of 1
                        neighbours.add(neighbour to 1)
                    }
                }
            }
            graph[point] = neighbours
        }

        return graph
    }

    // Read the input from the `src/Day16_input.txt` file.
//    val input = readInput("Day16_sample")
//    val input = readInput("Day16_sample2")
    val input = readInput("Day16_input")

    // convert the input strings to List of Points
    val parsed = parseInput(input)

    // build the weighted Graph of nodes
    val graph = buildGraph(parsed)

    // find all the shortest paths from the Start Point using Dijkstra's Algorithm
    val start = parsed.find { it.c == 'S' }
    dijkstraWithLoopsAndPaths(graph, start!!)

}