using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace si_ii_tp1_groupe5_dotnet_22_23.Entities;

public class League
{
    [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
    [Key]
    public int Id { get; set; }
    
    [Required]
    public string Name { get; set; }
    public ICollection<LeaguePlayer> Players { get; set; }
    public ICollection<Match> Matches { get; set; }
}