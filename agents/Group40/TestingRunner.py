import sys
from subprocess import run

def main():
    strength = int(sys.argv[1])
    timesToPlay = int(sys.argv[2])
    timeLimit = int(sys.argv[3])
    SimulationsPerThread = int(sys.argv[4])
    C = float(sys.argv[5])
    strengthToCmd = {
    1: "agents/TestAgents/alice/alice",
    3: "./agents/TestAgents/joni/joniagent",
    2: "./agents/TestAgents/bob/bobagent",
    4: f"java -jar agents/TestAgents/rita/rita.jar",
    5: "./agents/TestAgents/jimmie/Agentjimmie"
}

    EnemyCmd = strengthToCmd[strength]
    OurCmd = f"java -cp agents/Group40/java/dist/Group40.jar javaV.Index {timeLimit} {SimulationsPerThread} {C}"
    # Play ours as red
    for i in range(timesToPlay):
        command = ["python3", "Hex.py", f"a=Ours;{OurCmd}", f"a=Enemy;{EnemyCmd}", "-p", "-v", "-l"]
        process = run(command)
        process.check_returncode()
    
    # Play ours as blue
    for i in range(timesToPlay):
        command = ["python3", "Hex.py", f"a=Ours;{OurCmd}", f"a=Enemy;{EnemyCmd}", "-p", "-v", "-s", "-l"]
        process = run(command)
        process.check_returncode()

if __name__ == "__main__":
    main()