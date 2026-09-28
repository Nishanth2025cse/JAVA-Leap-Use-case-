// Financial & Environmental Reports Logic

document.addEventListener('DOMContentLoaded', () => {
    renderSidebar('reports');
    renderHeader('System Reports & Audits', 'Comprehensive Balance Sheet, Profit & Loss, Budget Utilization & Environmental Compliance Statements');
    switchReportTab('balance-sheet');
});

function switchReportTab(tabName) {
    document.getElementById('btn-tab-bs').className = tabName === 'balance-sheet' ? 'btn btn-primary' : 'btn btn-secondary';
    document.getElementById('btn-tab-pl').className = tabName === 'profit-loss' ? 'btn btn-primary' : 'btn btn-secondary';
    document.getElementById('btn-tab-bud').className = tabName === 'budget' ? 'btn btn-primary' : 'btn btn-secondary';
    document.getElementById('btn-tab-comp').className = tabName === 'compliance' ? 'btn btn-primary' : 'btn btn-secondary';

    if (tabName === 'balance-sheet') loadBalanceSheet();
    if (tabName === 'profit-loss') loadProfitLoss();
    if (tabName === 'budget') loadBudgetReport();
    if (tabName === 'compliance') loadComplianceReport();
}

async function loadBalanceSheet() {
    const container = document.getElementById('report-content-container');
    try {
        const data = await fetchAPI('/reports/balance-sheet');
        
        let assetsRows = '';
        for (const [k, v] of Object.entries(data.assets || {})) {
            assetsRows += `<tr><td>${k}</td><td style="text-align:right;">${formatCurrency(v)}</td></tr>`;
        }

        let liabRows = '';
        for (const [k, v] of Object.entries(data.liabilities || {})) {
            liabRows += `<tr><td>${k}</td><td style="text-align:right;">${formatCurrency(v)}</td></tr>`;
        }

        container.innerHTML = `
            <div class="card">
                <div class="card-header">
                    <h2 class="card-title">Statement of Financial Position (Balance Sheet)</h2>
                </div>

                <div class="report-section-title">ASSETS</div>
                <table class="custom-table" style="margin-bottom: 16px;">
                    <tbody>
                        ${assetsRows}
                        <tr class="report-total-row">
                            <td>TOTAL ASSETS</td>
                            <td style="text-align:right; font-size:16px;">${formatCurrency(data.totalAssets)}</td>
                        </tr>
                    </tbody>
                </table>

                <div class="report-section-title">LIABILITIES</div>
                <table class="custom-table" style="margin-bottom: 16px;">
                    <tbody>
                        ${liabRows}
                        <tr class="report-total-row">
                            <td>TOTAL LIABILITIES</td>
                            <td style="text-align:right; font-size:16px;">${formatCurrency(data.totalLiabilities)}</td>
                        </tr>
                    </tbody>
                </table>

                <div class="report-section-title" style="background-color: var(--primary-light); color: var(--primary-hover);">EQUITY & NET RESERVES</div>
                <table class="custom-table">
                    <tbody>
                        <tr class="report-total-row">
                            <td>NET EQUITY & CAPITAL FUND</td>
                            <td style="text-align:right; font-size:18px; color:var(--primary-hover);">${formatCurrency(data.equity)}</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        `;
    } catch (err) {
        console.error(err);
    }
}

async function loadProfitLoss() {
    const container = document.getElementById('report-content-container');
    try {
        const data = await fetchAPI('/reports/profit-loss');

        let incomeRows = '';
        for (const [k, v] of Object.entries(data.income || {})) {
            incomeRows += `<tr><td>${k}</td><td style="text-align:right;">${formatCurrency(v)}</td></tr>`;
        }

        let expenseRows = '';
        for (const [k, v] of Object.entries(data.expenses || {})) {
            expenseRows += `<tr><td>${k}</td><td style="text-align:right;">${formatCurrency(v)}</td></tr>`;
        }

        container.innerHTML = `
            <div class="card">
                <div class="card-header">
                    <h2 class="card-title">Statement of Income & Operational Expenses (Profit & Loss)</h2>
                </div>

                <div class="report-section-title">OPERATIONAL INCOME</div>
                <table class="custom-table" style="margin-bottom: 16px;">
                    <tbody>
                        ${incomeRows}
                        <tr class="report-total-row">
                            <td>TOTAL REVENUE / INCOME</td>
                            <td style="text-align:right; font-size:16px; color:var(--success);">${formatCurrency(data.totalIncome)}</td>
                        </tr>
                    </tbody>
                </table>

                <div class="report-section-title">OPERATIONAL EXPENSES</div>
                <table class="custom-table" style="margin-bottom: 16px;">
                    <tbody>
                        ${expenseRows}
                        <tr class="report-total-row">
                            <td>TOTAL EXPENSES</td>
                            <td style="text-align:right; font-size:16px; color:var(--danger);">${formatCurrency(data.totalExpenses)}</td>
                        </tr>
                    </tbody>
                </table>

                <div class="report-section-title" style="background-color: var(--primary-light);">NET OPERATIONAL PROFIT / LOSS</div>
                <table class="custom-table">
                    <tbody>
                        <tr class="report-total-row">
                            <td>NET SURPLUS / (DEFICIT)</td>
                            <td style="text-align:right; font-size:18px; color:${data.netProfitLoss >= 0 ? 'var(--success)' : 'var(--danger)'};">
                                ${formatCurrency(data.netProfitLoss)}
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        `;
    } catch (err) {
        console.error(err);
    }
}

