using betApi.Controllers;
using betApi.Data;
using System;
using Microsoft.Data.Sqlite;
using Microsoft.EntityFrameworkCore;

using Xunit;
namespace betApi.Tests;

public abstract class TestWithSqLite : IDisposable
{
    private const string InMemoryConnectionString = "DataSource=:memory:";
    private readonly SqliteConnection _connection;

    protected readonly BetApiContext DbContext;

    protected TestWithSqLite()
    {
        _connection = new SqliteConnection(InMemoryConnectionString);
        _connection.Open();
        var options = new DbContextOptionsBuilder<BetApiContext>()
            .UseSqlite(_connection)
            .EnableSensitiveDataLogging()
            .Options;
        DbContext = new BetApiContext(options);
        DbContext.Database.EnsureDeleted();
        DbContext.Database.EnsureCreated();

    }


    public void Dispose()
    {
        Console.WriteLine("Closing the connection...");
        _connection.Close();
    }
}

public class ServicesTest : TestWithSqLite
{ 

    public ServicesTest()
    {
    }

    [Fact]
    public void TestLeague()
    {
        var leagueCtrl = new LeaguesController(DbContext);
        leagueCtrl.GetAddLeague("test");
        DbContext.SaveChanges();
        Assert.NotNull(leagueCtrl.GetLeague(1));
        leagueCtrl.DeleteLeague(1);
        DbContext.SaveChanges();
        Assert.True(true);

    }
}