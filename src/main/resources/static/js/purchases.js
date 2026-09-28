// Purchases & Vendor Bills Module Logic

document.addEventListener('DOMContentLoaded', () => {
    if (window.location.pathname.endsWith('purchase-orders.html')) {
        renderSidebar('purchase-orders');
        renderHeader('Chemical Purchase Orders', 'Issue chemical agent purchase orders to suppliers and track delivery statuses');
        loadPurchaseOrders();
        loadPOFormDropdowns();
        const poForm = document.getElementById('poForm');
        if (poForm) poForm.addEventListener('submit', handleCreatePO);
    } else if (window.location.pathname.endsWith('vendor-bills.html')) {
        renderSidebar('vendor-bills');
        renderHeader('Chemical Vendor Bills', 'Manage chemical procurement supplier bills, payments and accounts payable');
        loadVendorBills();
        const vPayForm = document.getElementById('vendorPaymentForm');
        if (vPayForm) vPayForm.addEventListener('submit', handleRecordVendorPayment);
    }
});

let poList = [];
let vendorBillsList = [];

// Purchase Orders Logic
async function loadPurchaseOrders() {
    try {
        poList = await fetchAPI('/purchases/orders');
        renderPOTable(poList || []);
    } catch (err) {
        console.error(err);
    }
}

async function loadPOFormDropdowns() {
    try {
        const suppliers = await fetchAPI('/suppliers');
        const poSup = document.getElementById('poSupplierId');
        if (poSup) poSup.innerHTML = suppliers.map(s => `<option value="${s.id}">${s.supplierName}</option>`).join('');

        const products = await fetchAPI('/products?type=GOODS');
        const poProd = document.getElementById('poProductId');
        if (poProd) poProd.innerHTML = products.map(p => `<option value="${p.id}">${p.productName} (${p.productCode}) - $${p.price}</option>`).join('');
    } catch (err) {
        console.error(err);
    }
}

function renderPOTable(list) {
    const tbody = document.getElementById('po-tbody');
    if (!tbody) return;

    if (!list || list.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" style="text-align:center;">No purchase orders issued yet.</td></tr>';
        return;
    }

    tbody.innerHTML = list.map(po => `
        <tr>
            <td><strong>#PO-${po.id}</strong></td>
            <td>${po.supplier ? po.supplier.supplierName : 'Unknown'}</td>
            <td>${po.orderDate || ''}</td>
            <td><strong>${formatCurrency(po.totalAmount)}</strong></td>
            <td><span class="badge ${getPOBadgeClass(po.status)}">${po.status}</span></td>
            <td>
                ${po.status !== 'RECEIVED' && po.status !== 'CANCELLED' ? `
                    <button class="btn btn-secondary btn-sm" onclick="updatePOStatus(${po.id}, 'APPROVED')">Approve</button>
                    <button class="btn btn-primary btn-sm" onclick="updatePOStatus(${po.id}, 'RECEIVED')">Mark Received (Increases Stock) 📦</button>
                ` : '<span style="font-size:12px; color:var(--text-muted);">Completed</span>'}
            </td>
        </tr>
    `).join('');
}

function getPOBadgeClass(status) {
    if (status === 'RECEIVED') return 'badge-success';
    if (status === 'APPROVED') return 'badge-info';
    if (status === 'CANCELLED') return 'badge-danger';
    return 'badge-warning';
}

function openPOModal() {
    document.getElementById('poForm').reset();
    openModal('poModal');
}

async function handleCreatePO(e) {
    e.preventDefault();
    const supplierId = document.getElementById('poSupplierId').value;
    const productId = document.getElementById('poProductId').value;
    const quantity = parseFloat(document.getElementById('poQuantity').value);
    const unitPrice = parseFloat(document.getElementById('poUnitPrice').value);
    const remarks = document.getElementById('poRemarks').value;

    const payload = {
        supplier: { id: parseInt(supplierId) },
        remarks: remarks,
        status: 'DRAFT',
        items: [
            {
                product: { id: parseInt(productId) },
                quantity: quantity,
                unitPrice: unitPrice
            }
        ]
    };

    try {
        await fetchAPI('/purchases/orders', { method: 'POST', body: payload });
        showToast('Chemical Purchase Order generated!', 'success');
        closeModal('poModal');
        loadPurchaseOrders();
    } catch (err) {
        // Handled
    }
}

async function updatePOStatus(id, newStatus) {
    try {
        await fetchAPI(`/purchases/orders/${id}/status?status=${newStatus}`, { method: 'PUT' });
        if (newStatus === 'RECEIVED') {
            showToast('Purchase Order RECEIVED! Chemical Inventory stock automatically updated & Vendor Bill generated! 📦', 'success');
        } else {
            showToast(`PO status updated to ${newStatus}`, 'info');
        }
        loadPurchaseOrders();
    } catch (err) {
        // Handled
    }
}

// Vendor Bills Logic
async function loadVendorBills() {
    try {
        vendorBillsList = await fetchAPI('/vendor-bills');
        renderVendorBillsTable(vendorBillsList || []);
    } catch (err) {
        console.error(err);
    }
}

function renderVendorBillsTable(list) {
    const tbody = document.getElementById('bills-tbody');
    if (!tbody) return;

    if (!list || list.length === 0) {
        tbody.innerHTML = '<tr><td colspan="9" style="text-align:center;">No vendor bills found.</td></tr>';
        return;
    }

    tbody.innerHTML = list.map(b => `
        <tr>
            <td><strong>${b.billNumber}</strong></td>
            <td>${b.supplier ? b.supplier.supplierName : 'Supplier'}</td>
            <td>${b.billDate || ''}</td>
            <td>${b.dueDate || ''}</td>
            <td>${formatCurrency(b.totalAmount)}</td>
            <td>${formatCurrency(b.paidAmount)}</td>
            <td><strong style="color:var(--danger);">${formatCurrency(b.balanceAmount)}</strong></td>
            <td><span class="badge ${b.status === 'PAID' ? 'badge-success' : 'badge-danger'}">${b.status}</span></td>
            <td>
                ${b.balanceAmount > 0 ? `
                    <button class="btn btn-primary btn-sm" onclick="openVendorPaymentModal(${b.id}, '${b.billNumber}', ${b.balanceAmount})">💳 Pay Bill</button>
                ` : '<span style="color:var(--success); font-weight:600; font-size:12px;">Paid in Full</span>'}
            </td>
        </tr>
    `).join('');
}

function openVendorPaymentModal(billId, billNum, balance) {
    document.getElementById('vPayBillId').value = billId;
    document.getElementById('vPayBillNum').value = billNum;
    document.getElementById('vPayAmount').value = balance;
    openModal('vendorPaymentModal');
}

async function handleRecordVendorPayment(e) {
    e.preventDefault();
    const billId = document.getElementById('vPayBillId').value;
    const amount = parseFloat(document.getElementById('vPayAmount').value);
    const method = document.getElementById('vPayMethod').value;
    const ref = document.getElementById('vPayRef').value;

    const payload = {
        vendorBill: { id: parseInt(billId) },
        amount: amount,
        paymentMethod: method,
        referenceNumber: ref
    };

    try {
        await fetchAPI('/vendor-payments', { method: 'POST', body: payload });
        showToast('Vendor payment recorded! Bill balance & plant budget spending updated.', 'success');
        closeModal('vendorPaymentModal');
        loadVendorBills();
    } catch (err) {
        // Handled
    }
}
