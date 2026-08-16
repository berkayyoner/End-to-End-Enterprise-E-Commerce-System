import subprocess
import time
import sys
import os
import json
import urllib.request

os.environ["OLLAMA_NUM_CTX"] = "8192" 

def read_file(filepath):
    try:
        with open(filepath, 'r', encoding='utf-8') as file:
            return file.read().strip()
    except FileNotFoundError:
        return ""

def get_next_task_from_manager():
    print("\n[MANAGER] Analyzing project state to determine the next task...")
    
    analysis_content = read_file("ANALYSIS.md")
    done_content = read_file("DONE.md")

    if not analysis_content:
        return "PROJECT_COMPLETED"

    prompt = f"""You are a Technical Project Manager.
Compare the Roadmap (ANALYSIS.md) and Completed Steps (DONE.md) below.

Roadmap:
{analysis_content}

Completed Steps:
{done_content}

INSTRUCTIONS:
1. Find the FIRST task from the roadmap that is explicitly MISSING from the Completed Steps.
2. Output ONLY the technical task description.
3. If all tasks are present in DONE.md, output exactly: PROJECT_COMPLETED
4. Be precise and short. DO NOT output markdown, explanations, or greetings."""

    data = json.dumps({
        "model": "qwen2.5-coder:14b",
        "prompt": prompt,
        "stream": False,
        "options": {
            "num_ctx": 8192,
            "temperature": 0.0 
        }
    }).encode('utf-8')

    req = urllib.request.Request("http://localhost:11434/api/generate", data=data, headers={'Content-Type': 'application/json'})
    
    try:
        with urllib.request.urlopen(req) as response:
            result = json.loads(response.read().decode('utf-8'))
            task = result.get("response", "").strip()
            
            print(f"[MANAGER DEBUG] Task identified: {task}")
            
            if not task or "thinking" in task.lower():
                return None
            return task
    except Exception as e:
        print(f"[MANAGER] Failed: {e}")
        return None

def run_worker_step(specific_task):
    # Aider'a verilecek komutu en sade haline getirdik.
    # Zorunlu formatlama kurallarını sildik ki Aider'ın kafası karışmasın.
    prompt = (
        f"Please execute this task: '{specific_task}'.\n\n"
        "1. Write or modify the necessary code to completely implement this feature (Entities, Repositories, Services, Controllers, and Frontend components as needed).\n"
        "2. Add a short summary line indicating completion to DONE.md.\n"
        "3. Commit your changes."
    )

    # --file parametresini sildik! Aider hangi dosyalara dokunacağını kendi Repo Map'inden bulacak.
    # --yes komutuyla tüm git commit onaylarını otomatikleştiriyoruz.
    command = [
        "python", "-m", "aider",
        "--yes",
        "--no-show-model-warnings",
        "--model", "ollama/qwen2.5-coder:14b",
        "--message", prompt
    ]

    print(f"[WORKER] Starting Aider...")
    
    try:
        result = subprocess.run(command)
        return result.returncode
    except KeyboardInterrupt:
        print("\n>>> Stopped by user.")
        sys.exit(0)
    except Exception as e:
        print(f"\n>>> Error: {e}")
        return 1

def main():
    print("=====================================================")
    print(" Conflict-Free Two-Agent Orchestrator Started")
    print("=====================================================\n")
    
    max_iterations = 50 
    iteration = 0

    while iteration < max_iterations:
        iteration += 1
        print(f"\n================ [ Iteration {iteration} ] ================")

        next_task = get_next_task_from_manager()

        if not next_task:
            print(">>> Waiting 5 seconds to retry...")
            time.sleep(5)
            continue
            
        if "PROJECT_COMPLETED" in next_task.upper():
            print("\n>>> Manager reported completion! Stopping.")
            break

        exit_code = run_worker_step(next_task)

        if exit_code != 0:
            print(">>> Aider returned non-zero exit code. Cooling down (10s)...")
            time.sleep(10)
        else:
            print(">>> Cycle complete. Resetting for next task (5s)...")
            time.sleep(5)

if __name__ == "__main__":
    main()