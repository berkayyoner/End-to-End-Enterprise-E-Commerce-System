import subprocess
import time
import sys
import os
import json
import urllib.request

# Ollama'nın bağlam sınırını 8K'ya zorluyoruz
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

    if not analysis_content:
        return "PROJECT_COMPLETED"

    # Yöneticiye devasa işler yerine "küçük, tekil adımlar" bulmasını emrediyoruz
    prompt = f"""You are a strict Technical Project Manager.
Compare the Roadmap (ANALYSIS.md) and Completed Steps (DONE.md) below.

Roadmap:
{analysis_content}

Completed Steps:
{done_content}

CRITICAL INSTRUCTIONS:
1. Identify the FIRST uncompleted task from the roadmap that is NOT listed in the completed steps.
2. Break it down into a SINGLE, small, actionable coding step.
3. Output ONLY the technical task description. (e.g., 'Create the ProductController.java file in the product-management service').
4. If ALL tasks are completed, output EXACTLY: PROJECT_COMPLETED
5. Do not include formatting, markdown, or greetings. Be short and precise."""

    data = json.dumps({
        "model": "qwen2.5-coder:14b",
        "prompt": prompt,
        "stream": False,
        "options": {
            "num_ctx": 8192,
            "temperature": 0.1 
        }
    }).encode('utf-8')

    req = urllib.request.Request("http://localhost:11434/api/generate", data=data, headers={'Content-Type': 'application/json'})
    
    try:
        with urllib.request.urlopen(req) as response:
            result = json.loads(response.read().decode('utf-8'))
            task = result.get("response", "").strip()
            
            if not task or "thinking" in task.lower() or "here is" in task.lower():
                return None
            return task
    except Exception as e:
        print(f"[MANAGER] Failed to get task from Ollama API: {e}")
        return None

def run_worker_step(specific_task):
    # Formatlama talimatlarını sildik! Aider'ın kendi zekasına güveniyoruz.
    prompt = (
        f"TASK: {specific_task}\n\n"
        "STRICT ENTERPRISE CODING STANDARDS (MANDATORY):\n"
        "1. FULL IMPLEMENTATION: You MUST fully implement the requested feature. Do NOT leave placeholders, 'dummy' logic, or 'TODO' comments.\n"
        "2. ARCHITECTURE: If this is a backend task, you MUST build the complete flow: Entity, Repository, Service, and Controller classes. Add proper error handling.\n"
        "3. FRONTEND: If this is a frontend task, implement full state management, real API calls, and styling.\n"
        "4. You must search for and edit ALL relevant files to make this feature 100% production-ready.\n"
        "5. NEVER use diffs (@@). Output the ENTIRE updated file content.\n"
        "6. ONLY AFTER the entire feature is truly and completely coded, add a short summary line to DONE.md and exit."
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
    print(" REST API Two-Agent Autonomous Orchestrator Started")
    print("=====================================================\n")
    
    max_iterations = 50 
    iteration = 0

    while iteration < max_iterations:
        iteration += 1
        print(f"\n================ [ Iteration {iteration} ] ================")

        next_task = get_next_task_from_manager()

        if not next_task:
            print(">>> Manager provided an invalid response. Retrying in 5 seconds...")
            time.sleep(5)
            continue
            
        if next_task == "PROJECT_COMPLETED":
            print("\n>>> Manager reported that all project phases are completed! Orchestrator stopping.")
            break

        print(f"\n>>> Manager assigned task:\n    {next_task}\n")

        exit_code = run_worker_step(next_task)

        if exit_code != 0:
            print(">>> Worker (Aider) returned an error or warning. Resetting context and moving to next cycle in 10s...")
            time.sleep(10)
        else:
            print(">>> Task completed successfully. Resetting context for the next cycle...")
            time.sleep(5)

    if iteration >= max_iterations:
        print("\n>>> Reached the iteration limit. Orchestrator stopped.")

if __name__ == "__main__":
    main()