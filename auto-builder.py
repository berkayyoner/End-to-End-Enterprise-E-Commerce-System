import subprocess
import time
import sys
import os
import json
import urllib.request
import urllib.error

# API Keys
DEEPSEEK_KEY = os.environ.get("DEEPSEEK_API_KEY")
GEMINI_KEY = os.environ.get("GEMINI_API_KEY")
NVIDIA_KEY = os.environ.get("NVIDIA_API_KEY")

AVAILABLE_PROVIDERS = []
if GEMINI_KEY: AVAILABLE_PROVIDERS.append("GEMINI")
if NVIDIA_KEY: AVAILABLE_PROVIDERS.append("NVIDIA")
if DEEPSEEK_KEY: AVAILABLE_PROVIDERS.append("DEEPSEEK")

if not AVAILABLE_PROVIDERS:
    print("[FATAL ERROR] At least one API key must be set (GEMINI_API_KEY, NVIDIA_API_KEY, DEEPSEEK_API_KEY)!")
    sys.exit(1)

CURRENT_PROVIDER_INDEX = 0
ACTIVE_GEMINI_MODEL = None

def get_current_provider():
    return AVAILABLE_PROVIDERS[CURRENT_PROVIDER_INDEX]

def switch_provider():
    global CURRENT_PROVIDER_INDEX
    if len(AVAILABLE_PROVIDERS) > 1:
        CURRENT_PROVIDER_INDEX = (CURRENT_PROVIDER_INDEX + 1) % len(AVAILABLE_PROVIDERS)
        print(f"\n[SYSTEM ALERT] Provider switched to: {get_current_provider()}")
    else:
        print("\n[SYSTEM ALERT] No other providers available. Retrying...")

def read_file(filepath):
    try:
        with open(filepath, 'r', encoding='utf-8') as file:
            return file.read().strip()
    except FileNotFoundError:
        return ""

def get_valid_gemini_model():
    global ACTIVE_GEMINI_MODEL
    if ACTIVE_GEMINI_MODEL:
        return ACTIVE_GEMINI_MODEL
        
    print("[SYSTEM] Discovering a working Gemini model (Prioritizing Gen 3)...")
    candidate_models = [
        "gemini-3.1-pro-preview", 
        "gemini-3.7-flash",       
        "gemini-2.5-pro",
        "gemini-1.5-pro"
    ]
    
    for model in candidate_models:
        url = f"https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent?key={GEMINI_KEY}"
        payload = {"contents": [{"parts": [{"text": "ping"}]}]}
        req = urllib.request.Request(url, data=json.dumps(payload).encode('utf-8'), headers={"Content-Type": "application/json"})
        try:
            with urllib.request.urlopen(req, timeout=10) as response:
                if response.status == 200:
                    ACTIVE_GEMINI_MODEL = model
                    print(f"[SYSTEM] Successfully locked onto working model: {ACTIVE_GEMINI_MODEL}")
                    return ACTIVE_GEMINI_MODEL
        except Exception:
            continue
            
    print("[SYSTEM WARNING] Could not verify models. Falling back to default.")
    ACTIVE_GEMINI_MODEL = "gemini-1.5-flash"
    return ACTIVE_GEMINI_MODEL

def call_deepseek(prompt, system_role="You are an Elite Enterprise Software Architect."):
    url = "https://api.deepseek.com/chat/completions"
    headers = {"Content-Type": "application/json", "Authorization": f"Bearer {DEEPSEEK_KEY}"}
    payload = {"model": "deepseek-chat", "messages": [{"role": "system", "content": system_role}, {"role": "user", "content": prompt}], "temperature": 0.0}
    req = urllib.request.Request(url, data=json.dumps(payload).encode('utf-8'), headers=headers)
    try:
        with urllib.request.urlopen(req, timeout=60) as response:
            return json.loads(response.read().decode('utf-8'))["choices"][0]["message"]["content"].strip()
    except urllib.error.HTTPError as e:
        raise Exception(f"HTTP {e.code} - {e.read().decode('utf-8')}")

