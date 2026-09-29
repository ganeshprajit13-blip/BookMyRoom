/* ==========================================================================
   Automated Municipal Library E-Book Licensing - Clean Academic JavaScript
   ========================================================================== */

let currentView = 'dashboard';
let ebooksCache = [];
let licensesCache = [];
let patronsCache = [];
let publishersCache = [];

document.addEventListener('DOMContentLoaded', () => {
    showView('dashboard');
});

// Toast notification helper
function showToast(message, type = 'info') {
    const container = document.getElementById('toast-container');
    const toast = document.createElement('div');
    toast.className = `toast-msg toast-${type}`;
    
    let icon = 'info-circle';
    if (type === 'success') icon = 'check-circle';
    if (type === 'error') icon = 'exclamation-triangle';

    toast.innerHTML = `<i class="bi bi-${icon}"></i> <span>${message}</span>`;
    container.appendChild(toast);

    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transition = 'opacity 0.3s ease';
        setTimeout(() => toast.remove(), 300);
    }, 3500);
}

// Navigation between 9 Views
function showView(viewId) {
    currentView = viewId;

    // Update active nav link
    document.querySelectorAll('.sidebar-menu .nav-link').forEach(el => el.classList.remove('active'));
    const activeLink = document.querySelector(`.sidebar-menu .nav-link[onclick="showView('${viewId}')"]`);
    if (activeLink) activeLink.classList.add('active');

    // Hide all views
    document.querySelectorAll('.content-view').forEach(view => {
        view.style.display = 'none';
    });

    // Show target view
    const target = document.getElementById(`view-${viewId}`);
    if (target) target.style.display = 'block';

    // Update title
    const titles = {
        'dashboard': 'Dashboard',
        'ebooks': 'E-Books Catalog',
        'licenses': 'E-Book Licenses',
        'patrons': 'Library Patrons',
        'publishers': 'E-Book Publishers',
        'checkouts': 'Checkouts & Loans',
        'holds': 'Digital Holds Queue',
        'budget': 'Digital Content Budget',
        'reports': 'Financial & Operational Reports'
    };
    document.getElementById('page-title').innerText = titles[viewId] || 'Library Management';

    // Load data for specific view
    loadViewData(viewId);
}

function loadViewData(viewId) {
    switch (viewId) {
        case 'dashboard':
            loadDashboardData();
            break;
        case 'ebooks':
            loadEbooks();
            break;
        case 'licenses':
            loadLicenses();
            break;
        case 'patrons':
            loadPatrons();
            break;
        case 'publishers':
            loadPublishers();
            break;
        case 'checkouts':
            loadCheckouts();
            break;
        case 'holds':
            loadHolds();
            break;
        case 'budget':
            loadBudgetData();
            break;
        case 'reports':
            loadReportsData();
            break;
    }
}

// --------------------------------------------------------------------------
// 1. DASHBOARD
// --------------------------------------------------------------------------
async function loadDashboardData() {
    try {
        const res = await fetch('/api/reports/dashboard');
        if (!res.ok) throw new Error('Failed to load dashboard');
        const data = await res.json();

        document.getElementById('dash-ebooks').innerText = data.totalEbooks || 0;
        document.getElementById('dash-licenses').innerText = data.activeLicenses || 0;
        document.getElementById('dash-patrons').innerText = data.totalPatrons || 0;
        document.getElementById('dash-checkouts').innerText = data.activeCheckouts || 0;
        document.getElementById('dash-holds').innerText = data.activeHolds || 0;

        // Load recent checkouts
        const checkoutsRes = await fetch('/api/ebooks/checkout');
        const checkouts = await checkoutsRes.json();
        const tbody = document.getElementById('dashboard-recent-table');

        if (!checkouts || checkouts.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" class="text-center text-muted">No checkouts recorded yet.</td></tr>';
            return;
        }

        tbody.innerHTML = checkouts.slice(0, 5).map(c => `
            <tr>
                <td class="fw-bold">${c.ebook ? escapeHtml(c.ebook.title) : 'N/A'}</td>
                <td>${c.patron ? escapeHtml(c.patron.name) : 'N/A'}</td>
                <td><code>${c.accessKey || 'N/A'}</code></td>
                <td>${c.checkoutDate ? c.checkoutDate.substring(0, 10) : 'N/A'}</td>
                <td><span class="badge-status badge-${(c.status || '').toLowerCase()}">${c.status || 'ACTIVE'}</span></td>
            </tr>
        `).join('');
    } catch (err) {
        console.error(err);
    }
}

