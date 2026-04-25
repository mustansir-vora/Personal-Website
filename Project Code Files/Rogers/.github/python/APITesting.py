import os
import json
import traceback
import inspect

import functools
print = functools.partial(print, flush=True)

# os.system("pip3 install requests")
# import requests

import common

CURRENT_ACCOUNT = None
ACCESS_TOKEN = None
CURRENT_PROJECT = None
REGION = "northamerica-northeast1"

###############################################################################
# MAIN
###############################################################################
def main():
    # My account
    # Logging -> Overview
    # Compute -> Overview
    # Discovery Engine
    # Assets: https://console.cloud.google.com/iam-admin/asset-inventory/dashboard?referrer=search&project=gcp-prj-rogers-cct-iva-qa-01&supportedpurview=folder&pageState=(%22projectFacets%22:(%22f%22:%22%22),%22locationFacets%22:(%22f%22:%22%22),%22assetTypeFacets%22:(%22f%22:%22%22))
    
    global ACCESS_TOKEN, REGION, CURRENT_PROJECT, CURRENT_ACCOUNT
    common.log("***Exec: " + os.path.basename(__file__) + "->" + inspect.currentframe().f_code.co_name)
    
    CURRENT_ACCOUNT = getCurrentAccount()
    
    projectArr = listProjects()

    current_branch = os.environ.get('current_branch')
    tmpStringCheck = "Prd" if current_branch == "Prod" else current_branch
    for PROJECT_ID in projectArr:
        if tmpStringCheck.lower() in PROJECT_ID.lower():
            CURRENT_PROJECT = PROJECT_ID
            getAllAgents(REGION, CURRENT_PROJECT)
        else:
            print(f"Project '{PROJECT_ID}' does not match Branch '{tmpStringCheck}'")
    
    storageBuckets() 
    
    listServices()
    
    listSecrets()
    ## Permission 'secretmanager.secrets.list' denied for resource 'projects/gcp-prj-rogers-cct-iva-dev-01' (or it may not exist).
    
    listDatasets()
    ## EMPTY LIST
    
    listScheduler()
    
    listRoles()
    ## ERROR: (gcloud.iam.roles.list) [corporate-npe-ss-gh-runner-sa@gcp-prj-corporate-ss-01.iam.gserviceaccount.com] does not have permission to access projects instance [gcp-prj-rogers-cct-iva-dev-01] (or it may not exist): You don't have permission to list roles in projects/gcp-prj-rogers-cct-iva-dev-01. This command is authenticated as corporate-npe-ss-gh-runner-sa@gcp-prj-corporate-ss-01.iam.gserviceaccount.com which is the active account specified by the [core/account] property.
    
    iamPermissions(CURRENT_ACCOUNT)
    ## ERROR: (gcloud.iam.service-accounts.get-iam-policy) PERMISSION_DENIED: Permission 'iam.serviceAccounts.getIamPolicy' denied on resource (or it may not exist). This command is authenticated as corporate-npe-ss-gh-runner-sa@gcp-prj-corporate-ss-01.iam.gserviceaccount.com which is the active account specified by the [core/account] property.
    
    listResources()

    # iamPermissions("cloud-run-dev-cct-iva-sa@gcp-prj-rogers-cct-iva-dev-01.iam.gserviceaccount.com")
    ## ERROR: (gcloud.iam.service-accounts.get-iam-policy) PERMISSION_DENIED: Permission 'iam.serviceAccounts.getIamPolicy' denied on resource (or it may not exist). This command is authenticated as corporate-npe-ss-gh-runner-sa@gcp-prj-corporate-ss-01.iam.gserviceaccount.com which is the active account specified by the [core/account] property.

###############################################################################
# Authentication
###############################################################################   
def getToken():
    global ACCESS_TOKEN, REGION, CURRENT_PROJECT, CURRENT_ACCOUNT
    print("***Exec: " + os.path.basename(__file__) + "->" + inspect.currentframe().f_code.co_name)
    if ACCESS_TOKEN is None:    
        ACCESS_TOKEN = common.sh("gcloud auth print-access-token", printCmd = False, printOutput = False)
    return ACCESS_TOKEN

