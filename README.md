# Javions ADS-B Decoder Project

## Introduction

The Javions project is designed to enhance air traffic control by decoding ADS-B messages continuously broadcasted by various types of aircraft. These messages are transmitted over the 1090 MHz frequency and contain information such as identity, position, speed, and travel direction of the aircraft.

This project involves developing a Java application that decodes ADS-B messages received through a software-defined radio (SDR), specifically the AirSpy R2, and displays the corresponding aircraft on a map. The main goal is to provide a visual representation of aircraft data within the vicinity of Lausanne, using an optimally positioned antenna.

## Hardware Requirements

- **AirSpy R2 SDR**: This software-defined radio connects to both a computer and an antenna. It is set to the specified frequency to digitize the signals it receives.
- **Antenna**: Must be positioned with a clear line of sight to the sky, ideally on a rooftop, to ensure unobstructed reception.

## Software Requirements

- **Operating System**: Compatible with all major operating systems that support Java and JavaFX.
- **Java Development Environment**: Requires JavaFX and a suitable JDK to run and modify the program.

## Setup and Installation

1. **SDR Setup**: Connect the AirSpy R2 to your computer and antenna. Set it to the 1090 MHz frequency.
2. **Software Setup**: Clone this repository and navigate to the project directory:
    ```bash
    git clone https://github.com/ricardo-perello/javions.git
    cd javions
    ```
3. **Compile and Run the Program**: Make sure JavaFX is set up correctly, then compile and run the application:
    ```bash
    javac -cp "path/to/javafx-sdk/lib/*" ch/epfl/javions/gui/*.java
    java --module-path "path/to/javafx-sdk/lib" --add-modules javafx.controls,javafx.fxml -cp . ch.epfl.javions.gui.Main
    ```

Replace `"path/to/javafx-sdk/lib"` with the actual path to your JavaFX SDK library.


## Contributing

This project welcomes contributions, especially from students and aviation enthusiasts. For substantial changes, please open an issue first to discuss what you would like to change. Collaborations with [AleMerRom](https://github.com/AleMerRom) and other community members are highly encouraged.

## Acknowledgments

- Thanks to the contributors of ADS-B data collection sites like The OpenSky Network and ADSB Exchange.
- Special thanks to our instructors at EPFL and the broader JavaFX community for their support in developing this complex application.
