# FARMERS_CLAIMS_PROJECT

## Project Overview
The FARMERS_CLAIMS_PROJECT is a solution designed to enable event-driven communication between Cisco Webex Contact Center Enterprise (Webex CCE) and Google Dialogflow platforms. The project consists of three main sub-projects that work together to provide integration and communication between these platforms.

This middleware application acts as a bridge between the two platforms, facilitating seamless interaction by processing events and managing session data. It handles user interactions via an element called VirtualAgentVoice (VAV) in the callflow, and notifies Dialogflow of session termination events such as user hangup using the Detect Intent API through the FarmersAPIClient.

## Projects in the Workspace

### 1. FARMERS_CLAIMS_CVP
- **Description:** This is the Cisco CVP (Customer Voice Portal) application responsible for managing call flows and event-driven communication within the Cisco Webex CCE environment.
- **Key Files and Directories:**
  - `app.callflow`: Main call flow configuration file.
  - `callflow/`: Directory containing call flow pages, elements, groups, and hot events.
  - `deploy/`: Contains deployment scripts and Java utilities related to the CVP application.
- **Role:** Handles the telephony call flows, event handling, and interaction logic within the Cisco CVP environment. The VirtualAgentVoice element in the callflow acts as the interface to Dialogflow, passing session and event data.

### 2. FARMERS_CLAIMS_JAVA
- **Description:** This is the Java backend application that supports the CVP project by providing business logic, configuration management, and event processing.
- **Key Files and Directories:**
  - `src/com/farmers/`: Contains Java source code organized by functionality such as DialogFlow integration, call start/end handling, utilities, and global event handling.
  - `lib/`: Contains required Java libraries and dependencies.
  - `bin/`: Compiled Java classes.
- **Role:** Manages application startup, logging configuration, event processing, and integration logic that supports the CVP call flows. Key classes include:
  - `AppStart`: Initializes application properties and logging.
  - `CallStart`: Sets up call session variables and configuration at call start.
  - `CheckEventData`: Processes events received from Dialogflow via VirtualAgentVoice, handling language changes, agent handoff, session ID exchange, and disconnect events.
  - `CallEnd`: Handles call termination events, detects user hangup, DR Events, and triggers the Detect Intent API call to notify Dialogflow of session end.

### 3. FarmersAPIClient
- **Description:** This Java project acts as the API client responsible for communicating with Google Dialogflow and other external services.
- **Key Files and Directories:**
  - `src/main/java/com/farmers/APIUtil/`: Utility classes for encryption, token management, and constants.
  - `src/main/java/com/farmers/DialogFlowAPI/`: Classes handling HTTPS communication with Dialogflow and token storage.
  - `src/main/resources/`: Configuration files including `config.properties` and logging configuration.
- **Role:** Facilitates secure and efficient communication with Google Dialogflow APIs, handling authentication, request formatting, and response processing. It manages OAuth2 token caching and sends event notifications such as user hangup to Dialogflow using the Detect Intent API.

## How the Projects Work Together

- The **FARMERS_CLAIMS_CVP** project manages the telephony call flows and event-driven interactions within Cisco Webex CCE and Google Dialogflow.
- The VirtualAgentVoice element in the callflow passes session and event data to the between WebexCCE and Google's DialogFlow.
- The **FARMERS_CLAIMS_JAVA** backend processes these events, applies business logic, manages session variables, and determines callflow transitions.
- When a call ends due to user hangup events, the backend triggers the Detect Intent API call via the **FarmersAPIClient** to notify Dialogflow that the session has been terminated on the client side.
- Additionally, when a Hotevent or error occurs, the backend triggers the Detect Intent API call via the same **FarmersAPIClient** to notify Dialogflow that the call has been sent to DR menu.
- This event-driven architecture ensures seamless integration and communication between Cisco Webex CCE and Google Dialogflow platforms.

