using System.ComponentModel.DataAnnotations;

namespace betApi.Models
{
    public class Player
    {
        public int Id { get; set; }
        public string Lastname { get; set; }
        public string Firstname { get; set; }
        [DataType(DataType.Date)]
        [DisplayFormat(DataFormatString = "{0:yyyy-MM-dd}", ApplyFormatInEditMode = true)]
        public DateTime Birthdate { get; set; }
        public string FavoriteTeam { get; set; }
        public ICollection<LeaguePlayer>? Leagues { get; set; }

    }

    public class PlayerResponse
    {
        public int Id { get; set; }
        public string Lastname { get; set; }
        public string Firstname { get; set; }
        [DataType(DataType.Date)]
        [DisplayFormat(DataFormatString = "{0:yyyy-MM-dd}", ApplyFormatInEditMode = true)]
        public DateTime Birthdate { get; set; }
        public string FavoriteTeam { get; set; }
    }
}
