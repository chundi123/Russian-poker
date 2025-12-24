// Mock API service for Tournament Management System

// In-memory data store
let sites = [
    {
        id: 1,
        siteCode: 'SITE001',
        siteName: 'Las Vegas Main',
        status: 'ACTIVE',
        createdAt: '2025-12-20T10:00:00Z',
    },
    {
        id: 2,
        siteCode: 'SITE002',
        siteName: 'Atlantic City',
        status: 'ACTIVE',
        createdAt: '2025-12-21T10:00:00Z',
    },
];

let tournaments = [
    {
        id: 1,
        name: 'Winter Championship 2025',
        site: { id: 1, siteName: 'Las Vegas Main' },
        startingChips: 10000,
        totalRounds: 15,
        maxPlayers: 200,
        tournamentType: 'PVP',
        status: { id: 1, name: 'ACTIVE' },
        startTime: '2025-12-25T18:00:00Z',
        endTime: '2025-12-26T02:00:00Z',
        createdAt: '2025-12-22T10:00:00Z',
        registeredPlayers: 45,
    },
    {
        id: 2,
        name: 'Daily Dealer Challenge',
        site: { id: 1, siteName: 'Las Vegas Main' },
        startingChips: 5000,
        totalRounds: 10,
        maxPlayers: 100,
        tournamentType: 'PVD',
        status: { id: 2, name: 'CREATED' },
        startTime: '2025-12-23T20:00:00Z',
        endTime: '2025-12-24T00:00:00Z',
        createdAt: '2025-12-22T11:00:00Z',
        registeredPlayers: 12,
    },
];

let leaderboards = {
    1: [
        { username: 'PokerPro_42', chipsCurrent: 125000, totalWins: 45, totalLosses: 12, rank: 1 },
        { username: 'CardShark99', chipsCurrent: 98000, totalWins: 38, totalLosses: 15, rank: 2 },
        { username: 'BluffMaster', chipsCurrent: 87500, totalWins: 35, totalLosses: 18, rank: 3 },
        { username: 'AllIn_King', chipsCurrent: 76000, totalWins: 32, totalLosses: 20, rank: 4 },
        { username: 'RiverRat', chipsCurrent: 68000, totalWins: 28, totalLosses: 22, rank: 5 },
    ],
    2: [
        { username: 'DealerSlayer', chipsCurrent: 65000, totalWins: 25, totalLosses: 5, rank: 1 },
        { username: 'ChipCollector', chipsCurrent: 54000, totalWins: 22, totalLosses: 8, rank: 2 },
        { username: 'FoldOrGold', chipsCurrent: 48000, totalWins: 20, totalLosses: 10, rank: 3 },
    ],
};

let registrations = {};

let nextSiteId = 3;
let nextTournamentId = 3;

// Simulate network delay
const delay = (ms) => new Promise(resolve => setTimeout(resolve, ms));

// Site APIs
const siteApi = {
    createSite: async (data) => {
        await delay(500);
        const newSite = {
            id: nextSiteId++,
            siteCode: data.siteCode,
            siteName: data.siteName,
            status: data.status || 'ACTIVE',
            createdAt: new Date().toISOString(),
        };
        sites.push(newSite);
        return newSite;
    },

    getAllSites: async () => {
        await delay(300);
        return sites;
    },
};

// Tournament APIs
const tournamentApi = {
    createTournament: async (data) => {
        await delay(500);
        
        const site = sites.find(s => s.id === data.site.id);
        
        const newTournament = {
            id: nextTournamentId++,
            name: data.name,
            site: { id: data.site.id, siteName: site?.siteName },
            startingChips: data.startingChips,
            totalRounds: data.totalRounds,
            maxPlayers: data.maxPlayers,
            tournamentType: data.tournamentType || 'PVD',
            status: { id: data.status?.id || 2, name: data.status?.id === 1 ? 'ACTIVE' : 'CREATED' },
            startTime: data.startTime,
            endTime: data.endTime,
            createdAt: new Date().toISOString(),
            registeredPlayers: 0,
        };
        tournaments.push(newTournament);
        
        leaderboards[newTournament.id] = [];
        registrations[newTournament.id] = [];
        
        return newTournament;
    },

    getAllTournaments: async () => {
        await delay(300);
        return tournaments;
    },

    getLeaderboard: async (tournamentId) => {
        await delay(400);
        const leaderboard = leaderboards[tournamentId] || [];
        return leaderboard.map((entry, index) => ({
            ...entry,
            rank: index + 1,
        }));
    },

    registerPlayer: async (tournamentId, playerData) => {
        await delay(500);
        
        const tournament = tournaments.find(t => t.id === tournamentId);
        if (!tournament) {
            throw new Error('Tournament not found');
        }

        if (tournament.registeredPlayers >= tournament.maxPlayers) {
            throw new Error('Tournament is full');
        }

        if (!registrations[tournamentId]) {
            registrations[tournamentId] = [];
        }

        // Check if already registered
        const alreadyRegistered = registrations[tournamentId].find(
            r => r.username === playerData.username || r.email === playerData.email
        );
        
        if (alreadyRegistered) {
            throw new Error('Already registered for this tournament');
        }

        const registration = {
            id: Date.now(),
            tournamentId,
            username: playerData.username,
            email: playerData.email,
            registeredAt: new Date().toISOString(),
        };

        registrations[tournamentId].push(registration);
        tournament.registeredPlayers++;

        return registration;
    },
};
