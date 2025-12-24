import { useState, useEffect } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './ui/card';
import { Input } from './ui/input';
import { Label } from './ui/label';
import { Button } from './ui/button';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from './ui/select';
import { siteApi, tournamentApi, Site } from '../../services/mockApi';
import { toast } from 'sonner';
import { Trophy, Calendar, Users, Coins } from 'lucide-react';

export function TournamentCreation() {
  const [sites, setSites] = useState<Site[]>([]);
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    name: '',
    siteId: '',
    startingChips: '10000',
    totalRounds: '10',
    maxPlayers: '100',
    tournamentType: 'PVD' as 'PVD' | 'PVP',
    startTime: '',
    endTime: '',
    statusId: '2',
  });

  useEffect(() => {
    loadSites();
  }, []);

  const loadSites = async () => {
    try {
      const allSites = await siteApi.getAllSites();
      setSites(allSites.filter(s => s.status === 'ACTIVE'));
    } catch (error) {
      toast.error('Failed to load sites');
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!formData.siteId) {
      toast.error('Please select a site');
      return;
    }

    setLoading(true);

    try {
      const tournament = await tournamentApi.createTournament({
        name: formData.name,
        site: { id: parseInt(formData.siteId) },
        startingChips: parseInt(formData.startingChips),
        totalRounds: parseInt(formData.totalRounds),
        maxPlayers: parseInt(formData.maxPlayers),
        tournamentType: formData.tournamentType,
        status: { id: parseInt(formData.statusId) },
        startTime: formData.startTime || undefined,
        endTime: formData.endTime || undefined,
      });

      toast.success('Tournament created successfully!', {
        description: `${tournament.name} - ${tournament.tournamentType} mode`,
      });

      // Reset form
      setFormData({
        name: '',
        siteId: '',
        startingChips: '10000',
        totalRounds: '10',
        maxPlayers: '100',
        tournamentType: 'PVD',
        startTime: '',
        endTime: '',
        statusId: '2',
      });
    } catch (error) {
      toast.error('Failed to create tournament', {
        description: 'Please try again later',
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <Card>
      <CardHeader>
        <div className="flex items-center gap-3">
          <div className="p-2 bg-amber-500/10 rounded-lg">
            <Trophy className="h-6 w-6 text-amber-600" />
          </div>
          <div>
            <CardTitle>Create Tournament</CardTitle>
            <CardDescription>Set up a new poker tournament</CardDescription>
          </div>
        </div>
      </CardHeader>
      <CardContent>
        <form onSubmit={handleSubmit} className="space-y-6">
          {/* Basic Information */}
          <div className="space-y-4">
            <h3 className="font-medium flex items-center gap-2">
              <Trophy className="h-4 w-4" />
              Basic Information
            </h3>
            
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="space-y-2 md:col-span-2">
                <Label htmlFor="name">Tournament Name</Label>
                <Input
                  id="name"
                  placeholder="e.g., Spring Championship 2025"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  required
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="site">Site</Label>
                <Select
                  value={formData.siteId}
                  onValueChange={(value) => setFormData({ ...formData, siteId: value })}
                  required
                >
                  <SelectTrigger id="site">
                    <SelectValue placeholder="Select a site" />
                  </SelectTrigger>
                  <SelectContent>
                    {sites.map((site) => (
                      <SelectItem key={site.id} value={site.id.toString()}>
                        {site.siteName}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>

              <div className="space-y-2">
                <Label htmlFor="tournamentType">Tournament Type</Label>
                <Select
                  value={formData.tournamentType}
                  onValueChange={(value) =>
                    setFormData({ ...formData, tournamentType: value as 'PVD' | 'PVP' })
                  }
                >
                  <SelectTrigger id="tournamentType">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="PVD">Player vs Dealer (PVD)</SelectItem>
                    <SelectItem value="PVP">Player vs Player (PVP)</SelectItem>
                  </SelectContent>
                </Select>
              </div>
            </div>
          </div>

          {/* Schedule */}
          <div className="space-y-4">
            <h3 className="font-medium flex items-center gap-2">
              <Calendar className="h-4 w-4" />
              Schedule
            </h3>
            
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="startTime">Start Time</Label>
                <Input
                  id="startTime"
                  type="datetime-local"
                  value={formData.startTime}
                  onChange={(e) => setFormData({ ...formData, startTime: e.target.value })}
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="endTime">End Time</Label>
                <Input
                  id="endTime"
                  type="datetime-local"
                  value={formData.endTime}
                  onChange={(e) => setFormData({ ...formData, endTime: e.target.value })}
                />
              </div>
            </div>
          </div>

          {/* Game Settings */}
          <div className="space-y-4">
            <h3 className="font-medium flex items-center gap-2">
              <Coins className="h-4 w-4" />
              Game Settings
            </h3>
            
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div className="space-y-2">
                <Label htmlFor="startingChips">Starting Chips</Label>
                <Input
                  id="startingChips"
                  type="number"
                  min="1000"
                  step="1000"
                  value={formData.startingChips}
                  onChange={(e) => setFormData({ ...formData, startingChips: e.target.value })}
                  required
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="totalRounds">Total Rounds</Label>
                <Input
                  id="totalRounds"
                  type="number"
                  min="1"
                  value={formData.totalRounds}
                  onChange={(e) => setFormData({ ...formData, totalRounds: e.target.value })}
                  required
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="maxPlayers">Max Players</Label>
                <Input
                  id="maxPlayers"
                  type="number"
                  min="2"
                  value={formData.maxPlayers}
                  onChange={(e) => setFormData({ ...formData, maxPlayers: e.target.value })}
                  required
                />
              </div>
            </div>
          </div>

          {/* Status */}
          <div className="space-y-4">
            <h3 className="font-medium flex items-center gap-2">
              <Users className="h-4 w-4" />
              Status
            </h3>
            
            <div className="space-y-2">
              <Label htmlFor="status">Initial Status</Label>
              <Select
                value={formData.statusId}
                onValueChange={(value) => setFormData({ ...formData, statusId: value })}
              >
                <SelectTrigger id="status">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="1">Active</SelectItem>
                  <SelectItem value="2">Created (Draft)</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>

          <Button type="submit" disabled={loading} className="w-full" size="lg">
            {loading ? 'Creating Tournament...' : 'Create Tournament'}
          </Button>
        </form>
      </CardContent>
    </Card>
  );
}
