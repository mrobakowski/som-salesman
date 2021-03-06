import golem.*
import golem.matrix.Matrix
import java.util.*

open class Som(val cols: Int, val rows: Int, val weightDim: Int) {
    val nodes = ArrayList<ArrayList<Node>>(rows).apply {
        for (row in 0..rows - 1)
            add(ArrayList<Node>(cols).apply {
                for (col in 0..cols - 1) {
                    val angle = (2 * PI / cols) * col
                    val x = cos(angle)
                    val y = sin(angle)

                    add(Node(mat[x, y] * 0.05, col to row))
                }
            })
    }

    operator fun get(col: Int, row: Int): Node {
        return nodes[row][col]
    }

    open fun dist(a: Pair<Int, Int>, b: Pair<Int, Int>): Double {
        val (x1, y1) = a
        val (x2, y2) = b
        val dx = x1 - x2
        val dy = y1 - y2
        return sqrt(dx * dx + dy * dy)
    }

    fun update(input: Matrix<Double>, iteration: Int) {
        val bestNode: Node = findBest(input)
        updateNeighbourhood(input, bestNode, iteration)
    }

    private fun updateNeighbourhood(input: Matrix<Double>, bestNode: Node, iteration: Int) {
        val bestPos = bestNode.pos
        for (row in 0..rows - 1) {
            for (col in 0..cols - 1) {
                val node = this[col, row]
                val distFromBest = dist(bestPos, node.pos)
                node.weights += (input - node.weights) * neighbourhood(distFromBest, iteration) * learningRate(iteration)
            }
        }
    }

    private fun learningRate(iteration: Int): Double {
        return min(0.8 / max(sqrt(iteration)/20, 1), 0.1)
    }

    private fun neighbourhood(distFromBest: Double, iteration: Int): Double {
        val sigma = SomApp.neighbourhoodMultiplier*max(exp(-iteration/1000.0), 0.2)
        val mu = 0.0
        return 1 / (sqrt(2 * sigma * sigma * PI)) * exp(-(pow(distFromBest - mu, 2)) / (2 * sigma * sigma))
    }

    private fun findBest(input: Matrix<Double>): Node {
        var bestNode: Node = this[0, 0]
        var bestDist = Double.POSITIVE_INFINITY
        for (row in 0..rows - 1) {
            for (col in 0..cols - 1) {
                val node = this[col, row]
                val d = node.weights - input
                val dist = sqrt(dot(d, d))
                if (dist < bestDist) {
                    bestNode = node
                    bestDist = dist
                }
            }
        }
        return bestNode
    }
}

class RingSom(size: Int, weightDim: Int) : Som(size, 1, weightDim) {
    override fun dist(a: Pair<Int, Int>, b: Pair<Int, Int>): Double {
        return dist(a.first, b.first)
    }

    operator fun get(i: Int) = get(i, 0)

    fun dist(a: Int, b: Int): Double {
        val min: Int
        val max: Int
        if (a > b) {
            max = a; min = b
        } else {
            max = b; min = a
        }

        return min(max - min, cols + min - max).toDouble()
    }
}

data class Node(var weights: Matrix<Double>, val pos: Pair<Int, Int>)