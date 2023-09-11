using Microsoft.EntityFrameworkCore;
using si_ii_tp1_groupe5_dotnet_22_23.Contexts;
using si_ii_tp1_groupe5_dotnet_22_23.Dto;
using si_ii_tp1_groupe5_dotnet_22_23.Extensions;

namespace si_ii_tp1_groupe5_dotnet_22_23.Services;

public class MatchService
{
    private readonly Tp1DbContext _context;
    private readonly TeamService _teamService;

    public MatchService(Tp1DbContext context, TeamService teamService)
    {
        _context = context;
        _teamService = teamService;
    }

    public async Task<MatchDto?> GetMatchById(int id)
    {
        return (await _context.Matches.FindAsync(id))?.ToDto();
    }
    
    public async Task<List<MatchDto>> GetMatches()
    {
        
        var matches = await _context.Matches.ToListAsync();
        if (matches.Count == 0)
        {
            return new List<MatchDto>();
        }
        foreach (var match in matches)
        {
            match.Team1 = (await _teamService.GetTeamById(match.Team1Id)).ToEntity();
            match.Team2 = (await _teamService.GetTeamById(match.Team2Id)).ToEntity();
        }
        return matches.Select(match => match.ToDto()).ToList();
    }
    
    public async Task<MatchDto> CreateMatch(CreateMatchDto matchDto)
    {
        var match = matchDto.ToEntity();
        await _context.Matches.AddAsync(match);
        await _context.SaveChangesAsync();
        match.Team1 = (await _teamService.GetTeamById(match.Team1Id)).ToEntity();
        match.Team2 = (await _teamService.GetTeamById(match.Team2Id)).ToEntity();
        return match.ToDto();
    }
    
    public async Task<MatchDto> UpdateMatch(MatchDto matchDto)
    {
        var match = await _context.Matches.FindAsync(matchDto.Id);
        match.Date = matchDto.Date;
        match.Team1Id = matchDto.Team1.Id;
        match.Team2Id = matchDto.Team2.Id;
        match.Place = matchDto.Place;
        _context.Matches.Update(match);
        await _context.SaveChangesAsync();
        match.Team1 = (await _teamService.GetTeamById(match.Team1Id)).ToEntity();
        match.Team2 = (await _teamService.GetTeamById(match.Team2Id)).ToEntity();
        return match.ToDto();
    }
    
    public async Task DeleteMatch(int id)
    {
        var match = await _context.Matches.FindAsync(id);
        if (match != null)
        {
            _context.Matches.Remove(match);
            await _context.SaveChangesAsync();    
        }
    }
}