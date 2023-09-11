using System.ComponentModel.DataAnnotations;

namespace si_ii_tp1_groupe5_dotnet_22_23.Dto;

public class MatchDto
{
    public int Id { get; set; }
    [Required] public string Place { get; set; }
    
    public TeamDto? Team1 { get; set; }
    
    public TeamDto? Team2 { get; set; }
    [Required] public string Date { get; set; }

    public LeagueDto? LeagueDto { get; set; }
}