## Project Setup

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- Apache Maven (for building FarmersAPIClient)
- Cisco Webex Contact Center Enterprise environment
- Google Cloud Platform project with Dialogflow CX agent configured
- Proper configuration files for logging and application properties

### Building and Deployment
1. Build the FarmersAPIClient project using Maven:
   ```bash
   cd FarmersAPIClient
   mvn clean install
   ```
2. Deploy the FARMERS_CLAIMS_CVP callflow and configuration files to the Cisco CVP environment.
3. Deploy the FARMERS_CLAIMS_JAVA compiled classes and libraries to the appropriate Java runtime environment used by the CVP.
4. Ensure configuration files such as `config.properties` and `log4j2.xml` are correctly set up with project-specific values including Dialogflow project ID, agent ID, endpoints, and authentication details.
5. Start the CVP application and verify logging to ensure proper initialization.

## Key Components Explanation

### VirtualAgentVoice (VAV)
- A callflow voice element that interfaces with Dialogflow.
- Passes session data such as ANI, DNIS, language, call IDs, and event data.
- Used to send and receive events and data between Cisco CVP and Dialogflow.

### CheckEventData
- A decision element in the Java backend that parses event data from VirtualAgentVoice.
- Handles different event types including language change, agent handoff, session ID exchange, and IVR disconnect.
- Sets session variables and determines callflow exit states based on events.

### CallStart and AppStart
- Initialize call session variables and application properties.
- Configure logging and load necessary configuration for the middleware.

### CallEnd
- Detects call termination events such as user hangup.
- Constructs the Dialogflow Detect Intent API request URL with session and agent details.
- Invokes the Detect Intent API via FarmersAPIClient to notify Dialogflow of session termination.
- When a call ends due to user hangup (disconnect), the Detect Intent API is invoked to trigger the 'CALLER_DISCONNECT' event in the Dialogflow agent for the current conversation.
- Additionally, when there is an error or hot event and the DR flag is set to 'Y', the Detect Intent API is invoked to trigger the 'DR_Event' in the Dialogflow agent for the current conversation. This behavior ensures that Dialogflow is notified not only of user disconnects but also of specific error conditions requiring special handling.

### FarmersAPIClient
- Manages OAuth2 token generation and caching for Dialogflow API authentication.
- Sends HTTPS POST requests to Dialogflow Detect Intent API with event parameters.
- Handles response parsing, error logging as needed.

## Logging and Configuration
- Uses Apache Log4j2 for logging with configuration loaded at application start.
- Configuration files include application properties, Dialogflow agent details, API endpoints, and timeout settings.
- Session and application data are managed via properties and session variables in the callflow and Java backend.

## Summary
This multi-project workspace provides a robust framework for integrating Cisco Webex Contact Center Enterprise with Google Dialogflow using event-driven communication. The middleware ensures seamless interaction by managing callflows, processing events, and securely communicating with Dialogflow APIs. The architecture is modular, scalable, and maintainable, enabling efficient customer interaction experiences.


## Service Request CS1359601: Adding Parameters in Detect Intent API
This Service Request (SR) is a basis for Day2 Transformational Change, as defined in Section 2.3.1 Operational Change Management Process of the following Statement of Work:
C28263_70423077_Serviont_Global_Solutions_Inc._Farmers_IVR_Operate_Services_PSOW._JUL26_final_v2_signed
 
### Background and Overview
This request aims to introduce new key components to the Detect Intent API.

### Scope
The required modifications was made to the request payload of the Detect Intent API. The logic for API invocation has not been altered.
Previously, the request payload used the Query Input object in the request payload. As part of the new requirements, the QueryParam object was added. This object contains a nested "payload" object which can be used to include custom parameters.

The structure of the QueryParam payload is:

```json
QueryParam:{
  "payload": {
    "strOriginalANI":"",
    "strOriginalDNIS":"",
    "strBUid":"",
    "strActiveLang":"",
    "strCGUID":"",
    "strANI":"",
    "strDNIS":"",
    "strOCallID":""
  }
}
```