# End to End Enterprise E-Commerce System
This project has started due to GTech 2026 Academy project assignment. It will be developed further more.


## How it started
* Due to the request of GTech Developer team, main start and deadline of the project was 14.08.2026 18:00 and 17.08.2026 09:00. 
* Main goal is using AI models/agents to develope a project almost fully automated.
* I am using my own experiences and the education from GTech Academy.
* **Important Note:** Project has started from scratch again on 16.08.2026 because of Qwen 2.5 Coder model's failure. Moved forward with different models and API Keys with fallback method.

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
3. Creating a `ANALYSIS.md` for AI to fill with the phases and steps to take which it will decide.
4. Creating a `DONE.md` for AI to fill when it finishes a phase. So we can keep going in any issue.
5. Using a local agent for developing more economically. (I've used Gwen 2.5 Coder 14B Instruct model and created a local API with Ollama.)
> CMD: `ollama run qwen2.5-coder:14b`
6. Using Aider Python library for local API connection. 
> CMD: `python -m pip install aider-chat`
> CMD: `cd [project directory]`
> CMD: `python -m aider --model ollama/qwen2.5-coder:14b` or `CMD: python -m aider --yes --no-show-model-warnings" --model ollama/qwen2.5-coder:14b` for automatic permission.
7. Creating development branches. (PROD, PREPROD, UAT, INT, DEV)
8. Giving the first 3 prompts to AI to start working.
9. Start services with the `docker-compose.yml` file.
10. Give the 4th prompt to AI.
11. Run `auto-builder.py` from terminal.
> CMD: `python auto-builder.py`

## Prompts
1. `Read the added RULES.md file. Divide the project into logical Phases. Create a detailed roadmap in a new file named ANALYSIS.md specifying microservices, React front-ends, and database integration phases. Also, create an empty DONE.md file containing exactly 'Project Start Date: August 16, 2026'. Do not ask for confirmation or explain what you will do, just use the tools to create the files immediately.`
2. `Let's start Phase 1. Create a docker-compose.yml file in the root directory to set up the local development environment. It must include Oracle (XEPDB1, port 1521, user: berkay, pass: 1234), Redis, and Elasticsearch. Make sure the configurations are suitable for a local microservice environment. After creating the file, update the DONE.md file by adding '- docker-compose.yml created for local databases' under a new 'Completed Steps' section.`
3. `Read ANALYSIS.md. We will now autonomously complete the remaining steps for Phase 1. You MUST strictly use your file editing tools to actually create the files on the disk. DO NOT just print code blocks in the chat.
Follow this enterprise folder structure strictly:
    1. Create a backend/berkay-parent directory. Generate the Java 21 Spring Boot Parent pom.xml inside it.
    2. Create a frontend/berkay-public directory. Generate the initial React package.json and basic setup files inside it.
    3. NEVER create a src folder directly in the root directory.
Update DONE.md after successfully writing all these files to the disk. Execute the file creation tools immediately without asking for confirmation.`
4. `The database containers are already running successfully. Now, autonomously build the core microservice infrastructure inside the backend directory. MUST strictly use file creation tools.
Execute these steps:
    1. Create a backend/discovery-server Spring Boot project. It should act as a Netflix Eureka Server (running on port 8761). Create its pom.xml and main application class.
    2. Create a backend/api-gateway Spring Boot project. It should act as a Spring Cloud Gateway (running on port 8080) and a Eureka Client. Create its pom.xml, application.properties (or yml), and main application class.
    3. Update the backend/berkay-parent/pom.xml to include both discovery-server and api-gateway as <modules>.
    4. Update DONE.md indicating that the Discovery Server and API Gateway have been created.
Do not ask for confirmation. Write the files to the disk immediately.`

## How it works
* I have completely manually created a `RULES.md` file which includes the projects all of the expectations and the technologiles which will be used.
* I also created and stated empty `ANALYSIS.md` and `DONE.md` files. 
    * AI fills the `ANALYSIS.md` file with the phases of project to handle one by one by reading the `RULES.md` file.
    * Also fills `DONE.md` file with the steps it done. Because AIs has limited context sizes. So for long term step/phase remembering, I used the idea of ​​such a method.
* After few small promts for the AI to understand the project, `auto-builder.py` file which I created, creates a loop for AI to keep working on the project for a long time.

# Special Thanks to All GTech Team