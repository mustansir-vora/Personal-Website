# GPS & GSM Based Vehicle Tracking System

This repository contains the software and hardware details for a GPS and GSM-based vehicle tracking system. This project was developed as a part of a Bachelor of Engineering degree in Electronics and Telecommunication Engineering.

## Introduction

The project's objective is to build a low-cost, real-time location tracking device that can be attached to any vehicle or object. The system leverages a combination of a GPS module for accurate location data and a GSM module for transmitting this data to a mobile phone via SMS. The core of the system is an Arduino UNO microcontroller, which orchestrates the communication between these components.

## Features

* **Real-time Location Tracking:** Provides the current latitude and longitude of the tracking device.
* **SMS Notification:** Sends location data to a registered mobile number via SMS.
* **Google Maps Integration:** The SMS message includes a direct link to the location on Google Maps for easy viewing.
* **Cost-Effective Solution:** Built with readily available and affordable components.

## Components Required

### Hardware
* **Arduino UNO:** The main microcontroller board.
* **SIM900A GSM Module:** For cellular communication and sending SMS messages.
* **NEO-6M GPS Module:** For receiving satellite signals and obtaining location data.
* **5V Power Supply:** For powering the SIM900A GSM module.
* **Jumper Wires**
* **SIM Card:** A working SIM card with SMS service enabled.

### Software
* **Arduino IDE:** The development environment for writing and uploading code.
* **TinyGPS++ Library:** An Arduino library to simplify parsing data from the GPS module.

## Hardware Setup

The following connections must be made to interface the components with the Arduino UNO.

1.  **SIM900A GSM Module Connections:**
    * `VCC` to a **5V power supply**.
    * `GND` to **Arduino GND** and **5V power supply GND**.
    * `RXD` to **Arduino Digital Pin 7**.
    * `TXD` to **Arduino Digital Pin 8**.

2.  **NEO-6M GPS Module Connections:**
    * `VCC` to **Arduino 5V**.
    * `GND` to **Arduino GND**.
    * `TXD` to **Arduino Digital Pin 3**.
    * `RXD` to **Arduino Digital Pin 2**.

## Software Setup

1.  **Install the Arduino IDE:** Download and install the latest version from the official website.
2.  **Install the TinyGPS++ Library:**
    * Open the Arduino IDE.
    * Go to `Sketch` -> `Include Library` -> `Manage Libraries...`.
    * Search for "TinyGPS++" and install the library by Mikal Hart.
3.  **Upload the Code:** Copy the code into the Arduino IDE, connect your Arduino UNO, and upload the sketch.

## How It Works

1.  **Initialization:** Upon startup, the Arduino UNO initializes the serial communication with the GPS and GSM modules. It sends AT commands to the GSM module to prepare it for sending text messages.
2.  **GPS Data Acquisition:** The Arduino continuously reads data from the GPS module. The TinyGPS++ library is used to parse the raw NMEA data stream and extract meaningful information, such as latitude and longitude.
3.  **SMS Transmission:** Once a valid GPS location is acquired, the Arduino compiles an SMS message that includes a Google Maps link with the coordinates. It then uses the GSM module to send this message to the pre-configured mobile number.
4.  **Repeat:** The system waits for a set interval (3 seconds) before sending the next location update, providing a near-real-time tracking solution.