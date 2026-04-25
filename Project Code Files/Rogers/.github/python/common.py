import os
import inspect
import subprocess

TMP_PATH = "/var/tmp"

SH_TMP_FILE_PATH = TMP_PATH + '/sh_py_run_command.sh'

def log(msg, char='i'):
    width = 80
    msg = msg.strip()
    
    for line in msg.splitlines():
        if len(line) > width:
            width = len(line)

    for i in range(0, width+4): 
        print(char, end="")
    print("")

    for line in msg.splitlines():
        line = line.strip()
        tmpWidth = len(line)
        print(char + " " + line, end="")
        for i in range(0, width-tmpWidth):
            print(" ", end="")
        print(" " + char)

    for i in range(0, width+4):
        print(char, end="")
    print("")

def getHumanReadableCommandMessage(cmd, defaultMessage = "---NO-GROUP---"):
    
    cmd = cmd.strip()
    
    # Only do for single-line strings
    if "\n" not in cmd or len(cmd.splitlines()) == 1:
        if "git log" in cmd or "git show" in cmd or "git status" in cmd or "git diff" in cmd:
            return "Git Command"

        if "cat" in cmd or "pwd" in cmd:
            return "OS/FS Command"

        if "git add" in cmd:
            return "Add to repo"
        
        if "git commit" in cmd:
            return "Commit to repo"

    return defaultMessage

def sh(cmd, printCmd = True, printOutput = True, defaultMessage = "---NO-GROUP---"):
    retVal = ""
    
    # group_msg = getHumanReadableCommandMessage(cmd, defaultMessage)
    group_msg = defaultMessage
    
    if group_msg != "---NO-GROUP---" and (printCmd or printOutput):
        print("##[group]##[command]\u2304\u2304\u2304"+ group_msg +"\u2304\u2304\u2304")

    tmpCmd = ""
    for line in cmd.splitlines():
        tmpCmd += line.strip() + "\n"
    tmpCmd = tmpCmd.strip()

    cmd = "#!/bin/bash\nset -e\nset -o pipefail\n" + tmpCmd

    if printCmd:
        print(cmd)

    fpath = SH_TMP_FILE_PATH
    with open(fpath, 'w') as f:
        f.write(cmd)
    os.system("chmod 755 " + fpath)

    if printOutput:
        print("===") 

    p = subprocess.Popen(['/bin/bash', fpath], stdout=subprocess.PIPE)
    for line in iter(p.stdout.readline, b''):
        #line = line.decode("utf-8").strip() 
        line = line.decode("utf-8")
        line = line.replace("\n", "")
        if printOutput:
            print(line)
        retVal += line + "\n"
    p.stdout.close()
    p.wait()
    
    if p.returncode != 0:
        raise Exception("sh: script returned code " + str(p.returncode))
    
    if printOutput:
        print("^^^")

    if group_msg != "---NO-GROUP---" and (printCmd or printOutput):
        print("##[endgroup]")

    return retVal.strip()