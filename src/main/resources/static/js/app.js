const API_BASE = '/api/v1';

// DOM Elements
const loginView = document.getElementById('login-view');
const dashboardView = document.getElementById('dashboard-view');
const loginForm = document.getElementById('login-form');
const logoutBtn = document.getElementById('logout-btn');
const navItems = document.querySelectorAll('.nav-item');
const sections = document.querySelectorAll('.section');
const pageTitle = document.getElementById('page-title');
const loggedInUserSpan = document.getElementById('logged-in-user');
const flagsTbody = document.getElementById('flags-tbody');
const auditTbody = document.getElementById('audit-tbody');
const toastEl = document.getElementById('toast');

// Modal Elements
const modalOverlay = document.getElementById('flag-modal');
const modalTitle = document.getElementById('modal-title');
const newFlagBtn = document.getElementById('new-flag-btn');
const closeModalBtns = [document.getElementById('close-modal'), document.getElementById('cancel-modal')];
const flagForm = document.getElementById('flag-form');

// State
let token = localStorage.getItem('jwt_token') || null;
let currentUsername = localStorage.getItem('username') || null;
let editMode = false;
let currentEditFlag = null;

// Initialize
function init() {
    if (token) {
        showDashboard();
    } else {
        showLogin();
    }
}

// Authentication
loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    
    try {
        const res = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });
        
        if (!res.ok) throw new Error('Invalid credentials');
        
        const data = await res.json();
        token = data.token;
        currentUsername = username;
        localStorage.setItem('jwt_token', token);
        localStorage.setItem('username', username);
        
        showToast('Login successful', 'success');
        showDashboard();
    } catch (err) {
        showToast(err.message, 'error');
    }
});

logoutBtn.addEventListener('click', () => {
    token = null;
    currentUsername = null;
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('username');
    showLogin();
});

// View Switching
function showLogin() {
    dashboardView.classList.remove('active-view');
    loginView.classList.add('active-view');
    loginForm.reset();
}

function showDashboard() {
    loginView.classList.remove('active-view');
    dashboardView.classList.add('active-view');
    loggedInUserSpan.textContent = currentUsername;
    loadFlags();
}

// Navigation
navItems.forEach(item => {
    item.addEventListener('click', (e) => {
        e.preventDefault();
        navItems.forEach(nav => nav.classList.remove('active'));
        item.classList.add('active');
        
        const targetId = item.getAttribute('data-target');
        sections.forEach(sec => sec.classList.remove('active-section'));
        document.getElementById(targetId).classList.add('active-section');
        
        if (targetId === 'flags-section') {
            pageTitle.textContent = 'Feature Flags';
            loadFlags();
        } else if (targetId === 'audit-section') {
            pageTitle.textContent = 'Audit Logs';
            loadAuditLogs();
        }
    });
});

// Fetching Data with Auth Header
async function fetchAuth(url, options = {}) {
    if (!options.headers) options.headers = {};
    options.headers['Authorization'] = `Bearer ${token}`;
    
    const res = await fetch(url, options);
    if (res.status === 401 || res.status === 403) {
        logoutBtn.click();
        throw new Error('Session expired');
    }
    return res;
}

// Flags Management
async function loadFlags() {
    try {
        const res = await fetchAuth(`${API_BASE}/flags`);
        if (!res.ok) throw new Error('Failed to fetch flags');
        const flags = await res.json();
        renderFlags(flags);
    } catch (err) {
        showToast(err.message, 'error');
    }
}

function renderFlags(flags) {
    flagsTbody.innerHTML = '';
    flags.forEach(flag => {
        const tr = document.createElement('tr');
        const envClass = flag.environment === 'PROD' ? 'badge-danger' : 
                         flag.environment === 'STAGING' ? 'badge-primary' : 'badge-neutral';
        const statusClass = flag.enabled ? 'badge-success' : 'badge-neutral';
        
        tr.innerHTML = `
            <td>
                <strong>${flag.name}</strong>
                <div style="font-size: 12px; color: var(--text-secondary); margin-top: 4px;">${flag.description || ''}</div>
            </td>
            <td><span class="badge ${envClass}">${flag.environment}</span></td>
            <td>${flag.rolloutPercentage}%</td>
            <td><span class="badge ${statusClass}">${flag.enabled ? 'Enabled' : 'Disabled'}</span></td>
            <td>
                <div style="display:flex; gap:8px;">
                    <button class="btn-icon" onclick="toggleFlag('${flag.name}')" title="Toggle">
                        <ion-icon name="power-outline" style="color: ${flag.enabled ? 'var(--success)' : 'var(--text-secondary)'}"></ion-icon>
                    </button>
                    <button class="btn-icon" onclick="editFlag('${flag.name}')" title="Edit">
                        <ion-icon name="create-outline"></ion-icon>
                    </button>
                    <button class="btn-icon" onclick="deleteFlag('${flag.name}')" title="Delete">
                        <ion-icon name="trash-outline" style="color: var(--danger)"></ion-icon>
                    </button>
                </div>
            </td>
        `;
        flagsTbody.appendChild(tr);
    });
}

