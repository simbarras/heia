using System.ComponentModel.DataAnnotations;

namespace betApi.Models
{
    public class League
    {
        public int Id { get; set; }
        public string Name { get; set; }

        public ICollection<Game>? Games { get; set; }

        public ICollection<LeaguePlayer>? Players { get; set; }
    }
}
