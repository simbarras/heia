using System.ComponentModel.DataAnnotations;

namespace si_ii_tp1_groupe5_dotnet_22_23.Dto;

public class CreateMatchDto
{
    [Required] public string Place { get; set; }
    [Required]
    public TeamDto Team1 { get; set; }
    [Required]
    public TeamDto Team2 { get; set; }
    [Required] public string Date { get; set; }
}