###############################################################################
# Current Account
###############################################################################  
def getCurrentAccount():
    global ACCESS_TOKEN, REGION, CURRENT_PROJECT, CURRENT_ACCOUNT
    common.log("***Exec: " + os.path.basename(__file__) + "->" + inspect.currentframe().f_code.co_name)
    
    retVal = None
    
    response = common.sh("gcloud auth list --format=json", printCmd = False, printOutput = False)
    
    try:
        response = json.loads(response)    
        for item in response:
            print(f"{item['account']} ({item['status']})")
        retVal = item['account']
    except Exception:
        print(f"::error title=Error:: {inspect.currentframe().f_code.co_name}")
        common.log("Exception:", "e")
        traceback.print_exc()
        print(json.dumps(response, indent=4)) 
        
    return retVal

def listRegions():
    global ACCESS_TOKEN, REGION, CURRENT_PROJECT, CURRENT_ACCOUNT
    common.log("***Exec: " + os.path.basename(__file__) + "->" + inspect.currentframe().f_code.co_name)
    
    response = common.sh("gcloud compute regions list --format=json", printCmd = False, printOutput = False)
    
    try:
        response = json.loads(response)    
        for item in response:
            print(f"{item['projectNumber']}: {item['projectId']}")
    except Exception:
        print(f"::error title=Error:: {inspect.currentframe().f_code.co_name}")
        common.log("Exception:", "e")
        traceback.print_exc()
        print(json.dumps(response, indent=4)) 

def listProjects():
    global ACCESS_TOKEN, REGION, CURRENT_PROJECT, CURRENT_ACCOUNT
    common.log("***Exec: " + os.path.basename(__file__) + "->" + inspect.currentframe().f_code.co_name)
    
    response = common.sh("gcloud projects list --format=json", printCmd = False, printOutput = False)
    
    retArr = []
    try:
        response = json.loads(response)    
        for item in response:
            print(f"{item['projectNumber']}: {item['projectId']}")
            retArr.append(item['projectId'])
    except Exception:
        print(f"::error title=Error:: {inspect.currentframe().f_code.co_name}")
        common.log("Exception:", "e")
        traceback.print_exc()
        print(json.dumps(response, indent=4)) 
        
    return retArr

def getAllAgents(REGION, PROJECT_ID):
    
    common.log("***Exec: " + os.path.basename(__file__) + "->" + inspect.currentframe().f_code.co_name)
    ACCESS_TOKEN = getToken()
    
    SERVICE_URL = f"https://{REGION}-dialogflow.googleapis.com/v3/projects/{PROJECT_ID}/locations/{REGION}/agents"
    
    print(f"SERVICE_URL: {SERVICE_URL}")
    
    command = f"""
        curl -s -X GET \
            {SERVICE_URL} \
            -H "Authorization: Bearer {ACCESS_TOKEN}" \
            -H "x-goog-user-project: {PROJECT_ID}" \
            -H "Content-Type: application/json; charset=utf-8"
    """
    
    response = {}
    try:
        response = common.sh(command, printCmd = False, printOutput = False)
        response = json.loads(response)    
        
        # print(json.dumps(response, indent=4)) 
        arr = []
        for item in response["agents"]:
            # print(f"{item['displayName']}: {item['name']}")
            arr.append({
                "displayName": item['displayName'],
                "name": item['name']
            })
        print(json.dumps(arr, indent=4))  
    except Exception:
        print(f"::error title=Error:: {inspect.currentframe().f_code.co_name}")
        common.log("Exception:", "e")
        traceback.print_exc()
        print(json.dumps(response, indent=4)) 

def listServices():
    global ACCESS_TOKEN, REGION, CURRENT_PROJECT, CURRENT_ACCOUNT
    common.log("***Exec: " + os.path.basename(__file__) + "->" + inspect.currentframe().f_code.co_name)
    
    response = None
    try:
        command = f"gcloud run services list --project={CURRENT_PROJECT} --format=json"
        print(command)
        response = common.sh(command, printCmd = False, printOutput = False)
        response = json.loads(response)
        arr = [] 
        for item in response:
            # print(f"{item['metadata']['name']} - {item['spec']['template']['spec']['serviceAccountName']}")
            arr.append({
                "metadata.name": item['metadata']['name'],
                "location": item['metadata']['labels']['cloud.googleapis.com/location'],
                "serviceAccountName": item['spec']['template']['spec']['serviceAccountName'],
                "build-service-account": item['metadata']['annotations']['run.googleapis.com/build-service-account'],
                "lastModifier": item['metadata']['annotations']['serving.knative.dev/lastModifier']
            })
        print(json.dumps(arr, indent=4))  
    except Exception:
        print(f"::error title=Error:: {inspect.currentframe().f_code.co_name}")
        common.log("Exception:", "e")
        traceback.print_exc()
        # print(json.dumps(response, indent=4)) 
        print(response)

