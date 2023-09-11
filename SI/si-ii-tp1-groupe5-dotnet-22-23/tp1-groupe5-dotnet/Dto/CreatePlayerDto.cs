using System.ComponentModel.DataAnnotations;

namespace si_ii_tp1_groupe5_dotnet_22_23.Dto;

public class CreatePlayerDto
{
    [Required]
    public string Firstname { get; set; }
    
    [Required]
    public string Lastname { get; set; }
    
    [Required] public string Birthdate { get; set; }
    
    [Required] public TeamDto Team { get; set; }
}