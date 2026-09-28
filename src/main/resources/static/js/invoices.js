// Service Orders & Customer Invoices Logic

document.addEventListener('DOMContentLoaded', () => {
    if (window.location.pathname.endsWith('service-orders.html')) {
        renderSidebar('service-orders');
        renderHeader('Service Orders', 'Manage industrial effluent processing service orders for registered factories');
        loadServiceOrders();
        loadSOFactoriesDropdown();
        const soForm = document.getElementById('soForm');
        if (soForm) soForm.addEventListener('submit', handleCreateSO);
    } else if (window.location.pathname.endsWith('customer-invoices.html')) {
        renderSidebar('customer-invoices');
        renderHeader('Customer Invoices', 'Generate billing invoices with automatic environmental violation fine surcharges');
        loadCustomerInvoices();
        loadInvoiceDropdowns();
        const invForm = document.getElementById('invoiceForm');
        if (invForm) invForm.addEventListener('submit', handleCreateInvoice);
        const cPayForm = document.getElementById('custPaymentForm');
        if (cPayForm) cPayForm.addEventListener('submit', handleRecordCustomerPayment);
    }
});

let serviceOrdersList = [];
let customerInvoicesList = [];

// Service Orders
async function loadServiceOrders() {
    try {
        serviceOrdersList = await fetchAPI('/service-orders');
        renderSOTable(serviceOrdersList || []);
    } catch (err) {
        console.error(err);
    }
}

async function loadSOFactoriesDropdown() {
    try {
        const factories = await fetchAPI('/wastewater/plants');
        const select = document.getElementById('soFactoryId');
        if (select) select.innerHTML = factories.map(f => `<option value="${f.id}">${f.factoryName}</option>`).join('');
    } catch (err) {
        console.error(err);
    }
}

function renderSOTable(list) {
    const tbody = document.getElementById('so-tbody');
    if (!tbody) return;

    if (!list || list.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" style="text-align:center;">No service orders found.</td></tr>';
        return;
    }

    tbody.innerHTML = list.map(so => `
        <tr>
            <td><strong>#SO-${so.id}</strong></td>
            <td>${so.factory ? so.factory.factoryName : 'Factory'}</td>
            <td>${so.orderDate || ''}</td>
            <td>${so.serviceType}</td>
            <td>${so.quantity}</td>
            <td>${formatCurrency(so.unitPrice)}</td>
            <td><strong>${formatCurrency(so.totalAmount)}</strong></td>
            <td><span class="badge ${so.status === 'INVOICED' ? 'badge-info' : 'badge-warning'}">${so.status}</span></td>
        </tr>
    `).join('');
}

async function handleCreateSO(e) {
    e.preventDefault();
    const factoryId = document.getElementById('soFactoryId').value;
    const serviceType = document.getElementById('soServiceType').value;
    const period = document.getElementById('soPeriod').value;
    const qty = parseFloat(document.getElementById('soQuantity').value);
    const price = parseFloat(document.getElementById('soUnitPrice').value);

    const payload = {
        factory: { id: parseInt(factoryId) },
        serviceType: serviceType,
        servicePeriod: period,
        quantity: qty,
        unitPrice: price
    };

    try {
        await fetchAPI('/service-orders', { method: 'POST', body: payload });
        showToast('Service Order created successfully!', 'success');
        closeModal('soModal');
        loadServiceOrders();
    } catch (err) {
        // Handled
    }
}

function openSOModal() {
    document.getElementById('soForm').reset();
    openModal('soModal');
}

// Customer Invoices
async function loadCustomerInvoices() {
    try {
        customerInvoicesList = await fetchAPI('/customer-invoices');
        renderInvoicesTable(customerInvoicesList || []);
    } catch (err) {
        console.error(err);
    }
}

async function loadInvoiceDropdowns() {
    try {
        const factories = await fetchAPI('/wastewater/plants');
        const fSelect = document.getElementById('invFactoryId');
        if (fSelect) {
            fSelect.innerHTML = '<option value="">-- Select Factory --</option>' + 
                factories.map(f => `<option value="${f.id}">${f.factoryName}</option>`).join('');
        }
    } catch (err) {
        console.error(err);
    }
}

