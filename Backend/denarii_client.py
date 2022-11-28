# A python implementation of the Denarii client. Unlike the C++ and Java versions this one does not allow you
# to directly hash with randomx

import json
import os
import pathlib
import requests
import sys
import workspace_path_finder

# Append path with the location of all moved python protos
sys.path.append(str(workspace_path_finder.get_home() / "py_proto"))

from Proto import wallet_pb2


class DenariiClient:

    def send_command_to_wallet_rpc(self, method, params):
        """
        Send a json command to the wallet rpc server
        @param method The method to call
        @param params The parameters to call the method with
        @return The json representing the response
        """
        return self.send_command("http://127.0.0.1", "8080", method, params)

    def send_command_to_daemon(self, method, params):
        """
        Send a json command to the daemon rpc server
        @param method The method to call
        @param params The parameters to call the method with
        @return The json representing the response
        """
        return self.send_command("http://127.0.0.1", "8424", method, params)

    def send_command(self, ip, port, method, params):
        """
        Send a command to the specified ip address and port
        @param ip The ip to send to
        @param port The port to use
        @param method The rpc method to call
        @param params The params to pass the rpc method
        @return The json representing the response
        """
        # Empty json if there is no result
        res = json.dumps({})
        try:
            inputs = {
                "method": method,
                "params": params,
                "jsonrpc": "2.0",
                "id": 0,
            }
            print("Sending " + str(inputs))
            res = requests.post(ip + ":" + port + "/json_rpc", data=json.dumps(inputs),
                                headers={"content-type": "application/json"})
        except Exception as e:
            print("Ran into problem sending request " + str(e))

        try:
            print("Received " + str(res))
            res = res.json()
            print(str(res))
        except Exception as e:
            print("Ran into problem with the response " + str(e))

        return res

    def create_wallet(self, wallet):
        """
        Create a new wallet
        @param wallet The wallet proto with the name and password
        """

        params = {
            "filename": wallet.name,
            "password": wallet.password,
            "language": "English"
        }

        # no output expected
        res = self.send_command_to_wallet_rpc("create_wallet", params)

        if "result" in res:
            return True

        return False

    def create_no_label_address(self, wallet):
        """
        Create an address with no label
        @param wallet The wallet to store the sub address created in
        @return True on success and false otherwise
        """
        return self.create_address("", wallet)

    def create_address(self, label, wallet):
        """
        Create an address with no label
        @param label The label to give the wallet
        @param wallet The wallet to store the sub address created in
        @return True on success and false otherwise
        """

        params = {
            "account_index": 0,
            "label": label
        }

        res = self.send_command_to_wallet_rpc("create_address", params)

        if "result" not in res:
            return False

        result = res["result"]

        wallet.sub_addresses.append(bytes(result["address"], 'utf-8'))

        return True

    def get_address(self, wallet):
        """
        Get the first address for a wallet
        @param wallet The wallet to store the address in
        """

        params = {
            "account_index": 0
        }

        result = self.send_command_to_wallet_rpc("get_address", params)

        if "result" not in result:
            return False

        result_dict = result["result"]

        # Add the primary address
        if "address" in result_dict:
            wallet.address = bytes(result_dict["address"], 'utf-8')

            # Add any secondary addresses
            if "addresses" in result_dict:

                for address in result_dict["addresses"]:
                    if address["label"] != "Primary account":

                        wallet.sub_addresses.append(bytes(address["address"], 'utf-8'))

            return True
        return False

    def transfer_money(self, amount, sender, receiver):
        """
        Transfer some money between wallets
        @param amount The amount to send
        @param sender The senders wallet
        @param receiver The receivers wallet
        @return The amount sent
        """

        sender_current_amount = self.get_balance_of_wallet(sender)

        if sender_current_amount < amount:
            return False

        # First set the current wallet to be the sender's wallet
        self.set_current_wallet(sender)

        params = {
            "destinations": [
                {
                    "amount": amount,
                    "addresss": receiver.address
                }
            ]
        }

        res = self.send_command_to_wallet_rpc("transfer", params)

        if "result" not in res:
            return False

        result = res["result"]

        return "amount" in result

    def get_balance_of_wallet(self, wallet):
        """
        Get the balance of a wallet (only does the first address)
        @param wallet the wallet to get balance of
        @return The balance of the wallet
        """

        # First set the current wallet
        self.set_current_wallet(wallet)

        params = {
            "account_index": 0
        }

        res = self.send_command_to_wallet_rpc("get_balance", params)

        if "result" not in res:
            return 0.0

        result = res["result"]

        if "balance" not in result:
            return 0.0

        return result["balance"]

    def set_current_wallet(self, wallet):
        """
        Set the current wallet to be the one passed
        @param wallet the wallet to set as the current one
        """

        params = {
            "filename": wallet.name,
            "password": wallet.password
        }

        res = self.send_command_to_wallet_rpc("open_wallet", params)

        if "result" in res:
            return True

        return False

    def get_block_hashing_blob(self, wallet):
        """
        Get the block hashing blob
        @param wallet
        @return true on success and false otherwise
        @return the result json string
        """

        params = {
            "wallet_address": wallet.address
        }

        res = self.send_command_to_wallet_rpc("get_block_template", params)

        if "result" not in res:
            return False, res

        result = res["result"]

        return "blockhashing_blob" in result, result

    def attempt_submit_block(self, mined_block):
        """
        Attempt to submit a mined block
        @param mined_block The string representation of the mined block
        @return True on success and false otherwise
        """

        params = [mined_block]

        res = self.send_command_to_daemon("submit_block", params)

        return "error" not in res

    def restore_wallet(self, wallet):
        """
        Restore a deterministic wallet.
        @param wallet The wallet to be restored. Should have the name, password, and seed phrase.
        @return True on success and false otherwise.
        """

        params = {
            "filename": wallet.name,
            "password": wallet.password,
            "seed": wallet.phrase,
        }

        res = self.send_command_to_wallet_rpc("restore_deterministic_wallet", params)

        if "result" not in res:
            return False

        result = res["result"]

        if result["info"] != "Wallet has been restored successfully.":
            return False

        wallet.address = bytes(result["address"], 'utf-8')

        return True

    def query_seed(self, wallet):
        """
        Get the mnemonic seed for a wallet. Should have set the current wallet.
        @param wallet The wallet to store the seed phrase
        @return True on success and false otherwise
        """

        params = {
            "key_type": "mnemonic"
        }

        res = self.send_command_to_wallet_rpc("query_key", params)

        if "result" not in res:
            return False

        result = res["result"]

        wallet.phrase = result["key"]

        return True

    def start_mining(self, do_background_mining, ignore_battery, threads):
        """
        Start mining on this machine to the set wallet.
        @param do_background_mining Whether to mine in the background or not
        @param ignore_battery Whether to ignore the battery
        @param threads how many threads to use
        @return True on success and false otherwise
        """

        params = {
            "threads_count": threads,
            "do_background_mining": do_background_mining,
            "ignore_battery": ignore_battery
        }

        res = self.send_command_to_wallet_rpc("start_mining", params)

        return "result" in res

    def stop_mining(self):
        """
        Stop mining on this machine
        @return True on success and false otherwise
        """

        params = {}

        res = self.send_command_to_wallet_rpc("stop_mining", params)

        return "result" in res
