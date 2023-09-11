//
//  StringProtocol+.swift
//  TP06_Trivia
//
//  Created by Rafic Galli on 15.07.20.
//  Copyright © 2020 Rafic Galli. All rights reserved.
//

import Foundation

extension StringProtocol {
    var html2AttributedString: NSAttributedString? {
        Data(utf8).html2AttributedString
    }
    var html2String: String {
        html2AttributedString?.string ?? ""
    }
}

