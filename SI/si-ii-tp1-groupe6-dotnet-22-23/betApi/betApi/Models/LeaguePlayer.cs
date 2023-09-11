using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace betApi.Models
{
    public class LeaguePlayer
    {
        [Key, Column(Order = 1)]
        public int Leagueid { get; set; }
        [Key, Column(Order = 2)]
        public int Playerid { get; set; }
        public League League { get; set; }
        public Player Player { get; set; }
    }

    public class LeaguePlayerRequest
    {
        public int Leagueid { get; set; }
        public int Playerid { get; set; }
    }
}
