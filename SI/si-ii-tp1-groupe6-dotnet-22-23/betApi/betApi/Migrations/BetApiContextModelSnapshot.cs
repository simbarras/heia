﻿// <auto-generated />
using System;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Infrastructure;
using Microsoft.EntityFrameworkCore.Storage.ValueConversion;
using betApi.Data;

#nullable disable

namespace betApi.Migrations
{
    [DbContext(typeof(BetApiContext))]
    partial class BetApiContextModelSnapshot : ModelSnapshot
    {
        protected override void BuildModel(ModelBuilder modelBuilder)
        {
#pragma warning disable 612, 618
            modelBuilder
                .HasAnnotation("ProductVersion", "6.0.8")
                .HasAnnotation("Relational:MaxIdentifierLength", 64);

            modelBuilder.Entity("betApi.Models.Game", b =>
                {
                    b.Property<int>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("int");

                    b.Property<string>("AwayTeam")
                        .IsRequired()
                        .HasColumnType("longtext");

                    b.Property<DateTime>("Date")
                        .HasColumnType("datetime(6)");

                    b.Property<string>("HomeTeam")
                        .IsRequired()
                        .HasColumnType("longtext");

                    b.Property<int>("Leagueid")
                        .HasColumnType("int");

                    b.Property<string>("Location")
                        .IsRequired()
                        .HasColumnType("longtext");

                    b.HasKey("Id");

                    b.HasIndex("Leagueid");

                    b.ToTable("Game");
                });

            modelBuilder.Entity("betApi.Models.League", b =>
                {
                    b.Property<int>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("int");

                    b.Property<string>("Name")
                        .IsRequired()
                        .HasColumnType("longtext");

                    b.HasKey("Id");

                    b.ToTable("League");
                });

            modelBuilder.Entity("betApi.Models.LeaguePlayer", b =>
                {
                    b.Property<int>("LeagueId")
                        .HasColumnType("int")
                        .HasColumnOrder(1);

                    b.Property<int>("PlayerId")
                        .HasColumnType("int")
                        .HasColumnOrder(2);

                    b.HasKey("LeagueId", "PlayerId");

                    b.HasIndex("PlayerId");

                    b.ToTable("LeaguePlayer");
                });

            modelBuilder.Entity("betApi.Models.Player", b =>
                {
                    b.Property<int>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("int");

                    b.Property<DateTime>("Birthdate")
                        .HasColumnType("datetime(6)");

                    b.Property<string>("FavoriteTeam")
                        .IsRequired()
                        .HasColumnType("longtext");

                    b.Property<string>("FirstName")
                        .IsRequired()
                        .HasColumnType("longtext");

                    b.Property<string>("LastName")
                        .IsRequired()
                        .HasColumnType("longtext");

                    b.HasKey("Id");

                    b.ToTable("Player");
                });

            modelBuilder.Entity("betApi.Models.Game", b =>
                {
                    b.HasOne("betApi.Models.League", null)
                        .WithMany("Games")
                        .HasForeignKey("Leagueid")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();
                });

            modelBuilder.Entity("betApi.Models.LeaguePlayer", b =>
                {
                    b.HasOne("betApi.Models.League", "League")
                        .WithMany("Players")
                        .HasForeignKey("LeagueId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.HasOne("betApi.Models.Player", "Player")
                        .WithMany("Leagues")
                        .HasForeignKey("PlayerId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.Navigation("League");

                    b.Navigation("Player");
                });

            modelBuilder.Entity("betApi.Models.League", b =>
                {
                    b.Navigation("Games");

                    b.Navigation("Players");
                });

            modelBuilder.Entity("betApi.Models.Player", b =>
                {
                    b.Navigation("Leagues");
                });
#pragma warning restore 612, 618
        }
    }
}
