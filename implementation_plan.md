# Gemini AI Recruiter Assistant Plan (RAG Architecture)

## Overview
Build an interactive, highly intelligent AI chatbot embedded directly in your portfolio. It will be powered by the **Gemini 2.5 Flash** model and utilize a True RAG (Retrieval-Augmented Generation) pipeline. This will serve a dual purpose: answering recruiter questions accurately and serving as a live demonstration of your AI engineering skills (the bot will even be able to explain its own architecture to users).

## Architecture & System Design (Option B: True RAG)

Since we are building a True RAG system, the architecture will consist of:
1. **Embedding Model:** Google's `text-embedding-004` to convert your knowledge base and codebases into vector embeddings.
2. **Vector Database:** We will need a vector store to hold the embeddings. *Recommendation: Pinecone Serverless (Free tier, extremely fast, industry standard for RAG).*
3. **Backend:** Next.js API Routes (`/api/chat`).
4. **LLM:** `gemini-2.5-flash` for fast, intelligent generation with streaming.
5. **Deployment:** Vercel (Hobby Tier - 100% Free). This allows us to securely host the backend Next.js API routes without exposing API keys.

### Ensuring RAG Accuracy
To guarantee the agent retrieves correct chunks and doesn't hallucinate, we will implement these industry best practices:
*   **Semantic & Logical Chunking:** Instead of splitting text arbitrarily by character count, we will chunk by Markdown headers and logical code blocks, ensuring context isn't severed. We will also add a 10-15% chunk overlap.
*   **Metadata Tagging:** Every chunk will be tagged with metadata (e.g., `category: project`, `project_name: rogers-ivr`, `type: behavioral`). This allows for targeted retrieval and prevents the bot from mixing up projects.
*   **Query Expansion:** When a user asks a question, the API will use a lightweight, rapid prompt to rewrite the user's query into optimized search keywords before querying the vector DB.
*   **Generous Top-K Retrieval:** Because Gemini 2.5 Flash has a large context window, we can retrieve the top 10-15 chunks (rather than the standard 3-5). The LLM is incredibly smart and can filter the noise if we give it plenty of context.
*   **Strict System Prompting (Grounding):** The agent will be explicitly instructed: *"Answer strictly using the provided context chunks. If the answer is not present, politely state that you do not have that specific information but offer to talk about a related topic you DO know."*

## Proposed Frontend UI
*   **Styling:** Deep integration with the site's existing aesthetic. It will use the `glass` and `glass-card` CSS classes (emerald/teal gradients, backdrop blur, white/10 borders) to look completely native to the portfolio.
*   **Desktop:** A sleek glassmorphic pill (`✨ Ask AI`) at the bottom right. Expands into a polished chat panel.
*   **Mobile:** A floating circular button positioned cleanly above the bottom navigation bar so it doesn't obstruct content.
*   **Interaction:** `framer-motion` for spring animations, Markdown rendering for code snippets, and real-time text streaming.

## Open Questions & Next Steps
1. **Next.js Config Changes:** We will need to remove `output: 'export'` from `next.config.ts` so Vercel can build the dynamic API routes.
2. **Vercel Setup:** You will need to create a free Vercel account, link your GitHub repository, and add the Gemini & Pinecone API keys as Environment Variables in the Vercel dashboard.
3. **Pinecone Account:** Please create a free Pinecone account (no credit card required) to get your vector database API key.
4. **Data Processing:** Please fill out the `knowledge_base_questionnaire.md`. Once you provide the answers and the zipped codebases, I will write an ingestion script to chunk, embed, and upload everything to the database.
