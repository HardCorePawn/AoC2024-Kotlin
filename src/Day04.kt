fun main() {

    fun createBoard(input: List<String>): Array<CharArray> {
        return Array(input.size) { index -> input[index].toCharArray() }
    }

    fun validCoord(i: Int, j: Int, m: IntRange, n: IntRange): Boolean {
        return i in m && j in n
    }

    fun countCorners(i: Int, j: Int, board: Array<CharArray>): IntArray {
        val charCounts = intArrayOf(0, 0)

        //top-left, top-right, bottom-right, bottom-left
        val dirX = intArrayOf(-1, -1, 1, 1)
        val dirY = intArrayOf(-1, 1, 1, -1)

        //Check each corner and count M's and S's
        for (index in 0..3) {
            if (validCoord(i + dirX[index], j + dirY[index], board[0].indices, board.indices)) {
                // exactly 2x M's and 2x S's make an X-MAS, but they can't be in:
                // M S    S M
                // S M or M S arrangement
                // so check we have a proper X-MAS, by making sure the opposite corner is NOT the same
                if ((index >= 2)
                    && (board[i + dirX[index]][j + dirY[index]] == board[i + dirX[index - 2]][j + dirY[index - 2]])
                ) {
                    return charCounts
                }
                when (board[i + dirX[index]][j + dirY[index]]) {
                    'M' -> charCounts[0]++
                    'S' -> charCounts[1]++
                }
            } else {
                return charCounts
            }
        }

        return charCounts
    }

    fun findWord(
        index: Int, word: String, board: Array<CharArray>,
        x: Int, y: Int, dirX: Int, dirY: Int
    ): Boolean {

        // found the whole word
        if (index == word.length) return true

        if (validCoord(x, y, board[0].indices, board.indices)
            && word[index] == board[x][y]
        ) {
            return findWord(index + 1, word, board, x + dirX, y + dirY, dirX, dirY)
        }

        return false
    }

    fun findXMAS(x: Int, y: Int, board: Array<CharArray>): Boolean {
        var cornerCount = intArrayOf(0, 0)

        //Is this the middle of a potential X-MAS?
        if (board[x][y] == 'A') {
            //if so, check the corners
            cornerCount = countCorners(x, y, board)
        }

        // exactly 2x M's and 2x S's make an X-MAS
        if (cornerCount[0] == 2 && cornerCount[1] == 2) {
            return true
        }

        return false
    }

    fun part1(input: List<String>): Int {
        var safeCount = 0

        // convert input into an array of CharArray's
        val board = createBoard(input)

        val word = "XMAS"

        // search directions up-right, right, down-right, up, down, up-left, left, down-left
        val x = intArrayOf(1, 1, 1, 0, 0, -1, -1, -1)
        val y = intArrayOf(-1, 0, 1, -1, 1, -1, 0, 1)

        // m = width index Range, n = height index Range of board
        val m = board[0].indices  // assuming all rows are equal length
        val n = board.indices

        for (i in m) {
            for (j in n) {
                for (k in x.indices) {
                    if (findWord(0, word, board, i, j, x[k], y[k])) {
                        safeCount++
                    }
                }
            }
        }

        return safeCount
    }

    fun part2(input: List<String>): Int {
        var safeCount = 0

        // convert input into an array of CharArray's
        val board = createBoard(input)

        // m = width index Range, n = height index Range of board
        val m = board[0].indices  // assuming all rows are equal length
        val n = board.indices

        for (i in m) {
            for (j in n) {
                if (findXMAS(i, j, board)) {
                    safeCount++
                }
            }
        }
        return safeCount
    }

    // Read the input from the `src/Day03_input.txt` file.
//    val input = readInput("Day04_sample")
    val input = readInput("Day04_input")
    part1(input).println()
    part2(input).println()
}
