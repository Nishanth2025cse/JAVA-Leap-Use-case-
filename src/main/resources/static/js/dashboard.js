// Dashboard Logic & Chart Rendering

document.addEventListener('DOMContentLoaded', () => {
    renderSidebar('dashboard');
    renderHeader('Municipal Waste Water Control Dashboard', 'Real-time telemetry, chemical inventory & environmental compliance monitoring');
    loadDashboardSummary();
});

async function loadDashboardSummary() {
    try {
        const summary = await fetchAPI('/dashboard/summary');
        if (!summary) return;

        // Populate Top KPI Cards
        document.getElementById('kpi-factories').innerText = summary.totalFactories || 0;
        document.getElementById('kpi-cod-readings').innerText = summary.todayCodReadings || 0;
        document.getElementById('kpi-non-compliant').innerText = summary.nonCompliantDischarges || 0;
        document.getElementById('kpi-pending-invoices').innerText = summary.pendingCustomerInvoices || 0;
        
        const lowStockCount = summary.chemicalLowStock || 0;
        const lowStockElem = document.getElementById('kpi-low-stock');
        lowStockElem.innerText = lowStockCount;
        if (lowStockCount > 0) {
            document.getElementById('kpi-low-stock-sub').innerHTML = `<span class="badge badge-danger">LOW STOCK (${lowStockCount})</span>`;
        }

        document.getElementById('kpi-total-fines').innerText = formatCurrency(summary.totalComplianceFines);
        document.getElementById('kpi-budget-util').innerText = `${summary.currentBudgetUtilization || 0}%`;
        document.getElementById('kpi-pending-bills').innerText = formatCurrency(summary.pendingVendorBillsAmount);

        // Populate Tables
        renderRecentTelemetry(summary.recentTelemetry || []);
        renderRecentViolations(summary.recentViolations || []);
        renderLowStockItems(summary.lowStockItems || []);
        renderPendingInvoices(summary.pendingInvoices || []);

        // Render Charts
        initDashboardCharts(summary);

    } catch (err) {
        console.error('Failed to load dashboard summary:', err);
    }
}

function renderRecentTelemetry(readings) {
    const tbody = document.getElementById('recent-telemetry-tbody');
    if (!readings || readings.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" style="text-align:center;">No telemetry readings registered today.</td></tr>';
        return;
    }

    tbody.innerHTML = readings.slice(0, 5).map(r => `
        <tr>
            <td><strong>${r.factory ? r.factory.factoryName : 'Unknown'}</strong></td>
            <td><span style="font-weight: 700; color: ${r.status === 'NON_COMPLIANT' ? 'var(--danger)' : 'var(--success)'};">${r.codValue} mg/L</span></td>
            <td>${r.permittedCodLimit} mg/L</td>
            <td>
                <span class="badge ${r.status === 'COMPLIANT' ? 'badge-success' : 'badge-danger'}">
                    ${r.status === 'COMPLIANT' ? '✅ COMPLIANT' : '⚠️ NON-COMPLIANT'}
                </span>
            </td>
            <td>${formatCurrency(r.violationAmount)}</td>
        </tr>
    `).join('');
}

function renderRecentViolations(violations) {
    const tbody = document.getElementById('recent-violations-tbody');
    if (!violations || violations.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4" style="text-align:center; color: var(--success);">No environmental violations recorded.</td></tr>';
        return;
    }

    tbody.innerHTML = violations.slice(0, 5).map(v => `
        <tr>
            <td><strong>${v.factory ? v.factory.factoryName : 'Unknown'}</strong></td>
            <td><span style="color: var(--danger); font-weight: 700;">+${v.excessCod} mg/L</span></td>
            <td>${formatCurrency(v.fineAmount)}</td>
            <td><span class="badge badge-warning">${v.status}</span></td>
        </tr>
    `).join('');
}

function renderLowStockItems(items) {
    const tbody = document.getElementById('low-stock-tbody');
    if (!items || items.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4" style="text-align:center; color: var(--success);">All chemical stocks are optimal.</td></tr>';
        return;
    }

    tbody.innerHTML = items.map(item => `
        <tr>
            <td><strong>${item.product ? item.product.productName : 'Chemical'}</strong></td>
            <td><span style="color: var(--danger); font-weight: 700;">${item.quantity} ${item.product ? item.product.unit : ''}</span></td>
            <td>${item.reorderLevel} ${item.product ? item.product.unit : ''}</td>
            <td><span class="badge badge-danger">LOW STOCK</span></td>
        </tr>
    `).join('');
}

function renderPendingInvoices(invoices) {
    const tbody = document.getElementById('pending-invoices-tbody');
    if (!invoices || invoices.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4" style="text-align:center; color: var(--success);">No pending invoices.</td></tr>';
        return;
    }

    tbody.innerHTML = invoices.slice(0, 5).map(inv => `
        <tr>
            <td><strong>${inv.invoiceNumber}</strong></td>
            <td>${inv.factory ? inv.factory.factoryName : ''}</td>
            <td>${formatCurrency(inv.totalAmount)}</td>
            <td><span style="color: var(--danger); font-weight: 700;">${formatCurrency(inv.balanceAmount)}</span></td>
        </tr>
    `).join('');
}

function initDashboardCharts(summary) {
    // 1. Telemetry Chart
    const ctx1 = document.getElementById('telemetryChart').getContext('2d');
    new Chart(ctx1, {
        type: 'line',
        data: {
            labels: ['08:00', '10:00', '12:00', '14:00', '16:00', '18:00'],
            datasets: [
                {
                    label: 'Measured COD Level (mg/L)',
                    data: [180, 210, 360, 240, 270, 190],
                    borderColor: '#ef4444',
                    backgroundColor: 'rgba(239, 68, 68, 0.1)',
                    fill: true,
                    tension: 0.4
                },
                {
                    label: 'Permitted Threshold Limit',
                    data: [250, 250, 250, 250, 250, 250],
                    borderColor: '#10b981',
                    borderDash: [5, 5],
                    fill: false
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { position: 'top' } }
        }
    });

    // 2. Financial Chart
    const ctx2 = document.getElementById('financialChart').getContext('2d');
    new Chart(ctx2, {
        type: 'doughnut',
        data: {
            labels: ['Effluent Fees', 'Compliance Fines Surcharges'],
            datasets: [{
                data: [15000, summary.totalComplianceFines || 8000],
                backgroundColor: ['#0284c7', '#ef4444']
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { position: 'bottom' } }
        }
    });
}
