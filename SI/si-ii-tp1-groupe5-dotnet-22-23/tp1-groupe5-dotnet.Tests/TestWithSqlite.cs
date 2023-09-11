using Microsoft.Data.Sqlite;
using Microsoft.EntityFrameworkCore;
using si_ii_tp1_groupe5_dotnet_22_23.Contexts;

namespace tp1_groupe5_dotnet.Tests;


public abstract class TestWithSqlite : IDisposable
{
    private const string InMemoryConnectionString = "DataSource=:memory:";
    private readonly SqliteConnection _connection;

    protected readonly Tp1DbContext DbContext;

    protected TestWithSqlite()
    {
        _connection = new SqliteConnection(InMemoryConnectionString);
        _connection.Open();
        var options = new DbContextOptionsBuilder<Tp1DbContext>()
            .UseSqlite(_connection)
            .EnableSensitiveDataLogging()
            .Options;
        DbContext = new Tp1DbContext(options);
        DbContext.Database.EnsureDeleted();
        DbContext.Database.EnsureCreated();
        var seedData = new SeedData();
        foreach (var team in seedData.GetTeams())
        {
            DbContext.Teams.Add(team);
        }
        foreach (var league in seedData.GetLeagues())
        {
            DbContext.Leagues.Add(league);
        }
        foreach (var player in seedData.GetPlayers())
        {
            DbContext.Players.Add(player);
        }
        
        foreach (var match in seedData.GetMatches())
        {
            DbContext.Matches.Add(match);
        }

        DbContext.SaveChanges();
    }

    public void Dispose()
    {
        Console.WriteLine("Closing the connection...");
        _connection.Close();
    }
}
