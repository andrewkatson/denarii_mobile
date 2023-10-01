//
//  DenariiUser.swift
//  denarii
//
//  Created by Andrew Katson on 9/30/23.
//

import Foundation

struct DenariiUser {
    private(set) var resetID : Int = 0
    private(set) var reportID : String = ""
    private(set) var verificationReportStatus : String = ""
    private(set) var isVerified : Bool = false
    
    init(resetID: Int, reportID: String, verificationReportStatus: String, isVerified: Bool) {
        self.resetID = resetID
        self.reportID = reportID
        self.verificationReportStatus = verificationReportStatus
        self.isVerified = isVerified
    }
}
