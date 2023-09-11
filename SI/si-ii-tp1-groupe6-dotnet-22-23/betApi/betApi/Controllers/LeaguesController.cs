using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using betApi.Data;
using betApi.Models;

namespace betApi.Controllers
{
    [Route("league")]
    [ApiController]
    public class LeaguesController : ControllerBase
    {
        private readonly BetApiContext _context;

        public LeaguesController(BetApiContext context)
        {
            _context = context;
        }

        // GET: api/Leagues
        [Route("/leagues")]
        [HttpGet]
        public async Task<ActionResult<IEnumerable<League>>> GetLeague()
        {
            var leagues = _context.League.Select(l => new
            {
                l.Id,
                l.Name,
                Players = l.Players.Select(lp => new
                {
                    lp.Player.Id,
                    lp.Player.Lastname,
                    lp.Player.Firstname,
                    lp.Player.Birthdate,
                    lp.Player.FavoriteTeam
                }).ToList(),
                Games = l.Games.Select(g => new
                {
                    g.Id,
                    g.Date,
                    g.HomeTeam,
                    g.AwayTeam,
                    g.Location,
                    g.Leagueid
                }).ToList(),
            }).ToList();
            return Ok(leagues);
        }

        // GET: api/Leagues/5
        [HttpGet("{id}")]
        public async Task<ActionResult<League>> GetLeague(int id)
        {
            var leagues = _context.League.Select(l => new
            {
                l.Id,
                l.Name,
                Players = l.Players.Select(lp => new
                {
                    lp.Player.Id,
                    lp.Player.Lastname,
                    lp.Player.Firstname,
                    lp.Player.Birthdate,
                    lp.Player.FavoriteTeam
                }).ToList(),
                Games = l.Games.Select(g => new
                {
                    g.Id,
                    g.Date,
                    g.HomeTeam,
                    g.AwayTeam,
                    g.Location,
                    g.Leagueid
                }).ToList(),
            }).ToList();

            var league = leagues.Find(l => l.Id == id);

            return Ok(league);
        }

        // POST: api/Leagues
        // To protect from overposting attacks, see https://go.microsoft.com/fwlink/?linkid=2123754
        //[Route("/league")]
        [HttpGet("add/{name}")]
        public async Task<ActionResult<League>> GetAddLeague(string name)
        {
            League l = new League() { Name = name };
            _context.League.Add(l);
            await _context.SaveChangesAsync();

            return NoContent();
        }

        // DELETE: api/Leagues/5
        [HttpGet("delete/{id}")]
        public async Task<IActionResult> DeleteLeague(int id)
        {
            var league = await _context.League.FindAsync(id);
            if (league == null)
            {
                return NotFound();
            }

            _context.League.Remove(league);
            await _context.SaveChangesAsync();

            return NoContent();
        }

        [HttpPost("add/player")]
        public async Task<ActionResult> AddPlayerToLeague(LeaguePlayerRequest lp)
        {
            LeaguePlayer leaguePlayer = new LeaguePlayer() { Leagueid = lp.Leagueid, Playerid = lp.Playerid, League = await _context.League.FindAsync(lp.Leagueid), Player = await _context.Player.FindAsync(lp.Playerid) };
            _context.LeaguePlayer.Add(leaguePlayer);
            await _context.SaveChangesAsync();
            return NoContent();
        }

        [HttpPost("delete/player")]
        public async Task<ActionResult> DeletePlayerToLeague(LeaguePlayerRequest lp)
        {
            Console.WriteLine("Remove player to league");
            LeaguePlayer leaguePlayer = new LeaguePlayer() { Leagueid = lp.Leagueid, Playerid = lp.Playerid, League = await _context.League.FindAsync(lp.Leagueid), Player = await _context.Player.FindAsync(lp.Playerid) };
            _context.LeaguePlayer.Remove(leaguePlayer);
            await _context.SaveChangesAsync();
            return NoContent();
        }

        private bool LeagueExists(int id)
        {
            return _context.League.Any(e => e.Id == id);
        }
    }
}
