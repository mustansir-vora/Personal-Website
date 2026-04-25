import os
import pathlib
import glob
import traceback
import json

import ValidateWorkflowSecurity
import common

common.log(__file__)

current_branch = os.environ.get('current_branch')
input_command = os.environ.get('input_command')
print(f"current_branch: {current_branch}")
print(f"input_command: {input_command}")

if input_command == "prevalidations":
    import PreValidations
    PreValidations.main()
elif input_command == "apitest":
    import APITesting
    APITesting.main()
elif input_command == "gittest":
    import GitTesting
    GitTesting.main()
elif input_command == "repocompare":
    import RepoCompare
    RepoCompare.main()
elif input_command == "cctrepoclone":
    import CCTRepoCloneExample
    CCTRepoCloneExample.main()
else:
    print(f"*** Unknown command '{input_command}'.  Running PreValidations!")
    import PreValidations
    PreValidations.main()