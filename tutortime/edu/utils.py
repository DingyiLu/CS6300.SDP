import base64
import io
import json
import re
from minio import Minio
from minio.error import (ResponseError, BucketAlreadyOwnedByYou, BucketAlreadyExists)
from web3 import Web3
from web3.middleware import geth_poa_middleware
from solc import compile_files

# File path to deployable *.sol Ethereum Solidity contracts
DEPLOY_ROOT               = "/root/tutortime/edu/resources"
TUTOR_PROBLEM_CONTRACT    = DEPLOY_ROOT + "/tutorproblem.sol"
TUTEE_ANSWER_CONTRACT     = DEPLOY_ROOT + "/tuteeanswer.sol"
TUTORING_SESSION_CONTRACT = DEPLOY_ROOT + "/tutoringsession.sol"
BADGE_CONTRACT            = DEPLOY_ROOT + "/badge.sol"

# Web-app institution account address on the local Ethereum network
INSTITUTION_ADDR = "0xAA74E5B975A5afA0932DaF9437590a3cCeB08bd2"

# Local object storage values
OBJECT_ENDPOINT = "ec2-54-191-201-92.us-west-2.compute.amazonaws.com:9000"
OBJECT_KEY      = "1OJNA1TGWLSXO7CQVN1N"
OBJECT_PASSWORD = "LXjatUcNh83eJbrEdHlgek+xgAmGJr4kL5Cfk0Tm"


######################################
# Object storage-related functions
######################################

# Connect to an object storage endpoint
def connectObj():
    return Minio(OBJECT_ENDPOINT, access_key=OBJECT_KEY, secret_key=OBJECT_PASSWORD, secure=False)


# Create an object storage bucket if it doesn't already exist.
# Set the bucket up to allow for anonymous download
def createBucket(mc, bucket):
    policy_read_only = {"Version":"2012-10-17",
                        "Statement":[
                            {
                            "Sid":"",
                            "Effect":"Allow",
                            "Principal":{"AWS":"*"},
                            "Action":"s3:GetBucketLocation",
                            "Resource":"arn:aws:s3:::" + bucket
                            },
                            {
                            "Sid":"",
                            "Effect":"Allow",
                            "Principal":{"AWS":"*"},
                            "Action":"s3:ListBucket",
                            "Resource":"arn:aws:s3:::" + bucket
                            },
                            {
                            "Sid":"",
                            "Effect":"Allow",
                            "Principal":{"AWS":"*"},
                            "Action":"s3:GetObject",
                            "Resource":"arn:aws:s3:::" + bucket + "/*"
                            }
                        ]}
    try:
        mc.make_bucket(bucket)
    except BucketAlreadyExists as bae:
        print(bae)
    except BucketAlreadyOwnedByYou:
        pass
    except ResponseError as err:
        print(err)
    # Ensure bucket policy is set correctly for download access
    mc.set_bucket_policy(bucket, json.dumps(policy_read_only))


# Save an object given to us as a 'data:image/png;base64' encoded text version of an image
# that comes in from the saved canvas images. Decode this text and store it as a .PNG
# at a given bucket and object key name in object storage.
# Returns the complete external-facing URL of the image.
def saveImageToObject(imgdata, bucket, name):
    imgstr = re.search(r'base64,(.*)', imgdata).group(1)
    raw_img = io.BytesIO(base64.b64decode(imgstr))
    raw_img_size = raw_img.getbuffer().nbytes
    mc = connectObj()
    # Create the bucket if it doesn't exist already
    createBucket(mc, bucket)
    # Place the object to object storage
    try:
        mc.put_object(bucket, name, raw_img, raw_img_size)
    except ResponseError as err:
        print(err)
    # Example writing to a local file instead:
    # output = open('first.png', 'wb')
    # output.write(base64.b64decode(imgstr))
    # output.close()
    # Return url where this image can be found
    return "http://{}/{}/{}".format(OBJECT_ENDPOINT, bucket, name)


# Save an object given to use as a text string as an object storage object (.txt file).
# Store it in the given bucket with the given object key name.
# Returns the complete external-facing URL of the text data.
def saveTextToObject(txtdata, bucket, name):
    txt_bytes = txtdata.encode('utf-8')
    txt_stream = io.BytesIO(txt_bytes)
    mc = connectObj()
    # Create the bucket if it doesn't exist already
    createBucket(mc, bucket)
    # Place the object to object storage
    try:
        mc.put_object(bucket, name, txt_stream, len(txt_bytes))
    except ResponseError as err:
        print(err)
    # Return url where this image can be found
    return "http://{}/{}/{}".format(OBJECT_ENDPOINT, bucket, name)


