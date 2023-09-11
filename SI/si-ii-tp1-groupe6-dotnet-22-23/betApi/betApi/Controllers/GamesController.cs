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
    [Route("game")]
    [ApiController]
    public class GamesController : ControllerBase
    {
        private readonly BetApiContext _context;

        public GamesController(BetApiContext context)
        {
            _context = context;
        }

        // GET: api/Games
        [Route("/games")]
        [HttpGet]
        public async Task<ActionResult<IEnumerable<Game>>> GetGame()
        {
            return await _context.Game.ToListAsync();
        }

        // GET: api/Leagues/5F
        [HttpGet("{id}")]
        public async Task<ActionResult<Game>> GetGame(int id)
        {
            var game = await _context.Game.FindAsync(id);

            if (game == null)
            {
                return NotFound();
            }

            return game;
        }

        // POST: api/Games
        // To protect from overposting attacks, see https://go.microsoft.com/fwlink/?linkid=2123754
        [Route("add")]
        [HttpPost]
        public async Task<ActionResult<Game>> PostGame(Game gameRequest)
        {
            _context.Game.Add(gameRequest);
            await _context.SaveChangesAsync();

            return NoContent();
        }
    }
}
