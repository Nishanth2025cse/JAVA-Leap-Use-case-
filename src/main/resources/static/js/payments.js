// Payments Log Logic

document.addEventListener('DOMContentLoaded', () => {
    renderSidebar('payments');
    renderHeader('Customer Payments Log', 'Complete history of recorded customer payments and ledger bank deposits');
    loadPaymentsLog();
});

async function loadPaymentsLog() {
    try {
        const list = await fetchAPI('/payments');
        renderPaymentsTable(list || []);
    } catch (err) {
        console.error(err);
    }
}

function renderPaymentsTable(list) {
    const tbody = document.getElementById('payments-tbody');
    if (!list || list.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" style="text-align:center;">No payment transactions recorded yet.</td></tr>';
        return;
    }

    tbody.innerHTML = list.map(p => `
        <tr>
            <td>#PAY-${p.id}</td>
            <td><strong>${p.invoice ? p.invoice.invoiceNumber : 'N/A'}</strong></td>
            <td>${p.invoice && p.invoice.factory ? p.invoice.factory.factoryName : 'Factory'}</td>
            <td><strong style="color:var(--success); font-size:15px;">${formatCurrency(p.amount)}</strong></td>
            <td><span class="badge badge-info">${p.paymentMethod}</span></td>
            <td><code>${p.referenceNumber || 'N/A'}</code></td>
            <td>${p.paymentDate ? new Date(p.paymentDate).toLocaleString() : ''}</td>
        </tr>
    `).join('');
}
