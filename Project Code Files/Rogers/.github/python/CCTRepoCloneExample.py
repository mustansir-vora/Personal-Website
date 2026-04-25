import os
import inspect

import common

###############################################################################
# MAIN
###############################################################################
def main():
    common.log("***Exec: " + os.path.basename(__file__) + "->" + inspect.currentframe().f_code.co_name)

    current_branch = os.environ.get('current_branch')
    
    home_dir = "/var/tmp/juraj_test_cct"

    repoUrl = "https://github.com/RogersCommunications/cct-devops-utils"
    branchName = "main"

    common.sh(f"""
        echo "Git user.name configured as: $(git config user.name)"
        echo "Git user.email configured as: $(git config user.email)"
        echo 'Creating {home_dir}'
        mkdir -p {home_dir}
        cd {home_dir}
        echo 'Cloning {repoUrl} -> {branchName}'
        git clone -b {branchName} --depth 1 {repoUrl} .
        ls -la
    """) 

    common.sh(f"""
        echo 'Removing {home_dir}'
        rm -rf {home_dir}
    """) 

if __name__ == "__main__":
    main()