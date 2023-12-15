import sys
from subprocess import run
from os import listdir
from os.path import isfile, join, exists
import shutil

def main():
    params = sys.argv[2:]
    timesToPlay = int(sys.argv[1])
    base = "logs"
    for i in range(1, 6):
        if exists(base):
            shutil.rmtree(base)
        wins, total = runAgainst(i, timesToPlay, params)
        with open("results.csv", "+a") as file:
            file.writelines([f"\n{i}: {wins}/{total}"])

def runAgainst(strength, timesToPlay, params):
    
    strengthToCmd = {
    1: "agents/TestAgents/alice/alice",
    3: "./agents/TestAgents/joni/joniagent --agent minimax --depth 2 --heuristic monte-carlo --num-playouts 500",
    2: "./agents/TestAgents/bob/bobagent",
    4: f"java -jar agents/TestAgents/rita/rita.jar",
    5: "./agents/TestAgents/jimmie/Agentjimmie"
}

    EnemyCmd = strengthToCmd[strength]
    OurCmd = f"java -XX:+UnlockExperimentalVMOptions -XX:+UseG1GC -cp agents/Group40/java/dist/Group40.jar javaV.Index {' '.join(params)}"
    
    # Play ours as red
    for i in range(timesToPlay):
        command = ["python3", "Hex.py", f"a=Ours;{OurCmd}", f"a=Enemy;{EnemyCmd}", "-l"]
        process = run(command)
        process.check_returncode()
    
    # Play ours as blue
    for i in range(timesToPlay):
        command = ["python3", "Hex.py", f"a=Ours;{OurCmd}", f"a=Enemy;{EnemyCmd}", "-s", "-l"]
        process = run(command)
        process.check_returncode()

    wins = 0
    total = 0
    base = "logs"
    onlyfiles = [f for f in listdir(base) if isfile(join(base, f))]
    for fileName in onlyfiles:
        total += 1
        fullPath = join(base, fileName)
        with open(fullPath) as file:
            lines = file.readlines()
            resultLine = lines[-5]
            winner = resultLine.split(",")[1]
            if winner == "Ours": 
                wins += 1
    print(f"Ours: {wins}/{total}")
    return (wins, total)
    
if __name__ == "__main__":
    main()