using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using si_ii_tp1_groupe5_dotnet_22_23.Contexts;
using si_ii_tp1_groupe5_dotnet_22_23.Dto;
using si_ii_tp1_groupe5_dotnet_22_23.Entities;
using si_ii_tp1_groupe5_dotnet_22_23.Extensions;

namespace si_ii_tp1_groupe5_dotnet_22_23.Services;

public class LeagueService
{
    private readonly Tp1DbContext _context;

    public LeagueService(Tp1DbContext context)
    {
        _context = context;
    }

    public async Task<List<LeagueDto>> GetLeagues()
    {
        var leagues = await _context.Leagues.ToListAsync();
        leagues.ForEach((l) => l.Matches = _context.Matches.Where((m) => m.League.Id == l.Id).ToList());
        leagues.ForEach((l) => l.Players = _context.LeaguePlayer.Where(lp => lp.LeagueId == l.Id).ToList());
        leagues.ForEach(l => l.Players.ToList().ForEach(lp => lp.Player = _context.Players.Find(lp.PlayerId)));
        return (await _context.Leagues.ToListAsync()).Select((t) => t.ToDto()).ToList();
    }
    
    public async Task<LeagueDto?> GetLeague(int id)
    {
        var league = await _context.Leagues.FindAsync(id);
        if (league == null)
        {
            return null;
        }
        league.Matches = _context.Matches.Where((m) => m.League.Id == league.Id).ToList();
        league.Players = _context.LeaguePlayer.Where(lp => lp.LeagueId == league.Id).ToList();
        league.Players.ToList().ForEach(lp => lp.Player = _context.Players.Find(lp.PlayerId));
        return league.ToDto();
    }

    public async Task<LeagueDto> CreateLeague(CreateLeagueDto league)
    {
        var entity = league.ToEntity();
        entity.Players = league.Players.Select((p) => new LeaguePlayer { PlayerId = p.Id, League = entity })
            .ToList();
        entity.Matches = entity.Matches.Select((match) => _context.Matches.Where((m) => m.Id == match.Id).First())
            .ToList();
        _context.Leagues.Add(entity);
        await _context.SaveChangesAsync();
        
        return await GetLeague(entity.Id);
    }
    
    public async Task<LeagueDto> UpdateLeague(LeagueDto league)
    {
        var entity = await _context.Leagues.FindAsync(league.Id);
        entity.Name = league.Name;
        _context.Entry(entity).State = EntityState.Modified;
        entity.Matches = league.Matches.Select((match) => _context.Matches.Where((m) => m.Id == match.Id).First())
            .ToList();
        var leaguePlayers = new List<LeaguePlayer>();
        foreach (var playerDto in league.Players)
        {
            var leaguePlayer = _context.LeaguePlayer.Any(lp => lp.PlayerId == playerDto.Id && lp.LeagueId == league.Id);
            if (!leaguePlayer)
            {
                leaguePlayers.Add(new LeaguePlayer { PlayerId = playerDto.Id, League = entity });
            }
        }

        entity.Players = leaguePlayers;
        await _context.SaveChangesAsync();
        return league;
    }
    
    public async Task DeleteLeague(int id)
    {
        var league = await _context.Leagues.FindAsync(id);
        if (league != null)
        {
            _context.Leagues.Remove(league);
            await _context.SaveChangesAsync();
        }
    }
}