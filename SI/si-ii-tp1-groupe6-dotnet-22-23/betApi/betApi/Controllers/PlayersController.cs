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
    [Route("player")]
    [ApiController]
    public class PlayersController : ControllerBase
    {
        private readonly BetApiContext _context;

        public PlayersController(BetApiContext context)
        {
            _context = context;
        }

        // GET: api/Players
        [Route("/players")]
        [HttpGet]
        public async Task<ActionResult<IEnumerable<PlayerResponse>>> GetPlayer()
        {
            var playerList = await _context.Player.ToListAsync();
            var playerListResponse = playerList.Select(x => new PlayerResponse { Birthdate = x.Birthdate, FavoriteTeam = x.FavoriteTeam, Firstname = x.Firstname, Id = x.Id, Lastname = x.Lastname}).ToList();
            return playerListResponse;
        }


        // POST: api/Players
        // To protect from overposting attacks, see https://go.microsoft.com/fwlink/?linkid=2123754
        [Route("add")]
        [HttpPost]
        public async Task<ActionResult<Player>> PostPlayer(Player player)
        {
            _context.Player.Add(player);
            await _context.SaveChangesAsync();

            return NoContent();
        }
    }
}
