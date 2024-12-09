fun main() {

    data class Block(var empty: Boolean, var fileIndex: Long?, var length: Int)

    lateinit var disk: Array<Long?>
    lateinit var blocks: MutableList<Block>

    var size = 0L

    // expand diskMap into rawFileSystem
    fun parseDiskMap(diskmap: String) {

        var fileId = 0L
        var diskIndex = 0

        //Total disk size required is sum of each digit in the raw file system
        diskmap.forEach { c ->
            size += c.digitToInt()
        }

        //initialise blank disk
        disk = Array(size.toInt()) { null }
        blocks = mutableListOf()

        diskmap.forEachIndexed { index, c ->
            when (index % 2) {
                0 -> {
                    // every even digit is a filelength
                    blocks.add(Block(empty = false, fileIndex = fileId, length = c.digitToInt()))
                    repeat(c.digitToInt()) {
                        disk[diskIndex] = fileId
                        diskIndex++
                    }
                    fileId++
                }

                1 -> {
                    // every odd digit is a free space counter
                    diskIndex += c.digitToInt()
                    blocks.add(Block(empty = true, fileIndex = null, length = c.digitToInt()))
                }
            }
        }
    }

    // moves blocks, one at a time, from right most nonEmpty to left most empty space
    fun compactFileSystem() {

        // first empty block is the first null element in disk
        var leftMostEmptyBlockIndex = disk.indexOfFirst { it == null }

        // last nonEmpty block is the last not null element in disk
        var rightMostNonEmptyBlockIndex = disk.indexOfLast { it != null }

        while (leftMostEmptyBlockIndex < rightMostNonEmptyBlockIndex) {
            // swap left most empty block with right most nonEmpty block
            disk[leftMostEmptyBlockIndex] = disk[rightMostNonEmptyBlockIndex]
            disk[rightMostNonEmptyBlockIndex] = null

            // recalculate left most empty and right most nonEmpty
            leftMostEmptyBlockIndex = disk.indexOfFirst { it == null }
            rightMostNonEmptyBlockIndex = disk.indexOfLast { it != null }
        }
    }

    // Checksum = sum of (block index * fileId) for nonEmpty blocks
    fun calcCheckSum(): Long {
        var checkSum: Long = 0

        disk.forEachIndexed { index, l ->
            if (l != null) {
                checkSum += index * l
            }
        }

        return checkSum
    }

    // build disk array from blocks
    fun createDiskFromBlocks(): Array<Long?> {
        //init disk array to null values
        val diskArray = Array<Long?>(size.toInt()) { null }
        var i = 0

        //loop through blocks writing file index into disk array
        blocks.forEach { block ->
            repeat(block.length) {
                if (!block.empty) {
                    diskArray[i] = block.fileIndex
                }
                i++
            }
        }
        return diskArray
    }

    fun part1(input: List<String>): Long {
        parseDiskMap(input[0])
        compactFileSystem()
        return calcCheckSum()
    }

    fun part2(): Long {
        // get all the nonEmpty blocks
        val files = blocks.filter { !it.empty }.toMutableList()

        repeat(files.size) {
            // see if we can move the last file in the list
            val fileToMove = files.removeLast()

            val blockIndex = blocks.indexOf(fileToMove)
            val emptyBlockIndex = blocks.indexOfFirst { it.empty && it.length >= fileToMove.length }

            if (emptyBlockIndex != -1 && emptyBlockIndex < blockIndex) {
                //found an empty block to the left that is big enough

                // make the original location of the block we're moving empty
                blocks[blockIndex] = fileToMove.copy(empty = true, fileIndex = null)

                val emptyBlock = blocks[emptyBlockIndex]
                if (emptyBlock.length == fileToMove.length) {
                    // nice and easy, dump block we're moving into same sized empty space
                    blocks[emptyBlockIndex] = fileToMove
                } else {
                    // put block at start of empty space, and then create a new empty block in the remaining space
                    val sizeDiff = emptyBlock.length - fileToMove.length
                    blocks[emptyBlockIndex] = fileToMove
                    blocks.add(emptyBlockIndex + 1, Block(empty = true, fileIndex = null, length = sizeDiff))
                }
            } // else, no empty block big enough, do nothing
        }

        disk = createDiskFromBlocks()
        return calcCheckSum()
    }

    // Read the input from the `src/Day09_input.txt` file.
//    val input = readInput("Day09_sample")
    val input = readInput("Day09_input")

    part1(input).println()
    part2().println()

}