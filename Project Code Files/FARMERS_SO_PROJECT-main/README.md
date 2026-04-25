# Farmers Insurance - Service Operations IVR

## 1. Project Overview

This document provides a comprehensive overview of the Farmers Insurance Service Operations IVR system. This system is a voice application designed to handle inbound customer and agent calls, offering a wide range of self-service options and intelligent routing to the appropriate departments.

The primary goal of this IVR is to provide a centralized, efficient, and user-friendly first point of contact for callers interacting with various Farmers Insurance lines of business, including Personal Lines, Commercial Line, FWS, Bristol West, Foremost, 21st Century, and the recently added Farmers New World Life (FNWL).

The system leverages a combination of Cisco CVP, Java backend services, and Google Dialogflow for Natural Language Understanding (NLU) to create a seamless and conversational user experience.

**Target Audience:** This documentation is intended for developers, and technical support personnel responsible for the maintenance, development, and support of the Service Operations IVR.

---

## 2. Business Goals

The Service Operations IVR is designed to achieve several key business objectives:

*   **Enhance Customer Experience:** Provide callers with quick and easy access to information and self-service tasks 24/7, reducing wait times and improving overall satisfaction.
*   **Increase First Call Resolution:** Empower callers to resolve their issues within the IVR without needing to speak to an agent, through a rich set of self-service functionalities.
*   **Improve Operational Efficiency:** Automate routine tasks such as billing inquiries, payment processing, and ID card requests, freeing up agent time to handle more complex issues.
*   **Centralize Call Handling:** Act as a single, intelligent front door for multiple lines of business, ensuring calls are authenticated and routed to the correct agent or department efficiently.
*   **FNWL Self-Service Expansion:** The most recent project initiative focused on integrating Farmers New World Life (FNWL) into the IVR, introducing new self-service capabilities for FNWL customers and agents to align their experience with other lines of business.

---

## 3. System Architecture

The IVR system is built on a multi-component architecture, ensuring scalability, reliability, and maintainability.

```
+-----------------+      +---------------------+      +----------------------+
|   Telephony     |----->|    Cisco CVP        |----->|   Dialogflow (GDF)   |
| (Inbound Call)  |      | (Call Control)      |      | (NLU via VAV Element)|
+-----------------+      +---------------------+      +----------------------+
                             |           ^
                             |           | (Data/Decisions)
                             v           |
                       +---------------------+
                       |   FARMERS_SO_JAVA   |
                       | (Backend Services)  |
                       +---------------------+
                             |           ^
                             |           | (API Calls)
                             v           |
      +----------------------+------------------------+--------------------+
      | Farmers Internal APIs|  3rd Party APIs (AFNI) | Admin/Config APIs  |
      | (Policy, Billing...) |                        | (Routing, Prompts) |
      +----------------------+------------------------+--------------------+
```

**High-Level Flow:**
1.  A call enters the system through the telephony layer and is received by **Cisco Customer Voice Portal (CVP)**.
2.  CVP manages the core call flow logic, executing scripts (`.subflow` files) defined in the `FARMERS_SO_CVP` directory.
3.  For conversational interactions, CVP utilizes the **Virtual Agent Voice (VAV)** element to connect to a **Google Dialogflow (GDF)** agent. Dialogflow processes the caller's spoken request (NLU) and returns the identified intent.
4.  Throughout the call, CVP applications make calls to the **`FARMERS_SO_JAVA`** backend services. These Java applications contain the business logic for interacting with various APIs.
5.  The Java services fetch or post data to a suite of **backend APIs**, including internal Farmers APIs (for policy, billing, agent lookup), third-party services (like AFNI), and internal Admin APIs that provide configuration and routing information.
6.  Based on the data retrieved and the caller's intent, the Java services return a decision to CVP, which then plays the appropriate audio, collects further information, or routes the call to an agent.

---

## 4. Key Components & Technologies

