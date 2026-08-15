# End to End Enterprise E-Commerce System
This project has started due to GTech 2026 Academy project assignment. It will be developed further more.


## How it started
* Due to the request of GTech Developer team, main start and deadline of the project was 14.08.2026 18:00 and 17.08.2026 09:00. 
* Main goal is using AI models/agents to develope a project almost fully automated.
* I am using my own experiences and the education from GTech Academy.

## Steps
1. Creating a new project repository.
2. Creating a detailed RULES.md file for main goals and expectations from the AI model/agent.
3. Creating a ANALYSIS.md for AI to fill with the phases and steps to take which it will decide.
4. Creating a DONE.md for AI to fill when it finishes a phase. So we can keep going in any issue.
5. Using a local agent for developing more economically. (I've used Gwen 2.5 Coder 14B Instruct model and created a local API with LM Studio.)
6. Using a extension for local API connection. (I've used Cline free version.)
7. Creating development branches. (PROD, PREPROD, UAT, INT, DEV)
8. Giving prompts to AI to start working.

## Prompts
1. "You are a Software Architect and Full-Stack developer with 20 years of experience. Your task is to build an Enterprise-level E-Commerce system from scratch.

Please first carefully read and analyze the RULES.md file in my working directory from beginning to end.

DO NOT WRITE ANY CODE FOR NOW. Just perform these steps:

Confirm that you understand the requirements in RULES.md.

To implement this large microservice architecture, divide the project into logical, small 'Phases'. List the actions to be taken in each phase step by step (Step 1, Step 2...).

Save this detailed roadmap in a new file named ANALYSIS.md in your working directory.

Create an empty DONE.md file to track the process and simply write 'Project Start Date: [Today's Date]' inside it.

When creating the ANALYSIS.md file, specify the microservices (Gateway, Registry, Config Server)." Clearly specify in which phases the Docker Containers, React front-ends (Public and Admin panel), database (Oracle, Redis, Elasticsearch) integrations, and Testing (Unit, Selenium) phases will be performed.

When you have completed these preparations, tell me, "Analysis complete, I am waiting for your approval to start Phase 1." If you don't understand anything or see any contradictions in RULES.md, don't hesitate to ask me."

# Special Thanks to All GTech Team