using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace betApi.Migrations
{
    public partial class updatePlayer : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Game_League_LeagueId",
                table: "Game");

            migrationBuilder.RenameColumn(
                name: "BirthDate",
                table: "Player",
                newName: "Birthdate");

            migrationBuilder.RenameColumn(
                name: "FavoriTeam",
                table: "Player",
                newName: "FavoriteTeam");

            migrationBuilder.RenameColumn(
                name: "LeagueId",
                table: "Game",
                newName: "Leagueid");

            migrationBuilder.RenameIndex(
                name: "IX_Game_LeagueId",
                table: "Game",
                newName: "IX_Game_Leagueid");

            migrationBuilder.AddForeignKey(
                name: "FK_Game_League_Leagueid",
                table: "Game",
                column: "Leagueid",
                principalTable: "League",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Game_League_Leagueid",
                table: "Game");

            migrationBuilder.RenameColumn(
                name: "Birthdate",
                table: "Player",
                newName: "BirthDate");

            migrationBuilder.RenameColumn(
                name: "FavoriteTeam",
                table: "Player",
                newName: "FavoriTeam");

            migrationBuilder.RenameColumn(
                name: "Leagueid",
                table: "Game",
                newName: "LeagueId");

            migrationBuilder.RenameIndex(
                name: "IX_Game_Leagueid",
                table: "Game",
                newName: "IX_Game_LeagueId");

            migrationBuilder.AddForeignKey(
                name: "FK_Game_League_LeagueId",
                table: "Game",
                column: "LeagueId",
                principalTable: "League",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }
    }
}