*   **Cisco Customer Voice Portal (CVP):** The core platform for call control and IVR application execution. It uses `.subflow` files to define the call logic.
*   **FARMERS_SO_CVP:** The directory containing all CVP call flow scripts, subflows, and application definitions for this project.
*   **FARMERS_SO_JAVA:** A collection of Maven-based Java projects that provide the backend business logic. These applications are invoked by CVP and are responsible for all API interactions.
*   **Google Dialogflow (GDF):** The Natural Language Understanding (NLU) engine. It interprets caller utterances and identifies their intent (e.g., "I want to make a payment").
*   **Virtual Agent Voice (VAV):** A specific element within Cisco CVP that acts as the bridge to Google Dialogflow, enabling seamless conversational AI interactions.
*   **FarmersAPIClient:** A dedicated Java project (Maven-based) responsible for handling secure connections and communications with the suite of Farmers backend APIs.
*   **Maven:** The build and dependency management tool for all Java projects. The `pom.xml` file in each project defines its dependencies and build configuration.
*   **Verint:** The system is configured to integrate with Verint for call recording and quality management, as indicated by the Verint API endpoint in the configuration.

---

## 5. IVR Call Flow

The IVR handles a complex set of call flows that vary based on the line of business (LOB), caller type (customer vs. agent), and the dialed number (DNIS).

**Entry Point:**
1.  The call begins in the `app.callflow`.
2.  The system performs an initial ANI (Automatic Number Identification) lookup to identify the caller.
3.  Based on the DNIS and other business rules (defined in `ivrConfig.properties`), the system determines the caller's LOB (e.g., Farmers, FNWL, Foremost).
4.  The call is directed to the appropriate Main Menu.

**Authentication & Self-Service:**
1.  The caller is prompted to authenticate themselves, typically using policy number, date of birth, or other personal information.
2.  Once authenticated, the caller is presented with a menu of self-service options. These menus are dynamically constructed based on configurations in `MenuConfig.properties`.
3.  Some common self-service options include:
    *   Billing and Payments (`SharedBillingPayments.subflow`)
    *   Requesting ID Cards (`SharedIDCardsFlow.subflow`)
    *   Policy Changes & Questions (`SharedPolicyCHQA.subflow`)
    *   Agent Information (`SharedAgentContactInformation.subflow`)

**FNWL Flow:**
*   Calls identified as FNWL are routed through specific FNWL subflows (`FNWL_FLOW.subflow`).
*   The system distinguishes between FNWL agents and customers, presenting different menu options accordingly (e.g. `FNWL_FrontbookAgentIVRMenu.subflow` vs. `FNWL_FrontbookCustomerIVRMenu.subflow`).
*   A dedicated `FNWL_OLD` route was added to route calls to the old FNWL menus in case the callers are supposed to be provided with the older menus without the self service.

**Agent Transfer:**
*   If a caller's request cannot be handled through self-service, or if they explicitly ask to speak to a representative, the system routes the call to the appropriate agent queue. Routing logic is determined by API calls managed by the Java backend.

---

## 6. Integration Details

### Dialogflow and VAV Integration

The integration with Google Dialogflow is a cornerstone of the IVR's conversational capabilities.

*   **Mechanism:** CVP uses the **VAV (Virtual Agent Voice)** element in its call flows.
*   **Function:** When a VAV element is triggered, CVP streams the caller's audio to the Google Cloud Speech-to-Text API via Cisco's Virtual Voice Browser (VVB). The transcribed text is then sent to the configured Dialogflow agent.
*   **Data Exchange:** Dialogflow processes the text, matches it to an intent, and extracts any relevant entities (e.g., policy number, payment amount). This information is packaged into a JSON payload and sent back to CVP.
*   **Control:** CVP receives the intent from Dialogflow and uses it to make a routing decision, either continuing the script, calling a Java backend service for more complex logic, or transferring the call.

### Backend API Integration

The `FARMERS_SO_JAVA` applications act as the middleware for all backend integrations.

