// Environmental Settings Logic

document.addEventListener('DOMContentLoaded', () => {
    renderSidebar('settings');
    renderHeader('Environmental System Settings', 'Configure default COD thresholds, fine rates per excess COD unit and currency symbols');
    loadSettings();

    document.getElementById('settingsForm').addEventListener('submit', saveSettings);
});

async function loadSettings() {
    try {
        const settings = await fetchAPI('/wastewater/settings');
        if (settings) {
            document.getElementById('defaultCodLimit').value = settings.defaultCodLimit;
            document.getElementById('fineRate').value = settings.fineRate;
            document.getElementById('currency').value = settings.currency || '$';
        }
    } catch (err) {
        console.error(err);
    }
}

async function saveSettings(e) {
    e.preventDefault();
    const limit = parseFloat(document.getElementById('defaultCodLimit').value);
    const fineRate = parseFloat(document.getElementById('fineRate').value);
    const currency = document.getElementById('currency').value;

    const payload = {
        defaultCodLimit: limit,
        fineRate: fineRate,
        currency: currency
    };

    try {
        await fetchAPI('/wastewater/settings', { method: 'PUT', body: payload });
        showToast('Environmental settings updated successfully!', 'success');
        loadSettings();
    } catch (err) {
        // Handled
    }
}
