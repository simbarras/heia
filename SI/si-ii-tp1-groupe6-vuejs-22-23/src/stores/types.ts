export interface Game {
  id?: number;
  homeTeam: string;
  awayTeam: string;
  location: string;
  date: Date;
  leagueid: number;
}

export interface League {
  id?: number;
  name: string;
  players: [Player];
  games: [Game];
}

export interface Player {
  id: number;
  firstname: string;
  lastname: string;
  birthdate: Date;
  favoriteTeam: string;
}