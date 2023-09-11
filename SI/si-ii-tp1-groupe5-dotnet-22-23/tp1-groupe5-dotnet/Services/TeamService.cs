using Microsoft.EntityFrameworkCore;
using si_ii_tp1_groupe5_dotnet_22_23.Contexts;
using si_ii_tp1_groupe5_dotnet_22_23.Dto;
using si_ii_tp1_groupe5_dotnet_22_23.Extensions;

namespace si_ii_tp1_groupe5_dotnet_22_23.Services;

public class TeamService
{
    private readonly Tp1DbContext _context;

    public TeamService(Tp1DbContext context)
    {
        _context = context;
    }
    
    public async Task<List<TeamDto>> GetAllTeams()
    {
        var teams = await _context.Teams.ToListAsync();
        return await _context.Teams.Select((t) => t.ToDto()).ToListAsync();
    }
    
    public async Task<TeamDto?> GetTeamById(int id)
    {
        return await _context.Teams.Where((t) => t.Id == id).Select((t) => t.ToDto()).FirstOrDefaultAsync();
    }
    
    public async Task<TeamDto> CreateTeam(CreateTeamDto teamDto)
    {
        var team = teamDto.ToEntity();
        _context.Teams.Add(team);
        await _context.SaveChangesAsync();
        return team.ToDto();
    }
    
    public async Task<TeamDto> UpdateTeam(int id, UpdateTeamDto teamDto)
    {
        var team = await _context.Teams.FindAsync(id);
        if (team == null)
        {
            throw new Exception("Team not found");
        }
        team.Name = teamDto.Name;
        await _context.SaveChangesAsync();
        return team.ToDto();
    }
    
    public async Task DeleteTeam(int id)
    {
        var team = await _context.Teams.FindAsync(id);
        if (team != null)
        {
            _context.Teams.Remove(team);
            await _context.SaveChangesAsync();    
        }
    }
}