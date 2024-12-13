enum class Side {
    TOP, LEFT, BOTTOM, RIGHT
}

fun main() {

    // data class for each square on the board
    // Notes:
    // fences was originally an Int but converted to a List to make identifying "sides" easier
    data class Plot(var loc: Pair<Int, Int>, var type: String, var fences: MutableList<Boolean?>, var region: Int)

    // parse the input board into the individual plots
    // - including on which sides each plot has a fence, and what region it belongs to
    fun parseBoard(input: List<String>): MutableList<Plot> {

        // helper function to return all the neighbouring plots of a given plot
        fun getNeighbours(loc: Pair<Int, Int>, plots: List<Plot>): List<Plot?> {
            val neighbours = mutableListOf<Plot?>(null, null, null, null)

            neighbours[Side.TOP.ordinal] = plots.find { it.loc == loc.first to loc.second - 1 } // Top
            neighbours[Side.LEFT.ordinal] = plots.find { it.loc == loc.first - 1 to loc.second } // Left
            neighbours[Side.BOTTOM.ordinal] = plots.find { it.loc == loc.first to loc.second + 1 } // Bottom
            neighbours[Side.RIGHT.ordinal] = plots.find { it.loc == loc.first + 1 to loc.second } // Right

            return neighbours
        }

        // calc which sides, if any, plots have fences on
        // if no neighbour == fence
        // if neighbour is different type == fence
        fun calcFences(plots: MutableList<Plot>): MutableList<Plot> {

            plots.forEach { plot ->
                val neighbours = getNeighbours(plot.loc, plots)

                neighbours.forEachIndexed { index, neighbour ->
                    if (neighbour != null) {
                        // check type
                        if (plot.type != neighbour.type) {
                            // different plot type, need a fence
                            plot.fences[index] = true
                        }
                    } else {
                        // no neighbour, need a fence
                        plot.fences[index] = true
                    }
                }

            }

            return plots
        }

        // recursively finds and sets neighbouring plots of same type to same region as current plot
        fun setRegionForNeighbours(plot: Plot, neighbours: List<Plot?>, plots: MutableList<Plot>) {

            neighbours.forEach { neighbour ->
                if (neighbour != null) {
                    if (plot.type == neighbour.type && neighbour.region == -1) {
                        neighbour.region = plot.region
                        setRegionForNeighbours(neighbour, getNeighbours(neighbour.loc, plots), plots)
                    }
                }
            }
        }

        // create regions, starting at (0,0)
        fun calcRegions(plots: MutableList<Plot>): MutableList<Plot> {

            var currRegion = 0

            plots.forEach { plot ->
                val neighbours = getNeighbours(plot.loc, plots)

                if (plot.region == -1) plot.region = currRegion
                setRegionForNeighbours(plot, neighbours, plots)
                currRegion++
            }

            return plots
        }

        var plots = mutableListOf<Plot>()

        // add each plot to the board
        // set fences to null initially, and region to -1 to indicate it hasn't been calculated yet
        input.forEachIndexed { row, line ->
            line.forEachIndexed { column, plot ->
                plots.add(Plot(column to row, plot.toString(), mutableListOf(null, null, null, null), -1))
            }
        }

        // set fences for each plot
        plots = calcFences(plots)

        // set region for each plot
        calcRegions(plots)

        // return fully parsed plots
        return plots
    }

    // returns all plots that have a fence on the specified side
    fun getFences(region: List<Plot>, side: Side): List<Plot> {
        return region.filter { it.fences[side.ordinal] == true }
    }

    // Given a region, calculate the total number of sides it has
    // Start by grouping all plots in the region into "fence side" groups
    // for each side group, group by specific row (top/bottom) or specific column (left/right)
    // for each plot in a row/column group, check if the current plot is adjacent to previous plot, if not, new side
    // finally, return sum of all the sides
    fun calcRegionSides(region: List<Plot>): Int {

        var totalSides = 0

        // iterate through each side
        Side.entries.forEach { side ->
            val fences = getFences(region, side)

            val rowOrColumn: Map<Int, List<Plot>> = if (side.ordinal % 2 == 0) {
                // if top or bottom fences, group by row
                fences.groupBy { it.loc.second }
            } else {
                // if left or right fences, group by column
                fences.groupBy { it.loc.first }
            }

            rowOrColumn.forEach { (key, _) ->
                // if this group only has 1 member == 1 side
                if (rowOrColumn[key]!!.size == 1) {
                    totalSides++
                } else {
                    rowOrColumn[key]!!.forEachIndexed { index, plot ->
                        if (index == 0) {
                            // first member of the group, create a side
                            totalSides++
                        } else {
                            if (side.ordinal % 2 == 0) {
                                // if the plot is not adjacent to the previous plot, we have a new side
                                if (plot.loc.first - 1 != rowOrColumn[key]!![index - 1].loc.first) {
                                    totalSides++
                                }
                            } else {
                                // if the plot is not adjacent to the previous plot, we have a new side
                                if (plot.loc.second - 1 != rowOrColumn[key]!![index - 1].loc.second) {
                                    totalSides++
                                }
                            }

                        }
                    }
                }
            }
        }

        return totalSides
    }

    // get all the plots, group them into regions, then:
    // for part1: calculate total price for a region as (region area * # of fences)
    // for part2: calculate total price for a region as (region area * # of sides)
    // sum all the total from the regions and output
    fun calculateTotalPrices(input: List<String>) {
        var totalPricePart1 = 0
        var totalPricePart2 = 0

        // parse all the plots
        val plots = parseBoard(input)

        // group plots into their regions
        val regions = plots.groupBy { it.region }

        // for each region, calculate the price and add it to the total
        regions.forEach { (_, plots) ->
            // for part 1, Region price = area of region * # of fences in region
            totalPricePart1 += plots.sumOf { plot -> plot.fences.count { it != null } } * plots.size
            // for part 2, region price = area of region * # of sides of region
            totalPricePart2 += calcRegionSides(plots) * plots.size

        }

        // output totals
        println("Price1: $totalPricePart1")
        println("Price2: $totalPricePart2")
    }

    // Read the input from the `src/Day12_input.txt` file.
//    val input = readInput("Day12_sample")
    val input = readInput("Day12_input")
    calculateTotalPrices(input)

}