*   **API Configuration:** The primary endpoints and credentials for all APIs are stored in `Configs/APIconfig.properties` and `Configs/ivrConfig.properties`. This includes URLs for authentication (OAuth), policy lookups, billing summaries, and more.
*   **Authentication:** The system uses OAuth 2.0 (client credentials grant type) to obtain access tokens for the Farmers API gateway. The `FarmersAPIClient` project likely encapsulates this logic.
*   **Key APIs:**
    *   **Admin APIs (e.g., `https://104.156.46.196/IVRAPI_PROD/...`):** Used internally to fetch routing rules, business hours, prompt details, and other configuration data.
    *   **Farmers APIs (e.g., `https://api-ss.farmersinsurance.com/...`):** The primary gateway for accessing business data like policies (`/plcyms`), agents (`/agentms`), billing (`/billingms`), and household information (`/searchpolicyms`).
    *   **Verint API:** Used to send call metadata for recording and analytics.

---

## 7. Recent Changes (FNWL Self-Services)

A focused effort was to integrate FNWL (Farmers New World Life) self-service capabilities.

*   **Initial Code Drop (e2f79921):** The initial set of changes for FNWL was added to the production codebase.
*   **Old FNWL Route (89c1284b):** A dedicated `FNWL_OLD` route was added to route calls to the old FNWL menus in case the callers are supposed to be provided with the older menus without the self service.
*   **New CVP Flows:** New subflows were created to manage the FNWL user journey, including `FNWL_Agent_Or_Customer.subflow`, `FNWL_Customer_ID_Process.subflow`, and various menu flows (`FNWL_FrontbookCustomerIVRMenu.subflow`, etc.).

---

## 8. Technical Specifications

### API Endpoints

The system relies on a large number of API endpoints defined in `ivrConfig.properties`. Below is a summary of key service domains.

*   **Authentication:**
    *   `Admin.Token.URL`: `https://104.156.46.196/IVRAPI_PROD/api/JWTAuthentication/GetAccessToken`
    *   `Farmers.Token.URL`: `https://api-ss.farmersinsurance.com/oauthms/v1/oauth/token`
*   **Policy & Customer Data:**
    *   `S_ACCLINK_ANILOOKUP_URL`: `https://api-ss.farmersinsurance.com/plcyms/v3/policiesAndClaims?operation=searchByPhone`
    *   `S_MULESOFT_FARMER_POLICYINQUIRY_URL`: `https://api-ss.farmersinsurance.com/plcyms/v3/policies?operation=searchByCriteria`
    *   `S_POLICYINQUIRY_RETRIVEINSURANCEPOLICIESBYPARTY_URL`: `https://api-ss.farmersinsurance.com/searchpolicyms/v1/household`
*   **Billing:**
    *   `S_COMMERCIAL_BILLING_SUMMARY`: `https://api-ss.farmersinsurance.com/bibillingms/v1/customers/S_URL_POLICYNUM/billingSummary?idType=PolicyNumber`
    *   `S_FWS_BILLING_LOOKUP_URL`: `https://api-ss.farmersinsurance.com/billingms/v1/billingaccounts/...`
*   **Agent Data:**
    *   `S_ACE_LOOKUP_URL`: `https://api-ss.farmersinsurance.com/agentms/v2/agents?operation=retrieveByPhonenumber`
*   **IVR Configuration (Admin):**
    *   `S_BUSSINESS_OBJECTS_URL`: `https://104.156.46.196/IVRAPI_PROD/api/Dnis/GetBusinessObjectsByDnisKey`
    *   `S_ROUTING_URL`: `https://104.156.46.196/IVRAPI_PROD/api/Routing/GetRoutingTableByBusinessObjects`

### Configuration Properties

Key configuration files are located in the `Configs` directory.

*   **`APIconfig.properties`:** Stores URLs, client IDs, and client secrets for various API services. Contains different values for PROD and NON-PROD environments.
*   **`ivrConfig.properties`:** A critical file containing a wide range of application settings:
    *   Media server paths (`A_PROMPTS_PATH`).
    *   API timeouts.
    *   Line of Business (LOB) definitions (e.g., `A_FARMERS_LOB=...,FNWL,...`).
    *   The full list of Admin and Farmers API endpoints.
*   **`MenuConfig.properties`:** Defines the structure of IVR menus, including the audio files to play, DTMF options, and the Dialogflow event names to trigger.
*   **`log4j2.xml`:** Configures logging levels and appenders for the Java applications.