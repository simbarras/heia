using System.ComponentModel.DataAnnotations;
using si_ii_tp1_groupe5_dotnet_22_23.Entities;

namespace si_ii_tp1_groupe5_dotnet_22_23.Dto;

public class PlayerDto
{
    public int Id { get; set; }
    
    [Required]
    public string Firstname { get; set; }
    
    [Required]
    public string Lastname { get; set; }
    
    [Required] public string Birthdate { get; set; }
    
    public TeamDto? Team { get; set; }
}