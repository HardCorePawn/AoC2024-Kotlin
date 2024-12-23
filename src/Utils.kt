import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.math.abs

/**
 * Enum to represent movement direction offsets from current location when traversing a 2D grid
 */
enum class Dir(val pair: Pair<Int, Int>) {
    UP(0 to -1),
    DOWN(0 to 1),
    LEFT(-1 to 0),
    RIGHT(1 to 0)
}

/**
 * Class to represent a (x,y) Point in a 2D grid (because Pair<Int, Int> gets messy :P)
 */
data class Point(val x: Int, val y: Int) {
    operator fun plus(second: Point) = Point(x + second.x, y + second.y)
    operator fun minus(second: Point) = Point(x - second.x, y - second.y)
    operator fun times(multiplier: Int) = Point(x * multiplier, y * multiplier)

    fun manhattanDistTo(dest: Point) = abs(x - dest.x) + abs(y - dest.y)
}

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readText().trim().lines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)


/**
 * Convert String of space delimited numbers to List<Int>
 */
fun stringToList(inString: String): List<Int> {
    val strings = inString.split(" ")
    return strings.map { it.toInt() }
}

/**
 * Calculates the Manhattan Distance between two given points in a 2D grid
 * Given points (x1, y1) and (x2, y2), then Manhatten Distance = Abs(x1 - x2) + Abs(y1 - y2)
 */
fun getManhattenDist(point1: Point, point2: Point): Int {
    return abs(point1.x - point2.x) + abs(point1.y - point2.y)
}

/**
 * Represents a graph node
 *
 * @property value The value of the node.
 * @property neighbours The list of neighbouring nodes.
 */
data class Node<T>(val value: T, val neighbours: MutableList<Node<T>> = mutableListOf())

/**
 * Represents a graph
 *
 * @property nodes The list of nodes in the graph
 */
class Graph<T> {
    val nodes: MutableList<Node<T>> = mutableListOf()

    /**
     * Adds a node to the graph.
     *
     * @param value The value of the node.
     * @return The newly added node.
     */
    fun addNode(value: T): Node<T> {
        val node = Node(value)
        nodes.add(node)
        return node
    }

    /**
     * Adds an edge between two nodes in the graph.
     *
     * @param node1 The first node.
     * @param node2 The second node
     */
    fun addEdge(node1: Node<T>, node2: Node<T>) {
        node1.neighbours.add(node2)
        node2.neighbours.add(node1)
    }

    /**
     * Finds the shortest path between two nodes in the graph using breadth-first search (BFS)
     *
     * @param start The starting node.
     * @param end The ending node.
     * @return The shortest path as a list of nodes, or an empty list if no path exists
     */
    fun findShortestPath(start: Node<T>, end: Node<T>): List<Node<T>> {
        val queue = ArrayDeque<Node<T>>()
        val visited = mutableSetOf<Node<T>>()
        val parentMap = mutableMapOf<Node<T>, Node<T>?>()

        queue.add(start)
        visited.add(start)
        parentMap[start] = null

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()

            if (current == end) {
                // Reconstruct the path from end to start using the parent map
                val path = mutableListOf<Node<T>>()
                var node: Node<T>? = current
                while (node != null) {
                    path.add(node)
                    node = parentMap[node]
                }
                return path.reversed()
            }

            for (neighbour in current.neighbours) {
                if (neighbour !in visited) {
                    queue.add(neighbour)
                    visited.add(neighbour)
                    parentMap[neighbour] = current
                }
            }
        }

        // no path found :(
        return emptyList()
    }
}