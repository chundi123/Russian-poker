import { useState } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './ui/card';
import { Input } from './ui/input';
import { Label } from './ui/label';
import { Button } from './ui/button';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from './ui/select';
import { siteApi, Site } from '../../services/mockApi';
import { toast } from 'sonner';
import { Building2, CheckCircle2 } from 'lucide-react';
import { Badge } from './ui/badge';

export function SiteRegistration() {
  const [formData, setFormData] = useState({
    siteCode: '',
    siteName: '',
    status: 'ACTIVE' as 'ACTIVE' | 'INACTIVE',
  });
  const [loading, setLoading] = useState(false);
  const [sites, setSites] = useState<Site[]>([]);
  const [showSites, setShowSites] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);

    try {
      const newSite = await siteApi.createSite(formData);
      toast.success('Site created successfully!', {
        description: `${newSite.siteName} (${newSite.siteCode})`,
      });
      
      // Reset form
      setFormData({
        siteCode: '',
        siteName: '',
        status: 'ACTIVE',
      });

      // Refresh sites list if visible
      if (showSites) {
        const allSites = await siteApi.getAllSites();
        setSites(allSites);
      }
    } catch (error) {
      toast.error('Failed to create site', {
        description: 'Please try again later',
      });
    } finally {
      setLoading(false);
    }
  };

  const loadSites = async () => {
    setLoading(true);
    try {
      const allSites = await siteApi.getAllSites();
      setSites(allSites);
      setShowSites(true);
    } catch (error) {
      toast.error('Failed to load sites');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      <Card>
        <CardHeader>
          <div className="flex items-center gap-3">
            <div className="p-2 bg-blue-500/10 rounded-lg">
              <Building2 className="h-6 w-6 text-blue-600" />
            </div>
            <div>
              <CardTitle>Site Registration</CardTitle>
              <CardDescription>Register a new tournament site</CardDescription>
            </div>
          </div>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="siteCode">Site Code</Label>
              <Input
                id="siteCode"
                placeholder="e.g., SITE003"
                value={formData.siteCode}
                onChange={(e) => setFormData({ ...formData, siteCode: e.target.value })}
                required
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="siteName">Site Name</Label>
              <Input
                id="siteName"
                placeholder="e.g., Monaco Casino"
                value={formData.siteName}
                onChange={(e) => setFormData({ ...formData, siteName: e.target.value })}
                required
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="status">Status</Label>
              <Select
                value={formData.status}
                onValueChange={(value) =>
                  setFormData({ ...formData, status: value as 'ACTIVE' | 'INACTIVE' })
                }
              >
                <SelectTrigger id="status">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="ACTIVE">Active</SelectItem>
                  <SelectItem value="INACTIVE">Inactive</SelectItem>
                </SelectContent>
              </Select>
            </div>

            <div className="flex gap-3 pt-4">
              <Button type="submit" disabled={loading} className="flex-1">
                {loading ? 'Creating...' : 'Create Site'}
              </Button>
              <Button type="button" variant="outline" onClick={loadSites} disabled={loading}>
                View All Sites
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>

      {showSites && (
        <Card>
          <CardHeader>
            <CardTitle>Registered Sites</CardTitle>
            <CardDescription>{sites.length} sites registered</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              {sites.map((site) => (
                <div
                  key={site.id}
                  className="flex items-center justify-between p-4 border rounded-lg hover:bg-muted/50 transition-colors"
                >
                  <div className="flex items-center gap-3">
                    <div className="p-2 bg-blue-500/10 rounded-lg">
                      <Building2 className="h-4 w-4 text-blue-600" />
                    </div>
                    <div>
                      <div className="flex items-center gap-2">
                        <p className="font-medium">{site.siteName}</p>
                        {site.status === 'ACTIVE' && (
                          <CheckCircle2 className="h-4 w-4 text-green-600" />
                        )}
                      </div>
                      <p className="text-sm text-muted-foreground">{site.siteCode}</p>
                    </div>
                  </div>
                  <Badge variant={site.status === 'ACTIVE' ? 'default' : 'secondary'}>
                    {site.status}
                  </Badge>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      )}
    </div>
  );
}
