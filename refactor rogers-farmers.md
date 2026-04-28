# Update Portfolio Content for Farmers and Rogers Clients

Based on a thorough review of the codebases for the `FARMERS_CLAIMS_PROJECT`, `FARMERS_SO_PROJECT`, and `Rogers` projects, this plan outlines the necessary updates to the "Clients Handled" (`ExperienceTimeline.tsx`) and "My Work" (`ProjectsGallery.tsx`) sections of your portfolio. This update ensures accuracy and highlights your extensive work with cutting-edge AI technologies, conversational interfaces, and advanced reporting systems.

## Background Context
- **Farmers:** The code shows an extensive Service Operations and Claims IVR built to deliver next-generation conversational experiences. The core of this system is the integration of Cisco CVP with Google Dialogflow CX via the VirtualAgentVoice (VAV) element, enabling seamless Natural Language Understanding (NLU) and conversational AI flows. I also identified a robust custom reporting framework (`ReportLogger.java`, `Menu.java`) that tracks granular IVR metrics including: NoMatch/NoInput counters, caller utterances for NLU model tuning, Menu start/end times, and HotEvent retries. Additionally, there is integration with the Verint API for call recording and analytics. 
- **Rogers:** The `Rogers` project (R4B Repository) focuses heavily on multi-module enterprise architecture. As per the code, the Dialogflow CX agents leverage state-of-the-art **Agentic Workflows via Playbooks** (e.g., *Wireless Technical Support*, *TV Technical Support*, *Internet Tech Support*) and utilize **Generators** for dynamic generative AI tasks (e.g., implicit confirmations, landmark confirmations). The backend uses Google Cloud Functions, structured IVA configs, and automated CI/CD pipelines via GitHub Actions.

## Proposed Changes

### 1. src/components/sections/ExperienceTimeline.tsx
I will update the bullet points for **Rogers** and **Farmers** to be directly aligned with the project repositories, injecting impactful conversational AI terminology:

- **Farmers (Conversational AI & Data Analytics):**
  - Highlight the engineering of a cutting-edge conversational AI bridge by integrating Cisco CVP with Google Dialogflow CX using the VirtualAgentVoice (VAV) element, unlocking advanced NLU capabilities and fluid, human-like voice interactions.
  - Detail the custom Java middleware reporting framework that intercepts Cisco CVP events to power deep conversational analytics.
  - Emphasize the granular tracking of NLU metrics (like capturing raw caller utterances) and interaction heuristics (NoMatch/NoInput, HotEvent retries) used to continuously train and tune the AI models.
  - Mention the integration with the Verint API for call recording and conversational quality analytics, alongside the event-driven architecture managing multi-LOB Service Operations.

- **Rogers (Agentic AI & IVR Integration):**
  - Highlight the implementation of autonomous **Agentic Workflows** using Dialogflow CX Playbooks to handle complex, multi-turn technical support scenarios (Wireless, TV, and Internet).
  - Emphasize the deployment of **Generative AI features (Generators)** for dynamic conversational state management and intelligent contextual confirmations.
  - Refine the description to reflect the enterprise R4B (Rogers 4 Business) self-service applications architecture, seamlessly orchestrating Google Cloud Functions and structured IVA configurations.
  - Emphasize the automated CI/CD workflows implemented via GitHub Actions for scalable multi-environment deployments.
  - Remove inaccurate mentions of RAG pipelines and BigQuery to maintain strict portfolio accuracy.

### 2. src/components/sections/ProjectsGallery.tsx
I will mirror the accuracy improvements made in the Experience section for the respective projects in the gallery:

- **Farmers (CLAIMS IVR & Data Analytics Platform):**
  - Feature the Cisco CVP to Google Dialogflow CX integration via the VirtualAgentVoice (VAV) element as the central NLU engine of the project.
  - Replace the generic BigQuery/Looker Studio points with the specific Java-based CVP reporting logger (`ReportLogger.java`).
  - Add points about tracking `NoMatch`/`NoInput` sequences and raw NLU utterance extraction for system tuning.
  - Include the integration with Verint API for conversational analytics.

- **Rogers (Agentic AI IVR Platform):**
  - Shift focus to the implementation of state-of-the-art **Playbooks** and **Generators** for driving agentic technical support interactions.
  - Adjust the description to focus on the R4B centralized codebase, separating Cloud Functions, Dialogflow CX agents, and shared Node.js modules.
  - Mention enterprise-level governance through PR-based change management and environment lifecycles (Dev → QA → Prod).

## User Review Required

> [!NOTE]
> The plan has been refined to emphasize the VAV integration between Cisco CVP and Google Dialogflow CX for Farmers, alongside your use of cutting-edge Agentic Workflows and Generative AI features for Rogers. Strong conversational AI terminology has been incorporated throughout.

Please review. If this plan looks good, I will proceed with updating the components!

## Verification Plan

### Manual Verification
- Once the components are updated, I will verify that the project builds successfully (`npm run dev`) and that the UI renders the new text without breaking the layout or Framer Motion animations.
