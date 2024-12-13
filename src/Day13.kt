fun main() {

    val aCost = 3L
    val bCost = 1L

    // buttonA = (X offset, Y offset)
    // buttonB = (X offset, Y offset)
    // prize = (x,y) location of prize
    data class ClawMachine(var buttonA: Pair<Long, Long>, var buttonB: Pair<Long, Long>, var prize: Pair<Long, Long>)

    fun parseInput(input: List<String>): List<ClawMachine> {
        val machines = mutableListOf<ClawMachine>()

        var buttonA = -1L to -1L
        var buttonB = -1L to -1L
        var prize: Pair<Long, Long>

        input.forEach { string ->
            if (string.startsWith("Button A")) {
                // parse Button A
                val xRegex = Regex("X\\+(\\d+), Y\\+(\\d+)")
                val match = xRegex.find(string)
                buttonA = match!!.groupValues[1].toLong() to match.groupValues[2].toLong()
            } else if (string.startsWith("Button B")) {
                // parse Button B
                val xRegex = Regex("X\\+(\\d+), Y\\+(\\d+)")
                val match = xRegex.find(string)
                buttonB = match!!.groupValues[1].toLong() to match.groupValues[2].toLong()
            } else if (string.startsWith("Prize")) {
                // parse prize location
                val xRegex = Regex("X=(\\d+), Y=(\\d+)")
                val match = xRegex.find(string)
                prize = match!!.groupValues[1].toLong() to match.groupValues[2].toLong()
                // add machine to list
                machines.add(ClawMachine(buttonA, buttonB, prize))
            } // ignore blank lines
        }

        return machines
    }

    // not entirely sure the check for cheapest is required
    fun part1(input: List<String>): Long {
        val machines = parseInput(input)

        var totalCost = 0L

        machines.forEach { machine ->

            var found = false

            var machineCost = 0L

            // simple brute force solution, loop through 100 presses of B for every press of A (max 100) and see
            // if we can find a matching combination that gives the prize location
            for (a in 0L..100L) {
                for (b in 0L..100L) {
                    if ((machine.buttonA.first * a + machine.buttonB.first * b == machine.prize.first) &&
                        (machine.buttonA.second * a + machine.buttonB.second * b == machine.prize.second)
                    ) {
                        // we got it!
                        found = true
                        if (machineCost == 0L) {
                            machineCost = (a * aCost) + (b * bCost)
                        } else {
                            if ((a * aCost) + (b * bCost) < machineCost) {
                                // just in case we find multiple solutions
                                // ensure we track the "cheapest" for this machine
                                machineCost = (a * aCost) + (b * bCost)
                            }
                        }
                    }
                }
            }
            if (found) {
                // we found at least 1 solution, so add "cheapest" to the running total
                totalCost += machineCost
            }
        }

        return totalCost
    }

    fun part2(input: List<String>): Long {
        val machines = parseInput(input)

        var totalCost = 0L

        // finally figured out this is 2 variable linear equations :P
        // effectively:
        // ButtonAX(x) + ButtonBX(y) = prizeX
        // ButtonAY(x) + ButtonBY(y) = prizeY
        // we just need to solve for x and y

        machines.forEach { machine ->

            val buttonAX = machine.buttonA.first
            val buttonAY = machine.buttonA.second

            val buttonBX = machine.buttonB.first
            val buttonBY = machine.buttonB.second

            // adjust locations to the stupidly large number
            val prizeX = machine.prize.first + 10000000000000
            val prizeY = machine.prize.second + 10000000000000

            // calculate the determinant
            //
            // | (de)  (ant) | = (de)(ter) - (min)(ant)
            // | (min) (ter) |
            //
            // | ButtonAX  ButtonBX | => (ButtonAX * ButtonBY) - (ButtonAY * ButtonBX)
            // | ButtonAY  ButtonBY |
            //
            val determinant = (buttonAX * buttonBY) -
                    (buttonAY * buttonBX)

            // Need to solve:
            //
            //    | PrizeX ButtonBX |      | ButtonAX PrizeX |
            // x= | PrizeY ButtonBY |   y= | ButtonAY PrizeY |
            //    -------------------      -------------------
            //        determinant              determinant
            //
            val topX = (prizeX * buttonBY) - (prizeY * buttonBX)
            val topY = (buttonAX * prizeY) - (buttonAY * prizeX)

            // Check tops are evenly divisible by determinant, otherwise there is no solution
            if (topX % determinant == 0L) {
                if (topY % determinant == 0L) {
                    // everything checks out, so we can solve for x & y
                    val x = topX / determinant
                    val y = topY / determinant

                    // multiply x & y by appropriate button costs and add to running total
                    totalCost += (x * aCost) + (y * bCost)
                }
            }
        }

        return totalCost
    }

    // Read the input from the `src/Day13_input.txt` file.
//    val input = readInput("Day13_sample")
    val input = readInput("Day13_input")
    part1(input).println()
    part2(input).println()
}
