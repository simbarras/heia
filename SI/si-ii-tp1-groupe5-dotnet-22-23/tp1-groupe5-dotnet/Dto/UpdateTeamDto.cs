using System.ComponentModel.DataAnnotations;

namespace si_ii_tp1_groupe5_dotnet_22_23.Dto;

public class UpdateTeamDto
{
    [Required]
    public string Name { get; set; }
}