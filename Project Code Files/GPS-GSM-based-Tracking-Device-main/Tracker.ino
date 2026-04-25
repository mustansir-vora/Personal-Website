#include <SoftwareSerial.h>
#include <TinyGPS++.h>

// GPS Module Connections
SoftwareSerial gpsSerial(2, 3);
TinyGPSPlus gps;

// GSM Module Connections
SoftwareSerial gsmSerial(7, 8);

void setup() {
    Serial.begin(9600);
    gpsSerial.begin(9600);
    gsmSerial.begin(9600);
    delay(100);
    
    // Initializing the GSM module
    Serial.println("Initializing GSM...");
    delay(1000);
    gsmSerial.println("AT"); // AT command for module check
    delay(1000);
    gsmSerial.println("AT+CMGF=1"); // Set SMS to text mode
    delay(1000);
    gsmSerial.println("AT+CNMI=2,2,0,0,0"); // Enable new message indication
    delay(1000);
    
    Serial.println("GSM initialized. Waiting for GPS signal...");
}

void loop() {
    while (gpsSerial.available() > 0) {
        if (gps.encode(gpsSerial.read())) {
            // New GPS data available
            if (gps.location.isValid()) {
                sendSMS();
                delay(3000); // Wait for 30 seconds before sending another SMS
            }
        }
    }
}

void sendSMS() {
    // Replace with your own mobile number
    String recipientNumber = "+919876543210"; 
    
    String smsMessage = "Current Location: ";
    smsMessage += "[http://maps.google.com/maps?q=](http://maps.google.com/maps?q=)";
    smsMessage += gps.location.lat();
    smsMessage += ",";
    smsMessage += gps.location.lng();
    
    // Send SMS via GSM module
    gsmSerial.println("AT+CMGS=\"" + recipientNumber + "\"");
    delay(1000);
    gsmSerial.println(smsMessage);
    delay(100);
    gsmSerial.println((char)26); // End of SMS message
    delay(3000);
    
    Serial.println("SMS sent with location: " + smsMessage);
}