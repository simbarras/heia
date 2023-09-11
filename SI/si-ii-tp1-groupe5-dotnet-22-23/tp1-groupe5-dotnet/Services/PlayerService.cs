using Microsoft.EntityFrameworkCore;
using si_ii_tp1_groupe5_dotnet_22_23.Contexts;
using si_ii_tp1_groupe5_dotnet_22_23.Dto;
using si_ii_tp1_groupe5_dotnet_22_23.Extensions;

namespace si_ii_tp1_groupe5_dotnet_22_23.Services;

public class PlayerService
{
    private readonly Tp1DbContext _context;
    private readonly TeamService _teamService;

    public PlayerService(Tp1DbContext context, TeamService teamService)
    {
        _context = context;
        _teamService = teamService;
    }

    public async Task<PlayerDto?> GetById(int id)
    {
        return (await _context.Players.FindAsync(id))?.ToDto();
    }
    
    public async Task<List<PlayerDto>> GetAll()
    {
        var players = await _context.Players.ToListAsync();
        foreach (var player in players)
        {
            player.Team = (await _teamService.GetTeamById(player.TeamId)).ToEntity();
        }
        return await _context.Players.Select(p => p.ToDto()).ToListAsync();
    }
    
    public async Task<PlayerDto> Create(CreatePlayerDto playerDto)
    {
        var player = playerDto.ToEntity();
        _context.Players.Add(player);
        await _context.SaveChangesAsync();
        player.Team = (await _teamService.GetTeamById(player.TeamId))?.ToEntity();
        return player.ToDto();
    }
    
    public async Task<PlayerDto> Update(PlayerDto playerDto)
    {
        var player = await _context.Players.FindAsync(playerDto.Id);
        player.Birthdate = playerDto.Birthdate;
        player.Firstname = playerDto.Firstname;
        player.Lastname = playerDto.Lastname;
        if (playerDto.Team != null)
        {
            player.TeamId = playerDto.Team.Id;
        }
        _context.Players.Update(player);
        await _context.SaveChangesAsync();
        player.Team = (await _teamService.GetTeamById(player.TeamId))?.ToEntity();
        return player.ToDto();
    }
    
    public async Task Delete(int id)
    {
        var player = await _context.Players.FindAsync(id);
        if (player != null)
        {
            _context.Players.Remove(player);
            await _context.SaveChangesAsync();    
        }
    }
}