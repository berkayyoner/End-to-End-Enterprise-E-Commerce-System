import subprocess
import time
import sys
import os
import json
import urllib.request

# API Keys
DEEPSEEK_KEY = os.environ.get("DEEPSEEK_API_KEY")
GEMINI_KEY = os.environ.get("GEMINI_API_KEY")

if not DEEPSEEK_KEY and not GEMINI_KEY:
    print("[FATAL ERROR] At least one API key (DEEPSEEK_API_KEY or GEMINI_API_KEY) must be set!")
    sys.exit(1)

CURRENT_PROVIDER = "DEEPSEEK" if DEEPSEEK_KEY else "GEMINI"

def read_file(filepath):
    try:
        with open(filepath, 'r', encoding='utf-8') as file:
            return file.read().strip()
    except FileNotFoundError:
        return ""

def call_deepseek(prompt, system_role="You are an Elite Enterprise Software Architect."):
    url = "https://api.deepseek.com/chat/completions"
    headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {DEEPSEEK_KEY}"
    }
    payload = {
        "model": "deepseek-chat",
        "messages": [
            {"role": "system", "content": system_role},
            {"role": "user", "content": prompt}
        ],
        "temperature": 0.0
    }
    req = urllib.request.Request(url, data=json.dumps(payload).encode('utf-8'), headers=headers)
    with urllib.request.urlopen(req, timeout=60) as response:
        result = json.loads(response.read().decode('utf-8'))
        return result["choices"][0]["message"]["content"].strip()

def call_gemini(prompt):
    # Google SDK'sını çöpe attık! Artık doğrudan Google'ın REST API uç noktasına bağlanıyoruz.
    url = f"https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-pro-latest:generateContent?key={GEMINI_KEY}"
    headers = {
        "Content-Type": "application/json"
    }
    payload = {
        "contents": [{"parts": [{"text": prompt}]}],
        "generationConfig": {
            "temperature": 0.0
        }
    }
    req = urllib.request.Request(url, data=json.dumps(payload).encode('utf-8'), headers=headers)
    with urllib.request.urlopen(req, timeout=60) as response:
        result = json.loads(response.read().decode('utf-8'))
        return result["candidates"][0]["content"]["parts"][0]["text"].strip()

def initialize_project_if_needed():
    if os.path.exists("ANALYSIS.md"):
        return 

    print("\n=====================================================")
    print(" PROJECT INITIALIZATION PHASE")
    print("=====================================================")
    
    user_idea = input("\n>>> What do you want to build? (Describe your project briefly):\n> ")
    rules_content = read_file("RULES.md")
    
    if not user_idea.strip():
        print("[FATAL ERROR] Project idea cannot be empty. Exiting.")
        sys.exit(1)

    prompt = (
        f"The user wants to build the following project:\n'{user_idea}'\n\n"
        f"CRITICAL RULES AND TECHNOLOGIES:\n{rules_content}\n\n"
        "Create a highly detailed, step-by-step technical roadmap (ANALYSIS.md) for this project. "
        "You MUST strictly follow the architecture, technologies, and directory structures defined in the RULES. "
        "Break down the tasks into granular, atomic coding steps. "
        "Do NOT include conversational filler. Output ONLY the markdown roadmap."
    )

    global CURRENT_PROVIDER
    print(f"\n[INIT] Generating project roadmap using {CURRENT_PROVIDER}... Applying RULES.md...")
    
    roadmap_content = None
    
    if CURRENT_PROVIDER == "DEEPSEEK" and DEEPSEEK_KEY:
        try:
            roadmap_content = call_deepseek(prompt)
        except Exception as e:
            print(f"[INIT WARNING] DeepSeek failed ({e}). Switching to Gemini...")
            CURRENT_PROVIDER = "GEMINI"

    if not roadmap_content and GEMINI_KEY:
        try:
            roadmap_content = call_gemini(prompt)
        except Exception as e:
            print(f"[INIT ERROR] Gemini also failed: {e}")

    if not roadmap_content:
        print("[FATAL ERROR] AI failed to generate the roadmap. Exiting.")
        sys.exit(1)

    with open("ANALYSIS.md", "w", encoding="utf-8") as f:
        f.write(roadmap_content)
        
    with open("DONE.md", "w", encoding="utf-8") as f:
        f.write(f"Project Start Date: {time.strftime('%Y-%m-%d')}\n\nCompleted Steps:\n")
        
    print("[INIT] SUCCESS! ANALYSIS.md and DONE.md generated with strict adherence to RULES.md.\n")
    time.sleep(2)

