using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace si_ii_tp1_groupe5_dotnet_22_23.Entities;

public class Match
{
    [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
    [Key] 
    public int Id { get; set; }

    public Team Team1 { get; set; }
    
    public Team Team2 { get; set; }
    
    public string Place { get; set; }

    public int Team1Id { get; set; }
    
    public int Team2Id { get; set; }
    
    [Required] public string Date { get; set; }

    public League? League { get; set; }
    
    public int? LeagueId { get; set; }
}