package container

import org.testcontainers.containers.GenericContainer

abstract class ContainerWrapper {
    abstract val container: Container
    abstract val originalPort: OriginalPortProperty

    inner class Container(imageName: String) : GenericContainer<Container>(imageName) {
        var isContainerRunning = false

        override fun start() {
            if (isContainerRunning.not()) {
                println("Starting Container...")

                super.start()
                isContainerRunning = true
                println("Container started with port ${container.getMappedPort(originalPort.portNumber)}")
            } else {
                println("Container wasn't started because it is already running")
            }
            setDynamicPortsInProperty()
        }

        override fun stop() {
            if (isContainerRunning) {
                println("Stopping Container...")

                super.stop()
                isContainerRunning = false
            } else {
                println("Container wasn't stopped because it is not running")
            }
            setEmptyPortInProperty()
        }

        fun setDynamicPortsInProperty() {
            System.setProperty(
                originalPort.propertyName,
                container.getMappedPort(originalPort.portNumber).toString()
            )
        }

        fun setEmptyPortInProperty() {
            System.setProperty(originalPort.propertyName, "")
        }
    }
}
