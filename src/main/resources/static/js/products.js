// Products Module Logic

document.addEventListener('DOMContentLoaded', () => {
    renderSidebar('products');
    renderHeader('Products & Services Master', 'Define chemical goods, unit prices and effluent processing service rates');
    loadProducts();

    document.getElementById('productForm').addEventListener('submit', saveProduct);
});

let productsList = [];

async function loadProducts() {
    try {
        productsList = await fetchAPI('/products');
        renderProductsTable(productsList);
    } catch (err) {
        console.error(err);
    }
}

function renderProductsTable(list) {
    const tbody = document.getElementById('products-tbody');
    if (!list || list.length === 0) {
        tbody.innerHTML = '<tr><td colspan="9" style="text-align:center;">No products or services registered.</td></tr>';
        return;
    }

    tbody.innerHTML = list.map(p => `
        <tr>
            <td>#${p.id}</td>
            <td><strong>${p.productName}</strong></td>
            <td><code>${p.productCode}</code></td>
            <td>
                <span class="badge ${p.type === 'GOODS' ? 'badge-info' : 'badge-neutral'}">
                    ${p.type === 'GOODS' ? '🧪 GOODS' : '⚙️ SERVICE'}
                </span>
            </td>
            <td>${p.unit || 'Unit'}</td>
            <td><strong>${formatCurrency(p.price)}</strong></td>
            <td>${p.taxPercentage}%</td>
            <td><span class="badge badge-success">ACTIVE</span></td>
            <td>
                <button class="btn btn-secondary btn-sm" onclick="editProduct(${p.id})">✏️ Edit</button>
                <button class="btn btn-danger btn-sm" onclick="deleteProduct(${p.id})">🗑️ Delete</button>
            </td>
        </tr>
    `).join('');
}

function openProductModal() {
    document.getElementById('productForm').reset();
    document.getElementById('productId').value = '';
    document.getElementById('productModalTitle').innerText = 'Add Product / Service';
    openModal('productModal');
}

function editProduct(id) {
    const p = productsList.find(item => item.id === id);
    if (!p) return;

    document.getElementById('productId').value = p.id;
    document.getElementById('productName').value = p.productName;
    document.getElementById('productCode').value = p.productCode;
    document.getElementById('productType').value = p.type;
    document.getElementById('productUnit').value = p.unit || '';
    document.getElementById('productPrice').value = p.price;
    document.getElementById('productTax').value = p.taxPercentage;
    document.getElementById('productDescription').value = p.description || '';

    document.getElementById('productModalTitle').innerText = 'Edit Product / Service';
    openModal('productModal');
}

async function saveProduct(e) {
    e.preventDefault();
    const id = document.getElementById('productId').value;
    const payload = {
        productName: document.getElementById('productName').value,
        productCode: document.getElementById('productCode').value,
        type: document.getElementById('productType').value,
        unit: document.getElementById('productUnit').value,
        price: parseFloat(document.getElementById('productPrice').value),
        taxPercentage: parseFloat(document.getElementById('productTax').value),
        description: document.getElementById('productDescription').value
    };

    try {
        if (id) {
            await fetchAPI(`/products/${id}`, { method: 'PUT', body: payload });
            showToast('Product updated successfully!', 'success');
        } else {
            await fetchAPI('/products', { method: 'POST', body: payload });
            showToast('New Product added to master catalog!', 'success');
        }
        closeModal('productModal');
        loadProducts();
    } catch (err) {
        // Error handled
    }
}

async function deleteProduct(id) {
    if (!confirm('Are you sure you want to delete this product?')) return;
    try {
        await fetchAPI(`/products/${id}`, { method: 'DELETE' });
        showToast('Product deleted', 'warning');
        loadProducts();
    } catch (err) {
        // Error handled
    }
}
