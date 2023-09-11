using System.ComponentModel.DataAnnotations;

namespace betApi.Models
{
    public class Game
    {
        public int Id { get; set; }
        [DataType(DataType.Date)]
        [DisplayFormat(DataFormatString = "{0:yyyy-MM-dd}", ApplyFormatInEditMode = true)]
        public DateTime Date { get; set; }
        public String HomeTeam { get; set; }
        public String AwayTeam { get; set; }
        public String Location { get; set; }
        public int Leagueid { get; set; }
    }
}