def listSecrets():
    global ACCESS_TOKEN, REGION, CURRENT_PROJECT, CURRENT_ACCOUNT
    common.log("***Exec: " + os.path.basename(__file__) + "->" + inspect.currentframe().f_code.co_name)

    response = None
    try:
        command = f"gcloud secrets list --project={CURRENT_PROJECT} --format=json"
        print(command)
        response = common.sh(command, printCmd = False, printOutput = False)
        # response = json.loads(response)
        print(response)
        # arr = [] 
        # for item in response:
        #     # print(f"{item['metadata']['name']} - {item['spec']['template']['spec']['serviceAccountName']}")
        #     arr.append({
        #         "metadata.name": item['metadata']['name'],
        #         "location": item['metadata']['labels']['cloud.googleapis.com/location'],
        #         "serviceAccountName": item['spec']['template']['spec']['serviceAccountName'],
        #         "build-service-account": item['metadata']['annotations']['run.googleapis.com/build-service-account'],
        #         "lastModifier": item['metadata']['annotations']['serving.knative.dev/lastModifier']
        #     })
        # print(json.dumps(arr, indent=4))  
    except Exception:
        print(f"::error title=Error:: {inspect.currentframe().f_code.co_name}")
        common.log("Exception:", "e")
        traceback.print_exc()
        print(response)
    
    # ACCESS_TOKEN = getToken()
    
    # REGION = os.environ.get("REGION")
    # PROJECT_ID = os.environ.get("PROJECT_ID")
    
    # # SERVICE_URL = f"https://{REGION}-dialogflow.googleapis.com/v3/projects/{PROJECT_ID}/locations/{REGION}/agents"
    # SERVICE_URL = f"https://secretmanager.googleapis.com/v1/projects/{PROJECT_ID}/secrets"
    
    # print(f"SERVICE_URL: {SERVICE_URL}")
    
    # command = f"""
    #     curl -s -X GET \
    #         {SERVICE_URL} \
    #         -H "Authorization: Bearer {ACCESS_TOKEN}" \
    #         -H "x-goog-user-project: {PROJECT_ID}" \
    #         -H "Content-Type: application/json; charset=utf-8"
    # """
    
    # response = {}
    # try:
    #     response = common.sh(command, printCmd = False, printOutput = False)
    #     response = json.loads(response)    
        
    #     for item in response["agents"]:
    #         print(item["displayName"])
    #         print(item["name"])
    # except Exception:
    #     common.log("Exception:", "e")
    #     traceback.print_exc()
    #     print(json.dumps(response, indent=4)) 

def listDatasets():
    global ACCESS_TOKEN, REGION, CURRENT_PROJECT, CURRENT_ACCOUNT
    common.log("***Exec: " + os.path.basename(__file__) + "->" + inspect.currentframe().f_code.co_name)
    
    response = None
    try:
        response = common.sh("bq ls --all --format=json", printCmd = False, printOutput = False)
        if len(response.strip()) < 1:
            print(f"::error title=Error:: {inspect.currentframe().f_code.co_name}")
            print("RESPONSE IS EMPTY")
        else:
            print(response)
        # response = json.loads(response)
        # print(json.dumps(response, indent=4))
        # arr = [] 
        # for item in response:
        #     # print(f"{item['metadata']['name']} - {item['spec']['template']['spec']['serviceAccountName']}")
        #     arr.append({
        #         "metadata.name": item['metadata']['name'],
        #         "location": item['metadata']['labels']['cloud.googleapis.com/location'],
        #         "serviceAccountName": item['spec']['template']['spec']['serviceAccountName'],
        #         "build-service-account": item['metadata']['annotations']['run.googleapis.com/build-service-account'],
        #         "lastModifier": item['metadata']['annotations']['serving.knative.dev/lastModifier']
        #     })
        # print(json.dumps(arr, indent=4))  
    except Exception:
        print(f"::error title=Error:: {inspect.currentframe().f_code.co_name}")
        common.log("Exception:", "e")
        traceback.print_exc()
        print(response)
    
    # ACCESS_TOKEN = getToken()
    
    # # REGION = os.environ.get("REGION")
    # # PROJECT_ID = os.environ.get("PROJECT_ID")
    
    # # SERVICE_URL = f"https://{REGION}-dialogflow.googleapis.com/v3/projects/{PROJECT_ID}/locations/{REGION}/agents"
    # SERVICE_URL = f"https://bigquery.googleapis.com/bigquery/v2/projects/{CURRENT_PROJECT}/datasets?all=true"
    
    # print(f"SERVICE_URL: {SERVICE_URL}")
    
    # command = f"""
    #     curl -s -X GET \
    #         {SERVICE_URL} \
    #         -H "Authorization: Bearer {ACCESS_TOKEN}" \
    #         -H "x-goog-user-project: {CURRENT_PROJECT}" \
    #         -H "Content-Type: application/json; charset=utf-8"
    # """
    
    # response = {}
    # try:
    #     response = common.sh(command, printCmd = False, printOutput = False)
    #     response = json.loads(response)    
        
    #     for item in response["agents"]:
    #         print(item["displayName"])
    #         print(item["name"])
    # except Exception:
    #     common.log("Exception:", "e")
    #     traceback.print_exc()
    #     print(json.dumps(response, indent=4)) 