async function onInvoiceFactoryChange() {
    const factoryId = document.getElementById('invFactoryId').value;
    const previewBox = document.getElementById('violationPreviewBox');
    const previewText = document.getElementById('violationPreviewText');
    const soSelect = document.getElementById('invServiceOrderId');

    if (!factoryId) {
        previewBox.style.display = 'none';
        return;
    }

    try {
        // Check open violations for this factory
        const violations = await fetchAPI(`/wastewater/violations/factory/${factoryId}?openOnly=true`);
        if (violations && violations.length > 0) {
            const totalFines = violations.reduce((sum, v) => sum + (v.fineAmount || 0), 0);
            previewBox.style.display = 'block';
            previewText.innerHTML = `This factory has <strong>${violations.length} OPEN environmental violation(s)</strong> with unbilled COD excess fines totaling <strong style="color:var(--danger);">${formatCurrency(totalFines)}</strong>. This surcharge will be automatically included in the generated invoice.`;
        } else {
            previewBox.style.display = 'none';
        }

        // Load service orders for this factory
        const serviceOrders = await fetchAPI('/service-orders');
        const factorySO = serviceOrders.filter(so => so.factory && so.factory.id == factoryId && so.status === 'PENDING');
        soSelect.innerHTML = '<option value="">-- Direct Invoice / Standard Fee --</option>' + 
            factorySO.map(so => `<option value="${so.id}">#SO-${so.id} - ${so.serviceType} (${formatCurrency(so.totalAmount)})</option>`).join('');

    } catch (err) {
        console.error(err);
    }
}

function renderInvoicesTable(list) {
    const tbody = document.getElementById('invoices-tbody');
    if (!tbody) return;

    if (!list || list.length === 0) {
        tbody.innerHTML = '<tr><td colspan="10" style="text-align:center;">No customer invoices issued yet.</td></tr>';
        return;
    }

    tbody.innerHTML = list.map(inv => `
        <tr>
            <td><strong>${inv.invoiceNumber}</strong></td>
            <td>${inv.factory ? inv.factory.factoryName : ''}</td>
            <td>${formatCurrency(inv.treatmentAmount)}</td>
            <td><strong style="color:${inv.surchargeAmount > 0 ? 'var(--danger)' : 'var(--text-secondary)'};">${formatCurrency(inv.surchargeAmount)}</strong></td>
            <td>${formatCurrency(inv.taxAmount)}</td>
            <td><strong>${formatCurrency(inv.totalAmount)}</strong></td>
            <td>${formatCurrency(inv.paidAmount)}</td>
            <td><strong style="color:var(--danger);">${formatCurrency(inv.balanceAmount)}</strong></td>
            <td><span class="badge ${inv.status === 'PAID' ? 'badge-success' : 'badge-warning'}">${inv.status}</span></td>
            <td>
                ${inv.balanceAmount > 0 ? `
                    <button class="btn btn-primary btn-sm" onclick="openCustPaymentModal(${inv.id}, '${inv.invoiceNumber}', ${inv.balanceAmount})">💳 Record Payment</button>
                ` : '<span style="color:var(--success); font-weight:600; font-size:12px;">Paid</span>'}
            </td>
        </tr>
    `).join('');
}

function openInvoiceModal() {
    document.getElementById('invoiceForm').reset();
    document.getElementById('violationPreviewBox').style.display = 'none';
    openModal('invoiceModal');
}

async function handleCreateInvoice(e) {
    e.preventDefault();
    const factoryId = document.getElementById('invFactoryId').value;
    const serviceOrderId = document.getElementById('invServiceOrderId').value;
    const treatmentAmount = parseFloat(document.getElementById('invTreatmentAmount').value);

    const payload = {
        factory: { id: parseInt(factoryId) },
        treatmentAmount: treatmentAmount
    };
    if (serviceOrderId) {
        payload.serviceOrder = { id: parseInt(serviceOrderId) };
    }

    try {
        const created = await fetchAPI('/customer-invoices', { method: 'POST', body: payload });
        if (created.surchargeAmount > 0) {
            showToast(`Customer Invoice generated! Environmental surcharge of ${formatCurrency(created.surchargeAmount)} automatically included! 🧾`, 'success');
        } else {
            showToast('Customer Invoice generated successfully!', 'success');
        }
        closeModal('invoiceModal');
        loadCustomerInvoices();
    } catch (err) {
        // Handled
    }
}

function openCustPaymentModal(invId, invNum, balance) {
    document.getElementById('cPayInvoiceId').value = invId;
    document.getElementById('cPayInvoiceNum').value = invNum;
    document.getElementById('cPayAmount').value = balance;
    openModal('custPaymentModal');
}

async function handleRecordCustomerPayment(e) {
    e.preventDefault();
    const invId = document.getElementById('cPayInvoiceId').value;
    const amount = parseFloat(document.getElementById('cPayAmount').value);
    const method = document.getElementById('cPayMethod').value;
    const ref = document.getElementById('cPayRef').value;

    const payload = {
        invoice: { id: parseInt(invId) },
        amount: amount,
        paymentMethod: method,
        referenceNumber: ref
    };

    try {
        await fetchAPI('/payments', { method: 'POST', body: payload });
        showToast('Customer Payment recorded successfully! Invoice balance & ledger updated.', 'success');
        closeModal('custPaymentModal');
        loadCustomerInvoices();
    } catch (err) {
        // Handled
    }
}