// Audit Logs Management
async function loadAuditLogs() {
    try {
        const res = await fetchAuth(`${API_BASE}/audit-logs`);
        if (!res.ok) throw new Error('Failed to fetch audit logs');
        const logs = await res.json();
        renderAuditLogs(logs);
    } catch (err) {
        showToast(err.message, 'error');
    }
}

function renderAuditLogs(logs) {
    auditTbody.innerHTML = '';
    logs.forEach(log => {
        const tr = document.createElement('tr');
        const date = new Date(log.timestamp).toLocaleString();
        let actionClass = 'badge-neutral';
        if (log.action === 'CREATE') actionClass = 'badge-success';
        if (log.action === 'UPDATE') actionClass = 'badge-primary';
        if (log.action === 'DELETE') actionClass = 'badge-danger';
        if (log.action === 'TOGGLE') actionClass = 'badge-primary';

        tr.innerHTML = `
            <td style="font-size: 13px; color: var(--text-secondary);">${date}</td>
            <td><span class="badge ${actionClass}">${log.action}</span></td>
            <td><strong>${log.flagName}</strong></td>
            <td>${log.changedBy}</td>
            <td style="font-size: 13px;">${log.details || ''}</td>
        `;
        auditTbody.appendChild(tr);
    });
}

// Modal & Form Logic
newFlagBtn.addEventListener('click', () => {
    editMode = false;
    currentEditFlag = null;
    modalTitle.textContent = 'Create Feature Flag';
    document.getElementById('flag-name').disabled = false;
    flagForm.reset();
    modalOverlay.classList.add('active');
});

closeModalBtns.forEach(btn => {
    btn.addEventListener('click', () => {
        modalOverlay.classList.remove('active');
    });
});

window.editFlag = async (name) => {
    try {
        const res = await fetchAuth(`${API_BASE}/flags/${name}`);
        if (!res.ok) throw new Error('Failed to fetch flag details');
        const flag = await res.json();
        
        editMode = true;
        currentEditFlag = name;
        modalTitle.textContent = 'Edit Feature Flag';
        
        document.getElementById('flag-name').value = flag.name;
        document.getElementById('flag-name').disabled = true; // Cannot edit name
        document.getElementById('flag-desc').value = flag.description || '';
        document.getElementById('flag-env').value = flag.environment;
        document.getElementById('flag-rollout').value = flag.rolloutPercentage;
        document.getElementById('flag-targets').value = flag.targetUserIds || '';
        document.getElementById('flag-enabled').checked = flag.enabled;
        
        modalOverlay.classList.add('active');
    } catch (err) {
        showToast(err.message, 'error');
    }
};

window.deleteFlag = async (name) => {
    if (!confirm(`Are you sure you want to delete flag '${name}'?`)) return;
    
    try {
        const res = await fetchAuth(`${API_BASE}/flags/${name}`, { method: 'DELETE' });
        if (!res.ok) throw new Error('Failed to delete flag');
        showToast('Flag deleted', 'success');
        loadFlags();
    } catch (err) {
        showToast(err.message, 'error');
    }
};

window.toggleFlag = async (name) => {
    try {
        const res = await fetchAuth(`${API_BASE}/flags/${name}/toggle`, { method: 'PUT' });
        if (!res.ok) throw new Error('Failed to toggle flag');
        loadFlags();
    } catch (err) {
        showToast(err.message, 'error');
    }
};

flagForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const flagData = {
        name: document.getElementById('flag-name').value,
        description: document.getElementById('flag-desc').value,
        environment: document.getElementById('flag-env').value,
        rolloutPercentage: parseInt(document.getElementById('flag-rollout').value, 10),
        targetUserIds: document.getElementById('flag-targets').value,
        enabled: document.getElementById('flag-enabled').checked
    };
    
    try {
        let res;
        if (editMode) {
            res = await fetchAuth(`${API_BASE}/flags/${currentEditFlag}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(flagData)
            });
        } else {
            res = await fetchAuth(`${API_BASE}/flags`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(flagData)
            });
        }
        
        if (!res.ok) {
            const errorData = await res.json();
            throw new Error(errorData.error || 'Failed to save flag');
        }
        
        showToast(editMode ? 'Flag updated' : 'Flag created', 'success');
        modalOverlay.classList.remove('active');
        loadFlags();
    } catch (err) {
        showToast(err.message, 'error');
    }
});

// Toast Utility
function showToast(message, type = 'success') {
    toastEl.textContent = message;
    toastEl.style.backgroundColor = type === 'success' ? 'var(--success)' : 'var(--danger)';
    toastEl.classList.add('show');
    setTimeout(() => {
        toastEl.classList.remove('show');
    }, 3000);
}

// Start
init();
