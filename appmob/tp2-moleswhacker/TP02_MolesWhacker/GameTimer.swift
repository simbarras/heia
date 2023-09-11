//
//  GameTimer.swift
//  TP02_MolesWhacker
//
//  Created by Claudio Herren on 04.10.22.
//

import Foundation

class GameTimer {
    
    // MARK: - Properties
    
    // TODO: - To complete
    var countDownLeft = 0
    var viewCtrl: ViewController?
    
    
    // MARK: - Singleton pattern
    // Use the shared property to access the timer functions
    // Usage: GameTimer.shared.some_func()
    
    static let shared = GameTimer()
    
    private init() {}

    // MARK: - Public and private methods
    public func setView (view: ViewController) {
        viewCtrl = view
    }
    
    public func startTimer(tickCount: Int, tickFreq: Double) {
        countDownLeft = tickCount
        Timer.scheduledTimer(withTimeInterval: tickFreq, repeats: true){ timer in
            self.countDownLeft -= 1
            self.viewCtrl?.showTime(time: self.countDownLeft)
            if self.countDownLeft == 0 {
                self.viewCtrl?.timerEnded()
                timer.invalidate()
            }
        }
    }

}