def call_nvidia(prompt, system_role="You are an Elite Enterprise Software Architect."):
    url = "https://integrate.api.nvidia.com/v1/chat/completions"
    headers = {"Content-Type": "application/json", "Authorization": f"Bearer {NVIDIA_KEY}"}
    payload = {"model": "meta/llama-3.1-70b-instruct", "messages": [{"role": "system", "content": system_role}, {"role": "user", "content": prompt}], "temperature": 0.0}
    req = urllib.request.Request(url, data=json.dumps(payload).encode('utf-8'), headers=headers)
    try:
        with urllib.request.urlopen(req, timeout=60) as response:
            return json.loads(response.read().decode('utf-8'))["choices"][0]["message"]["content"].strip()
    except urllib.error.HTTPError as e:
        raise Exception(f"HTTP {e.code} - {e.read().decode('utf-8')}")

def call_gemini(prompt):
    model_name = get_valid_gemini_model()
    url = f"https://generativelanguage.googleapis.com/v1beta/models/{model_name}:generateContent?key={GEMINI_KEY}"
    headers = {"Content-Type": "application/json"}
    payload = {"contents": [{"parts": [{"text": prompt}]}], "generationConfig": {"temperature": 0.0}}
    req = urllib.request.Request(url, data=json.dumps(payload).encode('utf-8'), headers=headers)
    try:
        with urllib.request.urlopen(req, timeout=60) as response:
            return json.loads(response.read().decode('utf-8'))["candidates"][0]["content"]["parts"][0]["text"].strip()
    except urllib.error.HTTPError as e:
        raise Exception(f"HTTP {e.code} - {e.read().decode('utf-8')}")

def execute_manager_call(prompt, system_role=None):
    provider = get_current_provider()
    
    # 503 Hatalarına karşı dirençli bekleme ve tekrar deneme (Retry) mekanizması
    for attempt in range(3):
        try:
            if provider == "GEMINI":
                return call_gemini(prompt)
            elif provider == "NVIDIA":
                return call_nvidia(prompt, system_role)
            elif provider == "DEEPSEEK":
                return call_deepseek(prompt, system_role)
        except Exception as e:
            err_str = str(e)
            if "503" in err_str or "429" in err_str:
                print(f"[{provider} SERVER BUSY] Temporary load spike. Retrying ({attempt+1}/3) in 5 seconds...")
                time.sleep(5)
                continue
            else:
                print(f"\n[MANAGER ERROR] {provider} API Failed: {err_str}")
                switch_provider()
                return execute_manager_call(prompt, system_role)
                
    print(f"\n[MANAGER ERROR] {provider} is completely unresponsive after 3 retries. Forcing switch...")
    switch_provider()
    return execute_manager_call(prompt, system_role)

def initialize_project_if_needed():
    # HATA ÇÖZÜLDÜ: Artık sadece dosyanın varlığına değil, içine de bakıyoruz!
    if read_file("ANALYSIS.md"):
        return 

    print("\n=====================================================")
    print(" PROJECT INITIALIZATION PHASE")
    print("=====================================================")
    
    user_idea = input("\n>>> What do you want to build? (Describe your project briefly):\n> ")
    rules_content = read_file("RULES.md")
    
    if not user_idea.strip():
        sys.exit("[FATAL ERROR] Project idea cannot be empty.")

    prompt = (
        f"The user wants to build the following project:\n'{user_idea}'\n\n"
        f"CRITICAL RULES AND TECHNOLOGIES:\n{rules_content}\n\n"
        "Create a highly detailed, step-by-step technical roadmap (ANALYSIS.md) for this project. "
        "You MUST strictly follow the architecture, technologies, and directory structures defined in the RULES. "
        "Break down the tasks into granular, atomic coding steps. "
        "Do NOT include conversational filler. Output ONLY the markdown roadmap."
    )

    print(f"\n[INIT] Generating roadmap using {get_current_provider()}...")
    roadmap_content = execute_manager_call(prompt, "You are an Elite Enterprise Software Architect.")

    with open("ANALYSIS.md", "w", encoding="utf-8") as f:
        f.write(roadmap_content)
        
    with open("DONE.md", "w", encoding="utf-8") as f:
        f.write(f"Project Start Date: {time.strftime('%Y-%m-%d')}\n\nCompleted Steps:\n")
        
    print("[INIT] SUCCESS! ANALYSIS.md and DONE.md generated.\n")
    time.sleep(2)

