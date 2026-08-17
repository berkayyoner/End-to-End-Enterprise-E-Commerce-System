# End to End Enterprise E-Commerce System
This project has started due to GTech 2026 Academy project assignment. It will be developed further more.


## How it started
* Due to the request of GTech Developer team, main start and deadline of the project was `14.08.2026 18:00` and `17.08.2026 09:00`. 
* Main goal is using AI models/agents to develope a project almost fully automated.
* I am using my own experiences and the education from GTech Academy.
* **Important Note:** Project has started from scratch for the third time on `16.08.2026 18:00` because of local `Qwen 2.5 Coder` model, `Gemini`, `DeepSeek`, `Nvidia` API models' terrible failures. Moved forward with `Claude` API key.
    * All `Qwen 2.5 Coder`, `Llama 3.1`, `Gemini 3.1 Pro`, `DeepSeek V3/4` models started to halucinate and delete project files/codes on long term use.
    * Proceeding with `Claude Sonnet 5` for main tasking and `Haiku 4.5` for coding agent now for a more echonomical prototyping.

## Technologies
* **Front-end:** React.Js
* **Back-end:** Java Spring Boot (Java 21)
* **Back-end Dependancy and JVM:** Maven
* **Caching:** Redis
* **Containeriastion:** Docker
* **Main product search:** Elasticsearch
* **Environments for all projects:** development, production, local
* **ORM:** Hybernate with Spring JPA
* **Database:** Oracle
* **Server Management:** Kubernates
* **Deployment:** Jenkins
* **Front-end Test Tools:** Selenium
* **Back-end Test Tools:** Unit Test, Postman
* **Authentication:** OAuth 2.0

## Steps
1. Creating a new project repository.
2. Creating a detailed RULES.md file for main goals and expectations from the AI model/agent.
3. Creating a `ANALYSIS.md` for AI to fill with the phases and steps to take which it will decide.
4. Creating a `DONE.md` for AI to fill when it finishes a phase. So we can keep going in any issue.
5. Creating project bases for AI to work on.
6. Creating development branches. (PROD, PREPROD, UAT, INT, DEV)
7. Install Claude for terminal.
> CMD: `npm install -g @anthropic-ai/claude-code` <br />
> CMD: `Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser` <br />
> CMD: `claude --dangerously-skip-permissions` <br />
or <br/>
> CMD: `npx @anthropic-ai/claude-code --dangerously-skip-permissions`
8. Giving the 4 prompts below to AI to understand the project and start working.

## Prompts
1. Read the added RULES.md file. Divide the project into logical Phases. Create a detailed roadmap in a new file named ANALYSIS.md specifying microservices, React front-ends, and database integration phases. Also, create an empty DONE.md file containing exactly 'Project Start Date: August 16, 2026'. Do not ask for confirmation or explain what you will do, just use the tools to create the files immediately.`

2. /loop Now, enter full autonomous execution mode. Follow this exact loop indefinitely until the entire ANALYSIS.md roadmap is complete:
    1. Compare ANALYSIS.md with DONE.md. Identify the exact next uncompleted technical task.
    2. Implement the task completely. Strictly follow the architecture, tech stack (Spring Boot, React, etc.), and constraints defined in RULES.md. Do NOT leave any 'TODO' comments, placeholder logic, or empty methods.
    3. Search for, create, or modify ALL necessary files across the full-stack architecture to make this specific feature 100% production-ready.
    4. If you encounter compilation, dependency, or runtime errors during implementation, read the logs, debug the issue, and apply the fix autonomously without asking for my input.
    5. Once the feature is fully implemented and stable, append a specific, single-line summary of what you just built to DONE.md.
    6. Use Haiku 4.5 as a background agent for coding to keep the process echonomic as possible.
    7. Immediately move to the next uncompleted task in ANALYSIS.md and repeat this process from step 1.
CRITICAL RULE: Do NOT ask for confirmation, permission, or feedback between tasks. Do NOT stop to explain what you are doing. Simply execute, update DONE.md, and proceed to the next task autonomously until the project is finished.

## How it works
* I have completely manually created a `RULES.md` file which includes the projects all of the expectations and the technologiles which will be used.
* I also created and stated empty `ANALYSIS.md` and `DONE.md` files. 
    * AI fills the `ANALYSIS.md` file with the phases of project to handle one by one by reading the `RULES.md` file.
    * Also fills `DONE.md` file with the steps it done. Because AIs has limited context sizes. So for long term step/phase remembering, I used the idea of ​​such a method.
* After few small promts for the AI to understand the project, I created a loop for AI to keep working on the project for a long time.

# Special Thanks to All GTech Team