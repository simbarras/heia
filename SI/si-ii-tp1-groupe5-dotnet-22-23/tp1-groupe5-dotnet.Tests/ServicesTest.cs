using si_ii_tp1_groupe5_dotnet_22_23.Dto;
using si_ii_tp1_groupe5_dotnet_22_23.Entities;
using si_ii_tp1_groupe5_dotnet_22_23.Services;
using Xunit.Abstractions;

namespace tp1_groupe5_dotnet.Tests;

public class ServicesTest: TestWithSqlite
{
    private readonly PlayerService _playerServices;
    private readonly TeamService _teamServices;
    private readonly MatchService _matchService;
    private readonly LeagueService _leagueService;
    
    public ServicesTest()
    {
        _teamServices = new TeamService(DbContext);
        _playerServices = new PlayerService(DbContext, _teamServices);
        _matchService = new MatchService(DbContext, _teamServices);
        _leagueService = new LeagueService(DbContext);
    }


    [Fact]
    public async Task TestGetTeamById()
    {
        var team = await _teamServices.GetTeamById(1);
        Assert.NotNull(team);
        Assert.Equal("Team 1", team.Name);
    }

    [Fact]
    public async Task TestGetAllTeams()
    {
        var teams = await _teamServices.GetAllTeams();
        Assert.NotEmpty(teams);
        for (int i = 0; i < teams.Count; i++)
        {
            Assert.Equal($"Team {i + 1}", teams[i].Name);
        }
    }

    [Fact]
    public async Task TestCreateTeam()
    {
        var createTeamDto = new CreateTeamDto
        {
            Name = "Test Team"
        };
        var team = await _teamServices.CreateTeam(createTeamDto);
        Assert.NotNull(team);
        Assert.Equal(createTeamDto.Name, team.Name);
        var teamFromDb = await _teamServices.GetTeamById(team.Id);
        Assert.NotNull(teamFromDb);
        Assert.Equal(team.Id, teamFromDb.Id);
        Assert.Equal(team.Name, teamFromDb.Name);
    }

    [Fact]
    public async Task TestUpdateTeam()
    {
        var team = await _teamServices.UpdateTeam(1, new UpdateTeamDto
        {
            Name = "Test Team"
        });
        Assert.NotNull(team);
        Assert.Equal("Test Team", team.Name);
        var newTeam = await _teamServices.UpdateTeam(1, new UpdateTeamDto
        {
            Name = "Team 1"
        });
        Assert.NotNull(newTeam);
        Assert.Equal("Team 1", newTeam.Name);
    }

    [Fact]
    public async Task TestDeleteTeam()
    {
        var team = await _teamServices.GetTeamById(1);
        Assert.NotNull(team);
        await _teamServices.DeleteTeam(1);
        team = await _teamServices.GetTeamById(1);
        Assert.Null(team);
        await _teamServices.CreateTeam(new CreateTeamDto
        {
            Name = "Team 1"
        });
    }
    
    [Fact]
    public async Task TestGetPlayerById()
    {
        var player = await _playerServices.GetById(1);
        Assert.NotNull(player);
        Assert.Equal("Player 1", player.Firstname);
        Assert.Equal("Player 1", player.Lastname);
        Assert.Equal("1990-01-01", player.Birthdate);
        Assert.Equal(1, player.Team.Id);
    }
    
    [Fact]
    public async Task TestGetAllPlayers()
    {
        var players = await _playerServices.GetAll();
        Assert.NotEmpty(players);
        for (int i = 0; i < players.Count; i++)
        {
            Assert.Equal($"Player {i + 1}", players[i].Firstname);
            Assert.Equal($"Player {i + 1}", players[i].Lastname);
            Assert.Equal($"1990-01-0{i + 1}", players[i].Birthdate);
            Assert.Equal(i + 1, players[i].Team.Id);
        }
    }
    
    [Fact]
    public async Task TestCreatePlayer()
    {
        var createPlayerDto = new CreatePlayerDto
        {
            Firstname = "Test",
            Lastname = "Player",
            Birthdate = "1990-01-01",
            Team = new TeamDto
            {
                Id = 1
            }
        };
        var player = await _playerServices.Create(createPlayerDto);
        Assert.NotNull(player);
        Assert.Equal(createPlayerDto.Firstname, player.Firstname);
        Assert.Equal(createPlayerDto.Lastname, player.Lastname);
        Assert.Equal(createPlayerDto.Birthdate, player.Birthdate);
        Assert.Equal(createPlayerDto.Team.Id, player.Team.Id);
    }
    
    [Fact]
    public async Task TestUpdatePlayer()
    {
        var player = await _playerServices.Update(new PlayerDto
        {
            Id = 1,
            Firstname = "Test",
            Lastname = "Player",
            Birthdate = "1990-01-01",
        });
        Assert.NotNull(player);
        Assert.Equal("Test", player.Firstname);
        Assert.Equal("Player", player.Lastname);
        Assert.Equal("1990-01-01", player.Birthdate);
    }

    [Fact]
    public async Task TestDeletePlayer()
    {
        var player = await _playerServices.GetById(1);
        Assert.NotNull(player);
        await _playerServices.Delete(1);
        player = await _playerServices.GetById(1);
        Assert.Null(player);
    }
    
    [Fact]
    public async Task TestGetMatchById()
    {
        var match = await _matchService.GetMatchById(1);
        Assert.NotNull(match);
        Assert.Equal("Team 1", match.Team1.Name);
        Assert.Equal("Team 2", match.Team2.Name);
        Assert.Equal("2021-01-01", match.Date);
        Assert.Equal("Place 1", match.Place);
    }
    
