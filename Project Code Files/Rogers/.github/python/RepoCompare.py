import os
import json
import traceback
import inspect
import filecmp

import common

TMP_CLONE_DIR = "/var/tmp/juraj_clone_tmp"

###############################################################################
# MAIN
###############################################################################
def main():
    global TMP_CLONE_DIR
    common.log("***Exec: " + os.path.basename(__file__) + "->" + inspect.currentframe().f_code.co_name)

    ###############################################################################
    # INIT
    ###############################################################################
    env_arr = ["Dev", "Qa", "Prod"]
    current_repo = os.environ.get('current_repo')
    current_branch = os.environ.get('current_branch')
    
    print(f"Current: '{current_repo}' -> '{current_branch}'")
    
    ###############################################################################
    # VALIDATIONS
    ###############################################################################
    try:
        current_repo
        current_branch
        if len(current_repo.strip()) < 1:
            raise Exception(f"current_repo lenght is less than 1")
        if len(current_branch.strip()) < 1:
            raise Exception(f"current_branch lenght is less than 1")
    except:
        print(f"!!!current_repo or current_branch not initialized, exiting.")
        exit(0)
    
    if not current_branch in env_arr:
        print(f"!!!{current_branch} not in env_arr, exiting.")
        exit(0)
    
    ###############################################################################
    # PROCESS
    ###############################################################################
    for env in env_arr:
        print(f"Processing {env}")
    
    repoUrl = f"https://github.com/{current_repo}"
    branchName = "Prod"

    # repoUrl = "https://github.com/RogersCommunications/cct-devops-utils"
    # branchName = "main"

    common.sh(f"""
        echo 'Creating {TMP_CLONE_DIR}'
        mkdir -p {TMP_CLONE_DIR}
    """) 

    try:
        ###############################################################################
        # Clone Repo
        ###############################################################################
        common.sh(f"""
            echo 'pwd:'
            pwd
            echo 'Cloning {repoUrl} -> {branchName}'
            git clone -b {branchName} --depth 1 {repoUrl} {TMP_CLONE_DIR}/.
            cd {TMP_CLONE_DIR}
            ls -la
        """) 
        
        ###############################################################################
        # Compare
        ###############################################################################
        print("Comparing")

        base_dir = common.sh("pwd", printCmd = False, printOutput = False)

        dir1 = f"{TMP_CLONE_DIR}/.github/workflows"
        dir2 = f"{base_dir}/.github/workflows"
        print(f"Comparing {dir1} and {dir2}")

        compare_directories(dir1, dir2)

    except Exception:
        print(f"::error title=Error:: {inspect.currentframe().f_code.co_name}")
        common.log("Exception:", "e")
        traceback.print_exc()

    common.sh(f"""
        echo 'Removing {TMP_CLONE_DIR}'
        rm -rf {TMP_CLONE_DIR}
    """) 

###############################################################################
# COMPARE DIRECTORIES
###############################################################################
def compare_directories(dir1, dir2):
    """
    Compares two directories and prints a report of the differences.
    """
    dcmp = filecmp.dircmp(dir1, dir2)

    print(f"Comparison Report for '{dir1}' and '{dir2}':")

    if dcmp.same_files:
        print("\n* Identical files in both directories:")
        for item in dcmp.same_files:
            print(f"  - {item}")
            
    if dcmp.diff_files:
        print("\n* Files present in both directories but with different contents:")
        for item in dcmp.diff_files:
            print(f"  - {item}")

    if dcmp.left_only:
        print(f"\n* Files only found in '{dir1}':")
        for item in dcmp.left_only:
            print(f"  - {item}")

    if dcmp.right_only:
        print(f"\n* Files only found in '{dir2}':")
        for item in dcmp.right_only:
            print(f"  - {item}")
            
    if dcmp.common_dirs:
        print("\n* Common subdirectories:")
        for item in dcmp.common_dirs:
            print(f"  - {item}")

    # Recursively compare common subdirectories
    if dcmp.common_dirs:
        print("\n--- Recursing into subdirectories ---")
        for sub_dir in dcmp.common_dirs:
            compare_directories(os.path.join(dir1, sub_dir), os.path.join(dir2, sub_dir))

if __name__ == "__main__":
    main()