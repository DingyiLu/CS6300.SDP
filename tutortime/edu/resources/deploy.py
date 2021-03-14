import json
from web3 import Web3
from web3.middleware import geth_poa_middleware
from solc import compile_standard, compile_files

# Control variables
CONTRACT_PATH = "/root/deploy"

# Accounts
institution = "0xAA74E5B975A5afA0932DaF9437590a3cCeB08bd2"
educator1 = "0x8026ACB8332a08C64c4257773e39B6832bEDF564"
student1 = "0x7B89Bcd72590ef765b73A4bdac92B4E8d86d6a32"
student2 = "0xAC0fB5573C2f1AdE9E612fEACFE035ae922a30d6"

# Setup out connection to the Ethereum network using IPC.
# Note that this assumes we are running on the same system as a network node!
# Inject PoA compatibility middleware so that this will work on the Geth network
w3 = Web3(Web3.IPCProvider())
w3.middleware_onion.inject(geth_poa_middleware, layer=0)

# The message sender is always the institution
w3.eth.defaultAccount = institution

# Compile a tutoring problem contract
compiled_sol = compile_files([CONTRACT_PATH+"/tutorproblem.sol"])
contract_interface = None
for key in compiled_sol.keys():
    contract_interface = compiled_sol[key]
    break

# Deploy the contract
contract = w3.eth.contract(abi=contract_interface['abi'], bytecode=contract_interface['bin'])

# Get transaction hash from deployed contract
tx_hash = contract.constructor(student1, "http://test.com", "PUBLIC", "PRIVATE", "http://test.com", "PUBLIC", "PRIVATE").transact()

# BLOCKING CALL: Wait for confirmation that it has been mined
tx_receipt = w3.eth.waitForTransactionReceipt(tx_hash)

# Fetch the deployed contract so that we can make function calls on it now
tutorproblem = w3.eth.contract(address=tx_receipt.contractAddress, abi=contract_interface['abi'])

# Example: Fetch the creator field of the deployed contract
creator = tutorproblem.functions.creator().call()