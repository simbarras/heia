using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore;
using betApi.Models;

namespace betApi.Data
{
    public class BetApiContext : DbContext
    {
        public BetApiContext (DbContextOptions<BetApiContext> options)
            : base(options)
        {
        }

        public DbSet<betApi.Models.Game> Game { get; set; } = default!;

        public DbSet<betApi.Models.League> League { get; set; }

        public DbSet<betApi.Models.Player> Player { get; set; }

        public DbSet<betApi.Models.LeaguePlayer> LeaguePlayer { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<League>()
                .HasMany(l => l.Games)
                .WithOne();

            modelBuilder.Entity<LeaguePlayer>()
                .HasKey(lp => new { lp.Leagueid, lp.Playerid });

            modelBuilder.Entity<LeaguePlayer>()
                .HasOne(lp => lp.League)
                .WithMany(l => l.Players)
                .HasForeignKey(lp => lp.Leagueid);

            modelBuilder.Entity<LeaguePlayer>()
                .HasOne(lp => lp.Player)
                .WithMany(p => p.Leagues)
                .HasForeignKey(lp => lp.Playerid);
        }
    }
}
