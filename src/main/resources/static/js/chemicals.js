// Chemicals Inventory Logic

document.addEventListener('DOMContentLoaded', () => {
    renderSidebar('chemicals');
    renderHeader('Chemical Inventory Control', 'Track chemical stock levels, unit costs, reorder thresholds and low-stock alerts');
    loadChemicalStock();
    loadProductsDropdown();

    document.getElementById('chemicalSearchInput').addEventListener('input', (e) => {
        searchChemicals(e.target.value);
    });

    document.getElementById('stockForm').addEventListener('submit', handleAddStock);
});

let inventoryList = [];

async function loadChemicalStock() {
    try {
        inventoryList = await fetchAPI('/inventory/chemicals');
        renderStockTable(inventoryList);
    } catch (err) {
        console.error(err);
    }
}

async function searchChemicals(query) {
    if (!query) {
        renderStockTable(inventoryList);
        return;
    }
    try {
        const results = await fetchAPI(`/inventory/chemicals/search?query=${encodeURIComponent(query)}`);
        renderStockTable(results);
    } catch (err) {
        console.error(err);
    }
}

async function loadProductsDropdown() {
    try {
        const products = await fetchAPI('/products?type=GOODS');
        const select = document.getElementById('stockProductId');
        select.innerHTML = products.map(p => `<option value="${p.id}">${p.productName} (${p.productCode}) - Unit: ${p.unit}</option>`).join('');
    } catch (err) {
        console.error(err);
    }
}

function renderStockTable(list) {
    const tbody = document.getElementById('chemicals-tbody');
    if (!list || list.length === 0) {
        tbody.innerHTML = '<tr><td colspan="9" style="text-align:center;">No chemical inventory items found.</td></tr>';
        return;
    }

    tbody.innerHTML = list.map(item => {
        const isLow = item.quantity <= item.reorderLevel;
        const totalVal = (item.quantity || 0) * (item.unitCost || 0);

        return `
            <tr>
                <td>#${item.id}</td>
                <td><strong>${item.product ? item.product.productName : 'Chemical Agent'}</strong></td>
                <td><code>${item.product ? item.product.productCode : ''}</code></td>
                <td>
                    <span style="font-weight: 700; font-size: 15px; color: ${isLow ? 'var(--danger)' : 'var(--text-primary)'};">
                        ${item.quantity} ${item.product ? item.product.unit : ''}
                    </span>
                </td>
                <td>${item.reorderLevel} ${item.product ? item.product.unit : ''}</td>
                <td>${formatCurrency(item.unitCost)}</td>
                <td><strong>${formatCurrency(totalVal)}</strong></td>
                <td>
                    <span class="badge ${isLow ? 'badge-danger' : 'badge-success'}">
                        ${isLow ? '⚠️ LOW STOCK' : '✅ OPTIMAL'}
                    </span>
                </td>
                <td>
                    <button class="btn btn-secondary btn-sm" onclick="openAddStockModal(${item.product ? item.product.id : ''})">+ Restock</button>
                </td>
            </tr>
        `;
    }).join('');
}

function openAddStockModal(prodId = null) {
    document.getElementById('stockForm').reset();
    if (prodId) {
        document.getElementById('stockProductId').value = prodId;
    }
    openModal('stockModal');
}

async function handleAddStock(e) {
    e.preventDefault();
    const productId = document.getElementById('stockProductId').value;
    const quantity = parseFloat(document.getElementById('stockQuantity').value);
    const unitCost = document.getElementById('stockUnitCost').value ? parseFloat(document.getElementById('stockUnitCost').value) : null;

    try {
        let url = `/inventory/chemicals/add?productId=${productId}&quantity=${quantity}`;
        if (unitCost) url += `&unitCost=${unitCost}`;

        await fetchAPI(url, { method: 'POST' });
        showToast('Chemical stock updated successfully!', 'success');
        closeModal('stockModal');
        loadChemicalStock();
    } catch (err) {
        // Error handled in fetchAPI
    }
}
