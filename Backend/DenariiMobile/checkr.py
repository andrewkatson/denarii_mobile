import json
import requests

from requests.auth import HTTPBasicAuth

class CheckrClient:
    ip = "https://api.checkr.com/v1"
    # This is what curl uses for HTTPS
    port = "443"

    def __init__(self, api_key):
        self.api_key = api_key

    def create_candidate(self, first_name, last_name, middle_name, email, dob, ssn, zipcode, phone, work_locations):

        # Empty json if there is no result
        res = json.dumps({})
        ok = False
        try:
            params = {
                "first_name": first_name,
                "middle_name": middle_name,
                "last_name": last_name,
                "email": email,
                "phone": phone,
                "zipcode": zipcode,
                "dob": dob,
                "ssn": ssn,
                "work_locations": work_locations
            }

            inputs = {
                "params": params,
                "jsonrpc": "2.0",
                "id": 0,
            }
            print("Sending " + str(inputs))
            res = requests.post(
                f"{self.ip}:{self.port}/candidates/",
                data=json.dumps(inputs),
                headers={"content-type": "application/json"},
                auth=HTTPBasicAuth(self.api_key, "")
            )
            ok = res.ok
        except Exception as e:
            print("Ran into problem sending request " + str(e))

        try:
            print("Received " + str(res))
            res = res.json()
            print(str(res))
        except Exception as e:
            print("Ran into problem with the response " + str(e))

        return res, ok

    def create_invitation(self, candidate_id, work_locations):
        # Empty json if there is no result
        res = json.dumps({})
        ok = False
        try:
            params = {
                "candidate_id": candidate_id,
                "package": "tasker_plus",
                "work_locations": work_locations
            }

            inputs = {
                "params": params,
                "jsonrpc": "2.0",
                "id": 0,
            }
            print("Sending " + str(inputs))
            res = requests.post(
                f"{self.ip}:{self.port}/invitations/",
                data=json.dumps(inputs),
                headers={"content-type": "application/json"},
                auth=HTTPBasicAuth(self.api_key, "")
            )
            ok = res.ok
        except Exception as e:
            print("Ran into problem sending request " + str(e))

        try:
            print("Received " + str(res))
            res = res.json()
            print(str(res))
        except Exception as e:
            print("Ran into problem with the response " + str(e))

        return res, ok

    def get_report(self, report_id):
        # Empty json if there is no result
        res = json.dumps({})
        ok = False
        try:
            params = {
                "id": report_id,
                "include": "arrest_search,county_civil_searches,country_criminal_searches,federal_civil_search,"
                           "federal_criminal_search,global_watchlist_search,national_criminal_search,"
                           "sex_offender_search,ssn_trace,state_criminal_searches,terrorlist_watchlist_search "
            }

            inputs = {
                "params": params,
                "jsonrpc": "2.0",
                "id": 0,
            }
            print("Sending " + str(inputs))
            res = requests.get(
                f"{self.ip}:{self.port}/reports/",
                data=json.dumps(inputs),
                headers={"content-type": "application/json"},
                auth=HTTPBasicAuth(self.api_key, "")
            )
            ok = res.ok
        except Exception as e:
            print("Ran into problem sending request " + str(e))

        try:
            print("Received " + str(res))
            res = res.json()
            print(str(res))
        except Exception as e:
            print("Ran into problem with the response " + str(e))

        return res, ok
