// Suppliers Module Logic

document.addEventListener('DOMContentLoaded', () => {
    renderSidebar('suppliers');
    renderHeader('Chemical Suppliers', 'Manage chemical vendor details, tax registrations and procurement contacts');
    loadSuppliers();

    document.getElementById('supplierForm').addEventListener('submit', saveSupplier);
});

let suppliersList = [];

async function loadSuppliers() {
    try {
        suppliersList = await fetchAPI('/suppliers');
        renderSuppliersTable(suppliersList);
    } catch (err) {
        console.error(err);
    }
}

function renderSuppliersTable(list) {
    const tbody = document.getElementById('suppliers-tbody');
    if (!list || list.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" style="text-align:center;">No chemical suppliers registered.</td></tr>';
        return;
    }

    tbody.innerHTML = list.map(s => `
        <tr>
            <td>#${s.id}</td>
            <td><strong>${s.supplierName}</strong></td>
            <td>${s.contactPerson || 'N/A'}</td>
            <td>${s.phone || 'N/A'}</td>
            <td>${s.email || 'N/A'}</td>
            <td><code>${s.taxNumber || 'N/A'}</code></td>
            <td><span class="badge badge-success">${s.status || 'ACTIVE'}</span></td>
            <td>
                <button class="btn btn-secondary btn-sm" onclick="editSupplier(${s.id})">✏️ Edit</button>
                <button class="btn btn-danger btn-sm" onclick="deleteSupplier(${s.id})">🗑️ Delete</button>
            </td>
        </tr>
    `).join('');
}

function openSupplierModal() {
    document.getElementById('supplierForm').reset();
    document.getElementById('supplierId').value = '';
    document.getElementById('supplierModalTitle').innerText = 'Register Chemical Supplier';
    openModal('supplierModal');
}

function editSupplier(id) {
    const s = suppliersList.find(item => item.id === id);
    if (!s) return;

    document.getElementById('supplierId').value = s.id;
    document.getElementById('supplierName').value = s.supplierName;
    document.getElementById('supplierContact').value = s.contactPerson || '';
    document.getElementById('supplierPhone').value = s.phone || '';
    document.getElementById('supplierEmail').value = s.email || '';
    document.getElementById('supplierTaxNumber').value = s.taxNumber || '';
    document.getElementById('supplierAddress').value = s.address || '';

    document.getElementById('supplierModalTitle').innerText = 'Edit Chemical Supplier';
    openModal('supplierModal');
}

async function saveSupplier(e) {
    e.preventDefault();
    const id = document.getElementById('supplierId').value;
    const payload = {
        supplierName: document.getElementById('supplierName').value,
        contactPerson: document.getElementById('supplierContact').value,
        phone: document.getElementById('supplierPhone').value,
        email: document.getElementById('supplierEmail').value,
        taxNumber: document.getElementById('supplierTaxNumber').value,
        address: document.getElementById('supplierAddress').value
    };

    try {
        if (id) {
            await fetchAPI(`/suppliers/${id}`, { method: 'PUT', body: payload });
            showToast('Supplier updated!', 'success');
        } else {
            await fetchAPI('/suppliers', { method: 'POST', body: payload });
            showToast('Supplier registered!', 'success');
        }
        closeModal('supplierModal');
        loadSuppliers();
    } catch (err) {
        // Error handled in fetchAPI
    }
}

async function deleteSupplier(id) {
    if (!confirm('Are you sure you want to delete this supplier?')) return;
    try {
        await fetchAPI(`/suppliers/${id}`, { method: 'DELETE' });
        showToast('Supplier removed', 'warning');
        loadSuppliers();
    } catch (err) {
        // Error handled
    }
}
