import requests


def identifier(first, last, email):
    return f"{first}:{last}:{email}"


class Candidate:

    def __init__(self, first_name, last_name, middle_name, email, dob, ssn, zipcode, phone, work_locations,
                 candidate_id):
        self.first_name = first_name
        self.last_name = last_name
        self.middle_name = middle_name
        self.email = email
        self.dob = dob
        self.ssn = ssn
        self.zipcode = zipcode
        self.phone = phone
        self.work_locations = work_locations
        self.candidate_id = candidate_id


class Invitation:

    def __init__(self, package, candidate_id, work_locations, report_id):
        self.package = package,
        self.candidate_id = candidate_id
        self.work_locations = work_locations
        self.report_id = report_id


class Report:

    def __init__(self, status, report_id):
        self.status = status
        self.report_id = report_id


class User:

    def __init__(self, candidate, invitation=None, report=None):
        self.candidate = candidate
        self.invitation = invitation
        self.report = report


class CheckrClient:

    def __init__(self, api_key=""):
        self.api_key = api_key
        self.users = {}

    def get_user_with_candidate(self, candidate_id):
        for key, value in self.users.items():
            if value.candidate.candidate_id == candidate_id:
                return value
        return None

    def get_user_with_report(self, report_id):
        for key, value in self.users.items():
            if value.report.report_id == report_id:
                return value
        return None

    def create_candidate(self, first_name, last_name, middle_name, email, dob, ssn, zipcode, phone, work_locations):
        user_id = identifier(first_name, last_name, email)
        if self.users.get(user_id) is not None:
            user = self.users[user_id]

            if user.candidate is not None:
                return False, {}
            else:

                if first_name == "fail_candidate":
                    return False, {}

                candidate_id = first_name

                user.candidate = Candidate(first_name, last_name, middle_name, email, dob, ssn, zipcode, phone,
                                           work_locations, candidate_id)

                return True, {"id": candidate_id}

        else:
            if first_name == "fail_candidate":
                return False, {}

            candidate_id = first_name

            candidate = Candidate(first_name, last_name, middle_name, email, dob, ssn, zipcode, phone,
                                  work_locations, candidate_id)
            self.users[identifier(first_name, last_name, email)] = User(candidate)

            return True, {"id": candidate_id}

    def create_invitation(self, candidate_id, work_locations):
        user_with_candidate = self.get_user_with_candidate(candidate_id)

        if user_with_candidate.invitation is not None:
            return False, {}
        else:

            if candidate_id == "fail_invitation":
                return False, {}

            if candidate_id == "fail_report":
                return True, {"report_id": candidate_id, "status": "unknown"}

            if candidate_id == "report_not_clear":
                return True, {"report_id": candidate_id, "status": "complete"}

            if candidate_id == "report_pending":
                return True, {"report_id": candidate_id, "status": "pending"}

            report_id = "id"
            invitation = Invitation("tasker_plus", candidate_id, work_locations, report_id)
            user_with_candidate.invitation = invitation

            report = Report("complete", report_id)
            user_with_candidate.report = report

            return True, {"report_id": report_id, "status": "pending"}

    def get_report(self, report_id):

        if report_id == "id":
            return True, {"status": "complete", "result": "clear"}
        elif report_id == "fail_report":
            return False, {}
        elif report_id == "report_not_clear":
            return True, {"status": "complete", "result": "not_clear"}
        elif report_id == "report_pending":
            return True, {"status": "pending", "result": "unclear"}
        else:
            return False, {}
