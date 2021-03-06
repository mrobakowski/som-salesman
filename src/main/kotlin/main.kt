import golem.matrix.Matrix
import golem.plot
import golem.title
import javafx.application.Platform
import java.util.*


fun run() {
    with(SomApp) {
        val som = RingSom(numRing.toInt(), 2)

        val cities = ArrayList<Matrix<Double>>(numCities.toInt()).apply {
            for (_c in 0..numCities - 1)
                add(random(2))
        }

        val citiesXs = cities.map { it[0] }
        val citiesYs = cities.map { it[1] }

        Platform.runLater { SomApp.iteration = 1 }
        var iteration = 1L
        while (runs) {
            if(updates) {
                remSeries("Route", "City")
                figureEx(0)
                val (somXs, somYs) = somCoords(som)
                plot(citiesXs.toDoubleArray(), citiesYs.toDoubleArray(), "b", lineLabel = "City")
                plot(somXs.toDoubleArray(), somYs.toDoubleArray(), "r", lineLabel = "Route")

                scatter("City")
                title("travelling salesman")

                Collections.shuffle(cities)
                for (city in cities) {
                    som.update(city, iteration.toInt())
                }

                while (toSkip > 0) {
                    Collections.shuffle(cities)
                    for (city in cities) {
                        som.update(city, iteration.toInt())
                    }
                    iteration++
                    toSkip--
                }
                iteration++
                val i = iteration
                Platform.runLater { SomApp.iteration = i }
            }
            Thread.sleep(100)
        }
    }
}

fun somCoords(som: RingSom): Pair<List<Double>, List<Double>> {
    val xs = ArrayList<Double>(som.cols + 1)
    val ys = ArrayList<Double>(som.cols + 1)
    for (i in 0..som.cols - 1) {
        xs += som[i].weights[0]
        ys += som[i].weights[1]
    }
    xs += som[0].weights[0]
    ys += som[0].weights[1]

    return xs to ys
}