def iamPermissions(accountEmail):
    global ACCESS_TOKEN, REGION, CURRENT_PROJECT, CURRENT_ACCOUNT
    common.log("***Exec: " + os.path.basename(__file__) + "->" + inspect.currentframe().f_code.co_name)
    
    response = None
    try:
        command = f"gcloud iam service-accounts get-iam-policy {accountEmail} --format=json"
        print(command)
        response = common.sh(command, printCmd = False, printOutput = False)
        print(response)
    except Exception:
        print(f"::error title=Error:: {inspect.currentframe().f_code.co_name}")
        common.log("Exception:", "e")
        traceback.print_exc()
        print(response) 

def storageBuckets():
    global ACCESS_TOKEN, REGION, CURRENT_PROJECT, CURRENT_ACCOUNT
    common.log("***Exec: " + os.path.basename(__file__) + "->" + inspect.currentframe().f_code.co_name)
    
    response = None
    try:
        command = f"gcloud storage buckets list --format=json"
        print(command)
        response = common.sh(command, printCmd = False, printOutput = False)
        response = json.loads(response)    
        for item in response:
            print(item["name"])
    except Exception:
        print(f"::error title=Error:: {inspect.currentframe().f_code.co_name}")
        common.log("Exception:", "e")
        traceback.print_exc()
        print(json.dumps(response, indent=4)) 

def listRoles():
    global ACCESS_TOKEN, REGION, CURRENT_PROJECT, CURRENT_ACCOUNT
    common.log("***Exec: " + os.path.basename(__file__) + "->" + inspect.currentframe().f_code.co_name)
    # response = common.sh(f'gcloud iam roles list --filter="name: custom" --format=json', printCmd = False, printOutput = False)
    # print(response)
    response = None
    try:
        command = f"gcloud iam roles list --project={CURRENT_PROJECT} --format=json"
        print(command)
        response = common.sh(command, printCmd = False, printOutput = False)
        response = json.loads(response)    
        for item in response:
            print(item["name"])
    except Exception:
        print(f"::error title=Error:: {inspect.currentframe().f_code.co_name}")
        common.log("Exception:", "e")
        traceback.print_exc()
        print(response)

def listScheduler():
    global ACCESS_TOKEN, REGION, CURRENT_PROJECT, CURRENT_ACCOUNT
    common.log("***Exec: " + os.path.basename(__file__) + "->" + inspect.currentframe().f_code.co_name)

    response = None
    try:
        # command = f"gcloud scheduler jobs list --project={CURRENT_PROJECT} --format=json"
        command = f"gcloud scheduler jobs list --location={REGION} --format=json"
        print(command)
        response = common.sh(command, printCmd = False, printOutput = False)
        print(response)
    except Exception:
        print(f"::error title=Error:: {inspect.currentframe().f_code.co_name}")
        common.log("Exception:", "e")
        traceback.print_exc()
        print(response) 

def listResources():
    global ACCESS_TOKEN, REGION, CURRENT_PROJECT, CURRENT_ACCOUNT
    common.log("***Exec: " + os.path.basename(__file__) + "->" + inspect.currentframe().f_code.co_name)

    response = None
    try:
        # command = f"gcloud scheduler jobs list --project={CURRENT_PROJECT} --format=json"
        command = f"gcloud asset search-all-resources --scope=projects/{CURRENT_PROJECT} --format=json"
        print(command)
        response = common.sh(command, printCmd = False, printOutput = False)
        print(response)
    except Exception:
        print(f"::error title=Error:: {inspect.currentframe().f_code.co_name}")
        common.log("Exception:", "e")
        traceback.print_exc()
        print(response) 

if __name__ == "__main__":
    main()
    