# Fetch an object stored with UTF-8 encoded content and return it as
# a decoded, normal Python string.
def loadTextFromObject(bucket, name):
    string = ""
    mc = connectObj()
    try:
        raw_data = mc.get_object(bucket, name)
        string = raw_data.data.decode('utf-8')
    except ResponseError as err:
        print(err)
    # Return the decoded content
    return string


# NOTE: THIS FUNCTION DEPRECATED
# Load a specified object (key) from a specified bucket from object storage.
# Returns the complete buffer containing the data
def loadImageBufferFromObject(bucket, key):
    mc = connectObj()
    data = None
    buffer = None
    try:
        # Data will have type "urllib3.response.HTTPResponse"
        data = mc.get_object(bucket, key)
        buffer = bytearray(int(data.getheaders()['Content-Length']))
        data.readinto(buffer)
    except ResponseError as err:
        print(err)
    # Return the data which has a stream() method that can be used
    # to read out the data content.
    return base64.b64encode(buffer)


######################################
# Ethereum contract-related functions
######################################

# Connect to the local Ethereum network
def connectEth():
    # Setup out connection to the Ethereum network using IPC.
    # Note that this assumes we are running on the same system as a network node!
    # Inject PoA compatibility middleware so that this will work on the Geth network
    w3 = Web3(Web3.IPCProvider())
    w3.middleware_onion.inject(geth_poa_middleware, layer=0)
    # The message sender is always the institution
    w3.eth.defaultAccount = INSTITUTION_ADDR
    return w3


# Compile a contract and get it ready for deployment
def compileEth(w3, contract_file):
    compiled_sol = compile_files([contract_file])
    contract_interface = None
    contract = None
    for key in compiled_sol.keys():
        contract_interface = compiled_sol[key]
        break
    if contract_interface is not None:
        contract = w3.eth.contract(abi=contract_interface['abi'], bytecode=contract_interface['bin'])
    return contract


# Fetch the abi of a contract interface
def getAbi(w3, contract_file):
    compiled_sol = compile_files([contract_file])
    contract_interface = None
    abi = None
    for key in compiled_sol.keys():
        contract_interface = compiled_sol[key]
        break
    if contract_interface is not None:
        abi = contract_interface['abi']
    return abi


# Deploy a TutorProblem contract.
# Returns the address of the deployed contract
def deployTutorProblem(student_addr, problem_url, solution_url):

    # Fetch a connection to the network
    w3 = connectEth()

    # Compile a tutoring problem contract
    contract = compileEth(w3, TUTOR_PROBLEM_CONTRACT)
    contract_address = None

    if contract is not None:

        # Deploy the contract
        tx_hash = contract.constructor(student_addr, problem_url, solution_url).transact()

        # BLOCKING CALL: Wait for confirmation that it has been mined
        tx_receipt = w3.eth.waitForTransactionReceipt(tx_hash)
        contract_address = tx_receipt.contractAddress

    # Return the deployed contract address for later use
    return contract_address


# Deploy a TuteeAnswer contract.
# Returns the address of the deployed contract
def deployTuteeAnswer(problem_addr, student_addr, answer_url):

    # Fetch a connection to the network
    w3 = connectEth()

    # Compile a tutoring problem contract
    contract = compileEth(w3, TUTEE_ANSWER_CONTRACT)
    contract_address = None

    if contract is not None:

        # Deploy the contract
        tx_hash = contract.constructor(problem_addr, student_addr, answer_url).transact()

        # BLOCKING CALL: Wait for confirmation that it has been mined
        tx_receipt = w3.eth.waitForTransactionReceipt(tx_hash)
        contract_address = tx_receipt.contractAddress

    # Return the deployed contract address for later use
    return contract_address


# Deploy a TutoringSession contract.
# Returns the address of the deployed contract
def deployTutoringSession(sid, tutor_addr, tutee_addr, tutor_grade, tutee_grade, start, end, duration, chat_log_url, problem_list, answer_list):

    # Fetch a connection to the network
    w3 = connectEth()

    # Compile a tutoring problem contract
    contract = compileEth(w3, TUTORING_SESSION_CONTRACT)
    contract_address = None

    if contract is not None:

        # Deploy the contract
        tx_hash = contract.constructor(sid, tutor_addr, tutee_addr, tutor_grade, tutee_grade, str(start), str(end), duration, chat_log_url, problem_list, answer_list).transact()

        # BLOCKING CALL: Wait for confirmation that it has been mined
        tx_receipt = w3.eth.waitForTransactionReceipt(tx_hash)
        contract_address = tx_receipt.contractAddress

    # Return the deployed contract address for later use
    return contract_address


