import subprocess
import time
import sys
import os

os.environ["OLLAMA_NUM_CTX"] = "8192"

def read_file(filepath):
    try:
        with open(filepath, 'r', encoding='utf-8') as file:
            return file.read()
    except FileNotFoundError:
        return ""

def get_next_task_from_manager():
    print("\n[MANAGER] Analyzing project state to determine the next task...")
    
    analysis_content = read_file("ANALYSIS.md")
    done_content = read_file("DONE.md")

    prompt = f"""You are a strict Technical Project Manager.
Your job is to read the project roadmap and the completed steps, and determine the EXACT NEXT SINGLE atomic coding task that needs to be implemented.

Roadmap (ANALYSIS.md):
{analysis_content}

Completed Steps (DONE.md):
{done_content}

INSTRUCTIONS:
1. Find the first uncompleted sub-step in the roadmap.
2. Output ONLY a short, actionable technical command for a developer to execute (e.g., 'Create the backend/auth-service Spring Boot project and its pom.xml').
3. If all tasks in ANALYSIS.md are fully completed and present in DONE.md, output exactly the word: 'PROJECT_COMPLETED'.
4. DO NOT include any explanations, formatting, markdown, or greetings. Output strictly the task string."""

    command = ["ollama", "run", "qwen2.5-coder:14b", prompt]
    
    try:
        result = subprocess.run(command, capture_output=True, text=True, encoding='utf-8')
        task = result.stdout.strip()
        return task
    except Exception as e:
        print(f"[MANAGER] Failed to get task from Ollama: {e}")
        return None

def run_worker_step(specific_task):
    prompt = (
        f"TASK: {specific_task}\n\n"
        "STRICT WORKFLOW:\n"
        "1. Write or modify the necessary source code files to complete this specific task.\n"
        "2. Do NOT simulate a conversation or print system rules.\n"
        "3. Once the code is written, add a single summary line to DONE.md and exit."
    )

    command = [
        "python", "-m", "aider",
        "--yes",
        "--no-show-model-warnings",
        "--model", "ollama/qwen2.5-coder:14b",
        "--file", "DONE.md", 
        "--message", prompt
    ]

    print(f"[WORKER] Executing task: {specific_task}")
    
    try:
        result = subprocess.run(command)
        return result.returncode
    except KeyboardInterrupt:
        print("\n>>> Stopped by user.")
        sys.exit(0)
    except Exception as e:
        print(f"\n>>> Unexpected error: {e}")
        return 1

def main():
    print("=====================================================")
    print(" Two-Agent Autonomous Orchestrator Started")
    print("=====================================================\n")
    
    max_iterations = 50 
    iteration = 0

    while iteration < max_iterations:
        iteration += 1
        print(f"\n================ [ Iteration {iteration} ] ================")

        # Step 1: Manager decides the next task
        next_task = get_next_task_from_manager()

        if not next_task:
            print(">>> Failed to determine the next task. Retrying in 15 seconds...")
            time.sleep(15)
            continue
            
        if "PROJECT_COMPLETED" in next_task.upper():
            print("\n>>> Manager reported that all project phases are completed! Orchestrator stopping.")
            break

        print(f"\n>>> Manager assigned task:\n    {next_task}\n")

        # Step 2: Worker executes the task
        exit_code = run_worker_step(next_task)

        if exit_code != 0:
            print(">>> Worker (Aider) returned an error. Waiting 15 seconds to cool down...")
            time.sleep(15)
        else:
            print(">>> Task completed successfully. Resetting context for the next cycle...")
            time.sleep(10)

    if iteration >= max_iterations:
        print("\n>>> Reached the iteration limit. Orchestrator stopped.")

if __name__ == "__main__":
    main()