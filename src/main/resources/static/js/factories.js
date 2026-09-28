// Factories Module Logic

document.addEventListener('DOMContentLoaded', () => {
    renderSidebar('factories');
    renderHeader('Industrial Plants Management', 'Manage factory master records, contact details and permitted COD threshold limits');
    loadFactories();

    document.getElementById('factorySearchInput').addEventListener('input', (e) => {
        searchFactories(e.target.value);
    });

    document.getElementById('factoryForm').addEventListener('submit', saveFactory);
});

let factoriesData = [];

async function loadFactories() {
    try {
        factoriesData = await fetchAPI('/wastewater/plants');
        renderFactoriesTable(factoriesData);
    } catch (err) {
        console.error(err);
    }
}

async function searchFactories(query) {
    if (!query) {
        renderFactoriesTable(factoriesData);
        return;
    }
    try {
        const results = await fetchAPI(`/wastewater/plants/search?query=${encodeURIComponent(query)}`);
        renderFactoriesTable(results);
    } catch (err) {
        console.error(err);
    }
}

function renderFactoriesTable(list) {
    const tbody = document.getElementById('factories-tbody');
    if (!list || list.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" style="text-align:center;">No industrial factories found.</td></tr>';
        return;
    }

    tbody.innerHTML = list.map(f => `
        <tr>
            <td>#${f.id}</td>
            <td><strong>${f.factoryName}</strong></td>
            <td><code>${f.registrationNumber}</code></td>
            <td>${f.industryType || 'N/A'}</td>
            <td>${f.city || 'N/A'}</td>
            <td><span style="font-weight: 700; color: var(--primary);">${f.permittedCodLimit} mg/L</span></td>
            <td>
                <span class="badge ${f.status === 'FLAGGED' ? 'badge-danger' : 'badge-success'}">
                    ${f.status === 'FLAGGED' ? '⚠️ FLAGGED VIOLATION' : '✅ ACTIVE'}
                </span>
            </td>
            <td>
                <button class="btn btn-secondary btn-sm" onclick="editFactory(${f.id})">✏️ Edit</button>
                <button class="btn btn-danger btn-sm" onclick="deleteFactory(${f.id})">🗑️ Delete</button>
            </td>
        </tr>
    `).join('');
}

function openFactoryModal() {
    document.getElementById('factoryForm').reset();
    document.getElementById('factoryId').value = '';
    document.getElementById('factoryModalTitle').innerText = 'Add Industrial Plant';
    openModal('factoryModal');
}

function editFactory(id) {
    const f = factoriesData.find(item => item.id === id);
    if (!f) return;

    document.getElementById('factoryId').value = f.id;
    document.getElementById('factoryName').value = f.factoryName;
    document.getElementById('registrationNumber').value = f.registrationNumber;
    document.getElementById('contactPerson').value = f.contactPerson || '';
    document.getElementById('phone').value = f.phone || '';
    document.getElementById('email').value = f.email || '';
    document.getElementById('city').value = f.city || '';
    document.getElementById('industryType').value = f.industryType || '';
    document.getElementById('permittedCodLimit').value = f.permittedCodLimit;
    document.getElementById('address').value = f.address || '';

    document.getElementById('factoryModalTitle').innerText = 'Edit Industrial Plant';
    openModal('factoryModal');
}

async function saveFactory(e) {
    e.preventDefault();
    const id = document.getElementById('factoryId').value;
    
    const payload = {
        factoryName: document.getElementById('factoryName').value,
        registrationNumber: document.getElementById('registrationNumber').value,
        contactPerson: document.getElementById('contactPerson').value,
        phone: document.getElementById('phone').value,
        email: document.getElementById('email').value,
        city: document.getElementById('city').value,
        industryType: document.getElementById('industryType').value,
        permittedCodLimit: parseFloat(document.getElementById('permittedCodLimit').value),
        address: document.getElementById('address').value
    };

    try {
        if (id) {
            await fetchAPI(`/wastewater/plants/${id}`, { method: 'PUT', body: payload });
            showToast('Factory updated successfully!', 'success');
        } else {
            await fetchAPI('/wastewater/plants', { method: 'POST', body: payload });
            showToast('New Industrial Plant added!', 'success');
        }
        closeModal('factoryModal');
        loadFactories();
    } catch (err) {
        // Error handled in fetchAPI
    }
}

async function deleteFactory(id) {
    if (!confirm('Are you sure you want to delete this industrial plant record?')) return;
    try {
        await fetchAPI(`/wastewater/plants/${id}`, { method: 'DELETE' });
        showToast('Factory record deleted', 'warning');
        loadFactories();
    } catch (err) {
        // Error handled
    }
}
