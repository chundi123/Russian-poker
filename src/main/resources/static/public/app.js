// Global state
let currentTab = 'sites';
let selectedTournamentId = null;
let currentRegistrationTournamentId = null;

// Initialize app
document.addEventListener('DOMContentLoaded', () => {
    switchTab('tournaments'); // Start with tournaments tab
});

// Tab switching
function switchTab(tabName) {
    currentTab = tabName;
    
    // Update tab buttons
    document.querySelectorAll('.tab-button').forEach(btn => {
        btn.classList.remove('active');
    });
    document.getElementById(`tab-${tabName}`).classList.add('active');
    
    // Hide all content
    document.querySelectorAll('.tab-content').forEach(content => {
        content.classList.add('hidden');
    });
    
    // Show selected content
    const contentDiv = document.getElementById(`content-${tabName}`);
    contentDiv.classList.remove('hidden');
    
    // Load content based on tab
    switch(tabName) {
        case 'sites':
            renderSitesTab();
            break;
        case 'create':
            renderCreateTab();
            break;
        case 'tournaments':
            renderTournamentsTab();
            break;
        case 'leaderboard':
            renderLeaderboardTab();
            break;
    }
}

// Toast notifications
function showToast(message, description = '', type = 'success') {
    const toastContainer = document.getElementById('toast-container');
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.innerHTML = `
        <div class="toast-title">${message}</div>
        ${description ? `<div class="toast-description">${description}</div>` : ''}
    `;
    
    toastContainer.appendChild(toast);
    
    setTimeout(() => {
        toast.style.animation = 'slideIn 0.3s ease-out reverse';
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

// Sites Tab
async function renderSitesTab() {
    const contentDiv = document.getElementById('content-sites');
    contentDiv.innerHTML = `
        <div class="grid-2">
            <div class="card">
                <div class="card-header">
                    <div class="flex items-center gap-3">
                        <div class="icon-container blue">
                            <svg class="h-6 w-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"></path>
                            </svg>
                        </div>
                        <div>
                            <div class="card-title">Site Registration</div>
                            <div class="card-description">Register a new tournament site</div>
                        </div>
                    </div>
                </div>
                <div class="card-content">
                    <form id="site-form" class="space-y-4">
                        <div class="form-group">
                            <label class="form-label">Site Code</label>
                            <input type="text" id="site-code" class="form-input" placeholder="e.g., SITE003" required>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Site Name</label>
                            <input type="text" id="site-name" class="form-input" placeholder="e.g., Monaco Casino" required>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Status</label>
                            <select id="site-status" class="form-select">
                                <option value="ACTIVE">Active</option>
                                <option value="INACTIVE">Inactive</option>
                            </select>
                        </div>
                        <button type="submit" class="btn btn-primary w-full">Create Site</button>
                    </form>
                </div>
            </div>

            <div class="card">
                <div class="card-header">
                    <div class="card-title">Registered Sites</div>
                    <div class="card-description" id="sites-count">Loading...</div>
                </div>
                <div class="card-content">
                    <div id="sites-list" class="space-y-3"></div>
                </div>
            </div>
        </div>
    `;
    
    // Load sites
    loadSites();
    
    // Handle form submission
    document.getElementById('site-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        const formData = {
            siteCode: document.getElementById('site-code').value,
            siteName: document.getElementById('site-name').value,
            status: document.getElementById('site-status').value,
        };
        
        try {
            const newSite = await siteApi.createSite(formData);
            showToast('Site created successfully!', `${newSite.siteName} (${newSite.siteCode})`);
            e.target.reset();
            loadSites();
        } catch (error) {
            showToast('Failed to create site', 'Please try again later', 'error');
        }
    });
}

async function loadSites() {
    try {
        const sites = await siteApi.getAllSites();
        document.getElementById('sites-count').textContent = `${sites.length} sites registered`;
        
        const sitesList = document.getElementById('sites-list');
        sitesList.innerHTML = sites.map(site => `
            <div class="flex items-center justify-between p-4 border rounded-lg hover:bg-gray-50 transition-colors">
                <div class="flex items-center gap-3">
                    <div class="icon-container blue">
                        <svg class="h-4 w-4 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16"></path>
                        </svg>
                    </div>
                    <div>
                        <div class="flex items-center gap-2">
                            <p class="font-medium">${site.siteName}</p>
                            ${site.status === 'ACTIVE' ? '<svg class="h-4 w-4 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path></svg>' : ''}
                        </div>
                        <p class="text-sm text-muted">${site.siteCode}</p>
                    </div>
                </div>
                <span class="badge badge-${site.status === 'ACTIVE' ? 'success' : 'secondary'}">${site.status}</span>
            </div>
        `).join('');
    } catch (error) {
        showToast('Failed to load sites', '', 'error');
    }
}

// Create Tournament Tab
async function renderCreateTab() {
    const contentDiv = document.getElementById('content-create');
    
    // Load sites first
    const sites = await siteApi.getAllSites();
    const activeSites = sites.filter(s => s.status === 'ACTIVE');
    
    contentDiv.innerHTML = `
        <div class="card">
            <div class="card-header">
                <div class="flex items-center gap-3">
                    <div class="icon-container amber">
                        <svg class="h-6 w-6 text-amber-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15V3m0 12l-4-4m4 4l4-4"></path>
                        </svg>
                    </div>
                    <div>
                        <div class="card-title">Create Tournament</div>
                        <div class="card-description">Set up a new poker tournament</div>
                    </div>
                </div>
            </div>
            <div class="card-content">
                <form id="tournament-form">
                    <div class="space-y-6">
                        <div>
                            <h3 class="font-medium mb-4 flex items-center gap-2">
                                <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15V3m0 12l-4-4m4 4l4-4"></path>
                                </svg>
                                Basic Information
                            </h3>
                            <div class="grid-2">
                                <div class="form-group" style="grid-column: span 2;">
                                    <label class="form-label">Tournament Name</label>
                                    <input type="text" id="tournament-name" class="form-input" placeholder="e.g., Spring Championship 2025" required>
                                </div>
                                <div class="form-group">
                                    <label class="form-label">Site</label>
                                    <select id="tournament-site" class="form-select" required>
                                        <option value="">Select a site</option>
                                        ${activeSites.map(site => `<option value="${site.id}">${site.siteName}</option>`).join('')}
                                    </select>
                                </div>
                                <div class="form-group">
                                    <label class="form-label">Tournament Type</label>
                                    <select id="tournament-type" class="form-select">
                                        <option value="PVD">Player vs Dealer (PVD)</option>
                                        <option value="PVP">Player vs Player (PVP)</option>
                                    </select>
                                </div>
                            </div>
                        </div>

                        <div>
                            <h3 class="font-medium mb-4 flex items-center gap-2">
                                <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"></path>
                                </svg>
                                Schedule
                            </h3>
                            <div class="grid-2">
                                <div class="form-group">
                                    <label class="form-label">Start Time</label>
                                    <input type="datetime-local" id="tournament-start" class="form-input">
                                </div>
                                <div class="form-group">
                                    <label class="form-label">End Time</label>
                                    <input type="datetime-local" id="tournament-end" class="form-input">
                                </div>
                            </div>
                        </div>

                        <div>
                            <h3 class="font-medium mb-4 flex items-center gap-2">
                                <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1"></path>
                                </svg>
                                Game Settings
                            </h3>
                            <div class="grid-3">
                                <div class="form-group">
                                    <label class="form-label">Starting Chips</label>
                                    <input type="number" id="tournament-chips" class="form-input" value="10000" min="1000" step="1000" required>
                                </div>
                                <div class="form-group">
                                    <label class="form-label">Total Rounds</label>
                                    <input type="number" id="tournament-rounds" class="form-input" value="10" min="1" required>
                                </div>
                                <div class="form-group">
                                    <label class="form-label">Max Players</label>
                                    <input type="number" id="tournament-players" class="form-input" value="100" min="2" required>
                                </div>
                            </div>
                        </div>

                        <div>
                            <h3 class="font-medium mb-4">Status</h3>
                            <div class="form-group">
                                <label class="form-label">Initial Status</label>
                                <select id="tournament-status" class="form-select">
                                    <option value="1">Active</option>
                                    <option value="2" selected>Created (Draft)</option>
                                </select>
                            </div>
                        </div>

                        <button type="submit" class="btn btn-primary w-full py-3">Create Tournament</button>
                    </div>
                </form>
            </div>
        </div>
    `;
    
    document.getElementById('tournament-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const formData = {
            name: document.getElementById('tournament-name').value,
            site: { id: parseInt(document.getElementById('tournament-site').value) },
            startingChips: parseInt(document.getElementById('tournament-chips').value),
            totalRounds: parseInt(document.getElementById('tournament-rounds').value),
            maxPlayers: parseInt(document.getElementById('tournament-players').value),
            tournamentType: document.getElementById('tournament-type').value,
            status: { id: parseInt(document.getElementById('tournament-status').value) },
            startTime: document.getElementById('tournament-start').value || undefined,
            endTime: document.getElementById('tournament-end').value || undefined,
        };
        
        try {
            const tournament = await tournamentApi.createTournament(formData);
            showToast('Tournament created successfully!', `${tournament.name} - ${tournament.tournamentType} mode`);
            e.target.reset();
        } catch (error) {
            showToast('Failed to create tournament', 'Please try again later', 'error');
        }
    });
}

