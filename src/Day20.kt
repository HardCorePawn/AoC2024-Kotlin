fun main() {

    // Finds all points on the path
    fun getPathPoints(input: List<String>): Map<Point, Char> {
        val path = mutableMapOf<Point, Char>()

        input.forEachIndexed { row, line ->
            line.forEachIndexed { col, c ->
                if (c == 'S' || c == '.' || c == 'E') {
                    path[Point(col, row)] = c
                }
            }
        }
        return path
    }

    // for a given point, find all the neighbouring points on the path
    fun getNeighbours(pathPoints: Map<Point, Char>, point: Point): List<Point> {
        val neighbours = mutableListOf<Point>()
        Dir.entries.forEach {
            if (pathPoints[Point(point.x + it.pair.first, point.y + it.pair.second)] != null) {
                neighbours.add(Point(point.x + it.pair.first, point.y + it.pair.second))
            }
        }
        return neighbours
    }

    // find next point on the path that we haven't visited
    fun getNextPoint(pathPoints: Map<Point, Char>, currPoint: Point, prevPoint: Point): Point {
        val neighbours = getNeighbours(pathPoints, currPoint)
        return neighbours.filter { it != prevPoint }[0]
    }

    // builds a graph to track the distances travelled for each point on the path
    fun createPathGraph(pathPoints: Map<Point, Char>): Map<Point, Int> {
        val graph = mutableMapOf<Point, Int>()

        // start at end and work backwards
        var currPoint = pathPoints.filterValues { it == 'S' }.keys.first()
        var dist = 0
        graph[currPoint] = dist++

        var prevPoint = Point(-1, -1)
        var nextPoint: Point
        do {
            nextPoint = getNextPoint(pathPoints, currPoint, prevPoint)
            graph[nextPoint] = dist++
            prevPoint = currPoint
            currPoint = nextPoint
        } while (pathPoints[currPoint] != 'E')

        return graph
    }

    // find points where we can take a shortcut that results in a minSave of steps
    // Essentially we check if we can move 2 steps in a direction and end up back on the path, with the first
    // step being through a wall
    // Then we check that the distance saved
    fun findShortCuts(point: Point, dist: Int, minSave: Int, pathDist: Map<Point, Int>): List<Pair<Point, Point>> {
        // return list of <start, end> Pairs for shortcuts
        return pathDist.filter {
            it.value < dist - minSave &&
                    (it.key.x == point.x && it.key.y == point.y - 2 && !pathDist.containsKey(
                        Point(
                            point.x,
                            point.y - 1
                        )
                    ) || // UP
                            it.key.x == point.x && it.key.y == point.y + 2 && !pathDist.containsKey(
                        Point(
                            point.x,
                            point.y + 1
                        )
                    ) || // DOWN
                            it.key.x == point.x - 2 && it.key.y == point.y && !pathDist.containsKey(
                        Point(
                            point.x - 1,
                            point.y
                        )
                    ) || // LEFT
                            it.key.x == point.x + 2 && it.key.y == point.y && !pathDist.containsKey(
                        Point(
                            point.x + 1,
                            point.y
                        )
                    ))
        }  // RIGHT
            .map { point to it.key }.toList()
    }

    // from a given point, find all the points on the path that are within a Manhatten Distance of 20
    // Then we check that the distance already travelled + cheat path length puts us on a point
    // that has the required amount of savings
    fun findLongShortCuts(point: Point, dist: Int, minSave: Int, pathDist: Map<Point, Int>): List<Pair<Point, Point>> {
        return pathDist.filter {
            getManhattenDist(it.key, point) <= 20
                    && dist + getManhattenDist(it.key, point) <= it.value - minSave
        }.map { point to it.key }.toList()
    }

    // Read the input from the `src/Day20_input.txt` file.
//    val input = readInput("Day20_test")
    val input = readInput("Day20")

    val pathPoints = getPathPoints(input)
    val pathDist = createPathGraph(pathPoints)

    val minSave = 100

    val shortcuts = mutableSetOf<Pair<Point, Point>>()
    pathDist.forEach { (point, dist) ->
        shortcuts += findShortCuts(point, dist, minSave, pathDist)
    }
    println(shortcuts.size)

    val longShortcuts = mutableSetOf<Pair<Point, Point>>()
    pathDist.forEach { (point, dist) ->
        longShortcuts += findLongShortCuts(point, dist, minSave, pathDist)
    }
    println(longShortcuts.size)
}