import subprocess
import time
import sys

def run_aider_step():
    # Strict and standard prompt given to Aider in every loop
    prompt = (
        "TASK: You are a strict autonomous coding agent. Read ANALYSIS.md to find the EXACT NEXT uncompleted step. "
        "Check DONE.md to see what is already finished.\n\n"
        "STRICT WORKFLOW (MUST FOLLOW IN ORDER):\n"
        "1. Identify ONE pending task.\n"
        "2. You MUST create or modify the actual source code files (.java, .js, .yml, pom.xml, etc.) to implement this task. THIS IS MANDATORY.\n"
        "3. ONLY AFTER successfully writing the code, add a single bullet point to DONE.md detailing what you built.\n\n"
        "CRITICAL RULES:\n"
        "- IT IS ABSOLUTELY FORBIDDEN to edit DONE.md without also writing real source code.\n"
        "- DO NOT hallucinate conversations (never output 'User: ...').\n"
        "- DO NOT repeat these instructions. Write the code, update DONE.md, and exit."
    )

    # The terminal command to start Aider. 
    # Using 'python -m aider' prevents Windows PATH errors.
    # --file forces the critical documents into the fresh context every time.
    command = [
        "python", "-m", "aider",
        "--yes",
        "--no-show-model-warnings",
        "--model", "ollama/qwen2.5-coder:14b",
        "--file", "RULES.md", "ANALYSIS.md", "DONE.md",
        "--message", prompt
    ]

    print(">>> Starting Aider with a fresh context for the next task...")
    
    try:
        # Run the subprocess and wait for it to finish
        result = subprocess.run(command)
        return result.returncode
    except KeyboardInterrupt:
        print("\n>>> Stopped by user.")
        sys.exit(0)
    except Exception as e:
        print(f"\n>>> An unexpected error occurred: {e}")
        return 1

def main():
    print("=====================================================")
    print(" E-Commerce Auto-Builder Orchestrator Started")
    print(" Press CTRL+C to exit.")
    print("=====================================================\n")
    
    # A limit to prevent the system from getting lost in an infinite loop overnight
    max_iterations = 30 
    iteration = 0

    while iteration < max_iterations:
        iteration += 1
        print(f"\n[ Iteration {iteration} / {max_iterations} ]")

        # Execute the task
        exit_code = run_aider_step()

        if exit_code != 0:
            print(">>> Aider returned an unexpected exit code. Waiting 30 seconds to cool down...")
            time.sleep(30)
        else:
            print(">>> Step completed successfully. Resetting context and waiting 10 seconds before the next task...")
            time.sleep(10)

    print("\n>>> Reached the iteration limit. Orchestrator stopped.")

if __name__ == "__main__":
    main()