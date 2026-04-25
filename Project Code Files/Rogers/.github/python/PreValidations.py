import os
import pathlib
import glob
import traceback
import json

import ValidateWorkflowSecurity
import common

common.log(__file__)

VALIDATION_ERRORS = []

###############################################################################
# MAIN
###############################################################################
def main():
    ###############################################################################
    # Sample: Fail the pipeline 
    ###############################################################################
    # raise Exception("Juraj Test Exception")

    ###############################################################################
    # Test Permissions
    ###############################################################################
    # common.log("Test Permissions")
    # common.sh("mkdir -p /var/tmp/cct-devops_tmp")
    # common.sh("ls -la /var/tmp/cct-devops_tmp")

    ###############################################################################
    # Validate js files
    ###############################################################################
    common.log("JS File Validations")
    # print("::group::JS File Validations")
    # for filename in glob.iglob('**/*.js', recursive=True):
    for fileref in pathlib.Path('.').glob('**/*.js'):
        filename = str(fileref)
        print(f"Processing {filename}")
        try:
            common.sh(f"node --check {filename}", printCmd = False)
        except Exception:
            common.log("Validation Failure", "e")
            VALIDATION_ERRORS.append(f"{filename} failed JS validation.")
            traceback.print_exc()
    # print("::endgroup::")

    ###############################################################################
    # Validate json files
    ###############################################################################
    # for filename in glob.iglob('**/*.json', recursive=True):
    for fileref in pathlib.Path('.').glob('**/*.json'):
        filename = str(fileref)
        print(f"Processing {filename}")
        try:
            with open(filename) as file:
                json.load(file)
        except Exception:
            common.log("Validation Failure", "e")
            VALIDATION_ERRORS.append(f"{filename} failed JSON validation.")
            traceback.print_exc()

    ###############################################################################
    # Workflow Security
    ###############################################################################
    ValidateWorkflowSecurity.validate()

    common.log("Validations Output", "#")
    if len(VALIDATION_ERRORS) > 0:
        for error in VALIDATION_ERRORS:
            print(f"::notice title=Validation Error:: {error}")
            # print(f"{error}")
    else:
        print("NO ERRORS FOUND.")

###############################################################################
# Final: Print Errors
###############################################################################
if __name__ == "__main__":
    main()
