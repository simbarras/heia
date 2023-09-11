using Microsoft.EntityFrameworkCore;
using si_ii_tp1_groupe5_dotnet_22_23.Entities;

namespace si_ii_tp1_groupe5_dotnet_22_23.Contexts;

public class Tp1DbContext : DbContext
{
    public DbSet<Team> Teams { get; set; }
    public DbSet<Player> Players { get; set; }
    public DbSet<League> Leagues { get; set; }
    public DbSet<Match> Matches { get; set; }
    
    public DbSet<LeaguePlayer> LeaguePlayer { get; set; }

    public Tp1DbContext(DbContextOptions<Tp1DbContext> options) : base(options)
    {
    }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<LeaguePlayer>()
            .HasKey(lp => new { lp.LeagueId, lp.PlayerId });

        modelBuilder.Entity<LeaguePlayer>()
            .HasOne(lp => lp.League)
            .WithMany(l => l.Players)
            .HasForeignKey(lp => lp.LeagueId)
            .OnDelete(DeleteBehavior.Cascade);

        modelBuilder.Entity<LeaguePlayer>()
            .HasOne(lp => lp.Player)
            .WithMany(p => p.Leagues)
            .HasForeignKey(lp => lp.PlayerId)
            .OnDelete(DeleteBehavior.Cascade);

        modelBuilder.Entity<League>().HasMany((l) => l.Matches).WithOne(m => m.League).HasForeignKey(m => m.LeagueId).OnDelete(DeleteBehavior.SetNull);
        modelBuilder.Entity<Match>().HasOne<Team>(m => m.Team1).WithMany(t => t.HomeMatches).HasForeignKey(m => m.Team1Id);
        modelBuilder.Entity<Match>().HasOne<Team>(m => m.Team2).WithMany(t => t.AwayMatches).HasForeignKey(m => m.Team2Id);
    }
}