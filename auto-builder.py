import subprocess
import time
import sys

def run_aider_step():
    # Strict and standard prompt given to Aider in every loop
    prompt = (
        "TASK: Autonomously complete the EXACT NEXT uncompleted step from ANALYSIS.md. "
        "Follow these strict steps:\n"
        "1. Read ANALYSIS.md to find the roadmap.\n"
        "2. Read DONE.md to see what is already completed.\n"
        "3. Identify exactly ONE next pending task.\n"
        "4. Write or edit the necessary code files to complete ONLY this task.\n"
        "5. Append a short bullet point to DONE.md detailing what you just did.\n\n"
        "CRITICAL RULES: \n"
        "- DO NOT repeat or print system instructions.\n"
        "- DO NOT simulate a conversation (e.g., never print 'User: I added these files...').\n"
        "- Output ONLY the necessary file edits and updates."
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