// Telemetry Module Logic

document.addEventListener('DOMContentLoaded', () => {
    renderSidebar('telemetry');
    renderHeader('COD Telemetry Stream', 'Real-time telemetry monitoring for industrial wastewater discharge compliance');
    loadTelemetryLog();
    loadFactoriesDropdown();

    document.getElementById('telemetryForm').addEventListener('submit', submitTelemetry);
});

async function loadTelemetryLog() {
    try {
        const list = await fetchAPI('/wastewater/telemetry');
        renderTelemetryTable(list || []);
    } catch (err) {
        console.error(err);
    }
}

async function loadFactoriesDropdown() {
    try {
        const factories = await fetchAPI('/wastewater/plants');
        const select = document.getElementById('telemetryFactoryId');
        select.innerHTML = '<option value="">-- Select Factory --</option>' + 
            factories.map(f => `<option value="${f.id}">${f.factoryName} (Limit: ${f.permittedCodLimit} mg/L)</option>`).join('');
    } catch (err) {
        console.error(err);
    }
}

function renderTelemetryTable(list) {
    const tbody = document.getElementById('telemetry-tbody');
    if (!list || list.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" style="text-align:center;">No telemetry readings recorded.</td></tr>';
        return;
    }

    tbody.innerHTML = list.map(t => {
        const isCompliant = t.status === 'COMPLIANT';
        return `
            <tr>
                <td>#${t.id}</td>
                <td><strong>${t.factory ? t.factory.factoryName : 'Unknown'}</strong></td>
                <td>
                    <span style="font-weight: 700; font-size: 15px; color: ${isCompliant ? 'var(--success)' : 'var(--danger)'};">
                        ${t.codValue} mg/L
                    </span>
                </td>
                <td>${t.permittedCodLimit || 250} mg/L</td>
                <td>${t.location || 'Outfall'}</td>
                <td>${t.measurementDateTime ? new Date(t.measurementDateTime).toLocaleString() : ''}</td>
                <td>
                    <span class="badge ${isCompliant ? 'badge-success' : 'badge-danger'}">
                        ${isCompliant ? '✅ COMPLIANT' : '⚠️ NON-COMPLIANT'}
                    </span>
                </td>
                <td><strong style="color: ${t.violationAmount > 0 ? 'var(--danger)' : 'var(--text-secondary)'};">${formatCurrency(t.violationAmount)}</strong></td>
            </tr>
        `;
    }).join('');
}

function openTelemetryModal() {
    document.getElementById('telemetryForm').reset();
    openModal('telemetryModal');
}

async function submitTelemetry(e) {
    e.preventDefault();
    const factoryId = document.getElementById('telemetryFactoryId').value;
    const codValue = parseFloat(document.getElementById('codValue').value);
    const location = document.getElementById('telemetryLocation').value;
    const remarks = document.getElementById('telemetryRemarks').value;

    const payload = {
        factory: { id: parseInt(factoryId) },
        codValue: codValue,
        location: location,
        remarks: remarks
    };

    try {
        const result = await fetchAPI('/wastewater/telemetry', {
            method: 'POST',
            body: payload
        });

        if (result.status === 'NON_COMPLIANT') {
            showToast(`⚠️ Telemetry NON-COMPLIANT! Excess COD detected. Fine surcharge levied: ${formatCurrency(result.violationAmount)}`, 'error');
        } else {
            showToast('✅ Telemetry submitted! Discharge is COMPLIANT.', 'success');
        }

        closeModal('telemetryModal');
        loadTelemetryLog();
    } catch (err) {
        // Error handled in fetchAPI
    }
}