// --------------------------------------------------------------------------
// 2. E-BOOKS
// --------------------------------------------------------------------------
async function loadEbooks() {
    try {
        const res = await fetch('/api/ebooks');
        const data = await res.json();
        ebooksCache = data;
        renderEbooksTable(data);
    } catch (err) {
        showToast('Error loading e-books: ' + err.message, 'error');
    }
}

function renderEbooksTable(ebooks) {
    const tbody = document.getElementById('ebooks-table-body');
    if (!ebooks || ebooks.length === 0) {
        tbody.innerHTML = '<tr><td colspan="9" class="text-center text-muted">No e-books in catalog.</td></tr>';
        return;
    }
    tbody.innerHTML = ebooks.map(b => `
        <tr>
            <td>${b.id}</td>
            <td><code>${escapeHtml(b.isbn || '')}</code></td>
            <td class="fw-bold">${escapeHtml(b.title || '')}</td>
            <td>${escapeHtml(b.author || '')}</td>
            <td>${escapeHtml(b.publisher || '')}</td>
            <td><span class="badge bg-light text-dark border">${escapeHtml(b.category || '')}</span></td>
            <td>${b.publicationYear || ''}</td>
            <td><span class="badge-status badge-${(b.status || '').toLowerCase()}">${b.status || 'ACTIVE'}</span></td>
            <td>
                <button class="btn btn-outline-primary btn-sm" onclick="editEbook(${b.id})"><i class="bi bi-pencil"></i></button>
                <button class="btn btn-outline-danger btn-sm" onclick="deleteEbook(${b.id})"><i class="bi bi-trash"></i></button>
            </td>
        </tr>
    `).join('');
}

function filterEbooks() {
    const q = (document.getElementById('ebook-search').value || '').toLowerCase();
    const filtered = ebooksCache.filter(b => 
        (b.title && b.title.toLowerCase().includes(q)) ||
        (b.author && b.author.toLowerCase().includes(q)) ||
        (b.isbn && b.isbn.toLowerCase().includes(q))
    );
    renderEbooksTable(filtered);
}

function openAddEbookModal() {
    document.getElementById('ebookModalTitle').innerText = 'Add New E-Book';
    document.getElementById('ebook-id').value = '';
    document.getElementById('ebook-form').reset();
    document.getElementById('ebook-year').value = new Date().getFullYear();
    new bootstrap.Modal(document.getElementById('addEbookModal')).show();
}

function editEbook(id) {
    const b = ebooksCache.find(x => x.id === id);
    if (!b) return;
    document.getElementById('ebookModalTitle').innerText = 'Edit E-Book';
    document.getElementById('ebook-id').value = b.id;
    document.getElementById('ebook-isbn').value = b.isbn || '';
    document.getElementById('ebook-title').value = b.title || '';
    document.getElementById('ebook-author').value = b.author || '';
    document.getElementById('ebook-publisher').value = b.publisher || '';
    document.getElementById('ebook-category').value = b.category || '';
    document.getElementById('ebook-year').value = b.publicationYear || 2026;
    document.getElementById('ebook-language').value = b.language || 'English';
    document.getElementById('ebook-status').value = b.status || 'ACTIVE';
    document.getElementById('ebook-desc').value = b.description || '';
    new bootstrap.Modal(document.getElementById('addEbookModal')).show();
}

