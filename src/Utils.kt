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
data class Point(val x: Int, val y: Int)

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

