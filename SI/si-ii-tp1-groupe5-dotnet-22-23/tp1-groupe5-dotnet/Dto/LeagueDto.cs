using System.ComponentModel.DataAnnotations;

namespace si_ii_tp1_groupe5_dotnet_22_23.Dto;

public class LeagueDto
{
    public int Id { get; set; }
    [Required] public string Name { get; set; }
    
    [Required] public ICollection<PlayerDto> Players { get; set; }
    
    [Required] public ICollection<MatchDto> Matches { get; set; }
}