namespace si_ii_tp1_groupe5_dotnet_22_23.Entities;

public class LeaguePlayer
{
    public int LeagueId { get; set; }
    public League League { get; set; }
    public int PlayerId { get; set; }
    public Player Player { get; set; }
}