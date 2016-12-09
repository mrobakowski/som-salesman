import SomApp.Companion.numCitiesProp
import SomApp.Companion.numRingProp
import SomApp.Companion.runs
import SomApp.Companion.somThread
import SomApp.Companion.updates
import javafx.beans.property.Property
import javafx.geometry.Insets
import tornadofx.*

class SomApp : App() {
    override val primaryView = SomView::class
    companion object {
        lateinit var somThread: Thread

        val numCitiesProp = property(10L)
        val numRingProp = property(10L)
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