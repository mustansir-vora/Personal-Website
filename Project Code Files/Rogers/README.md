
# 📘 R4B Repository – Code Architecture & Contribution Standards

**Author:** **Pavithra N**  
**Contact:** **pavithra.n@servion.com**    
**Organization:** **Servion Global Solutions**

**Developed By :** **Logesh B**

## 📌 Introduction

The **R4B Repository** is the centralized codebase for hosting, developing, and deploying key modules of the R4B self-service applications. These include Cloud Functions, Conversational Agents, IVA configurations, NLU resources, and shared Node.js utilities.

This structure is designed with enterprise-level governance, enabling:

- Scalable multi-module development  
- Clear separation of components  
- Pull-request–based change management  
- Automated CI/CD workflows  
- Environment lifecycle (Dev → QA → Prod)  
- Version-controlled updates across services  

## 📂 Repository Structure Overview

```
/
├── .github/workflows/
├── 1. Workflows/
├── 2. CloudFunctions/
├── 3. ConversationalAgents/
├── 4. Iva-Configs/
└── 5. CommonNodeModules/
```

## 📁 Folder-Level Details

### 📦 `.github/workflows/`
Contains all automation workflows for:
- CI/CD  
- Cloud Function deployments  
- Agent deployments  
- IVA Config deployment
- CommonNode Modules deployment 
- Auto version tagging  


### `1. CloudFunctions/`
Google Cloud Function source code:
- Function modules    
- Shared logic  
- Webhook templates  

### `2. ConversationalAgents/`
Dialogflow CX agent files:
- Flows  
- Pages  
- Routes  
- Exported agent

### `3. Iva-Configs/`
IVA configuration files: 
- Global IVA configs with stucture of JSON parameters and CSV 

### `4. CommonNodeModules/`
Shared Node.js modules for:
- Logging    
- API Clients 
- Config loaders  

## 🔄 Branching & Contribution Standards

### Branch Protection
Environment branches (Dev, QA, Prod) are protected.  
**All changes must go through Pull Requests.**

