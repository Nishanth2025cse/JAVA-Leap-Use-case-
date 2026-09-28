// Violations Module Logic

document.addEventListener('DOMContentLoaded', () => {
    renderSidebar('violations');
    renderHeader('Environmental Compliance Violations', 'Automated violation registry for industrial effluent COD threshold exceedances');
    loadViolations();
});

async function loadViolations() {
    try {
        const list = await fetchAPI('/wastewater/violations');
        renderViolationsTable(list || []);
    } catch (err) {
        console.error(err);
    }
}

function renderViolationsTable(list) {
    const tbody = document.getElementById('violations-tbody');
    if (!list || list.length === 0) {
        tbody.innerHTML = '<tr><td colspan="9" style="text-align:center; color: var(--success);">No environmental violations logged. All industrial discharges are compliant!</td></tr>';
        return;
    }

    tbody.innerHTML = list.map(v => {
        let badgeClass = 'badge-warning';
        if (v.status === 'INVOICED') badgeClass = 'badge-info';
        if (v.status === 'PAID') badgeClass = 'badge-success';

        return `
            <tr>
                <td>#${v.id}</td>
                <td><strong>${v.factory ? v.factory.factoryName : 'Unknown'}</strong></td>
                <td><span style="font-weight:700; color:var(--danger);">${v.codValue} mg/L</span></td>
                <td>${v.permittedLimit} mg/L</td>
                <td><span style="color:var(--danger); font-weight:700;">+${v.excessCod} mg/L</span></td>
                <td><strong style="color:var(--danger); font-size:15px;">${formatCurrency(v.fineAmount)}</strong></td>
                <td>${v.violationDate ? new Date(v.violationDate).toLocaleString() : ''}</td>
                <td><span class="badge ${badgeClass}">${v.status}</span></td>
                <td><small>${v.remarks || ''}</small></td>
            </tr>
        `;
    }).join('');
}
