import SomApp.Companion.iterationProp
import SomApp.Companion.neighbourhoodMultiplierProp
import SomApp.Companion.numCitiesProp
import SomApp.Companion.numRingProp
import SomApp.Companion.runs
import SomApp.Companion.somThread
import SomApp.Companion.updates
import javafx.beans.property.Property
import javafx.geometry.Insets
import javafx.util.StringConverter
import tornadofx.*

class SomApp : App() {
    init {
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            throwable.printStackTrace()
        }
    }
    override val primaryView = SomView::class
    companion object {
        lateinit var somThread: Thread

        val numCitiesProp = property(10L)
        val numRingProp = property(20L)
        val iterationProp = property(0L)
        val neighbourhoodMultiplierProp = property(2.0)
        val neighbourhoodMultiplier by neighbourhoodMultiplierProp
        var iteration by iterationProp
        var numCities by numCitiesProp
        var numRing by numRingProp
        var runs = true
        var updates = true
        var toSkip = 0
    }
}

class SomView : View() {
    init {
        title = "SOM Controls"

        somThread = Thread {
            run()
        }.apply { start() }

        primaryStage.setOnCloseRequest {
            runs = false
            somThread.join()
            closeFig()
        }
    }

    val defaultWidth = 500.0

    override val root = vbox {
        form {
            fieldset {
                label {
                    textProperty().bindBidirectional(iterationProp.fxProperty, object : StringConverter<Long>() {
                        override fun fromString(string: String?): Long {
                            throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                        override fun toString(`object`: Long?): String {
                            return `object`.toString()
                        }
                    })
                }

                spacing = 10.0
                button("Reset") {
                    setOnAction {
                        restart()
                    }
                    prefWidth = defaultWidth
                    padding = Insets(20.0)

                }

                button("Skip 200 epochs") {
                    setOnAction { SomApp.toSkip += 200 }
                    prefWidth = defaultWidth
                    padding = Insets(20.0)
                }

                field("Number of Cities") {
                    textfield(numCitiesProp.fxProperty as Property<Number>)
                }

                field("Number of som nodes") {
                    textfield(numRingProp.fxProperty as Property<Number>)
                }

                field("Neighbourhood size multiplier") {
                    textfield {
                        textProperty().bindBidirectional(neighbourhoodMultiplierProp.fxProperty, object : StringConverter<Double>(){
                            override fun fromString(string: String?): Double {
                                return string?.toDouble() ?: 0.0
                            }

                            override fun toString(`object`: Double?): String {
                                return `object`.toString()
                            }
                        })
                    }
                }

                button("Pause") {
                    prefWidth = defaultWidth
                    padding = Insets(20.0)
                    setOnAction {
                        if (updates) {
                            updates = false
                            this.text = "Start"
                        } else {
                            updates = true
                            this.text = "Pause"
                        }
                    }
                }
            }
        }
    }
}

fun restart() {
    runs = false
    somThread.join()
    runs = true
    somThread = Thread {
        run()
    }
    somThread.start()
}