async function saveEbook(e) {
    e.preventDefault();
    const id = document.getElementById('ebook-id').value;
    const payload = {
        isbn: document.getElementById('ebook-isbn').value,
        title: document.getElementById('ebook-title').value,
        author: document.getElementById('ebook-author').value,
        publisher: document.getElementById('ebook-publisher').value,
        category: document.getElementById('ebook-category').value,
        publicationYear: parseInt(document.getElementById('ebook-year').value) || 2026,
        language: document.getElementById('ebook-language').value,
        status: document.getElementById('ebook-status').value,
        description: document.getElementById('ebook-desc').value
    };

    try {
        const url = id ? `/api/ebooks/${id}` : '/api/ebooks';
        const method = id ? 'PUT' : 'POST';
        const res = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        if (!res.ok) {
            const err = await res.json();
            throw new Error(err.message || 'Failed to save e-book');
        }
        showToast(id ? 'E-Book updated successfully' : 'E-Book added successfully', 'success');
        bootstrap.Modal.getInstance(document.getElementById('addEbookModal')).hide();
        loadEbooks();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

async function deleteEbook(id) {
    if (!confirm('Are you sure you want to deactivate/delete this e-book?')) return;
    try {
        const res = await fetch(`/api/ebooks/${id}`, { method: 'DELETE' });
        if (!res.ok) throw new Error('Failed to delete e-book');
        showToast('E-Book deleted', 'success');
        loadEbooks();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

// --------------------------------------------------------------------------
// 3. LICENSES
// --------------------------------------------------------------------------
async function loadLicenses() {
    try {
        const res = await fetch('/api/ebooks/licenses');
        const data = await res.json();
        licensesCache = data;
        const tbody = document.getElementById('licenses-table-body');
        if (!data || data.length === 0) {
            tbody.innerHTML = '<tr><td colspan="11" class="text-center text-muted">No licenses found.</td></tr>';
            return;
        }
        tbody.innerHTML = data.map(l => {
            const active = l.activeCheckoutCount || 0;
            const limit = l.concurrentUserLimit || 1;
            const pct = Math.round((active / limit) * 100);
            return `
                <tr>
                    <td>${l.id}</td>
                    <td><strong>${escapeHtml(l.licenseNumber || '')}</strong></td>
                    <td class="fw-bold">${l.ebook ? escapeHtml(l.ebook.title) : 'N/A'}</td>
                    <td>${l.publisher ? escapeHtml(l.publisher.name) : 'N/A'}</td>
                    <td><span class="badge bg-light text-dark border">${l.licenseType || 'PERPETUAL'}</span></td>
                    <td>${limit}</td>
                    <td>
                        <span class="fw-bold">${active} / ${limit}</span>
                        <span class="badge ${pct >= 100 ? 'bg-danger' : (pct >= 70 ? 'bg-warning text-dark' : 'bg-success')} ms-1">${pct}%</span>
                    </td>
                    <td>${l.licenseStartDate || 'N/A'}</td>
                    <td>${l.licenseExpiryDate || 'PERPETUAL'}</td>
                    <td>₹${(l.purchaseCost || 0).toLocaleString()}</td>
                    <td><span class="badge-status badge-${(l.status || '').toLowerCase()}">${l.status || 'ACTIVE'}</span></td>
                </tr>
            `;
        }).join('');
    } catch (err) {
        showToast('Error loading licenses: ' + err.message, 'error');
    }
}

async function openAddLicenseModal() {
    // Populate dropdowns
    const [ebooksRes, pubsRes] = await Promise.all([
        fetch('/api/ebooks'),
        fetch('/api/publishers')
    ]);
    const ebooks = await ebooksRes.json();
    const pubs = await pubsRes.json();

    const ebookSel = document.getElementById('lic-ebook-select');
    ebookSel.innerHTML = ebooks.map(b => `<option value="${b.id}">${escapeHtml(b.title)} (${b.isbn})</option>`).join('');

    const pubSel = document.getElementById('lic-pub-select');
    pubSel.innerHTML = pubs.map(p => `<option value="${p.id}">${escapeHtml(p.name)}</option>`).join('');

    document.getElementById('lic-number').value = 'LIC-000' + (licensesCache.length + 1);
    document.getElementById('lic-start-date').value = new Date().toISOString().substring(0, 10);
    document.getElementById('lic-type').value = 'PERPETUAL';
    toggleLicenseExpiry();

    new bootstrap.Modal(document.getElementById('addLicenseModal')).show();
}

function toggleLicenseExpiry() {
    const type = document.getElementById('lic-type').value;
    const col = document.getElementById('lic-expiry-col');
    if (type === 'TERM') {
        col.style.display = 'block';
        const nextYear = new Date();
        nextYear.setFullYear(nextYear.getFullYear() + 1);
        document.getElementById('lic-expiry-date').value = nextYear.toISOString().substring(0, 10);
    } else {
        col.style.display = 'none';
        document.getElementById('lic-expiry-date').value = '';
    }
}

async function saveLicense(e) {
    e.preventDefault();
    const payload = {
        licenseNumber: document.getElementById('lic-number').value,
        ebook: { id: Long(document.getElementById('lic-ebook-select').value) },
        publisher: { id: Long(document.getElementById('lic-pub-select').value) },
        licenseType: document.getElementById('lic-type').value,
        concurrentUserLimit: parseInt(document.getElementById('lic-limit').value) || 10,
        purchaseCost: parseFloat(document.getElementById('lic-cost').value) || 0,
        amortizationPeriod: parseInt(document.getElementById('lic-amort').value) || 36,
        licenseStartDate: document.getElementById('lic-start-date').value,
        licenseExpiryDate: document.getElementById('lic-expiry-date').value || null,
        status: 'ACTIVE'
    };

    try {
        const res = await fetch('/api/ebooks/licenses', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        if (!res.ok) {
            const err = await res.json();
            throw new Error(err.message || 'Failed to create license');
        }
        showToast('License created successfully', 'success');
        bootstrap.Modal.getInstance(document.getElementById('addLicenseModal')).hide();
        loadLicenses();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

// --------------------------------------------------------------------------
// 4. PATRONS
// --------------------------------------------------------------------------
async function loadPatrons() {
    try {
        const res = await fetch('/api/patrons');
        const data = await res.json();
        patronsCache = data;
        const tbody = document.getElementById('patrons-table-body');
        if (!data || data.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="text-center text-muted">No patrons registered.</td></tr>';
            return;
        }
        tbody.innerHTML = data.map(p => `
            <tr>
                <td>${p.id}</td>
                <td class="fw-bold">${escapeHtml(p.name || '')}</td>
                <td>${escapeHtml(p.email || '')}</td>
                <td>${escapeHtml(p.phone || '')}</td>
                <td><span class="badge ${p.membershipType === 'NON_RESIDENT' ? 'bg-warning text-dark' : 'bg-primary'}">${p.membershipType || 'RESIDENT'}</span></td>
                <td><span class="badge-status badge-${(p.status || '').toLowerCase()}">${p.status || 'ACTIVE'}</span></td>
                <td>
                    <button class="btn btn-outline-primary btn-sm" onclick="editPatron(${p.id})"><i class="bi bi-pencil"></i></button>
                    <button class="btn btn-outline-danger btn-sm" onclick="deletePatron(${p.id})"><i class="bi bi-trash"></i></button>
                </td>
            </tr>
        `).join('');
    } catch (err) {
        showToast('Error loading patrons: ' + err.message, 'error');
    }
}

function openAddPatronModal() {
    document.getElementById('patronModalTitle').innerText = 'Add Patron';
    document.getElementById('patron-id').value = '';
    document.getElementById('patron-form').reset();
    new bootstrap.Modal(document.getElementById('addPatronModal')).show();
}

function editPatron(id) {
    const p = patronsCache.find(x => x.id === id);
    if (!p) return;
    document.getElementById('patronModalTitle').innerText = 'Edit Patron';
    document.getElementById('patron-id').value = p.id;
    document.getElementById('patron-name').value = p.name || '';
    document.getElementById('patron-email').value = p.email || '';
    document.getElementById('patron-phone').value = p.phone || '';
    document.getElementById('patron-type').value = p.membershipType || 'RESIDENT';
    document.getElementById('patron-status').value = p.status || 'ACTIVE';
    new bootstrap.Modal(document.getElementById('addPatronModal')).show();
}

async function savePatron(e) {
    e.preventDefault();
    const id = document.getElementById('patron-id').value;
    const payload = {
        name: document.getElementById('patron-name').value,
        email: document.getElementById('patron-email').value,
        phone: document.getElementById('patron-phone').value,
        membershipType: document.getElementById('patron-type').value,
        status: document.getElementById('patron-status').value
    };

    try {
        const url = id ? `/api/patrons/${id}` : '/api/patrons';
        const method = id ? 'PUT' : 'POST';
        const res = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        if (!res.ok) {
            const err = await res.json();
            throw new Error(err.message || 'Failed to save patron');
        }
        showToast(id ? 'Patron updated' : 'Patron added', 'success');
        bootstrap.Modal.getInstance(document.getElementById('addPatronModal')).hide();
        loadPatrons();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

async function deletePatron(id) {
    if (!confirm('Deactivate patron?')) return;
    try {
        const res = await fetch(`/api/patrons/${id}`, { method: 'DELETE' });
        if (!res.ok) throw new Error('Failed to delete patron');
        showToast('Patron deleted', 'success');
        loadPatrons();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

// --------------------------------------------------------------------------
// 5. PUBLISHERS
// --------------------------------------------------------------------------
async function loadPublishers() {
    try {
        const res = await fetch('/api/publishers');
        const data = await res.json();
        publishersCache = data;
        const tbody = document.getElementById('publishers-table-body');
        if (!data || data.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="text-center text-muted">No publishers found.</td></tr>';
            return;
        }
        tbody.innerHTML = data.map(p => `
            <tr>
                <td>${p.id}</td>
                <td class="fw-bold">${escapeHtml(p.name || '')}</td>
                <td>${escapeHtml(p.email || '')}</td>
                <td>${escapeHtml(p.phone || '')}</td>
                <td>${escapeHtml(p.address || '')}</td>
                <td><span class="badge-status badge-${(p.status || '').toLowerCase()}">${p.status || 'ACTIVE'}</span></td>
                <td>
                    <button class="btn btn-outline-primary btn-sm" onclick="editPublisher(${p.id})"><i class="bi bi-pencil"></i></button>
                    <button class="btn btn-outline-danger btn-sm" onclick="deletePublisher(${p.id})"><i class="bi bi-trash"></i></button>
                </td>
            </tr>
        `).join('');
    } catch (err) {
        showToast('Error loading publishers: ' + err.message, 'error');
    }
}

function openAddPublisherModal() {
    document.getElementById('publisherModalTitle').innerText = 'Add Publisher';
    document.getElementById('pub-id').value = '';
    document.getElementById('publisher-form').reset();
    new bootstrap.Modal(document.getElementById('addPublisherModal')).show();
}

function editPublisher(id) {
    const p = publishersCache.find(x => x.id === id);
    if (!p) return;
    document.getElementById('publisherModalTitle').innerText = 'Edit Publisher';
    document.getElementById('pub-id').value = p.id;
    document.getElementById('pub-name').value = p.name || '';
    document.getElementById('pub-email').value = p.email || '';
    document.getElementById('pub-phone').value = p.phone || '';
    document.getElementById('pub-address').value = p.address || '';
    new bootstrap.Modal(document.getElementById('addPublisherModal')).show();
}

async function savePublisher(e) {
    e.preventDefault();
    const id = document.getElementById('pub-id').value;
    const payload = {
        name: document.getElementById('pub-name').value,
        email: document.getElementById('pub-email').value,
        phone: document.getElementById('pub-phone').value,
        address: document.getElementById('pub-address').value
    };

    try {
        const url = id ? `/api/publishers/${id}` : '/api/publishers';
        const method = id ? 'PUT' : 'POST';
        const res = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        if (!res.ok) {
            const err = await res.json();
            throw new Error(err.message || 'Failed to save publisher');
        }
        showToast(id ? 'Publisher updated' : 'Publisher added', 'success');
        bootstrap.Modal.getInstance(document.getElementById('addPublisherModal')).hide();
        loadPublishers();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

async function deletePublisher(id) {
    if (!confirm('Deactivate publisher?')) return;
    try {
        const res = await fetch(`/api/publishers/${id}`, { method: 'DELETE' });
        if (!res.ok) throw new Error('Failed to delete publisher');
        showToast('Publisher deleted', 'success');
        loadPublishers();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

// --------------------------------------------------------------------------
// 6. CHECKOUTS & DIGITAL ACCESS KEYS
// --------------------------------------------------------------------------
async function loadCheckouts() {
    try {
        const [patronsRes, licRes, checkoutsRes] = await Promise.all([
            fetch('/api/patrons'),
            fetch('/api/ebooks/licenses'),
            fetch('/api/ebooks/checkout')
        ]);
        const patrons = await patronsRes.json();
        const licenses = await licRes.json();
        const checkouts = await checkoutsRes.json();

        // Populate Borrow form dropdowns
        const patronSel = document.getElementById('borrow-patron-select');
        patronSel.innerHTML = '<option value="">Choose Patron...</option>' + 
            patrons.map(p => `<option value="${p.id}">${escapeHtml(p.name)} (${p.membershipType})</option>`).join('');

        const licSel = document.getElementById('borrow-license-select');
        licSel.innerHTML = '<option value="">Choose License...</option>' + 
            licenses.map(l => {
                const book = l.ebook ? l.ebook.title : 'N/A';
                const slots = (l.concurrentUserLimit || 0) - (l.activeCheckoutCount || 0);
                return `<option value="${l.id}">${escapeHtml(book)} [${l.licenseNumber}] - Available: ${slots}/${l.concurrentUserLimit}</option>`;
            }).join('');

        // Populate Table
        const tbody = document.getElementById('checkouts-table-body');
        if (!checkouts || checkouts.length === 0) {
            tbody.innerHTML = '<tr><td colspan="9" class="text-center text-muted">No checkouts found.</td></tr>';
            return;
        }

        tbody.innerHTML = checkouts.map(c => `
            <tr>
                <td>${c.id}</td>
                <td class="fw-bold">${c.patron ? escapeHtml(c.patron.name) : 'N/A'}</td>
                <td>${c.ebook ? escapeHtml(c.ebook.title) : 'N/A'}</td>
                <td><code>${c.license ? escapeHtml(c.license.licenseNumber) : 'N/A'}</code></td>
                <td><code>${c.accessKey || 'N/A'}</code></td>
                <td>${c.checkoutDate ? c.checkoutDate.substring(0, 10) : 'N/A'}</td>
                <td>${c.expiryDate ? c.expiryDate.substring(0, 10) : 'N/A'}</td>
                <td><span class="badge-status badge-${(c.status || '').toLowerCase()}">${c.status || 'ACTIVE'}</span></td>
                <td>
                    ${c.status === 'ACTIVE' ? `
                        <button class="btn btn-outline-warning btn-sm" onclick="returnCheckout(${c.id})"><i class="bi bi-arrow-return-left"></i> Return</button>
                    ` : '<span class="text-muted small">Returned</span>'}
                </td>
            </tr>
        `).join('');
    } catch (err) {
        showToast('Error loading checkouts: ' + err.message, 'error');
    }
}

async function handleBorrowEbook(e) {
    e.preventDefault();
    const patronId = document.getElementById('borrow-patron-select').value;
    const licenseId = document.getElementById('borrow-license-select').value;

    if (!patronId || !licenseId) {
        showToast('Please select both a patron and an e-book license.', 'error');
        return;
    }

    try {
        const res = await fetch('/api/ebooks/checkout', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ patronId: Long(patronId), licenseId: Long(licenseId) })
        });

        if (res.status === 409) {
            showToast('Concurrent checkout limit reached for this e-book license.', 'error');
            return;
        }

        if (!res.ok) {
            const err = await res.json();
            throw new Error(err.message || 'Checkout failed');
        }

        const data = await res.json();
        showToast(`Checkout successful! Generated Access Key: ${data.accessKey}`, 'success');
        document.getElementById('borrow-form').reset();
        loadCheckouts();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

async function returnCheckout(id) {
    if (!confirm('Return this borrowed e-book?')) return;
    try {
        const res = await fetch(`/api/ebooks/checkout/${id}/return`, { method: 'POST' });
        if (!res.ok) {
            const err = await res.json();
            throw new Error(err.message || 'Failed to return checkout');
        }
        showToast('E-book returned successfully. Access key revoked and capacity freed.', 'success');
        loadCheckouts();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

// --------------------------------------------------------------------------
// 7. HOLDS
// --------------------------------------------------------------------------
async function loadHolds() {
    try {
        const res = await fetch('/api/ebooks/holds');
        const data = await res.json();
        const tbody = document.getElementById('holds-table-body');
        if (!data || data.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="text-center text-muted">No holds in queue.</td></tr>';
            return;
        }
        tbody.innerHTML = data.map(h => `
            <tr>
                <td>${h.id}</td>
                <td class="fw-bold">${h.patron ? escapeHtml(h.patron.name) : 'N/A'}</td>
                <td>${h.ebook ? escapeHtml(h.ebook.title) : 'N/A'}</td>
                <td><span class="badge bg-secondary">Position #${h.queuePosition || 1}</span></td>
                <td>${h.holdDate ? h.holdDate.substring(0, 10) : 'N/A'}</td>
                <td><span class="badge-status badge-${(h.status || '').toLowerCase()}">${h.status || 'WAITING'}</span></td>
                <td>
                    ${h.status === 'WAITING' ? `
                        <button class="btn btn-outline-danger btn-sm" onclick="cancelHold(${h.id})">Cancel</button>
                    ` : '<span class="text-muted small">Closed</span>'}
                </td>
            </tr>
        `).join('');
    } catch (err) {
        showToast('Error loading holds: ' + err.message, 'error');
    }
}

async function openPlaceHoldModal() {
    const [patronsRes, ebooksRes] = await Promise.all([
        fetch('/api/patrons'),
        fetch('/api/ebooks')
    ]);
    const patrons = await patronsRes.json();
    const ebooks = await ebooksRes.json();

    document.getElementById('hold-patron-select').innerHTML = patrons.map(p => 
        `<option value="${p.id}">${escapeHtml(p.name)}</option>`
    ).join('');

    document.getElementById('hold-ebook-select').innerHTML = ebooks.map(b => 
        `<option value="${b.id}">${escapeHtml(b.title)} (${b.isbn})</option>`
    ).join('');

    new bootstrap.Modal(document.getElementById('placeHoldModal')).show();
}

async function saveHold(e) {
    e.preventDefault();
    const patronId = document.getElementById('hold-patron-select').value;
    const ebookId = document.getElementById('hold-ebook-select').value;

    try {
        const res = await fetch('/api/ebooks/holds', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ patronId: Long(patronId), ebookId: Long(ebookId) })
        });
        if (!res.ok) {
            const err = await res.json();
            throw new Error(err.message || 'Failed to place hold');
        }
        showToast('Hold placed in queue successfully', 'success');
        bootstrap.Modal.getInstance(document.getElementById('placeHoldModal')).hide();
        loadHolds();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

async function cancelHold(id) {
    if (!confirm('Cancel this hold?')) return;
    try {
        const res = await fetch(`/api/ebooks/holds/${id}/cancel`, { method: 'DELETE' });
        if (!res.ok) throw new Error('Failed to cancel hold');
        showToast('Hold cancelled', 'success');
        loadHolds();
    } catch (err) {
        showToast(err.message, 'error');
    }
}



// --------------------------------------------------------------------------
// 11. BUDGET
// --------------------------------------------------------------------------
async function loadBudgetData() {
    try {
        const res = await fetch('/api/reports/budget');
        const data = await res.json();
        const tbody = document.getElementById('budget-table-body');
        if (!data || data.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" class="text-center text-muted">No budget allocations found.</td></tr>';
            return;
        }
        tbody.innerHTML = data.map(b => {
            const pct = b.utilizationPercentage || 0;
            return `
                <tr>
                    <td class="fw-bold">${escapeHtml(b.department || 'Digital Content Department')}</td>
                    <td>₹${(b.plannedBudget || 0).toLocaleString()}</td>
                    <td class="text-primary fw-bold">₹${(b.actualLicensingExpenditure || b.actualExpenditure || 0).toLocaleString()}</td>
                    <td class="text-success fw-bold">₹${(b.remainingBudget || 0).toLocaleString()}</td>
                    <td>
                        <span class="badge ${pct > 80 ? 'bg-danger' : 'bg-primary'}">${pct}%</span>
                    </td>
                </tr>
            `;
        }).join('');
    } catch (err) {
        showToast('Error loading budget: ' + err.message, 'error');
    }
}

// --------------------------------------------------------------------------
// 12. REPORTS (Balance Sheet, Profit & Loss, Budget Report)
// --------------------------------------------------------------------------
function showReportTab(tab) {
    document.getElementById('tab-bs').classList.toggle('active', tab === 'bs');
    document.getElementById('tab-pl').classList.toggle('active', tab === 'pl');
    document.getElementById('tab-bg').classList.toggle('active', tab === 'bg');

    document.getElementById('report-bs').style.display = (tab === 'bs') ? 'block' : 'none';
    document.getElementById('report-pl').style.display = (tab === 'pl') ? 'block' : 'none';
    document.getElementById('report-bg').style.display = (tab === 'bg') ? 'block' : 'none';
}

async function loadReportsData() {
    // 1. Balance Sheet
    try {
        const res = await fetch('/api/reports/balance-sheet');
        const bs = await res.json();

        const assetsBody = document.getElementById('bs-assets-body');
        if (bs.assets) {
            assetsBody.innerHTML = Object.entries(bs.assets).map(([k, v]) => 
                `<tr><td>${escapeHtml(k)}</td><td class="text-end">₹${parseFloat(v).toLocaleString()}</td></tr>`
            ).join('');
        }
        document.getElementById('bs-total-assets').innerText = `₹${(bs.totalAssets || 0).toLocaleString()}`;

        const liabBody = document.getElementById('bs-liab-body');
        if (bs.liabilities) {
            liabBody.innerHTML = Object.entries(bs.liabilities).map(([k, v]) => 
                `<tr><td>${escapeHtml(k)}</td><td class="text-end">₹${parseFloat(v).toLocaleString()}</td></tr>`
            ).join('');
        }
        document.getElementById('bs-total-liab').innerText = `₹${(bs.totalLiabilities || 0).toLocaleString()}`;
    } catch (e) {
        console.error(e);
    }

    // 2. Profit & Loss
    try {
        const res = await fetch('/api/reports/profit-loss');
        const pl = await res.json();

        const incBody = document.getElementById('pl-income-body');
        if (pl.income) {
            incBody.innerHTML = Object.entries(pl.income).map(([k, v]) => 
                `<tr><td>${escapeHtml(k)}</td><td class="text-end">₹${parseFloat(v).toLocaleString()}</td></tr>`
            ).join('');
        }
        document.getElementById('pl-total-income').innerText = `₹${(pl.totalIncome || 0).toLocaleString()}`;

        const expBody = document.getElementById('pl-expense-body');
        if (pl.expenses) {
            expBody.innerHTML = Object.entries(pl.expenses).map(([k, v]) => 
                `<tr><td>${escapeHtml(k)}</td><td class="text-end">₹${parseFloat(v).toLocaleString()}</td></tr>`
            ).join('');
        }
        document.getElementById('pl-total-expenses').innerText = `₹${(pl.totalExpenses || 0).toLocaleString()}`;

        const net = pl.netProfitLoss != null ? pl.netProfitLoss : (pl.netProfitOrLoss || 0);
        document.getElementById('pl-net-result').innerText = `Net Surplus / Deficit: ₹${parseFloat(net).toLocaleString()}`;
    } catch (e) {
        console.error(e);
    }

    // 3. Budget Report
    try {
        const res = await fetch('/api/reports/budget');
        const bg = await res.json();
        const tbody = document.getElementById('report-budget-body');
        if (bg && bg.length > 0) {
            tbody.innerHTML = bg.map(b => `
                <tr>
                    <td class="fw-bold">${escapeHtml(b.department || 'Digital Content')}</td>
                    <td>₹${(b.plannedBudget || 0).toLocaleString()}</td>
                    <td class="text-primary fw-bold">₹${(b.actualLicensingExpenditure || b.actualExpenditure || 0).toLocaleString()}</td>
                    <td class="text-success fw-bold">₹${(b.remainingBudget || 0).toLocaleString()}</td>
                    <td>₹${(b.variance || 0).toLocaleString()}</td>
                    <td><span class="badge bg-primary">${b.utilizationPercentage || 0}%</span></td>
                </tr>
            `).join('');
        }
    } catch (e) {
        console.error(e);
    }
}

// Helpers
function escapeHtml(str) {
    if (!str) return '';
    return String(str).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
}

function Long(val) {
    return parseInt(val, 10);
}