// Tournaments Tab
async function renderTournamentsTab() {
    const contentDiv = document.getElementById('content-tournaments');
    contentDiv.innerHTML = '<div class="text-center py-8 text-gray-600">Loading tournaments...</div>';
    
    try {
        const tournaments = await tournamentApi.getAllTournaments();
        
        contentDiv.innerHTML = `
            <div class="mb-6">
                <div class="flex items-center justify-between">
                    <div>
                        <h2 class="text-2xl font-bold">All Tournaments</h2>
                        <p class="text-muted">${tournaments.length} tournaments available</p>
                    </div>
                    <button onclick="renderTournamentsTab()" class="btn btn-outline">
                        <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path>
                        </svg>
                        Refresh
                    </button>
                </div>
            </div>
            
            <div class="grid grid-cols-1 lg:grid-cols-2 gap-4">
                ${tournaments.map(tournament => `
                    <div class="card">
                        <div class="card-header">
                            <div class="flex items-start justify-between">
                                <div class="flex items-start gap-3">
                                    <div class="icon-container amber">
                                        <svg class="h-5 w-5 text-amber-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15V3m0 12l-4-4m4 4l4-4"></path>
                                        </svg>
                                    </div>
                                    <div>
                                        <div class="card-title text-lg">${tournament.name}</div>
                                        <div class="card-description flex items-center gap-1 mt-1">
                                            <svg class="h-3 w-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"></path>
                                            </svg>
                                            ${tournament.site.siteName || `Site #${tournament.site.id}`}
                                        </div>
                                    </div>
                                </div>
                                <span class="badge badge-${tournament.status.name === 'ACTIVE' ? 'default' : 'secondary'}">
                                    ${tournament.status.name || 'Unknown'}
                                </span>
                            </div>
                        </div>
                        <div class="card-content space-y-4">
                            ${(tournament.startTime || tournament.endTime) ? `
                                <div class="space-y-2 pb-4 border-b">
                                    ${tournament.startTime ? `
                                        <div class="flex items-center gap-2 text-sm">
                                            <svg class="h-4 w-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"></path>
                                            </svg>
                                            <span class="text-muted">Start:</span>
                                            <span>${formatDateTime(tournament.startTime)}</span>
                                        </div>
                                    ` : ''}
                                    ${tournament.endTime ? `
                                        <div class="flex items-center gap-2 text-sm">
                                            <svg class="h-4 w-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                                            </svg>
                                            <span class="text-muted">End:</span>
                                            <span>${formatDateTime(tournament.endTime)}</span>
                                        </div>
                                    ` : ''}
                                </div>
                            ` : ''}
                            
                            <div class="grid grid-cols-2 gap-4">
                                <div>
                                    <div class="flex items-center gap-2 text-sm text-muted mb-1">
                                        <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"></path>
                                        </svg>
                                        Players
                                    </div>
                                    <p class="font-medium">${tournament.registeredPlayers || 0} / ${tournament.maxPlayers.toLocaleString()}</p>
                                </div>
                                
                                <div>
                                    <div class="flex items-center gap-2 text-sm text-muted mb-1">
                                        <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1"></path>
                                        </svg>
                                        Starting Chips
                                    </div>
                                    <p class="font-medium">${tournament.startingChips.toLocaleString()}</p>
                                </div>
                                
                                <div>
                                    <div class="text-sm text-muted mb-1">Total Rounds</div>
                                    <p class="font-medium">${tournament.totalRounds}</p>
                                </div>
                                
                                <div>
                                    <div class="text-sm text-muted mb-1">Type</div>
                                    <span class="badge badge-outline">
                                        ${tournament.tournamentType === 'PVP' ? 'Player vs Player' : 'Player vs Dealer'}
                                    </span>
                                </div>
                            </div>
                            
                            <div class="grid grid-cols-2 gap-2 pt-2">
                                <button onclick="openRegistrationModal(${tournament.id})" class="btn btn-success w-full">
                                    <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z"></path>
                                    </svg>
                                    Register
                                </button>
                                <button onclick="viewLeaderboard(${tournament.id})" class="btn btn-secondary w-full">
                                    <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 00-2-2m0 0h2a2 2 0 012-2h2a2 2 0 012 2v6a2 2 0 01-2 2h-2a2 2 0 01-2-2v-6z"></path>
                                    </svg>
                                    Leaderboard
                                </button>
                            </div>
                        </div>
                    </div>
                `).join('')}
            </div>
            
            ${tournaments.length === 0 ? `
                <div class="card">
                    <div class="card-content py-12 text-center">
                        <svg class="h-12 w-12 mx-auto text-gray-300 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15V3m0 12l-4-4m4 4l4-4"></path>
                        </svg>
                        <h3 class="font-medium mb-2">No tournaments found</h3>
                        <p class="text-sm text-muted">Create your first tournament to get started</p>
                    </div>
                </div>
            ` : ''}
        `;
    } catch (error) {
        contentDiv.innerHTML = '<div class="text-center py-8 text-red-600">Failed to load tournaments</div>';
    }
}

