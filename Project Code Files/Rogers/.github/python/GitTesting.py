import os
import inspect

import common

###############################################################################
# MAIN
###############################################################################
def main():
    common.log("***Exec: " + os.path.basename(__file__) + "->" + inspect.currentframe().f_code.co_name)
    
    response = common.sh("""
        echo "Set User:"  
        echo "Current User:"
        git config user.name
        git config user.email
        echo "git status"
        git status
        echo "create file"
        date > validations_last_update_time.txt
        echo "git status"
        git status
        echo "git add"
        git add validations_last_update_time.txt
        echo "git status"
        git status
        echo "git commit"
        git commit -m "[skip ci][ci skip] Validations Test Commit"
        echo "Current User:"
        git config user.name
        git config user.email
        echo "git push"
        git push
    """)

if __name__ == "__main__":
    main()