    [Fact]
    public async Task TestGetAllMatches()
    {
        var matches = await _matchService.GetMatches();
        Assert.NotEmpty(matches);
        for (int i = 0; i < matches.Count; i++)
        {
            if (i == 0)
            {
                Assert.Equal($"Team {i + 1}", matches[i].Team1.Name);
                Assert.Equal($"Team {i + 2}", matches[i].Team2.Name);
                Assert.Equal($"2021-01-0{i + 1}", matches[i].Date);
                Assert.Equal($"Place {i + 1}", matches[i].Place);    
            }
            else
            {
                Assert.Equal($"Team {i + 2}", matches[i].Team1.Name);
                Assert.Equal($"Team {i + 3}", matches[i].Team2.Name);
                Assert.Equal($"2021-01-0{i + 1}", matches[i].Date);
                Assert.Equal($"Place {i + 1}", matches[i].Place);
            }
        }
    }
    
    [Fact]
    public async Task TestCreateMatch()
    {
        var createMatchDto = new CreateMatchDto
        {
            Team1 = new TeamDto
            {
                Id = 1
            },
            Team2 = new TeamDto
            {
                Id = 2
            },
            Date = "2021-01-01",
            Place = "Place 1"
        };
        var match = await _matchService.CreateMatch(createMatchDto);
        Assert.NotNull(match);
        Assert.Equal(createMatchDto.Team1.Id, match.Team1.Id);
        Assert.Equal(createMatchDto.Team2.Id, match.Team2.Id);
        Assert.Equal(createMatchDto.Date, match.Date);
        Assert.Equal(createMatchDto.Place, match.Place);
    }
    
    [Fact]
    public async Task TestUpdateMatch()
    {
        var match = await _matchService.UpdateMatch(new MatchDto
        {
            Id = 1,
            Team1 = new TeamDto
            {
                Id = 1
            },
            Team2 = new TeamDto
            {
                Id = 2
            },
            Date = "2021-01-01",
            Place = "Place Test"
        });
        Assert.NotNull(match);
        Assert.Equal(1, match.Team1.Id);
        Assert.Equal(2, match.Team2.Id);
        Assert.Equal("2021-01-01", match.Date);
        Assert.Equal("Place Test", match.Place);
    }

    [Fact]
    public async Task TestDeleteMatch()
    {
        var match = await _matchService.GetMatchById(1);
        Assert.NotNull(match);
        await _matchService.DeleteMatch(1);
        match = await _matchService.GetMatchById(1);
        Assert.Null(match);
    }

    [Fact]
    public async Task TestGetLeagueById()
    {
        var league = await _leagueService.GetLeague(1);
        Assert.NotNull(league);
        Assert.Equal("League 1", league.Name);
        Assert.Equal(2, league.Matches.Count);
        Assert.Equal(4, league.Players.Count);
    }
    
    [Fact]
    public async Task TestGetAllLeagues()
    {
        var leagues = await _leagueService.GetLeagues();
        Assert.NotEmpty(leagues);
        for (int i = 0; i < leagues.Count; i++)
        {
            Assert.Equal($"League {i + 1}", leagues[i].Name);
            Assert.Equal(2, leagues[i].Matches.Count);
            Assert.Equal(4, leagues[i].Players.Count);
        }
    }
    
    [Fact]
    public async Task TestCreateLeague()
    {
        var createLeagueDto = new CreateLeagueDto
        {
            Name = "League Test",
            Matches = new List<MatchDto>
            {
                new MatchDto
                {
                    Id = 1
                },
                new MatchDto
                {
                    Id = 2
                }
            },
            Players = new List<PlayerDto>
            {
                new PlayerDto
                {
                    Id = 1
                },
                new PlayerDto
                {
                    Id = 2
                },
                new PlayerDto
                {
                    Id = 3
                },
                new PlayerDto
                {
                    Id = 4
                }
            }
        };
        var league = await _leagueService.CreateLeague(createLeagueDto);
        Assert.NotNull(league);
        Assert.Equal(createLeagueDto.Name, league.Name);
        Assert.Equal(createLeagueDto.Matches.Count, league.Matches.Count);
        Assert.Equal(createLeagueDto.Players.Count, league.Players.Count);
    }
    
    [Fact]
    public async Task TestUpdateLeague()
    {
        var league = await _leagueService.UpdateLeague(new LeagueDto
        {
            Id = 1,
            Name = "League Test",
            Matches = new List<MatchDto>
            {
                new MatchDto
                {
                    Id = 1
                },
                new MatchDto
                {
                    Id = 2
                }
            },
            Players = new List<PlayerDto>
            {
                new PlayerDto
                {
                    Id = 1
                },
                new PlayerDto
                {
                    Id = 2
                },
                new PlayerDto
                {
                    Id = 3
                },
                new PlayerDto
                {
                    Id = 4
                }
            }
        });
        Assert.NotNull(league);
        Assert.Equal("League Test", league.Name);
        Assert.Equal(2, league.Matches.Count);
        Assert.Equal(4, league.Players.Count);
    }
    
    [Fact]
    public async Task TestDeleteLeague()
    {
        var league = await _leagueService.GetLeague(1);
        Assert.NotNull(league);
        await _leagueService.DeleteLeague(1);
        league = await _leagueService.GetLeague(1);
        Assert.Null(league);
    }
}