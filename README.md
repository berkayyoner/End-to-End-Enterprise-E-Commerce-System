# End to End Enterprise E-Commerce System
This project has started due to GTech 2026 Academy project assignment. It will be developed further more.


## How it started
* Due to the request of GTech Developer team, main start and deadline of the project was 14.08.2026 18:00 and 17.08.2026 09:00. 
* Main goal is using AI models/agents to develope a project almost fully automated.
* I am using my own experiences and the education from GTech Academy.

## Technologies
* **Front-end:** React.Js
* **Back-end:** Java Spring Boot (Java 21)
* **Back-end Dependancy and JVM:** Maven
* **Caching:** Redis
* **Containeriastion:** Docker
* **Main product search:** Elasticsearch
* **Environments for all projects:** development, production, local
* **ORM:** Hybernate
* **Database:** Oracle
* **Server Management:** Kubernates
* **Deployment:** Jenkins
* **Front-end Test Tools:** Selenium
* **Back-end Test Tools:** Unit Test, Postman
* **Authentication:** OAuth 2.0

## Steps
1. Creating a new project repository.
2. Creating a detailed RULES.md file for main goals and expectations from the AI model/agent.
3. Creating a ANALYSIS.md for AI to fill with the phases and steps to take which it will decide.
4. Creating a DONE.md for AI to fill when it finishes a phase. So we can keep going in any issue.
5. Using a local agent for developing more economically. (I've used Gwen 2.5 Coder 14B Instruct model and created a local API with Ollama.) (CMD: ollama run qwen2.5-coder:14b)
6. Using Aider Python library for local API connection. (CMD: python -m pip install aider-chat) (CMD: cd [project directory]) (CMD: python -m aider --model ollama/qwen2.5-coder:14b)
7. Creating development branches. (PROD, PREPROD, UAT, INT, DEV)
8. Giving prompts to AI to start working.

## Prompts
1. "Read the added RULES.md file. Divide the project into logical Phases. Create a detailed roadmap in a new file named ANALYSIS.md specifying microservices, React front-ends, and database integration phases. Also, create an empty DONE.md file containing exactly 'Project Start Date: August 16, 2026'. Do not ask for confirmation or explain what you will do, just use the tools to create the files immediately."

# Special Thanks to All GTech Team