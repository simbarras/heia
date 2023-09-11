using Microsoft.AspNetCore.Mvc;
using si_ii_tp1_groupe5_dotnet_22_23.Dto;
using si_ii_tp1_groupe5_dotnet_22_23.Services;

namespace si_ii_tp1_groupe5_dotnet_22_23.Controllers;

public class TeamController: ApiControllerBase
{
    private readonly TeamService _service;

    public TeamController(TeamService service)
    {
        _service = service;
    }
    
    [HttpGet("teams")]
    public async Task<IActionResult> GetTeam()
    {
        var team = await _service.GetAllTeams();
        return Ok(team);
    }
    
    [HttpGet("teams/{id}")]
    public async Task<IActionResult> GetTeamById(int id)
    {
        var team = await _service.GetTeamById(id);
        return Ok(team);
    }
    
    [HttpPost("teams")]
    public async Task<IActionResult> CreateTeam([FromBody] CreateTeamDto team)
    {
        var newTeam = await _service.CreateTeam(team);
        return Ok(newTeam);
    }
    
    [HttpPut("teams/{id}")]
    public async Task<IActionResult> UpdateTeam(int id, [FromBody] UpdateTeamDto team)
    {
        try
        {
            var updatedTeam = await _service.UpdateTeam(id, team);
            return Ok(updatedTeam);
        }
        catch (Exception e)
        {
            return NotFound();
        }
        
    }
    
    [HttpDelete("teams/{id}")]
    public async Task<IActionResult> DeleteTeam(int id)
    {
        await _service.DeleteTeam(id);
        return Ok();
    }
}