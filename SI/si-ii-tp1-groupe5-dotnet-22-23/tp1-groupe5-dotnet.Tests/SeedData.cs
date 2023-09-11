using si_ii_tp1_groupe5_dotnet_22_23.Dto;
using si_ii_tp1_groupe5_dotnet_22_23.Entities;

namespace tp1_groupe5_dotnet.Tests;

public class SeedData
{
    public List<Team> GetTeams()
    {
        return new List<Team>
        {
            new Team
            {
                Id = 1,
                Name = "Team 1",
            },
            new Team
            {
                Id = 2,
                Name = "Team 2",
            },
            new Team
            {
                Id = 3,
                Name = "Team 3",
            },
            new Team
            {
                Id = 4,
                Name = "Team 4",
            },
        };
    }

    public List<Match> GetMatches()
    {
        return new List<Match>
        {
            new Match
            {
                Id = 1,
                Date = "2021-01-01",
                Team1Id = 1,
                Team2Id = 2,
                Place = "Place 1",
                LeagueId = 1,
            },
            new Match
            {
                Id = 2,
                Date = "2021-01-02",
                Team1Id = 3,
                Team2Id = 4,
                Place = "Place 2",
                LeagueId = 1,
            },
        };
    }

    public List<Player> GetPlayers()
    {
        return new List<Player>
        {
            new Player
            {
                Id = 1,
                Birthdate = "1990-01-01",
                Firstname = "Player 1",
                Lastname = "Player 1",
                TeamId = 1,
            },
            new Player
            {
                Id = 2,
                Birthdate = "1990-01-02",
                Firstname = "Player 2",
                Lastname = "Player 2",
                TeamId = 2,
            },
            new Player
            {
                Id = 3,
                Birthdate = "1990-01-03",
                Firstname = "Player 3",
                Lastname = "Player 3",
                TeamId = 3,
            },
            new Player
            {
                Id = 4,
                Birthdate = "1990-01-04",
                Firstname = "Player 4",
                Lastname = "Player 4",
                TeamId = 4,
            },
        };
    }

    public List<League> GetLeagues()
    {
        return new List<League>
        {
            new League
            {
                Id = 1,
                Name = "League 1",
                Players = new List<LeaguePlayer>
                {
                    new LeaguePlayer
                    {
                        PlayerId = 1
                    },
                    new LeaguePlayer
                    {
                        PlayerId = 2
                    },
                    new LeaguePlayer
                    {
                        PlayerId = 3
                    },
                    new LeaguePlayer
                    {
                        PlayerId = 4
                    },
                },
            }
        };
    }
}