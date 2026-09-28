// Accounts, Facilities & Budgets Logic

document.addEventListener('DOMContentLoaded', () => {
    if (window.location.pathname.endsWith('chart-of-accounts.html')) {
        renderSidebar('chart-of-accounts');
        renderHeader('Chart of Accounts', 'General ledger accounts for municipal wastewater infrastructure and finances');
        loadAccounts();
    } else if (window.location.pathname.endsWith('analytic-accounts.html')) {
        renderSidebar('analytic-accounts');
        renderHeader('Treatment Facilities', 'Analytic accounts for wastewater purification plants');
        loadFacilities();
        const fForm = document.getElementById('facilityForm');
        if (fForm) fForm.addEventListener('submit', handleCreateFacility);
    } else if (window.location.pathname.endsWith('budgets.html')) {
        renderSidebar('budgets');
        renderHeader('Budget Allocations', 'Track actual spending vs allocated operational budgets per treatment facility');
        loadBudgets();
        loadFacilityDropdown();
        const bForm = document.getElementById('budgetForm');
        if (bForm) bForm.addEventListener('submit', handleCreateBudget);
    }
});

// Accounts
async function loadAccounts() {
    try {
        const list = await fetchAPI('/accounts');
        renderAccountsTable(list || []);
    } catch (err) {
        console.error(err);
    }
}

function renderAccountsTable(list) {
    const tbody = document.getElementById('accounts-tbody');
    if (!tbody) return;

    tbody.innerHTML = list.map(a => `
        <tr>
            <td><code>${a.accountCode}</code></td>
            <td><strong>${a.accountName}</strong></td>
            <td><span class="badge badge-info">${a.accountType}</span></td>
            <td>${formatCurrency(a.openingBalance)}</td>
            <td><strong style="color:var(--primary); font-size:15px;">${formatCurrency(a.currentBalance)}</strong></td>
            <td><span class="badge badge-success">ACTIVE</span></td>
        </tr>
    `).join('');
}

// Facilities
async function loadFacilities() {
    try {
        const list = await fetchAPI('/analytic-accounts');
        renderFacilityTable(list || []);
    } catch (err) {
        console.error(err);
    }
}

function renderFacilityTable(list) {
    const tbody = document.getElementById('facility-tbody');
    if (!tbody) return;

    tbody.innerHTML = list.map(f => `
        <tr>
            <td>#${f.id}</td>
            <td><strong>${f.name}</strong></td>
            <td>${f.plantLocation || 'N/A'}</td>
            <td>${f.description || ''}</td>
            <td><span class="badge badge-success">ACTIVE</span></td>
        </tr>
    `).join('');
}

function openFacilityModal() {
    document.getElementById('facilityForm').reset();
    openModal('facilityModal');
}

async function handleCreateFacility(e) {
    e.preventDefault();
    const payload = {
        name: document.getElementById('facilityName').value,
        plantLocation: document.getElementById('facilityLocation').value,
        description: document.getElementById('facilityDesc').value
    };
    try {
        await fetchAPI('/analytic-accounts', { method: 'POST', body: payload });
        showToast('New Treatment Facility registered!', 'success');
        closeModal('facilityModal');
        loadFacilities();
    } catch (err) {
        // Handled
    }
}

// Budgets
async function loadBudgets() {
    try {
        const list = await fetchAPI('/budgets');
        renderBudgetsTable(list || []);
    } catch (err) {
        console.error(err);
    }
}

async function loadFacilityDropdown() {
    try {
        const facilities = await fetchAPI('/analytic-accounts');
        const select = document.getElementById('bFacilityId');
        if (select) select.innerHTML = facilities.map(f => `<option value="${f.id}">${f.name}</option>`).join('');
    } catch (err) {
        console.error(err);
    }
}

function renderBudgetsTable(list) {
    const tbody = document.getElementById('budgets-tbody');
    if (!tbody) return;

    if (!list || list.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" style="text-align:center;">No budgets allocated yet.</td></tr>';
        return;
    }

    tbody.innerHTML = list.map(b => {
        const util = b.allocatedAmount > 0 ? ((b.actualAmount / b.allocatedAmount) * 100).toFixed(1) : 0;
        let badgeClass = 'badge-success';
        if (b.status === 'WARNING') badgeClass = 'badge-warning';
        if (b.status === 'CRITICAL') badgeClass = 'badge-danger';

        return `
            <tr>
                <td>#BUD-${b.id}</td>
                <td><strong>${b.analyticAccount ? b.analyticAccount.name : 'Facility'}</strong></td>
                <td>${b.financialYear}</td>
                <td>${formatCurrency(b.allocatedAmount)}</td>
                <td><strong style="color:var(--danger);">${formatCurrency(b.actualAmount)}</strong></td>
                <td><strong style="color:var(--success);">${formatCurrency(b.remainingAmount)}</strong></td>
                <td><strong>${util}%</strong></td>
                <td><span class="badge ${badgeClass}">${b.status}</span></td>
            </tr>
        `;
    }).join('');
}

function openBudgetModal() {
    document.getElementById('budgetForm').reset();
    openModal('budgetModal');
}

async function handleCreateBudget(e) {
    e.preventDefault();
    const facilityId = document.getElementById('bFacilityId').value;
    const year = document.getElementById('bYear').value;
    const allocated = parseFloat(document.getElementById('bAllocated').value);

    const payload = {
        analyticAccount: { id: parseInt(facilityId) },
        financialYear: year,
        allocatedAmount: allocated
    };

    try {
        await fetchAPI('/budgets', { method: 'POST', body: payload });
        showToast('Budget allocation saved successfully!', 'success');
        closeModal('budgetModal');
        loadBudgets();
    } catch (err) {
        // Handled
    }
}