def get_next_task_from_manager():
    global CURRENT_PROVIDER
    print(f"\n[MANAGER] Analyzing project state and RULES.md using {CURRENT_PROVIDER}...")
    
    analysis_content = read_file("ANALYSIS.md")
    done_content = read_file("DONE.md")
    rules_content = read_file("RULES.md")

    prompt = f"""Compare the Roadmap (ANALYSIS.md) and Completed Steps (DONE.md) against the Project Rules (RULES.md).

RULES:
{rules_content}

Roadmap:
{analysis_content}

Completed Steps:
{done_content}

INSTRUCTIONS:
1. Find the FIRST specific technical task from the roadmap that is explicitly MISSING from the Completed Steps.
2. The task MUST align with the technologies and architecture demanded in RULES.
3. Output ONLY the technical task description.
4. If all tasks are logically complete, output exactly: PROJECT_COMPLETED
5. Be granular, precise, and short."""

    if CURRENT_PROVIDER == "DEEPSEEK" and DEEPSEEK_KEY:
        try:
            task = call_deepseek(prompt)
            print(f"[MANAGER DEBUG] Task identified via DeepSeek: {task}")
            return task
        except Exception as e:
            print(f"[MANAGER WARNING] DeepSeek API failed ({e}). Switching to GEMINI...")
            CURRENT_PROVIDER = "GEMINI"

    if GEMINI_KEY:
        try:
            task = call_gemini(prompt)
            print(f"[MANAGER DEBUG] Task identified via Gemini: {task}")
            return task
        except Exception as e:
            print(f"[MANAGER ERROR] Gemini API failed: {e}")
            return None

    return None

def run_worker_step(specific_task):
    global CURRENT_PROVIDER
    prompt = (
        f"Please execute this task: '{specific_task}'.\n\n"
        "STRICT ENTERPRISE MANDATES:\n"
        "1. Read the RULES.md file provided in this chat. You MUST strictly obey all architectural layers, tech stacks, and constraints defined in it.\n"
        "2. FULL IMPLEMENTATION: You are forbidden from leaving 'TODO' comments, dummy logic, or empty methods. Implement the complete flow (e.g., Entity, Repository, Service, Controller).\n"
        "3. Search for and correctly modify ALL relevant files needed to make this feature 100% functional and production-ready.\n"
        "4. When finished, append a single specific summary line to DONE.md explaining what you actually built.\n"
        "5. Commit your changes."
    )

    model_flag = "deepseek/deepseek-coder" if CURRENT_PROVIDER == "DEEPSEEK" else "gemini/gemini-1.5-pro-latest"

    command = [
        "python", "-m", "aider",
        "--yes",
        "--no-show-model-warnings",
        "--model", model_flag,
        "--file", "RULES.md",
        "--file", "DONE.md",
        "--message", prompt
    ]

    print(f"[WORKER] Executing task with {model_flag} (Adhering to RULES.md)...")
    
    try:
        result = subprocess.run(command)
        if result.returncode != 0 and CURRENT_PROVIDER == "DEEPSEEK" and GEMINI_KEY:
            print("[WORKER WARNING] DeepSeek failed or rate-limited. Falling back to Gemini...")
            CURRENT_PROVIDER = "GEMINI"
            return run_worker_step(specific_task)
        return result.returncode
    except KeyboardInterrupt:
        print("\n>>> Stopped by user.")
        sys.exit(0)
    except Exception as e:
        print(f"\n>>> Error: {e}")
        return 1

def main():
    print("=====================================================")
    print(f" Rule-Enforced Multi-AI Orchestrator (Primary: {CURRENT_PROVIDER})")
    print("=====================================================\n")
    
    initialize_project_if_needed()
    
    max_iterations = 50 
    iteration = 0

    while iteration < max_iterations:
        iteration += 1
        print(f"\n================ [ Iteration {iteration} ] ================")

        next_task = get_next_task_from_manager()

        if not next_task:
            print(">>> Failed to get task. Retrying in 10s...")
            time.sleep(10)
            continue
            
        if "PROJECT_COMPLETED" in next_task.upper():
            print("\n>>> Manager reported that all project phases are completed! Orchestrator stopping.")
            break

        exit_code = run_worker_step(next_task)

        if exit_code != 0:
            print(">>> Worker returned an error. Waiting 10s...")
            time.sleep(10)
        else:
            print(">>> Task cycle finished successfully. Next cycle in 5s...")
            time.sleep(5)

    if iteration >= max_iterations:
        print("\n>>> Reached maximum iteration limit. Orchestrator stopped.")

if __name__ == "__main__":
    main()