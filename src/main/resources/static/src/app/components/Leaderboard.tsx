import { useState, useEffect } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './ui/card';
import { tournamentApi, LeaderboardEntry, Tournament } from '../../services/mockApi';
import { toast } from 'sonner';
import { Trophy, Medal, Crown, Coins, TrendingUp, TrendingDown, ArrowLeft } from 'lucide-react';
import { Badge } from './ui/badge';
import { Button } from './ui/button';

interface LeaderboardProps {
  tournamentId: number;
  onBack: () => void;
}

export function Leaderboard({ tournamentId, onBack }: LeaderboardProps) {
  const [leaderboard, setLeaderboard] = useState<LeaderboardEntry[]>([]);
  const [tournament, setTournament] = useState<Tournament | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadLeaderboard();
  }, [tournamentId]);

  const loadLeaderboard = async () => {
    setLoading(true);
    try {
      const [leaderboardData, tournamentsData] = await Promise.all([
        tournamentApi.getLeaderboard(tournamentId),
        tournamentApi.getAllTournaments(),
      ]);
      
      setLeaderboard(leaderboardData);
      const foundTournament = tournamentsData.find(t => t.id === tournamentId);
      setTournament(foundTournament || null);
    } catch (error) {
      toast.error('Failed to load leaderboard');
    } finally {
      setLoading(false);
    }
  };

  const getRankIcon = (rank: number) => {
    switch (rank) {
      case 1:
        return <Crown className="h-5 w-5 text-yellow-500" />;
      case 2:
        return <Medal className="h-5 w-5 text-gray-400" />;
      case 3:
        return <Medal className="h-5 w-5 text-amber-700" />;
      default:
        return null;
    }
  };

  const getRankBadgeVariant = (rank: number) => {
    if (rank === 1) return 'default';
    if (rank <= 3) return 'secondary';
    return 'outline';
  };

  if (loading) {
    return (
      <Card>
        <CardContent className="p-8 text-center text-muted-foreground">
          Loading leaderboard...
        </CardContent>
      </Card>
    );
  }

  return (
    <div className="space-y-4">
      <div className="flex items-center gap-4">
        <Button onClick={onBack} variant="outline" size="icon">
          <ArrowLeft className="h-4 w-4" />
        </Button>
        <div>
          <h2 className="text-2xl font-bold flex items-center gap-2">
            <Trophy className="h-6 w-6 text-amber-600" />
            {tournament?.name || 'Tournament'} Leaderboard
          </h2>
          <p className="text-muted-foreground">
            {leaderboard.length} players competing
          </p>
        </div>
      </div>

      {/* Top 3 Podium */}
      {leaderboard.length >= 3 && (
        <div className="grid grid-cols-3 gap-4 mb-6">
          {/* 2nd Place */}
          <Card className="mt-8">
            <CardContent className="pt-6 text-center">
              <div className="flex justify-center mb-2">
                <Medal className="h-8 w-8 text-gray-400" />
              </div>
              <Badge variant="secondary" className="mb-2">
                #2
              </Badge>
              <p className="font-medium">{leaderboard[1].username}</p>
              <p className="text-sm text-muted-foreground mt-1">
                {leaderboard[1].chipsCurrent.toLocaleString()} chips
              </p>
            </CardContent>
          </Card>

          {/* 1st Place */}
          <Card className="border-amber-500/50 bg-gradient-to-b from-amber-500/5 to-transparent">
            <CardContent className="pt-6 text-center">
              <div className="flex justify-center mb-2">
                <Crown className="h-10 w-10 text-yellow-500" />
              </div>
              <Badge className="mb-2 bg-amber-500">#1</Badge>
              <p className="font-bold text-lg">{leaderboard[0].username}</p>
              <p className="text-sm text-muted-foreground mt-1">
                {leaderboard[0].chipsCurrent.toLocaleString()} chips
              </p>
            </CardContent>
          </Card>

          {/* 3rd Place */}
          <Card className="mt-8">
            <CardContent className="pt-6 text-center">
              <div className="flex justify-center mb-2">
                <Medal className="h-8 w-8 text-amber-700" />
              </div>
              <Badge variant="secondary" className="mb-2">
                #3
              </Badge>
              <p className="font-medium">{leaderboard[2].username}</p>
              <p className="text-sm text-muted-foreground mt-1">
                {leaderboard[2].chipsCurrent.toLocaleString()} chips
              </p>
            </CardContent>
          </Card>
        </div>
      )}

      {/* Full Leaderboard Table */}
      <Card>
        <CardHeader>
          <CardTitle>Rankings</CardTitle>
          <CardDescription>Complete player standings</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="space-y-2">
            {leaderboard.map((entry, index) => {
              const winRate = (entry.totalWins / (entry.totalWins + entry.totalLosses)) * 100;
              
              return (
                <div
                  key={index}
                  className={`flex items-center justify-between p-4 rounded-lg border ${
                    entry.rank === 1
                      ? 'bg-amber-500/5 border-amber-500/20'
                      : 'hover:bg-muted/50'
                  } transition-colors`}
                >
                  <div className="flex items-center gap-4 flex-1">
                    {/* Rank */}
                    <div className="flex items-center gap-2 min-w-[60px]">
                      {getRankIcon(entry.rank || index + 1)}
                      <Badge variant={getRankBadgeVariant(entry.rank || index + 1)}>
                        #{entry.rank || index + 1}
                      </Badge>
                    </div>

                    {/* Username */}
                    <div className="flex-1 min-w-[150px]">
                      <p className="font-medium">{entry.username}</p>
                    </div>

                    {/* Stats */}
                    <div className="hidden md:flex items-center gap-6">
                      {/* Chips */}
                      <div className="flex items-center gap-2 min-w-[120px]">
                        <Coins className="h-4 w-4 text-amber-600" />
                        <span className="font-medium">
                          {entry.chipsCurrent.toLocaleString()}
                        </span>
                      </div>

                      {/* Wins/Losses */}
                      <div className="flex items-center gap-3 min-w-[140px]">
                        <div className="flex items-center gap-1 text-sm">
                          <TrendingUp className="h-4 w-4 text-green-600" />
                          <span className="text-green-600">{entry.totalWins}</span>
                        </div>
                        <div className="flex items-center gap-1 text-sm">
                          <TrendingDown className="h-4 w-4 text-red-600" />
                          <span className="text-red-600">{entry.totalLosses}</span>
                        </div>
                      </div>

                      {/* Win Rate */}
                      <div className="min-w-[80px]">
                        <Badge variant="outline">
                          {winRate.toFixed(1)}% WR
                        </Badge>
                      </div>
                    </div>
                  </div>

                  {/* Mobile stats */}
                  <div className="md:hidden flex flex-col items-end gap-1">
                    <div className="flex items-center gap-1 text-sm">
                      <Coins className="h-3 w-3 text-amber-600" />
                      <span className="font-medium">
                        {entry.chipsCurrent.toLocaleString()}
                      </span>
                    </div>
                    <div className="text-xs text-muted-foreground">
                      {entry.totalWins}W / {entry.totalLosses}L
                    </div>
                  </div>
                </div>
              );
            })}
          </div>

          {leaderboard.length === 0 && (
            <div className="text-center py-12 text-muted-foreground">
              <Trophy className="h-12 w-12 mx-auto mb-4 opacity-50" />
              <p>No players yet</p>
              <p className="text-sm">Leaderboard will update as players join</p>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
