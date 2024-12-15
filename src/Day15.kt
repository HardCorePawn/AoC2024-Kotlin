enum class SquareType(val char: Char) {
    WALL('#'),
    BOX('O'),
    ROBOT('@'),
    EMPTY('.'),
    BOXLEFT('['),
    BOXRIGHT(']')
}

enum class Dir(val pair: Pair<Int, Int>) {
    UP(0 to -1),
    DOWN(0 to 1),
    LEFT(-1 to 0),
    RIGHT(1 to 0)
}

fun main() {

    // convert character to SquareType
    fun getType(c: Char): SquareType {
        return when (c) {
            '#' -> SquareType.WALL
            'O' -> SquareType.BOX
            '@' -> SquareType.ROBOT
            else -> SquareType.EMPTY
        }
    }

    // convert movement character to Dir
    fun getDir(c: Char): Dir {
        return when (c) {
            '^' -> Dir.UP
            'v' -> Dir.DOWN
            '<' -> Dir.LEFT
            else -> Dir.RIGHT
        }
    }

    fun parseInput(input: List<String>): Pair<MutableMap<Pair<Int, Int>, SquareType>, List<Dir>> {

        val board = mutableMapOf<Pair<Int, Int>, SquareType>()
        val moves = mutableListOf<Dir>()

        input.forEachIndexed { row, line ->
            if (line.contains('#')) {
                // initial board layout line
                line.forEachIndexed { col, square ->
                    board[col to row] = getType(square)
                }
            } else if (line.contains('v') || line.contains('^') ||
                line.contains('<') || line.contains('>')
            ) {
                // line containing movement instructions
                line.forEach { move ->
                    moves.add(getDir(move))
                }
            }
        }

        return board to moves
    }

    // calculate the pos of the next square using the given direction of movement
    fun nextSquare(pos: Pair<Int, Int>, dir: Dir): Pair<Int, Int> {
        return pos.first + dir.pair.first to pos.second + dir.pair.second
    }

    // recursive function to see if the box we're pushing can move in the direction we're trying to go
    fun boxCanMove(boxPos: Pair<Int, Int>, board: MutableMap<Pair<Int, Int>, SquareType>, dir: Dir): Boolean {

        val nextSquarePos = nextSquare(boxPos, dir)

        return when (board[nextSquarePos]) {
            SquareType.EMPTY -> true // space is empty, so box can move into it
            SquareType.BOX -> boxCanMove(nextSquarePos, board, dir) // check if next box can be moved
            else -> false // we've hit a wall
        }
    }

    // recursive function to see if the "Big" box we're pushing can move in the direction we're trying to go
    fun bigBoxCanMove(boxPos: Pair<Int, Int>, board: MutableMap<Pair<Int, Int>, SquareType>, dir: Dir): Boolean {

        var canMove = false

        // are we pushing on the left side of the box?
        if (board[boxPos] == SquareType.BOXLEFT) {
            val nextSquarePos = nextSquare(boxPos, dir)
            when (dir) {
                Dir.UP, Dir.DOWN -> {
                    val nextBoxRightPos = nextSquarePos.first + 1 to nextSquarePos.second
                    if (board[nextSquarePos] == SquareType.EMPTY &&
                        board[nextBoxRightPos] == SquareType.EMPTY
                    ) {
                        // both sides of the box are moving into empty space, we're good.
                        canMove = true
                    } else {
                        canMove =
                            if (board[nextSquarePos] == SquareType.WALL || board[nextBoxRightPos] == SquareType.WALL) {
                                // box is blocked on one side or the other by a wall
                                false
                            } else if (board[nextSquarePos] == SquareType.EMPTY) {
                                // right side, is against a box, see if that box can move
                                bigBoxCanMove(nextBoxRightPos, board, dir)
                            } else if (board[nextBoxRightPos] == SquareType.EMPTY) {
                                // left side, is against a box, see if that box can move
                                bigBoxCanMove(nextSquarePos, board, dir)
                            } else {
                                // both sides are against a box, see if the box(es) can be moved
                                bigBoxCanMove(nextBoxRightPos, board, dir) &&
                                        bigBoxCanMove(nextSquarePos, board, dir)
                            }
                    }
                }

                Dir.LEFT -> {
                    canMove = if (board[nextSquarePos] == SquareType.EMPTY) {
                        // nothing to the left of the box, so move
                        true
                    } else if (board[nextSquarePos] == SquareType.BOXRIGHT) {
                        // see if the box next to us can move
                        bigBoxCanMove(nextSquarePos, board, dir)
                    } else {
                        false
                    }
                }

                Dir.RIGHT -> {
                    canMove = if (board[nextSquarePos] == SquareType.BOXRIGHT) {
                        // move to the right side of the box and see if it can be moved
                        bigBoxCanMove(nextSquarePos, board, dir)
                    } else {
                        false
                    }
                }
            }
        } else { //SquareType.BOXRIGHT
            // or on the right side of the box?
            val nextSquarePos = nextSquare(boxPos, dir)
            when (dir) {
                Dir.UP, Dir.DOWN -> {
                    val nextBoxLeftPos = nextSquarePos.first - 1 to nextSquarePos.second
                    if (board[nextSquarePos] == SquareType.EMPTY &&
                        board[nextBoxLeftPos] == SquareType.EMPTY
                    ) {
                        // both sides of the box are moving into empty space, we're good.
                        canMove = true
                    } else {
                        canMove =
                            if (board[nextSquarePos] == SquareType.WALL || board[nextBoxLeftPos] == SquareType.WALL) {
                                // box is blocked on one side or the other by a wall
                                false
                            } else if (board[nextSquarePos] == SquareType.EMPTY) {
                                // left side, is against a box, see if that box can move
                                bigBoxCanMove(nextBoxLeftPos, board, dir)
                            } else if (board[nextBoxLeftPos] == SquareType.EMPTY) {
                                // right side, is against a box, see if that box can move
                                bigBoxCanMove(nextSquarePos, board, dir)
                            } else {
                                // both sides are against a box, see if the box(es) can be moved
                                bigBoxCanMove(nextBoxLeftPos, board, dir) &&
                                        bigBoxCanMove(nextSquarePos, board, dir)
                            }
                    }
                }

                Dir.LEFT -> {
                    canMove = if (board[nextSquarePos] == SquareType.BOXLEFT) {
                        // move to the left side of the box and see if it can be moved
                        bigBoxCanMove(nextSquarePos, board, dir)
                    } else {
                        false
                    }
                }

                Dir.RIGHT -> {
                    canMove = if (board[nextSquarePos] == SquareType.EMPTY) {
                        // nothing to the right of the box, so move
                        true
                    } else if (board[nextSquarePos] == SquareType.BOXLEFT) {
                        // see if the box next to us can move
                        bigBoxCanMove(nextSquarePos, board, dir)
                    } else {
                        false
                    }
                }
            }
        }

        return canMove
    }

    // Recursive function to move 1 space boxes
    fun moveBox(boxPos: Pair<Int, Int>, board: MutableMap<Pair<Int, Int>, SquareType>, dir: Dir) {

        val nextSquarePos = nextSquare(boxPos, dir)
        if (board[nextSquarePos] == SquareType.EMPTY) {
            // empty space, move the box
            board[nextSquarePos] = SquareType.BOX
            board[boxPos] = SquareType.EMPTY
        } else if (board[nextSquarePos] == SquareType.BOX) {
            // move the box next to us first
            moveBox(nextSquarePos, board, dir)
            // then move the box
            board[nextSquarePos] = SquareType.BOX
            board[boxPos] = SquareType.EMPTY
        }
    }

    // Recursive function to move 2 space boxes
    fun moveBigBox(boxPos: Pair<Int, Int>, board: MutableMap<Pair<Int, Int>, SquareType>, dir: Dir) {
        val boxType = board[boxPos]
        val nextSquarePos = nextSquare(boxPos, dir)

        // This section will move both box parts when pushing left and right
        if (board[nextSquarePos] == SquareType.EMPTY) {
            // empty space, move the box part
            board[nextSquarePos] = boxType!!
            board[boxPos] = SquareType.EMPTY
        } else if (board[nextSquarePos] == SquareType.BOXLEFT || board[nextSquarePos] == SquareType.BOXRIGHT) {
            // move the box part next to us first
            moveBigBox(nextSquarePos, board, dir)
            // then move the current box part
            board[nextSquarePos] = boxType!!
            board[boxPos] = SquareType.EMPTY
        }

        // But we need some special handling when pushing up or down, so the other half of the box moves
        if (dir == Dir.UP || dir == Dir.DOWN) {
            val otherHalfType = if (boxType == SquareType.BOXLEFT) {
                SquareType.BOXRIGHT
            } else {
                SquareType.BOXLEFT
            }

            val otherHalfPos = if (boxType == SquareType.BOXLEFT) {
                boxPos.first + 1 to boxPos.second
            } else {
                boxPos.first - 1 to boxPos.second
            }

            val otherHalfNext = nextSquare(otherHalfPos, dir)

            if (board[otherHalfNext] == SquareType.EMPTY) {
                // empty space above/below the other half of box, so easy move
                board[otherHalfNext] = otherHalfType
                board[otherHalfPos] = SquareType.EMPTY
            } else if (board[otherHalfNext] == SquareType.BOXLEFT || board[otherHalfNext] == SquareType.BOXRIGHT) {
                // there is a box above/below the other half of the box
                // move that new box first
                moveBigBox(otherHalfNext, board, dir)
                // then we can move the other half of the box
                board[otherHalfNext] = otherHalfType
                board[otherHalfPos] = SquareType.EMPTY
            }
        }
    }

    // Outputs the given board
    // Useful as a "Visual Debugging Tool" ;)
    fun displayBoard(board: MutableMap<Pair<Int, Int>, SquareType>) {
        var prevRow = 0

        board.forEach { (pos, value) ->
            if (pos.second != prevRow) {
                println()
                prevRow = pos.second
            }
            print(value.char)
        }
        println()
        println()
    }

    // Calculates the total sum of GPS co-ords for Boxes
    //
    // "The GPS coordinate of a box is equal to 100 times its distance from the top edge of the map plus its
    // distance from the left edge of the map. (This process does not stop at wall tiles; measure all the way
    // to the edges of the map.)"
    fun calcSumGPSCoords(board: MutableMap<Pair<Int, Int>, SquareType>, boxType: SquareType): Int {
        var sum = 0

        // get just the box objects we are interested in counting
        val boxes = board.filterValues { it == boxType }

        boxes.forEach { (key, _) ->
            sum += key.second * 100 + key.first
        }

        return sum
    }

    fun part1(board: MutableMap<Pair<Int, Int>, SquareType>, moves: List<Dir>): Int {

        moves.forEach { move ->
            val robotSquare = board.filterValues { it == SquareType.ROBOT }.keys.first()
            val nextSquarePos = nextSquare(robotSquare, move)

            if (board[nextSquarePos] == SquareType.EMPTY) {
                // move the robot
                board[nextSquarePos] = SquareType.ROBOT
                board[robotSquare] = SquareType.EMPTY
            } else if (board[nextSquarePos] == SquareType.BOX) {
                // see if box can move
                if (boxCanMove(nextSquarePos, board, move)) {
                    //move box(es)
                    moveBox(nextSquarePos, board, move)
                    //move robot
                    board[nextSquarePos] = SquareType.ROBOT
                    board[robotSquare] = SquareType.EMPTY
                }
            }
        }
        displayBoard(board)

        return calcSumGPSCoords(board, SquareType.BOX)
    }

    // Takes the standard board after Input parsing, and expands all the walls, boxes and empty spaces into 2 spaces
    // NOTE: Boxes now become BOXLEFT and BOXRIGHT objects
    // Robot stays as single space, and an extra empty space is put next to it.
    fun expandBoard(board: MutableMap<Pair<Int, Int>, SquareType>): MutableMap<Pair<Int, Int>, SquareType> {
        val expandedBoard = mutableMapOf<Pair<Int, Int>, SquareType>()

        var prevRow = 0
        var col = 0
        board.forEach { (pos, value) ->
            if (pos.second != prevRow) {
                // found a new row, reset col count
                prevRow = pos.second
                col = 0
            }
            when (value) {
                SquareType.WALL -> {
                    expandedBoard[col++ to prevRow] = SquareType.WALL
                    expandedBoard[col++ to prevRow] = SquareType.WALL
                }

                SquareType.EMPTY -> {
                    expandedBoard[col++ to prevRow] = SquareType.EMPTY
                    expandedBoard[col++ to prevRow] = SquareType.EMPTY
                }

                SquareType.BOX -> {
                    // BOX becomes BOXLEFT and BOXRIGHT
                    expandedBoard[col++ to prevRow] = SquareType.BOXLEFT
                    expandedBoard[col++ to prevRow] = SquareType.BOXRIGHT
                }

                SquareType.ROBOT -> {
                    // ROBOT becomes ROBOT + EMPTY SPACE
                    expandedBoard[col++ to prevRow] = SquareType.ROBOT
                    expandedBoard[col++ to prevRow] = SquareType.EMPTY
                }

                else -> {}
            }
        }
        return expandedBoard
    }

    fun part2(expandedBoard: MutableMap<Pair<Int, Int>, SquareType>, moves: List<Dir>): Int {

        moves.forEach { move ->
            val robotSquare = expandedBoard.filterValues { it == SquareType.ROBOT }.keys.first()
            val nextSquarePos = nextSquare(robotSquare, move)

            if (expandedBoard[nextSquarePos] == SquareType.EMPTY) {
                // move the robot
                expandedBoard[nextSquarePos] = SquareType.ROBOT
                expandedBoard[robotSquare] = SquareType.EMPTY
            } else if (expandedBoard[nextSquarePos] == SquareType.BOXLEFT ||
                expandedBoard[nextSquarePos] == SquareType.BOXRIGHT
            ) {
                // see if box can move
                if (bigBoxCanMove(nextSquarePos, expandedBoard, move)) {
                    //move box(es)
                    moveBigBox(nextSquarePos, expandedBoard, move)
                    //move robot
                    expandedBoard[nextSquarePos] = SquareType.ROBOT
                    expandedBoard[robotSquare] = SquareType.EMPTY
                }
            }
        }
        displayBoard(expandedBoard)

        // NOTE: calculate the GPS co-ords using the left side of the box
        return calcSumGPSCoords(expandedBoard, SquareType.BOXLEFT)
    }

    // Read the input from the `src/Day15_input.txt` file.
//    val input = readInput("Day15_sample")
//    val input = readInput("Day15_sampleBig")
    var input = readInput("Day15_input")

    var parsed = parseInput(input)
    part1(parsed.first, parsed.second).println()

    input = readInput("Day15_input")
    parsed = parseInput(input)
    val expanded = expandBoard(parsed.first)
    part2(expanded, parsed.second).println()
}
