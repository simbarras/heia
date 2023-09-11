//
//  ViewControllerScoreboard.swift
//  TP02_MolesWhacker
//
//  Created by Claudio Herren on 01.11.22.
//

import UIKit

class ViewControllerScoreboard: UITableViewController {
    @IBOutlet var resultLbl: UILabel!
    
    var scores: [Score]?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        if let data = UserDefaults.standard.data(forKey: "score") {
            do {    // Create JSON Decoder
                let decoder = JSONDecoder()     // Decode Note
                scores = try decoder.decode([Score].self, from: data)
                
            } catch {
                print("Unable to Decode Notes (\(error))")
                
            }
        }
        if scores != nil {
            resultLbl.isHidden = true
        }
        print(scores)
    }
    
    // MARK: - Table view data source

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return scores?.count ?? 0
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "ScoreCell", for: indexPath) as! UITableViewCell
        
        let timestampLbl = cell.viewWithTag(1) as! UILabel
        let scoreLbl = cell.viewWithTag(2) as! UILabel
        let nameLbl = cell.viewWithTag(3) as! UILabel
        
//        for score in scores ?? [] {
//            timestampLbl.text = score.timestamp
//            scoreLbl.text = String(score.points)
//        }
        
        timestampLbl.text = scores?[indexPath.row].timestamp
        scoreLbl.text = String(scores?[indexPath.row].points ?? 0)
        nameLbl.text = String(scores?[indexPath.row].name ?? "")
        
        return cell
    }
    
    override func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        return "Your scores"
    }
}
