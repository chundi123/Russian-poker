import { useState } from "react";
import {
  Tabs,
  TabsContent,
  TabsList,
  TabsTrigger,
} from "./components/ui/tabs";
import { SiteRegistration } from "./components/SiteRegistration";
import { TournamentCreation } from "./components/TournamentCreation";
import { TournamentsList } from "./components/TournamentsList";
import { Leaderboard } from "./components/Leaderboard";
import { Toaster } from "./components/ui/sonner";
import { Building2, Trophy, List, Medal } from "lucide-react";

export default function App() {
  const [selectedTournamentId, setSelectedTournamentId] =
    useState<number | null>(null);
  const [activeTab, setActiveTab] = useState("tournaments");

  const handleSelectTournament = (tournamentId: number) => {
    setSelectedTournamentId(tournamentId);
    setActiveTab("leaderboard");
  };

  const handleBackToTournaments = () => {
    setSelectedTournamentId(null);
    setActiveTab("tournaments");
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 to-slate-100 dark:from-slate-950 dark:to-slate-900">
      <div className="container mx-auto py-8 px-4 max-w-7xl">
        {/* Header */}
        <div className="mb-8 text-center">
          <div className="flex items-center justify-center gap-3 mb-4">
            <div className="p-3 bg-gradient-to-br from-amber-500 to-orange-600 rounded-2xl shadow-lg">
              <Trophy className="h-8 w-8 text-white" />
            </div>
          </div>
          <h1 className="text-4xl font-bold mb-2 bg-gradient-to-r from-amber-600 to-orange-600 bg-clip-text text-transparent">
            Tournament Management System
          </h1>
          <p className="text-muted-foreground">
            Manage tournaments, sites, and track player
            performance
          </p>
        </div>

        {/* Main Content */}
        <Tabs
          value={activeTab}
          onValueChange={setActiveTab}
          className="space-y-6"
        >
          <TabsList className="grid w-full grid-cols-4 lg:w-[600px] mx-auto">
            <TabsTrigger
              value="sites"
              className="flex items-center gap-2"
            >
              <Building2 className="h-4 w-4" />
              <span className="hidden sm:inline">Sites</span>
            </TabsTrigger>
            <TabsTrigger
              value="create"
              className="flex items-center gap-2"
            >
              <Trophy className="h-4 w-4" />
              <span className="hidden sm:inline">Create</span>
            </TabsTrigger>
            <TabsTrigger
              value="tournaments"
              className="flex items-center gap-2"
            >
              <List className="h-4 w-4" />
              <span className="hidden sm:inline">
                Tournaments
              </span>
            </TabsTrigger>
            <TabsTrigger
              value="leaderboard"
              className="flex items-center gap-2"
            >
              <Medal className="h-4 w-4" />
              <span className="hidden sm:inline">
                Leaderboard
              </span>
            </TabsTrigger>
          </TabsList>

          <TabsContent value="sites">
            <SiteRegistration />
          </TabsContent>

          <TabsContent value="create">
            <TournamentCreation />
          </TabsContent>

          <TabsContent value="tournaments">
            <TournamentsList
              onSelectTournament={handleSelectTournament}
            />
          </TabsContent>

          <TabsContent value="leaderboard">
            {selectedTournamentId ? (
              <Leaderboard
                tournamentId={selectedTournamentId}
                onBack={handleBackToTournaments}
              />
            ) : (
              <div className="bg-card rounded-lg border p-12 text-center">
                <Medal className="h-16 w-16 mx-auto text-muted-foreground/50 mb-4" />
                <h3 className="font-medium text-lg mb-2">
                  No Tournament Selected
                </h3>
                <p className="text-muted-foreground mb-6">
                  Select a tournament from the Tournaments tab
                  to view its leaderboard
                </p>
                <button
                  onClick={() => setActiveTab("tournaments")}
                  className="text-primary hover:underline"
                >
                  Go to Tournaments
                </button>
              </div>
            )}
          </TabsContent>
        </Tabs>

        <Toaster />
      </div>
    </div>
  );
}