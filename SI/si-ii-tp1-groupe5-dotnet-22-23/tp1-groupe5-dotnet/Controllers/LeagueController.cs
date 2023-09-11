using Microsoft.AspNetCore.Mvc;
using si_ii_tp1_groupe5_dotnet_22_23.Dto;
using si_ii_tp1_groupe5_dotnet_22_23.Entities;
using si_ii_tp1_groupe5_dotnet_22_23.Services;

namespace si_ii_tp1_groupe5_dotnet_22_23.Controllers;

public class LeagueController: ApiControllerBase
{
    private readonly LeagueService _leagueService;
    
    public LeagueController(LeagueService leagueService)
    {
        _leagueService = leagueService;
    }
    
    [HttpGet("leagues")]
    public async Task<IActionResult> GetLeagues()
    {
        var leagues = await _leagueService.GetLeagues();
        return Ok(leagues);
    }
    
    [HttpGet("leagues/{id}")]
    public async Task<IActionResult> GetLeague(int id)
    {
        var league = await _leagueService.GetLeague(id);
        if (league == null)
        {
            return NotFound();
        }
        return Ok(league);
    }
    
    [HttpPost("leagues")]
    public async Task<IActionResult> CreateLeague([FromBody] CreateLeagueDto league)
    {
        var createdLeague = await _leagueService.CreateLeague(league);
        return CreatedAtAction(nameof(GetLeague), new {id = createdLeague.Id}, createdLeague);
    }
    
    [HttpPut("leagues/{id}")]
    public async Task<IActionResult> UpdateLeague(int id, [FromBody] LeagueDto league)
    {
        league.Id = id;
        var updatedLeague = await _leagueService.UpdateLeague(league);
        if (updatedLeague == null)
        {
            return NotFound();
        }
        return Ok(updatedLeague);
    }
    
    [HttpDelete("leagues/{id}")]
    public async Task<IActionResult> DeleteLeague(int id)
    {
        await _leagueService.DeleteLeague(id);
        return Ok();
    }
}