def get_next_task_from_manager():
    print(f"\n[MANAGER] Analyzing project state using {get_current_provider()}...")
    analysis_content = read_file("ANALYSIS.md")
    done_content = read_file("DONE.md")
    rules_content = read_file("RULES.md")

    prompt = f"""Compare the Roadmap (ANALYSIS.md) and Completed Steps (DONE.md) against the Project Rules (RULES.md).
RULES: {rules_content}
Roadmap: {analysis_content}
Completed Steps: {done_content}

INSTRUCTIONS:
1. Find the FIRST specific technical task from the roadmap that is explicitly MISSING from the Completed Steps.
2. The task MUST align with the technologies and architecture demanded in RULES.
3. Output ONLY the technical task description.
4. If all tasks are logically complete, output exactly: PROJECT_COMPLETED
5. Be granular, precise, and short."""

    try:
        task = execute_manager_call(prompt, "You are a Technical Project Manager.")
        print(f"[MANAGER DEBUG] Task identified: {task}")
        return task
    except Exception:
        return None

def run_worker_step(specific_task):
    provider = get_current_provider()
    prompt = (
        f"Please execute this task: '{specific_task}'.\n\n"
        "STRICT ENTERPRISE MANDATES:\n"
        "1. Read the RULES.md file provided in this chat. You MUST strictly obey all architectural layers, tech stacks, and constraints defined in it.\n"
        "2. FULL IMPLEMENTATION: You are forbidden from leaving 'TODO' comments, dummy logic, or empty methods. Implement the complete flow.\n"
        "3. Search for and correctly modify ALL relevant files needed to make this feature 100% functional and production-ready.\n"
        "4. When finished, append a single specific summary line to DONE.md explaining what you actually built.\n"
        "5. Commit your changes."
    )

    env = os.environ.copy()
    
    if provider == "NVIDIA":
        env["OPENAI_API_KEY"] = NVIDIA_KEY
        env["OPENAI_API_BASE"] = "https://integrate.api.nvidia.com/v1"
        model_flag = "openai/meta/llama-3.1-70b-instruct"
    elif provider == "DEEPSEEK":
        model_flag = "deepseek/deepseek-coder"
    else:
        model_flag = f"gemini/{get_valid_gemini_model()}"

    command = [
        "python", "-m", "aider",
        "--yes",
        "--no-show-model-warnings",
        "--model", model_flag,
        "--file", "RULES.md",
        "--file", "DONE.md",
        "--message", prompt
    ]

    print(f"[WORKER] Executing task with {model_flag}...")
    
    try:
        result = subprocess.run(command, env=env)
        if result.returncode != 0:
            print(f"[WORKER WARNING] {provider} failed or rate-limited.")
            switch_provider()
        return result.returncode
    except KeyboardInterrupt:
        print("\n>>> Stopped by user.")
        sys.exit(0)
    except Exception as e:
        print(f"\n>>> Error: {e}")
        return 1

def main():
    print("=====================================================")
    print(" Triple-Fallback Multi-AI Orchestrator Started")
    print("=====================================================\n")
    
    try:
        initialize_project_if_needed()
        max_iterations = 50 
        iteration = 0

        while iteration < max_iterations:
            iteration += 1
            print(f"\n================ [ Iteration {iteration} ] ================")

            next_task = get_next_task_from_manager()

            if not next_task:
                print(">>> Retrying in 5s...")
                time.sleep(5)
                continue
                
            if "PROJECT_COMPLETED" in next_task.upper():
                print("\n>>> Manager reported that all project phases are completed! Stopping.")
                break

            exit_code = run_worker_step(next_task)

            if exit_code != 0:
                print(">>> Retrying next cycle in 5s...")
                time.sleep(5)
            else:
                print(">>> Cycle finished successfully. Next cycle in 3s...")
                time.sleep(3)

    except KeyboardInterrupt:
        print("\n\n>>> Process manually interrupted by user. Exiting safely.")
        sys.exit(0)

if __name__ == "__main__":
    main()