function formatDateTime(dateString) {
    if (!dateString) return 'Not set';
    try {
        const date = new Date(dateString);
        const options = { month: 'short', day: 'numeric', year: 'numeric', hour: '2-digit', minute: '2-digit' };
        return date.toLocaleDateString('en-US', options).replace(',', ' •');
    } catch {
        return 'Invalid date';
    }
}

function viewLeaderboard(tournamentId) {
    selectedTournamentId = tournamentId;
    switchTab('leaderboard');
}

// Registration Modal
function openRegistrationModal(tournamentId) {
    currentRegistrationTournamentId = tournamentId;
    document.getElementById('registration-modal').classList.add('show');
    document.getElementById('registration-form').reset();
}

function closeRegistrationModal() {
    document.getElementById('registration-modal').classList.remove('show');
    currentRegistrationTournamentId = null;
}

document.getElementById('registration-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const playerData = {
        username: document.getElementById('reg-username').value,
        email: document.getElementById('reg-email').value,
    };
    
    try {
        await tournamentApi.registerPlayer(currentRegistrationTournamentId, playerData);
        showToast('Registration successful!', `Welcome, ${playerData.username}!`);
        closeRegistrationModal();
        renderTournamentsTab(); // Refresh tournaments list
    } catch (error) {
        showToast('Registration failed', error.message, 'error');
    }
});

