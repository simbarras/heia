//
//  ViewController.swift
//  TP01_DiceRoller
//
//  Created by Simon Barras ISC-IL-3 on 25.09.22.
//

import UIKit

class ViewController: UIViewController {
    @IBOutlet weak var dice1: UIImageView!
    @IBOutlet weak var dice2: UIImageView!
    var doubleDice: Bool = true
    var padding: CGFloat = 69
    
    @IBAction func dice_to_one(_ sender: UIButton) {
        if (!doubleDice) {return}
        dice2.isHidden = doubleDice
        UIView.animate(withDuration: 0.2,
                animations: { () -> Void in
            self.dice1.center.x += self.padding
            },
        completion: nil)
        doubleDice = !doubleDice
    }
    
    @IBAction func dice_to_two(_ sender: UIButton) {
        if (doubleDice) {return}
        UIView.animate(withDuration: 0.2,
                animations: { () -> Void in
            self.dice1.center.x -= self.padding
            },
        completion: nil )
        dice2.isHidden = false
        doubleDice = !doubleDice
    }
    
    @IBAction func roll(_ sender: UIButton) {
        let dice_id1 = String(Int.random(in: 1..<7))
        dice1.image = UIImage(named: "dice_"+dice_id1)
        let dice_id2 = String(Int.random(in: 1..<7))
        dice2.image = UIImage(named: "dice_"+dice_id2)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
}

