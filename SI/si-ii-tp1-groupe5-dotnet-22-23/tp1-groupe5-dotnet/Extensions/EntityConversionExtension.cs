using System.Runtime.CompilerServices;
using si_ii_tp1_groupe5_dotnet_22_23.Dto;
using si_ii_tp1_groupe5_dotnet_22_23.Entities;

namespace si_ii_tp1_groupe5_dotnet_22_23.Extensions;

public static class EntityConversionExtension
{
    public static League ToEntity(this LeagueDto dto)
    {
        var league = new League
        {
            Id = dto.Id,
            Name = dto.Name,
        };
        var matchesDto = dto.Matches.ToList().Select((m) => m.ToEntity()).ToList();
        league.Matches = matchesDto;
        return league;
    } 
    
    public static Match ToEntity(this MatchDto dto)
    {
        var match = new Match
        {
            Id = dto.Id,
            Date = dto.Date,
            Place = dto.Place,
        };
        if (dto.Team1 != null)
        {
            match.Team1 = dto.Team1.ToEntity();
        }
        if (dto.Team2 != null)
        {
            match.Team2 = dto.Team2.ToEntity();
        }
        return match;
    }
    
    public static Team ToEntity(this TeamDto dto)
    {
        var team = new Team
        {
            Id = dto.Id,
            Name = dto.Name,
        };
        return team;
    }
    
    public static Player ToEntity(this PlayerDto dto)
    {
        var player = new Player
        {
            Id = dto.Id,
            Firstname = dto.Firstname,
            Lastname = dto.Lastname,
            TeamId = dto.Team.Id,
            Birthdate = dto.Birthdate
        };
        return player;
    }
    
    public static LeagueDto ToDto(this League entity)
    {
        var league = new LeagueDto
        {
            Id = entity.Id,
            Name = entity.Name,
        };
        var matchesDto = entity.Matches.ToList().Select((m) => m.ToDto()).ToList();
        var PlayersDto = entity.Players.ToList().Select((p) => p.Player.ToDto()).ToList();
        league.Matches = matchesDto;
        league.Players = PlayersDto;
        return league;
    }
    
    public static MatchDto ToDto(this Match entity)
    {
        var match = new MatchDto
        {
            Id = entity.Id,
            Date = entity.Date,
            Place = entity.Place,
        };
        if (entity.Team1 != null)
        {
            match.Team1 = entity.Team1.ToDto();
        }
        if (entity.Team2 != null)
        {
            match.Team2 = entity.Team2.ToDto();
        }
        return match;
    }
    
    public static TeamDto ToDto(this Team entity)
    {
        var team = new TeamDto
        {
            Id = entity.Id,
            Name = entity.Name,
        };
        return team;
    }
    
    public static PlayerDto ToDto(this Player entity)
    {
        var player = new PlayerDto
        {
            Id = entity.Id,
            Firstname = entity.Firstname,
            Lastname = entity.Lastname,
            Birthdate = entity.Birthdate
        };
        if (entity.Team != null)
        {
            player.Team = entity.Team.ToDto();
        }
        return player;
    }
    
    public static Team ToEntity(this CreateTeamDto dto)
    {
        var team = new Team
        {
            Name = dto.Name,
        };
        return team;
    }
    
    public static Match ToEntity(this CreateMatchDto dto)
    {
        var match = new Match
        {
            Date = dto.Date,
            Place = dto.Place,
            Team1Id = dto.Team1.Id,
            Team2Id = dto.Team2.Id,
        };
        return match;
    }
    
    public static Player ToEntity(this CreatePlayerDto dto)
    {
        var player = new Player
        {
            Firstname = dto.Firstname,
            Lastname = dto.Lastname,
            TeamId = dto.Team.Id,
            Birthdate = dto.Birthdate
        };
        return player;
    }
    
    public static League ToEntity(this CreateLeagueDto dto)
    {
        var league = new League
        {
            Name = dto.Name,
        };
        var matches = dto.Matches.ToList().Select((m) => m.ToEntity()).ToList();
        league.Matches = matches;
        return league;
    } 
    
}