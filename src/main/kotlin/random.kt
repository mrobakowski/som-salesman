import golem.matrix.Matrix
import java.util.*

private val r = Random()
fun random(i: Int): Matrix<Double> {
    return golem.rand(1, i, r.nextLong())
}