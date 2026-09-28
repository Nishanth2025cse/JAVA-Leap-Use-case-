// Main Shared Utility JS for Waste Water Platform

const API_BASE = '/api';

// Utility: Fetch API with Error Handling
async function fetchAPI(endpoint, options = {}) {
    const url = endpoint.startsWith('http') ? endpoint : `${API_BASE}${endpoint}`;
    
    const defaultHeaders = {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    };

    options.headers = { ...defaultHeaders, ...options.headers };

    if (options.body && typeof options.body === 'object') {
        options.body = JSON.stringify(options.body);
    }

    try {
        const response = await fetch(url, options);
        if (response.status === 204) return null; // No Content
        
        const data = await response.json();
        
        if (!response.ok) {
            const errorMsg = data.message || data.error || (data.errors ? Object.values(data.errors).join(', ') : 'API Request Failed');
            throw new Error(errorMsg);
        }
        return data;
    } catch (err) {
        console.error('API Error:', err);
        showToast(err.message || 'Network error occurred', 'error');
        throw err;
    }
}

// Check Auth & Return User Session
function checkAuth() {
    const sessionStr = localStorage.getItem('userSession');
    if (!sessionStr) {
        if (!window.location.pathname.endsWith('login.html')) {
            window.location.href = 'login.html';
        }
        return null;
    }
    return JSON.parse(sessionStr);
}

// Render Sidebar Navigation
function renderSidebar(activePage) {
    const session = checkAuth();
    if (!session) return;

    const sidebarContainer = document.getElementById('sidebar-container');
    if (!sidebarContainer) return;

    const isAdmin = session.role === 'ADMIN';
    const isAccountant = session.role === 'ACCOUNTANT';

    // Allow clear navigation for both roles while maintaining primary view
    const sidebarHTML = `
        <aside class="sidebar">
            <div class="sidebar-header">
                <div class="brand-icon">💧</div>
                <div class="brand-text-container">
                    <div class="brand-title">AquaPulse</div>
                    <div class="brand-subtitle">Smart Wastewater Platform</div>
                </div>
            </div>
            
            <nav class="sidebar-nav">
                <a href="dashboard.html" class="nav-item ${activePage === 'dashboard' ? 'active' : ''}">
                    <span class="nav-icon">📊</span> Dashboard
                </a>

                <div class="nav-section-title">Environmental Monitoring</div>
                <a href="telemetry.html" class="nav-item ${activePage === 'telemetry' ? 'active' : ''}">
                    <span class="nav-icon">📡</span> COD Telemetry
                </a>
                <a href="violations.html" class="nav-item ${activePage === 'violations' ? 'active' : ''}">
                    <span class="nav-icon">⚠️</span> Violations Log
                </a>
                <a href="factories.html" class="nav-item ${activePage === 'factories' ? 'active' : ''}">
                    <span class="nav-icon">🏭</span> Industrial Plants
                </a>

                <div class="nav-section-title">Inventory & Purchasing</div>
                <a href="chemicals.html" class="nav-item ${activePage === 'chemicals' ? 'active' : ''}">
                    <span class="nav-icon">🧪</span> Chemical Stock
                </a>
                <a href="suppliers.html" class="nav-item ${activePage === 'suppliers' ? 'active' : ''}">
                    <span class="nav-icon">🚚</span> Suppliers & Vendors
                </a>
                <a href="purchase-orders.html" class="nav-item ${activePage === 'purchase-orders' ? 'active' : ''}">
                    <span class="nav-icon">📋</span> Purchase Orders
                </a>
                <a href="vendor-bills.html" class="nav-item ${activePage === 'vendor-bills' ? 'active' : ''}">
                    <span class="nav-icon">📄</span> Vendor Bills
                </a>

                <div class="nav-section-title">Sales & Customer Billing</div>
                <a href="products.html" class="nav-item ${activePage === 'products' ? 'active' : ''}">
                    <span class="nav-icon">📦</span> Products & Services
                </a>
                <a href="service-orders.html" class="nav-item ${activePage === 'service-orders' ? 'active' : ''}">
                    <span class="nav-icon">⚙️</span> Service Orders
                </a>
                <a href="customer-invoices.html" class="nav-item ${activePage === 'customer-invoices' ? 'active' : ''}">
                    <span class="nav-icon">🧾</span> Customer Invoices
                </a>
                <a href="payments.html" class="nav-item ${activePage === 'payments' ? 'active' : ''}">
                    <span class="nav-icon">💳</span> Customer Payments
                </a>

                <div class="nav-section-title">Accounting & Facility Budget</div>
                <a href="chart-of-accounts.html" class="nav-item ${activePage === 'chart-of-accounts' ? 'active' : ''}">
                    <span class="nav-icon">📖</span> Chart of Accounts
                </a>
                <a href="analytic-accounts.html" class="nav-item ${activePage === 'analytic-accounts' ? 'active' : ''}">
                    <span class="nav-icon">🏗️</span> Treatment Facilities
                </a>
                <a href="budgets.html" class="nav-item ${activePage === 'budgets' ? 'active' : ''}">
                    <span class="nav-icon">💰</span> Budget Allocations
                </a>

                <div class="nav-section-title">System Audit & Settings</div>
                <a href="reports.html" class="nav-item ${activePage === 'reports' ? 'active' : ''}">
                    <span class="nav-icon">📈</span> Financial Reports
                </a>
                <a href="settings.html" class="nav-item ${activePage === 'settings' ? 'active' : ''}">
                    <span class="nav-icon">⚙️</span> Environmental Settings
                </a>
            </nav>

            <div class="sidebar-footer">
                <div class="user-info">
                    <div class="avatar">${session.fullName ? session.fullName.charAt(0) : 'U'}</div>
                    <div class="user-details">
                        <div class="user-name">${session.fullName || session.username}</div>
                        <div class="user-role-badge">${session.role}</div>
                    </div>
                </div>
                <button class="logout-btn" onclick="logout()" title="Logout">🚪</button>
            </div>
        </aside>
    `;

    sidebarContainer.innerHTML = sidebarHTML;
}

// Render Top Navbar Header
function renderHeader(title, subtitle) {
    const session = checkAuth();
    const headerContainer = document.getElementById('top-navbar-container');
    if (!headerContainer) return;

    headerContainer.innerHTML = `
        <div class="top-navbar">
            <div class="page-title-group">
                <h1>${title}</h1>
                <p>${subtitle}</p>
            </div>
            <div class="navbar-actions">
                <div class="status-indicator-badge">
                    <span class="status-dot"></span>
                    <span>IoT Sensors Online</span>
                </div>
                <div class="user-role-pill">
                    👤 ${session ? session.role : ''}
                </div>
            </div>
        </div>
    `;
}

// Toast Notifications
function showToast(message, type = 'info') {
    let container = document.getElementById('toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        container.className = 'toast-container';
        document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    
    let icon = 'ℹ️';
    if (type === 'success') icon = '✅';
    if (type === 'error') icon = '❌';
    if (type === 'warning') icon = '⚠️';

    toast.innerHTML = `<span>${icon}</span><span>${message}</span>`;
    container.appendChild(toast);

    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateX(100%)';
        setTimeout(() => toast.remove(), 300);
    }, 4000);
}

// Modal Controllers
function openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) modal.classList.add('active');
}

function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) modal.classList.remove('active');
}

// Logout Action
function logout() {
    localStorage.removeItem('userSession');
    window.location.href = 'login.html';
}

// Helper: Format Currency
function formatCurrency(amount, symbol = '$') {
    if (amount === undefined || amount === null) return `${symbol}0.00`;
    return `${symbol}${Number(amount).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
}
