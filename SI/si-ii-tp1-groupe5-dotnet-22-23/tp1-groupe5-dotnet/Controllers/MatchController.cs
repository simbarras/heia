using Microsoft.AspNetCore.Mvc;
using si_ii_tp1_groupe5_dotnet_22_23.Dto;
using si_ii_tp1_groupe5_dotnet_22_23.Services;

namespace si_ii_tp1_groupe5_dotnet_22_23.Controllers;

public class MatchController: ApiControllerBase
{
    private readonly MatchService _matchService;

    public MatchController(MatchService matchService)
    {
        _matchService = matchService;
    }
    
    [HttpGet("matches")]
    public async Task<IActionResult> Get()
    {
        var matches = await _matchService.GetMatches();
        return Ok(matches);
    }
    
    [HttpGet("matches/{id}")]
    public async Task<IActionResult> Get(int id)
    {
        var match = await _matchService.GetMatchById(id);
        if (match == null)
        {
            return NotFound();
        }
        return Ok(match);
    }
    
    [HttpPost("matches")]
    public async Task<IActionResult> Post([FromBody] CreateMatchDto match)
    {
        var newMatch = await _matchService.CreateMatch(match);
        return CreatedAtAction(nameof(Get), new {id = newMatch.Id}, newMatch);
    }
    
    [HttpPut("matches/{id}")]
    public async Task<IActionResult> Put(int id, [FromBody] MatchDto match)
    {
        match.Id = id;
        var updatedMatch = await _matchService.UpdateMatch(match);
        if (updatedMatch == null)
        {
            return NotFound();
        }
        return Ok(updatedMatch);
    }
    
    [HttpDelete("matches/{id}")]
    public async Task<IActionResult> Delete(int id)
    {
        await _matchService.DeleteMatch(id);
        return NoContent();
    }
    
    
}