import subprocess
import time
import sys
import os

# Ollama's context window forced to 8K
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

    prompt = f"""You are a strict, emotionless Technical Project Manager.
Compare the Roadmap (ANALYSIS.md) and Completed Steps (DONE.md).

Roadmap:
{analysis_content}

Completed Steps:
{done_content}

CRITICAL INSTRUCTIONS:
1. Identify the FIRST uncompleted technical task from the roadmap.
2. Output ONLY the task description. Do not add formatting, greetings, or markdown.
3. If, and ONLY if, every single task in the roadmap exists in DONE.md, output EXACTLY the text: PROJECT_COMPLETED
4. Be precise. (e.g., 'Implement API Gateway microservice')."""

    command = ["ollama", "run", "qwen2.5-coder:14b", prompt]
    
    try:
        result = subprocess.run(command, capture_output=True, text=True, encoding='utf-8')
        task = result.stdout.strip()
        # Temizleme filtreleri
        if not task or "thinking" in task.lower() or "here is" in task.lower():
            return None
        return task
    except Exception as e:
        print(f"[MANAGER] Failed to get task from Ollama: {e}")
        return None

def run_worker_step(specific_task):
    prompt = (
        f"TASK: {specific_task}\n\n"
        "STRICT WORKFLOW AND FORMATTING RULES:\n"
        "1. Write or modify the necessary source code files to complete this specific task.\n"
        "2. IMPORTANT: Aider is using the 'whole' edit format. You MUST output the ENTIRE, completely updated file content inside your code blocks. NEVER output diffs (e.g., @@ -1,2 +1,4 @@) inside the code blocks!\n"
        "3. Provide the EXACT filename immediately before the ``` code block.\n"
        "4. Do NOT simulate a conversation. Do NOT write 'User:' or 'Assistant:'.\n"
        "5. Once the actual code is written, add a single summary line to DONE.md and exit."
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
            print(">>> Worker (Aider) returned an error. Waiting 10 seconds to cool down...")
            time.sleep(10)
        else:
            print(">>> Task completed successfully. Resetting context for the next cycle...")
            time.sleep(5)

    if iteration >= max_iterations:
        print("\n>>> Reached the iteration limit. Orchestrator stopped.")

if __name__ == "__main__":
    main()