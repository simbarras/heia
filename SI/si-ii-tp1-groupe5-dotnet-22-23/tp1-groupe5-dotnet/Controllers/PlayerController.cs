using Microsoft.AspNetCore.Mvc;
using si_ii_tp1_groupe5_dotnet_22_23.Dto;
using si_ii_tp1_groupe5_dotnet_22_23.Services;

namespace si_ii_tp1_groupe5_dotnet_22_23.Controllers;

public class PlayerController: ApiControllerBase
{
    private readonly PlayerService _service;

    public PlayerController(PlayerService service)
    {
        _service = service;
    }
    
    [HttpGet("players")]
    public async Task<IActionResult> Get()
    {
        var players = await _service.GetAll();
        return Ok(players);
    }
    
    [HttpGet("players/{id}")]
    public async Task<IActionResult> Get(int id)
    {
        var player = await _service.GetById(id);
        return Ok(player);
    }
    
    [HttpPost("players")]
    public async Task<IActionResult> Post([FromBody] CreatePlayerDto player)
    {
        var newPlayer = await _service.Create(player);
        return Ok(newPlayer);
    }
    
    [HttpPut("players/{id}")]
    public async Task<IActionResult> Put(int id, [FromBody] PlayerDto player)
    {
        player.Id = id;
        var updatedPlayer = await _service.Update(player);
        return Ok(updatedPlayer);
    }
    
    [HttpDelete("players/{id}")]
    public async Task<IActionResult> Delete(int id)
    {
        await _service.Delete(id);
        return Ok();
    }
}