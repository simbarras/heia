using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace si_ii_tp1_groupe5_dotnet_22_23.Migrations
{
    public partial class UpdateOnDeleteLeague : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_LeaguePlayer_Leagues_LeagueId",
                table: "LeaguePlayer");

            migrationBuilder.DropForeignKey(
                name: "FK_LeaguePlayer_Players_PlayerId",
                table: "LeaguePlayer");

            migrationBuilder.AddForeignKey(
                name: "FK_LeaguePlayer_Leagues_LeagueId",
                table: "LeaguePlayer",
                column: "LeagueId",
                principalTable: "Leagues",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_LeaguePlayer_Players_PlayerId",
                table: "LeaguePlayer",
                column: "PlayerId",
                principalTable: "Players",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_LeaguePlayer_Leagues_LeagueId",
                table: "LeaguePlayer");

            migrationBuilder.DropForeignKey(
                name: "FK_LeaguePlayer_Players_PlayerId",
                table: "LeaguePlayer");

            migrationBuilder.AddForeignKey(
                name: "FK_LeaguePlayer_Leagues_LeagueId",
                table: "LeaguePlayer",
                column: "LeagueId",
                principalTable: "Leagues",
                principalColumn: "Id");

            migrationBuilder.AddForeignKey(
                name: "FK_LeaguePlayer_Players_PlayerId",
                table: "LeaguePlayer",
                column: "PlayerId",
                principalTable: "Players",
                principalColumn: "Id");
        }
    }
}
