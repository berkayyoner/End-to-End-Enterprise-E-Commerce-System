import subprocess
import time
import sys

def run_aider_step():
    # Strict and standard prompt given to Aider in every loop
    prompt = (
        "Review ANALYSIS.md and DONE.md. "
        "Identify the EXACT NEXT logical, uncompleted task according to the roadmap. "
        "Autonomously implement ONLY that specific task using your file creation/editing tools. "
        "Follow the enterprise architecture rules in RULES.md strictly. "
        "When finished, update DONE.md by adding a new bullet point under 'Completed Steps' detailing exactly what you built. "
        "Do NOT attempt to complete the entire phase at once, just execute the next major task and exit."
    )

    # The terminal command to start Aider. 
    # Using 'python -m aider' prevents Windows PATH errors.
    # --file forces the critical documents into the fresh context every time.
    command = [
        "python", "-m", "aider",
        "--yes",
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