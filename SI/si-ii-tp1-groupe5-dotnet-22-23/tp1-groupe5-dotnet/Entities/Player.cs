using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace si_ii_tp1_groupe5_dotnet_22_23.Entities;

public class Player
{
    [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
    [Key] 
    public int Id { get; set; }
    
    [Required] public string Firstname { get; set; }
    
    [Required] public string Lastname { get; set; }
    
    [Required] public string Birthdate { get; set; }

    public Team Team { get; set; }
    
    public int TeamId { get; set; }

    public ICollection<LeaguePlayer> Leagues { get; set; }
}