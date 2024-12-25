fun main() {

    data class Lock(val pins: MutableList<Int>)
    data class Key(val heights: MutableList<Int>)

    val locks = mutableListOf<Lock>()
    val keys = mutableListOf<Key>()

    fun parseInput(input: List<String>) {
        var inLock = false
        var lock = Lock(mutableListOf(0, 0, 0, 0, 0))
        var key = Key(mutableListOf(5, 5, 5, 5, 5))
        input.forEachIndexed { i, s ->
            if (i % 8 == 0) {
                // first line of block has no '.' == Lock, else Key.
                if (!s.contains(".")) {
                    inLock = true
                    lock = Lock(mutableListOf(0, 0, 0, 0, 0))
                } else {
                    key = Key(mutableListOf(5, 5, 5, 5, 5))
                }
            } else if (i % 8 == 6) {
                // reached the end, add the current lock or key to the appropriate list
                if (inLock) locks.add(lock)
                else keys.add(key)
            } else if (i % 8 == 7) {
                // reset key/lock flag for next block
                inLock = false
            } else {
                s.forEachIndexed { index, c ->
                    if (c == '#') {
                        // for locks, we increment the pin height if the current column has a #
                        if (inLock) lock.pins[index]++
                    } else {
                        // for keys, we decrement the column height it the current column has .
                        if (!inLock) key.heights[index]--
                    }
                }
            }
        }
    }

    fun part1(): Int {
        var count = 0
        var fit = true
        // check all keys against all locks. if the combined length of a given lock pin and column height is
        // greater than the available space (5), then the key won't fit
        locks.forEach { lock ->
            keys.forEach { key ->
                key.heights.forEachIndexed { k, height ->
                    if (height + lock.pins[k] > 5) fit = false
                }
                if (fit) count++
                fit = true
            }
        }
        return count // of key/lock combinations that work
    }

    // Read the input from the `src/Day25.txt` file.
//    val input = readInput("Day25_test")
    val input = readInput("Day25")
    parseInput(input)
    part1().println()
}