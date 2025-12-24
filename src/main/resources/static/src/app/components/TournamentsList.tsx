import { useState, useEffect } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './ui/card';
import { tournamentApi, Tournament } from '../../services/mockApi';
import { toast } from 'sonner';
import { Trophy, MapPin, Users, Coins, Calendar, Clock } from 'lucide-react';
import { Badge } from './ui/badge';
import { Button } from './ui/button';
import { format } from 'date-fns';

interface TournamentsListProps {
  onSelectTournament: (tournamentId: number) => void;
}

export function TournamentsList({ onSelectTournament }: TournamentsListProps) {
  const [tournaments, setTournaments] = useState<Tournament[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadTournaments();
  }, []);

  const loadTournaments = async () => {
    setLoading(true);
    try {
      const data = await tournamentApi.getAllTournaments();
      setTournaments(data);
    } catch (error) {
      toast.error('Failed to load tournaments');
    } finally {
      setLoading(false);
    }
  };

  const formatDateTime = (dateString?: string) => {
    if (!dateString) return 'Not set';
    try {
      return format(new Date(dateString), 'MMM dd, yyyy • HH:mm');
    } catch {
      return 'Invalid date';
    }
  };

  if (loading) {
    return (
      <Card>
        <CardContent className="p-8 text-center text-muted-foreground">
          Loading tournaments...
        </CardContent>
      </Card>
    );
  }

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold">All Tournaments</h2>
          <p className="text-muted-foreground">{tournaments.length} tournaments available</p>
        </div>
        <Button onClick={loadTournaments} variant="outline">
          Refresh
        </Button>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
        {tournaments.map((tournament) => (
          <Card key={tournament.id} className="hover:shadow-lg transition-shadow">
            <CardHeader>
              <div className="flex items-start justify-between">
                <div className="flex items-start gap-3">
                  <div className="p-2 bg-amber-500/10 rounded-lg">
                    <Trophy className="h-5 w-5 text-amber-600" />
                  </div>
                  <div>
                    <CardTitle className="text-lg">{tournament.name}</CardTitle>
                    <CardDescription className="flex items-center gap-1 mt-1">
                      <MapPin className="h-3 w-3" />
                      {tournament.site.siteName || `Site #${tournament.site.id}`}
                    </CardDescription>
                  </div>
                </div>
                <Badge
                  variant={tournament.status.name === 'ACTIVE' ? 'default' : 'secondary'}
                >
                  {tournament.status.name || 'Unknown'}
                </Badge>
              </div>
            </CardHeader>
            <CardContent className="space-y-4">
              {/* Schedule */}
              {(tournament.startTime || tournament.endTime) && (
                <div className="space-y-2 pb-4 border-b">
                  {tournament.startTime && (
                    <div className="flex items-center gap-2 text-sm">
                      <Calendar className="h-4 w-4 text-muted-foreground" />
                      <span className="text-muted-foreground">Start:</span>
                      <span>{formatDateTime(tournament.startTime)}</span>
                    </div>
                  )}
                  {tournament.endTime && (
                    <div className="flex items-center gap-2 text-sm">
                      <Clock className="h-4 w-4 text-muted-foreground" />
                      <span className="text-muted-foreground">End:</span>
                      <span>{formatDateTime(tournament.endTime)}</span>
                    </div>
                  )}
                </div>
              )}

              {/* Tournament Details */}
              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-1">
                  <div className="flex items-center gap-2 text-sm text-muted-foreground">
                    <Users className="h-4 w-4" />
                    Max Players
                  </div>
                  <p className="font-medium">{tournament.maxPlayers.toLocaleString()}</p>
                </div>

                <div className="space-y-1">
                  <div className="flex items-center gap-2 text-sm text-muted-foreground">
                    <Coins className="h-4 w-4" />
                    Starting Chips
                  </div>
                  <p className="font-medium">{tournament.startingChips.toLocaleString()}</p>
                </div>

                <div className="space-y-1">
                  <div className="text-sm text-muted-foreground">Total Rounds</div>
                  <p className="font-medium">{tournament.totalRounds}</p>
                </div>

                <div className="space-y-1">
                  <div className="text-sm text-muted-foreground">Type</div>
                  <Badge variant="outline">
                    {tournament.tournamentType === 'PVP' ? 'Player vs Player' : 'Player vs Dealer'}
                  </Badge>
                </div>
              </div>

              <Button
                onClick={() => onSelectTournament(tournament.id)}
                className="w-full"
                variant="secondary"
              >
                View Leaderboard
              </Button>
            </CardContent>
          </Card>
        ))}
      </div>

      {tournaments.length === 0 && (
        <Card>
          <CardContent className="p-12 text-center">
            <Trophy className="h-12 w-12 mx-auto text-muted-foreground/50 mb-4" />
            <h3 className="font-medium mb-2">No tournaments found</h3>
            <p className="text-sm text-muted-foreground">
              Create your first tournament to get started
            </p>
          </CardContent>
        </Card>
      )}
    </div>
  );
}
