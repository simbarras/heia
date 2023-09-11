//
//  ViewController.swift
//  TP02_MolesWhacker
//
//  Created by Claudio Herren on 27.09.22.
//

import UIKit

class ViewController: UIViewController {
    
    var points = 0
    let gameTimer = GameTimer.shared

    @IBOutlet var moles: [UIButton]!
    
    @IBOutlet var startBtn: UIButton!
    @IBOutlet var timeLbl: UILabel!
    @IBOutlet var scoreLbl: UILabel!
    @IBOutlet var nameField: UITextField!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        gameTimer.setView(view: self)
    }

    @IBAction func startClicked(_ sender: Any) {
        startBtn.isEnabled = false
        points = 0
        showPoints()
        let countDown = 15
        showTime(time: countDown)
        gameTimer.startTimer(tickCount: countDown, tickFreq: 1)
        turn()
    }
    
    private func turn() {
        for mole in moles {
            mole.isHidden = true
        }
        let rnd = Int.random(in: 0..<moles.count)
        moles[rnd].isHidden = false
        moles[rnd].addTarget(self, action: #selector(moleClicked), for: .touchUpInside)
    }
    
    @objc func moleClicked(sender: UIButton) {
        points += 1
        showPoints()
        turn()
    }
    
    private func showPoints(){
        scoreLbl.text = String(points)
    }
    
    public func showTime(time: Int) {
        timeLbl.text = String(time)
    }
    
    func stringFromDate(date: Date) -> String {
        let formatter = DateFormatter()
        formatter.dateFormat = "dd MMM yyyy HH:mm:ss" //yyyy
        return formatter.string(from: date)
    }
    
    public func timerEnded() {
        timeLbl.text = String(0)
        for mole in moles {
            mole.isHidden = false
            mole.removeTarget(self, action: #selector(moleClicked), for: .touchUpInside)
        }
        let timestamp = NSDate().timeIntervalSince1970
        let myTimeInterval = TimeInterval(timestamp)
        let time = NSDate(timeIntervalSince1970: TimeInterval(myTimeInterval))
        let timeConverted = stringFromDate(date: time as Date)
        
        let name = nameField.text ?? ""
                
        let score = Score(timestamp: timeConverted, name: name, points: points)
        var scores = [score]
        
        if let data = UserDefaults.standard.data(forKey: "score") {
            do {    // Create JSON Decoder
                let decoder = JSONDecoder()     // Decode Note
                scores = try decoder.decode([Score].self, from: data)
                scores.insert(score, at: 0)
            } catch {
                print("Failed to add existing data")
            }
        }
        
        do {
            let encoder = JSONEncoder()    // Encode Note
            let data = try encoder.encode(scores)    // Write/Set Data
            UserDefaults.standard.set(data, forKey: "score")
        } catch {
            print("Failed convert to JSON")
        }
        
        UserDefaults.standard.synchronize()
                                  
        startBtn.isEnabled = true
    }
    
}