async function loadBudgetReport() {
    const container = document.getElementById('report-content-container');
    try {
        const data = await fetchAPI('/reports/budget');
        const list = data.facilityDetails || [];

        let rows = list.map(item => `
            <tr>
                <td><strong>${item.facilityName}</strong></td>
                <td>${item.plantLocation || ''}</td>
                <td>${item.financialYear}</td>
                <td>${formatCurrency(item.allocatedAmount)}</td>
                <td><span style="color:var(--danger); font-weight:700;">${formatCurrency(item.actualAmount)}</span></td>
                <td><span style="color:var(--success); font-weight:700;">${formatCurrency(item.remainingAmount)}</span></td>
                <td><strong>${item.utilizationPercentage}%</strong></td>
                <td><span class="badge ${item.status === 'CRITICAL' ? 'badge-danger' : 'badge-success'}">${item.status}</span></td>
            </tr>
        `).join('');

        container.innerHTML = `
            <div class="card">
                <div class="card-header">
                    <h2 class="card-title">Treatment Facility Budget Performance Summary</h2>
                </div>
                <div class="table-responsive">
                    <table class="custom-table">
                        <thead>
                            <tr>
                                <th>Facility Name</th>
                                <th>Location</th>
                                <th>FY</th>
                                <th>Allocated Budget</th>
                                <th>Actual Spending</th>
                                <th>Remaining Balance</th>
                                <th>Utilization %</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${rows}
                            <tr class="report-total-row">
                                <td colspan="3">OVERALL TOTALS</td>
                                <td>${formatCurrency(data.totalAllocated)}</td>
                                <td>${formatCurrency(data.totalActual)}</td>
                                <td>${formatCurrency(data.totalRemaining)}</td>
                                <td>${data.overallUtilizationPercentage}%</td>
                                <td>-</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        `;
    } catch (err) {
        console.error(err);
    }
}

async function loadComplianceReport() {
    const container = document.getElementById('report-content-container');
    try {
        const data = await fetchAPI('/reports/compliance');
        const summaries = data.factorySummaries || [];

        let factoryRows = summaries.map(s => `
            <tr>
                <td><strong>${s.factoryName}</strong></td>
                <td><span class="badge badge-danger">${s.violationCount} Violations</span></td>
                <td><strong style="color:var(--danger); font-size:15px;">${formatCurrency(s.totalFineAmount)}</strong></td>
            </tr>
        `).join('');

        container.innerHTML = `
            <div class="card">
                <div class="card-header">
                    <h2 class="card-title">Municipal Environmental Compliance & Violation Audit</h2>
                </div>
                
                <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 16px; margin-bottom: 24px;">
                    <div style="padding:16px; background:#f8fafc; border-radius:var(--radius-md); border:1px solid var(--border);">
                        <div style="font-size:12px; color:var(--text-muted); font-weight:700;">TOTAL COD READINGS</div>
                        <div style="font-size:22px; font-weight:700;">${data.totalTelemetryRecords}</div>
                    </div>
                    <div style="padding:16px; background:var(--success-bg); border-radius:var(--radius-md);">
                        <div style="font-size:12px; color:var(--success); font-weight:700;">COMPLIANT READINGS</div>
                        <div style="font-size:22px; font-weight:700; color:var(--success);">${data.compliantRecords}</div>
                    </div>
                    <div style="padding:16px; background:var(--danger-bg); border-radius:var(--radius-md);">
                        <div style="font-size:12px; color:var(--danger); font-weight:700;">NON-COMPLIANT READINGS</div>
                        <div style="font-size:22px; font-weight:700; color:var(--danger);">${data.nonCompliantRecords}</div>
                    </div>
                    <div style="padding:16px; background:#fef3c7; border-radius:var(--radius-md);">
                        <div style="font-size:12px; color:#b45309; font-weight:700;">TOTAL FINES LEVIED</div>
                        <div style="font-size:22px; font-weight:700; color:#b45309;">${formatCurrency(data.totalFineAmount)}</div>
                    </div>
                </div>

                <div class="report-section-title">FACTORY-WISE VIOLATION BREAKDOWN</div>
                <div class="table-responsive">
                    <table class="custom-table">
                        <thead>
                            <tr>
                                <th>Industrial Plant</th>
                                <th>Violation Count</th>
                                <th>Total Levied Fine Surcharge</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${factoryRows.length > 0 ? factoryRows : '<tr><td colspan="3" style="text-align:center;">No violations recorded across any factories.</td></tr>'}
                        </tbody>
                    </table>
                </div>
            </div>
        `;
    } catch (err) {
        console.error(err);
    }
}