# Deploy a digital Badge contract.
# Returns the address of the deployed contract
def deployBadge(title, info, student_addr, teacher_addr):

    # Fetch a connection to the network
    w3 = connectEth()

    # Compile a tutoring problem contract
    contract = compileEth(w3, BADGE_CONTRACT)
    contract_address = None

    if contract is not None:

        # Deploy the contract
        tx_hash = contract.constructor(title, info, student_addr, teacher_addr).transact()

        # BLOCKING CALL: Wait for confirmation that it has been mined
        tx_receipt = w3.eth.waitForTransactionReceipt(tx_hash)
        contract_address = tx_receipt.contractAddress

    # Return the deployed contract address for later use
    return contract_address


# Assign a TutorProblem
def assignTutorProblem(problem_addr, approver_addr):

    # Fetch a connection to the network
    w3 = connectEth()

    # Fetch the needed abi and use it to fetch the entity from the network
    abi = getAbi(w3, TUTOR_PROBLEM_CONTRACT)
    tutorproblem = w3.eth.contract(address=problem_addr, abi=abi)

    # Assign and wait for completion (BLOCKING CALL)
    tx_hash = tutorproblem.functions.assign(approver_addr).transact()
    w3.eth.waitForTransactionReceipt(tx_hash)


# Assign a TuteeAnswer
def assignTuteeAnswer(answer_addr, approver_addr):

    # Fetch a connection to the network
    w3 = connectEth()

    # Fetch the needed abi and use it to fetch the entity from the network
    abi = getAbi(w3, TUTEE_ANSWER_CONTRACT)
    tuteeanswer = w3.eth.contract(address=answer_addr, abi=abi)

    # Assign and wait for completion (BLOCKING CALL)
    tx_hash = tuteeanswer.functions.assign(approver_addr).transact()
    w3.eth.waitForTransactionReceipt(tx_hash)


# Assign a TutoringSession
def assignTutoringSession(session_addr, approver_addr):

    # Fetch a connection to the network
    w3 = connectEth()

    # Fetch the needed abi and use it to fetch the entity from the network
    abi = getAbi(w3, TUTORING_SESSION_CONTRACT)
    session = w3.eth.contract(address=session_addr, abi=abi)
    
    # Assign and wait for completion (BLOCKING CALL)
    tx_hash = session.functions.assign(approver_addr).transact()
    w3.eth.waitForTransactionReceipt(tx_hash)


# Assess a TutorProblem
def assessTutorProblem(problem_addr, approved, comments, grade):

    # Fetch a connection to the network
    w3 = connectEth()

    # Fetch the needed abi and use it to fetch the entity from the network
    abi = getAbi(w3, TUTOR_PROBLEM_CONTRACT)
    tutorproblem = w3.eth.contract(address=problem_addr, abi=abi)

    # Assess and wait for completion (BLOCKING CALL)
    tx_hash = tutorproblem.functions.assess(approved, comments, grade).transact()
    w3.eth.waitForTransactionReceipt(tx_hash)


# Assess a TuteeAnswer
def assessTuteeAnswer(answer_addr, approved, correct, comments):

    # Fetch a connection to the network
    w3 = connectEth()

    # Fetch the needed abi and use it to fetch the entity from the network
    abi = getAbi(w3, TUTEE_ANSWER_CONTRACT)
    tuteeanswer = w3.eth.contract(address=answer_addr, abi=abi)

    # Assess and wait for completion (BLOCKING CALL)
    tx_hash = tuteeanswer.functions.assess(approved, correct, comments).transact()
    w3.eth.waitForTransactionReceipt(tx_hash)


# Assess a TutoringSession
def assessTutoringSession(session_addr, approved, comments):

    # Fetch a connection to the network
    w3 = connectEth()

    # Fetch the needed abi and use it to fetch the entity from the network
    abi = getAbi(w3, TUTORING_SESSION_CONTRACT)
    session = w3.eth.contract(address=session_addr, abi=abi)

    # Assess and wait for completion (BLOCKING CALL)
    tx_hash = session.functions.assess(approved, comments).transact()
    w3.eth.waitForTransactionReceipt(tx_hash)