// Leaderboard Tab
async function renderLeaderboardTab() {
    const contentDiv = document.getElementById('content-leaderboard');
    
    if (!selectedTournamentId) {
        contentDiv.innerHTML = `
            <div class="card">
                <div class="card-content py-12 text-center">
                    <svg class="h-16 w-16 mx-auto text-gray-300 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4M7.835 4.697a3.42 3.42 0 001.946-.806 3.42 3.42 0 014.438 0 3.42 3.42 0 001.946.806 3.42 3.42 0 013.138 3.138 3.42 3.42 0 00.806 1.946 3.42 3.42 0 010 4.438 3.42 3.42 0 00-.806 1.946 3.42 3.42 0 01-3.138 3.138 3.42 3.42 0 00-1.946.806 3.42 3.42 0 01-4.438 0 3.42 3.42 0 00-1.946-.806 3.42 3.42 0 01-3.138-3.138 3.42 3.42 0 00-.806-1.946 3.42 3.42 0 010-4.438 3.42 3.42 0 00.806-1.946 3.42 3.42 0 013.138-3.138z"></path>
                    </svg>
                    <h3 class="font-medium text-lg mb-2">No Tournament Selected</h3>
                    <p class="text-muted mb-6">Select a tournament from the Tournaments tab to view its leaderboard</p>
                    <button onclick="switchTab('tournaments')" class="text-amber-600 hover:underline">
                        Go to Tournaments
                    </button>
                </div>
            </div>
        `;
        return;
    }
    
    contentDiv.innerHTML = '<div class="text-center py-8 text-gray-600">Loading leaderboard...</div>';
    
    try {
        const [leaderboard, tournaments] = await Promise.all([
            tournamentApi.getLeaderboard(selectedTournamentId),
            tournamentApi.getAllTournaments()
        ]);
        
        const tournament = tournaments.find(t => t.id === selectedTournamentId);
        
        contentDiv.innerHTML = `
            <div class="mb-6">
                <div class="flex items-center gap-4">
                    <button onclick="backToTournaments()" class="btn btn-outline">
                        <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18"></path>
                        </svg>
                    </button>
                    <div>
                        <h2 class="text-2xl font-bold flex items-center gap-2">
                            <svg class="h-6 w-6 text-amber-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15V3m0 12l-4-4m4 4l4-4"></path>
                            </svg>
                            ${tournament?.name || 'Tournament'} Leaderboard
                        </h2>
                        <p class="text-muted">${leaderboard.length} players competing</p>
                    </div>
                </div>
            </div>
            
            ${leaderboard.length >= 3 ? `
                <div class="podium mb-6">
                    <div class="podium-item second">
                        <svg class="h-8 w-8 text-gray-400 mx-auto mb-2" fill="currentColor" viewBox="0 0 20 20">
                            <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z"></path>
                        </svg>
                        <span class="badge badge-secondary mb-2">#2</span>
                        <p class="font-medium">${leaderboard[1].username}</p>
                        <p class="text-sm text-muted mt-1">${leaderboard[1].chipsCurrent.toLocaleString()} chips</p>
                    </div>
                    
                    <div class="podium-item first">
                        <svg class="h-10 w-10 text-yellow-500 mx-auto mb-2" fill="currentColor" viewBox="0 0 20 20">
                            <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z"></path>
                        </svg>
                        <span class="badge badge-default mb-2">#1</span>
                        <p class="font-bold text-lg">${leaderboard[0].username}</p>
                        <p class="text-sm text-muted mt-1">${leaderboard[0].chipsCurrent.toLocaleString()} chips</p>
                    </div>
                    
                    <div class="podium-item third">
                        <svg class="h-8 w-8 text-amber-700 mx-auto mb-2" fill="currentColor" viewBox="0 0 20 20">
                            <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z"></path>
                        </svg>
                        <span class="badge badge-secondary mb-2">#3</span>
                        <p class="font-medium">${leaderboard[2].username}</p>
                        <p class="text-sm text-muted mt-1">${leaderboard[2].chipsCurrent.toLocaleString()} chips</p>
                    </div>
                </div>
            ` : ''}
            
            <div class="card">
                <div class="card-header">
                    <div class="card-title">Rankings</div>
                    <div class="card-description">Complete player standings</div>
                </div>
                <div class="card-content">
                    <div class="space-y-2">
                        ${leaderboard.map((entry, index) => {
                            const winRate = (entry.totalWins / (entry.totalWins + entry.totalLosses)) * 100;
                            const rank = entry.rank || index + 1;
                            
                            return `
                                <div class="leaderboard-row ${rank === 1 ? 'first-place' : ''}">
                                    <div class="flex items-center gap-4 flex-1">
                                        <div class="flex items-center gap-2" style="min-width: 60px;">
                                            ${rank <= 3 ? `
                                                <svg class="h-5 w-5 ${rank === 1 ? 'text-yellow-500' : rank === 2 ? 'text-gray-400' : 'text-amber-700'}" fill="currentColor" viewBox="0 0 20 20">
                                                    <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z"></path>
                                                </svg>
                                            ` : ''}
                                            <span class="badge badge-${rank === 1 ? 'default' : rank <= 3 ? 'secondary' : 'outline'}">#${rank}</span>
                                        </div>
                                        
                                        <div class="flex-1" style="min-width: 150px;">
                                            <p class="font-medium">${entry.username}</p>
                                        </div>
                                        
                                        <div class="hidden md:flex items-center gap-6">
                                            <div class="flex items-center gap-2" style="min-width: 120px;">
                                                <svg class="h-4 w-4 text-amber-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2"></path>
                                                </svg>
                                                <span class="font-medium">${entry.chipsCurrent.toLocaleString()}</span>
                                            </div>
                                            
                                            <div class="flex items-center gap-3" style="min-width: 140px;">
                                                <span class="text-sm text-success">${entry.totalWins}W</span>
                                                <span class="text-sm text-error">${entry.totalLosses}L</span>
                                            </div>
                                            
                                            <div style="min-width: 80px;">
                                                <span class="badge badge-outline">${winRate.toFixed(1)}% WR</span>
                                            </div>
                                        </div>
                                    </div>
                                    
                                    <div class="md:hidden flex flex-col items-end gap-1">
                                        <div class="flex items-center gap-1 text-sm">
                                            <svg class="h-3 w-3 text-amber-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2"></path>
                                            </svg>
                                            <span class="font-medium">${entry.chipsCurrent.toLocaleString()}</span>
                                        </div>
                                        <div class="text-xs text-muted">${entry.totalWins}W / ${entry.totalLosses}L</div>
                                    </div>
                                </div>
                            `;
                        }).join('')}
                    </div>
                    
                    ${leaderboard.length === 0 ? `
                        <div class="text-center py-12 text-muted">
                            <svg class="h-12 w-12 mx-auto mb-4 opacity-50" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15V3m0 12l-4-4m4 4l4-4"></path>
                            </svg>
                            <p>No players yet</p>
                            <p class="text-sm">Leaderboard will update as players join</p>
                        </div>
                    ` : ''}
                </div>
            </div>
        `;
    } catch (error) {
        contentDiv.innerHTML = '<div class="text-center py-8 text-red-600">Failed to load leaderboard</div>';
    }
}

function backToTournaments() {
    selectedTournamentId = null;
    switchTab('tournaments');
}
