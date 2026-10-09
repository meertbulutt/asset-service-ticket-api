const secenekler = {
  tur: { LAPTOP: 'Dizüstü bilgisayar', DESKTOP: 'Masaüstü bilgisayar', PRINTER: 'Yazıcı', SERVER: 'Sunucu', NETWORK_DEVICE: 'Ağ cihazı', OTHER: 'Diğer' },
  demirbasDurumu: { ACTIVE: 'Aktif', IN_SERVICE: 'Serviste', RETIRED: 'Kullanım dışı', LOST: 'Kayıp' },
  talepDurumu: { OPEN: 'Açık', ASSIGNED: 'Atandı', IN_PROGRESS: 'İşlemde', RESOLVED: 'Çözüldü', CLOSED: 'Kapalı', CANCELLED: 'İptal edildi' },
  oncelik: { LOW: 'Düşük', MEDIUM: 'Orta', HIGH: 'Yüksek', CRITICAL: 'Kritik' },
  rol: { ADMIN: 'Yönetici', TECHNICIAN: 'Teknisyen', EMPLOYEE: 'Çalışan' }
};

const durum = { assets: [], tickets: [], users: [], departments: [], summary: null, currentUser: null };
const sayfaBasliklari = { dashboard: 'Genel Bakış', tickets: 'Destek Talepleri', assets: 'Demirbaşlar', users: 'Kullanıcılar', departments: 'Departmanlar' };
let toastTimer;

function html(value) {
  return String(value ?? '').replace(/[&<>"']/g, (char) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' })[char]);
}

function etiket(map, key) { return map[key] || key || '—'; }

function tarih(value) {
  if (!value) return '—';
  const date = new Date(value);
  return Number.isNaN(date.getTime()) ? '—' : new Intl.DateTimeFormat('tr-TR', { day: '2-digit', month: 'short', year: 'numeric' }).format(date);
}

async function api(url, options = {}) {
  const csrfCookie = document.cookie.split('; ').find((cookie) => cookie.startsWith('XSRF-TOKEN='));
  const csrfToken = csrfCookie ? decodeURIComponent(csrfCookie.substring('XSRF-TOKEN='.length)) : null;
  const headers = { 'X-Requested-With': 'XMLHttpRequest', ...(options.body ? { 'Content-Type': 'application/json' } : {}), ...(options.headers || {}) };
  if (csrfToken && options.method && options.method !== 'GET') headers['X-XSRF-TOKEN'] = csrfToken;
  const response = await fetch(url, {
    ...options,
    headers
  });
  const raw = await response.text();
  let body = null;
  if (raw) {
    try { body = JSON.parse(raw); } catch { body = raw; }
  }
  if (response.status === 401) { location.href = '/login.html?expired=true'; throw new Error('Oturum süresi doldu.'); }
  if (!response.ok) throw new Error(body?.message || `İstek başarısız oldu (${response.status}).`);
  return body;
}

function bildirim(message, error = false) {
  const toast = document.querySelector('#toast');
  toast.textContent = message;
  toast.classList.toggle('error', error);
  toast.classList.add('visible');
  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => toast.classList.remove('visible'), 3600);
}

function setConnection(online) {
  const badge = document.querySelector('#connection-status');
  badge.classList.toggle('offline', !online);
  badge.innerHTML = `<span class="connection-dot"></span>${online ? 'API bağlantısı hazır' : 'API bağlantısı yok'}`;
}

function badge(map, key, prefix) {
  const className = String(key || '').toLowerCase().replace(/[^a-z0-9_-]/g, '_');
  return `<span class="${prefix} ${prefix}-${className}">${html(etiket(map, key))}</span>`;
}

function fillOptions(select, items, placeholder, labelFn) {
  const oldValue = select.value;
  select.innerHTML = `<option value="">${html(placeholder)}</option>` + items.map((item) => `<option value="${html(item.value)}">${html(labelFn(item))}</option>`).join('');
  if (items.some((item) => String(item.value) === oldValue)) select.value = oldValue;
}

function syncSelects() {
  const types = Object.entries(secenekler.tur).map(([value, label]) => ({ value, label }));
  const assetStatuses = Object.entries(secenekler.demirbasDurumu).map(([value, label]) => ({ value, label }));
  const ticketStatuses = Object.entries(secenekler.talepDurumu).map(([value, label]) => ({ value, label }));
  const priorities = Object.entries(secenekler.oncelik).map(([value, label]) => ({ value, label }));
  const roles = Object.entries(secenekler.rol).map(([value, label]) => ({ value, label }));

  fillOptions(document.querySelector('#asset-type-filter'), types, 'Tüm türler', (item) => item.label);
  fillOptions(document.querySelector('#asset-status-filter'), assetStatuses, 'Tüm durumlar', (item) => item.label);
  fillOptions(document.querySelector('#ticket-status-filter'), ticketStatuses, 'Tüm durumlar', (item) => item.label);
  fillOptions(document.querySelector('#ticket-priority-filter'), priorities, 'Tüm öncelikler', (item) => item.label);
  fillOptions(document.querySelector('#user-role-filter'), roles, 'Tüm roller', (item) => item.label);

  const assetType = document.querySelector('#asset-form [name="type"]');
  const assetStatus = document.querySelector('#asset-form [name="status"]');
  const ticketPriority = document.querySelector('#ticket-form [name="priority"]');
  fillOptions(assetType, types, 'Tür seçin', (item) => item.label);
  fillOptions(assetStatus, assetStatuses, 'Durum seçin', (item) => item.label);
  fillOptions(ticketPriority, priorities, 'Öncelik seçin', (item) => item.label);

  const departments = durum.departments.map((item) => ({ value: item.id, label: item.name }));
  const users = durum.users.filter((item) => item.active).map((item) => ({ value: item.id, label: `${item.fullName} · ${etiket(secenekler.rol, item.role)}` }));
  const assets = durum.assets.map((item) => ({ value: item.id, label: `${item.assetTag} · ${item.name}` }));
  fillOptions(document.querySelector('#asset-form [name="departmentId"]'), departments, 'Departman seçilmedi', (item) => item.label);
  fillOptions(document.querySelector('#asset-form [name="assignedUserId"]'), users, 'Kullanıcı seçilmedi', (item) => item.label);
  fillOptions(document.querySelector('#user-form [name="role"]'), roles, 'Rol seçin', (item) => item.label);
  fillOptions(document.querySelector('#user-form [name="departmentId"]'), departments, 'Departman seçilmedi', (item) => item.label);
  fillOptions(document.querySelector('#ticket-form [name="assetId"]'), assets, 'Demirbaş seçin', (item) => item.label);
}

function applyAccess() {
  const authorities = durum.currentUser.authorities || [];
  const role = durum.currentUser.role;
  document.querySelectorAll('[data-role]').forEach((element) => {
    const allowed = element.dataset.role.split(',').some((role) => authorities.includes(`ROLE_${role}`));
    element.classList.toggle('hidden', !allowed);
  });
  document.querySelector('#signed-in-user').textContent = `${durum.currentUser.fullName} · ${etiket(secenekler.rol, durum.currentUser.role)}`;
  const descriptions = {
    ADMIN: ['Tüm demirbaş ve destek kayıtlarını yönetici yetkisiyle görüntülüyorsun.', 'Kurum envanterindeki cihazları görüntülüyorsun.', 'Tüm destek taleplerini görüntüleyebilir, teknisyen atayabilir ve durumlarını yönetebilirsin.'],
    TECHNICIAN: ['Sana atanmış destek işlerini ve kurum envanterini görüntülüyorsun.', 'Kurum cihazlarını görüntüleyebilirsin; envanter değişikliklerini yöneticiler yapar.', 'Yalnızca sana atanmış talepleri görüntüleyip durumlarını güncelleyebilir ve yorum ekleyebilirsin.'],
    EMPLOYEE: ['Zimmetindeki cihazları ve oluşturduğun destek taleplerini görüntülüyorsun.', 'Yalnızca sana zimmetli cihazlar listelenir.', 'Yalnızca kendi oluşturduğun talepleri görüntüleyebilir ve bu taleplere yorum ekleyebilirsin.']
  }[role];
  document.querySelector('#dashboard-description').textContent = descriptions[0];
  document.querySelector('#assets-description').textContent = descriptions[1];
  document.querySelector('#tickets-description').textContent = descriptions[2];
}

function updateSummary() {
  const s = durum.summary;
  if (!s) return;
  document.querySelector('#stat-total-assets').textContent = s.totalAssets;
  document.querySelector('#stat-active-assets').textContent = s.activeAssets;
  document.querySelector('#stat-open-tickets').textContent = s.openTickets;
  document.querySelector('#stat-progress-tickets').textContent = s.inProgressTickets;
  document.querySelector('#summary-assets-total').textContent = s.totalAssets;
  document.querySelector('#summary-active').textContent = s.activeAssets;
  document.querySelector('#summary-service').textContent = s.inServiceAssets;
  document.querySelector('#open-ticket-count').textContent = s.openTickets;
  const total = Math.max(Number(s.totalAssets), 1);
  document.querySelector('#meter-active').style.width = `${Math.min(100, (s.activeAssets / total) * 100)}%`;
  document.querySelector('#meter-service').style.width = `${Math.min(100, (s.inServiceAssets / total) * 100)}%`;
}

function renderRecentTickets() {
  const rows = [...durum.tickets].sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt)).slice(0, 5);
  document.querySelector('#recent-tickets').innerHTML = rows.length ? rows.map((ticket) => `<tr>
    <td><span class="ticket-primary">${html(ticket.ticketNumber)}</span><span class="ticket-secondary">${html(tarih(ticket.createdAt))}</span></td>
    <td>${html(ticket.assetName)}</td><td>${badge(secenekler.oncelik, ticket.priority, 'priority')}</td><td>${badge(secenekler.talepDurumu, ticket.status, 'status-badge')}</td>
  </tr>`).join('') : '<tr><td colspan="4" class="empty-cell">Henüz destek talebi bulunmuyor.</td></tr>';
}

function renderAssets() {
  const search = document.querySelector('#asset-search').value.trim().toLocaleLowerCase('tr-TR');
  const type = document.querySelector('#asset-type-filter').value;
  const status = document.querySelector('#asset-status-filter').value;
  const items = durum.assets.filter((asset) => {
    const matchesSearch = !search || `${asset.name} ${asset.assetTag} ${asset.brand || ''} ${asset.model || ''}`.toLocaleLowerCase('tr-TR').includes(search);
    return matchesSearch && (!type || asset.type === type) && (!status || asset.status === status);
  });
  const isAdmin = durum.currentUser.role === 'ADMIN';
  document.querySelector('#assets-table').innerHTML = items.length ? items.map((asset) => `<tr>
    <td><span class="ticket-primary">${html(asset.name)}</span><span class="ticket-secondary">${html([asset.brand, asset.model].filter(Boolean).join(' '))}</span></td>
    <td>${html(asset.assetTag)}</td><td>${html(etiket(secenekler.tur, asset.type))}</td><td>${html(asset.departmentName || 'Atanmamış')}</td><td>${isAdmin
      ? `<div class="row-actions"><select data-asset-assignee="${asset.id}" aria-label="${html(asset.name)} zimmetlenecek kullanıcı"><option value="">Zimmetli değil</option>${durum.users.filter((user) => user.active).map((user) => `<option value="${user.id}" ${Number(user.id) === Number(asset.assignedUserId) ? 'selected' : ''}>${html(user.fullName)}</option>`).join('')}</select><button class="mini-button" data-action="assign-asset" data-id="${asset.id}">Kaydet</button></div>`
      : html(asset.assignedUserName || 'Zimmetli değil')}</td><td>${badge(secenekler.demirbasDurumu, asset.status, 'status-badge')}</td>
  </tr>`).join('') : '<tr><td colspan="6" class="empty-cell">Bu ölçütlere uygun demirbaş bulunamadı.</td></tr>';
  document.querySelector('#assets-count').textContent = `${items.length} demirbaş gösteriliyor`;
}

function renderTickets() {
  const search = document.querySelector('#ticket-search').value.trim().toLocaleLowerCase('tr-TR');
  const statusFilter = document.querySelector('#ticket-status-filter').value;
  const priorityFilter = document.querySelector('#ticket-priority-filter').value;
  const items = [...durum.tickets].filter((ticket) => {
    const matchesSearch = !search || `${ticket.ticketNumber} ${ticket.title} ${ticket.assetName} ${ticket.createdByName}`.toLocaleLowerCase('tr-TR').includes(search);
    return matchesSearch && (!statusFilter || ticket.status === statusFilter) && (!priorityFilter || ticket.priority === priorityFilter);
  }).sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
  const statuses = Object.entries(secenekler.talepDurumu);
  const isAdmin = durum.currentUser.role === 'ADMIN';
  const canManageTickets = isAdmin || durum.currentUser.role === 'TECHNICIAN';
  const technicians = durum.users.filter((user) => user.active && user.role === 'TECHNICIAN');
  document.querySelector('#tickets-table').innerHTML = items.length ? items.map((ticket) => {
    const locked = ['CLOSED', 'CANCELLED'].includes(ticket.status);
    const controlsDisabled = locked || !canManageTickets;
    const assignmentCell = isAdmin
      ? `<select data-ticket-assignee="${ticket.id}" aria-label="${html(ticket.ticketNumber)} teknisyen ata" ${controlsDisabled ? 'disabled' : ''}><option value="">Atanmamış</option>${technicians.map((user) => `<option value="${user.id}" ${Number(user.id) === Number(ticket.assignedTechnicianId) ? 'selected' : ''}>${html(user.fullName)}</option>`).join('')}</select>`
      : html(ticket.assignedTechnicianName || 'Henüz atanmadı');
    const statusCell = canManageTickets
      ? `<select data-ticket-status="${ticket.id}" aria-label="${html(ticket.ticketNumber)} durum seç" ${controlsDisabled ? 'disabled' : ''}>${statuses.map(([key, label]) => `<option value="${key}" ${ticket.status === key ? 'selected' : ''}>${html(label)}</option>`).join('')}</select>`
      : badge(secenekler.talepDurumu, ticket.status, 'status-badge');
    const saveAction = canManageTickets
      ? `<button class="mini-button save-status" data-action="save-status" data-id="${ticket.id}" ${controlsDisabled ? 'disabled' : ''}>Kaydet</button>`
      : '';
    return `<tr>
      <td><span class="ticket-primary">${html(ticket.ticketNumber)}</span><span class="ticket-secondary ticket-title" title="${html(ticket.title)}">${html(ticket.title)}</span></td>
      <td><span class="ticket-primary">${html(ticket.assetName)}</span><span class="ticket-secondary">Talep sahibi: ${html(ticket.createdByName)}</span></td>
      <td>${badge(secenekler.oncelik, ticket.priority, 'priority')}</td>
      <td>${assignmentCell}</td>
      <td>${statusCell}</td>
      <td><div class="row-actions">${saveAction}<button class="mini-button" data-action="comments" data-id="${ticket.id}" data-number="${html(ticket.ticketNumber)}">Yorumlar</button></div></td>
    </tr>`;
  }).join('') : '<tr><td colspan="6" class="empty-cell">Bu ölçütlere uygun destek talebi bulunamadı.</td></tr>';
  document.querySelector('#tickets-count').textContent = `${items.length} talep gösteriliyor`;
}

function renderUsers() {
  const search = document.querySelector('#user-search').value.trim().toLocaleLowerCase('tr-TR');
  const role = document.querySelector('#user-role-filter').value;
  const items = durum.users.filter((user) => `${user.fullName} ${user.email} ${user.departmentName || ''}`.toLocaleLowerCase('tr-TR').includes(search) && (!role || user.role === role));
  document.querySelector('#users-table').innerHTML = items.length ? items.map((user) => {
    const initials = user.fullName.split(/\s+/).slice(0, 2).map((part) => part[0]).join('').toLocaleUpperCase('tr-TR');
    const canManageUsers = durum.currentUser.authorities.includes('ROLE_ADMIN');
    return `<tr><td><span class="user-name"><span class="avatar">${html(initials)}</span><span class="ticket-primary">${html(user.fullName)}</span></span></td><td>${html(user.email)}</td><td>${badge(secenekler.rol, user.role, 'role-badge')}</td><td>${html(user.departmentName || 'Atanmamış')}</td><td><span class="${user.active ? 'account-active' : 'account-inactive'}">${user.active ? '● Aktif' : '● Devre dışı'}</span></td><td>${canManageUsers && user.active ? `<button class="mini-button" data-action="deactivate-user" data-id="${user.id}">Devre dışı bırak</button>` : '—'}</td></tr>`;
  }).join('') : '<tr><td colspan="6" class="empty-cell">Kullanıcı bulunamadı.</td></tr>';
}

function renderDepartments() {
  const icons = ['⌂', '▦', '⌘', '◇', '▤', '◈'];
  document.querySelector('#department-cards').innerHTML = durum.departments.length ? durum.departments.map((department, index) => {
    const userCount = durum.users.filter((user) => Number(user.departmentId) === Number(department.id)).length;
    const assetCount = durum.assets.filter((asset) => Number(asset.departmentId) === Number(department.id)).length;
    return `<article class="panel department-card"><div class="department-icon">${icons[index % icons.length]}</div><h2>${html(department.name)}</h2><p>${html(department.description || 'Departman açıklaması eklenmemiş.')}</p><div class="department-meta"><span>${userCount} kullanıcı</span><strong>${assetCount} demirbaş</strong></div></article>`;
  }).join('') : '<article class="panel empty-cell">Henüz departman bulunmuyor.</article>';
}

async function yenile({ silent = false } = {}) {
  try {
    const currentUser = await api('/api/auth/me');
    const isAdmin = currentUser.role === 'ADMIN';
    const [summary, assets, tickets, users, departments] = await Promise.all([
      api('/api/reports/summary'), api('/api/assets'), api('/api/tickets'),
      isAdmin ? api('/api/users') : Promise.resolve([]),
      isAdmin ? api('/api/departments') : Promise.resolve([])
    ]);
    durum.currentUser = currentUser;
    durum.summary = summary; durum.assets = assets; durum.tickets = tickets; durum.users = users; durum.departments = departments;
    setConnection(true);
    document.querySelector('#load-error').classList.add('hidden');
    applyAccess(); syncSelects(); updateSummary(); renderRecentTickets(); renderAssets(); renderTickets(); renderUsers(); renderDepartments();
    if (!silent) bildirim('Veriler güncellendi.');
  } catch (error) {
    setConnection(false);
    const alert = document.querySelector('#load-error');
    alert.textContent = `API verileri yüklenemedi: ${error.message} Uygulamanın çalıştığını kontrol edip yeniden dene.`;
    alert.classList.remove('hidden');
    if (!silent) bildirim(error.message, true);
  }
}

function showPage(name) {
  if (!sayfaBasliklari[name]) return;
  document.querySelectorAll('.page').forEach((page) => page.classList.toggle('active', page.id === `page-${name}`));
  document.querySelectorAll('.nav-link').forEach((link) => link.classList.toggle('active', link.dataset.page === name));
  document.querySelector('#current-page-label').textContent = sayfaBasliklari[name];
}

function openDialog(id) {
  const dialog = document.getElementById(id);
  if (dialog?.showModal) dialog.showModal();
}

function closeDialog(element) { element.closest('dialog')?.close(); }

async function createAsset(event) {
  event.preventDefault();
  const form = event.currentTarget;
  const data = Object.fromEntries(new FormData(form));
  for (const key of ['departmentId', 'assignedUserId']) if (!data[key]) delete data[key]; else data[key] = Number(data[key]);
  try {
    const created = await api('/api/assets', { method: 'POST', body: JSON.stringify(data) });
    form.reset(); closeDialog(form); await yenile({ silent: true }); showPage('assets'); bildirim(`${created.assetTag} etiketli demirbaş eklendi.`);
  } catch (error) { bildirim(error.message, true); }
}

async function assignAsset(button) {
  const asset = durum.assets.find((item) => Number(item.id) === Number(button.dataset.id));
  if (!asset) return;
  const selectedUser = document.querySelector(`[data-asset-assignee="${asset.id}"]`).value;
  const data = {
    assetTag: asset.assetTag, name: asset.name, type: asset.type, status: asset.status,
    serialNumber: asset.serialNumber, brand: asset.brand, model: asset.model,
    departmentId: asset.departmentId, assignedUserId: selectedUser ? Number(selectedUser) : null
  };
  try {
    await api(`/api/assets/${asset.id}`, { method: 'PUT', body: JSON.stringify(data) });
    await yenile({ silent: true }); bildirim('Demirbaş zimmeti güncellendi.');
  } catch (error) { bildirim(error.message, true); }
}

async function createUser(event) {
  event.preventDefault();
  const form = event.currentTarget;
  const data = Object.fromEntries(new FormData(form));
  if (data.departmentId) data.departmentId = Number(data.departmentId); else delete data.departmentId;
  try {
    const created = await api('/api/users', { method: 'POST', body: JSON.stringify(data) });
    form.reset(); closeDialog(form); await yenile({ silent: true }); showPage('users');
    bildirim(`${created.fullName} kullanıcı olarak eklendi.`);
  } catch (error) { bildirim(error.message, true); }
}

async function deactivateUser(id) {
  try {
    await api(`/api/users/${id}/deactivate`, { method: 'PATCH' });
    await yenile({ silent: true }); bildirim('Kullanıcı devre dışı bırakıldı.');
  } catch (error) { bildirim(error.message, true); }
}

async function logout() {
  try {
    await api('/logout', { method: 'POST' });
  } finally {
    location.href = '/login.html?logout=true';
  }
}

async function createTicket(event) {
  event.preventDefault();
  const form = event.currentTarget;
  const data = Object.fromEntries(new FormData(form));
  data.assetId = Number(data.assetId);
  try {
    const created = await api('/api/tickets', { method: 'POST', body: JSON.stringify(data) });
    form.reset(); closeDialog(form); await yenile({ silent: true }); showPage('tickets'); bildirim(`${created.ticketNumber} numaralı talep oluşturuldu.`);
  } catch (error) { bildirim(error.message, true); }
}

async function saveTicket(event) {
  const button = event.target.closest('[data-action="save-status"]');
  if (!button) return;
  const id = button.dataset.id;
  const ticket = durum.tickets.find((item) => Number(item.id) === Number(id));
  const status = document.querySelector(`[data-ticket-status="${id}"]`).value;
  const technicianId = document.querySelector(`[data-ticket-assignee="${id}"]`).value;
  try {
    const previousTechnicianId = ticket?.assignedTechnicianId == null ? '' : String(ticket.assignedTechnicianId);
    if (technicianId !== previousTechnicianId) {
      await api(`/api/tickets/${id}/assign`, { method: 'PATCH', body: JSON.stringify({ technicianId: technicianId ? Number(technicianId) : null }) });
    }
    if (ticket && status !== ticket.status) {
      await api(`/api/tickets/${id}/status`, { method: 'PATCH', body: JSON.stringify({ status }) });
    }
    await yenile({ silent: true }); bildirim('Talep bilgileri güncellendi.');
  } catch (error) { bildirim(error.message, true); await yenile({ silent: true }); }
}

async function openComments(button) {
  const id = button.dataset.id;
  document.querySelector('#comments-title').textContent = `${button.dataset.number} · Yorumlar`;
  document.querySelector('#comment-form [name="ticketId"]').value = id;
  document.querySelector('#comments-list').innerHTML = '<div class="empty-cell">Yorumlar yükleniyor…</div>';
  openDialog('comments-dialog');
  try {
    const comments = await api(`/api/tickets/${id}/comments`);
    document.querySelector('#comments-list').innerHTML = comments.length ? comments.map((comment) => `<article class="comment-item"><div class="comment-meta"><strong>${html(comment.userFullName)}</strong><span>${html(tarih(comment.createdAt))}</span></div><p>${html(comment.content)}</p></article>`).join('') : '<div class="empty-cell">Bu talebe henüz yorum eklenmemiş.</div>';
  } catch (error) { document.querySelector('#comments-list').innerHTML = `<div class="alert">${html(error.message)}</div>`; }
}

async function createComment(event) {
  event.preventDefault();
  const form = event.currentTarget;
  const data = Object.fromEntries(new FormData(form));
  const id = data.ticketId; delete data.ticketId;
  try {
    await api(`/api/tickets/${id}/comments`, { method: 'POST', body: JSON.stringify(data) });
    form.reset(); form.querySelector('[name="ticketId"]').value = id;
    const comments = await api(`/api/tickets/${id}/comments`);
    document.querySelector('#comments-list').innerHTML = comments.length ? comments.map((comment) => `<article class="comment-item"><div class="comment-meta"><strong>${html(comment.userFullName)}</strong><span>${html(tarih(comment.createdAt))}</span></div><p>${html(comment.content)}</p></article>`).join('') : '<div class="empty-cell">Bu talebe henüz yorum eklenmemiş.</div>';
    bildirim('Yorum talebe eklendi.');
  } catch (error) { bildirim(error.message, true); }
}

document.querySelectorAll('[data-page]').forEach((button) => button.addEventListener('click', () => showPage(button.dataset.page)));
document.querySelectorAll('[data-open-dialog]').forEach((button) => button.addEventListener('click', () => openDialog(button.dataset.openDialog)));
document.querySelectorAll('[data-close-dialog]').forEach((button) => button.addEventListener('click', () => closeDialog(button)));
document.querySelector('#asset-form').addEventListener('submit', createAsset);
document.querySelector('#user-form').addEventListener('submit', createUser);
document.querySelector('#ticket-form').addEventListener('submit', createTicket);
document.querySelector('#comment-form').addEventListener('submit', createComment);
document.querySelector('#tickets-table').addEventListener('click', (event) => {
  if (event.target.closest('[data-action="save-status"]')) saveTicket(event);
  const commentButton = event.target.closest('[data-action="comments"]');
  if (commentButton) openComments(commentButton);
});
document.querySelector('#users-table').addEventListener('click', (event) => {
  const button = event.target.closest('[data-action="deactivate-user"]');
  if (button) deactivateUser(button.dataset.id);
});
document.querySelector('#assets-table').addEventListener('click', (event) => {
  const button = event.target.closest('[data-action="assign-asset"]');
  if (button) assignAsset(button);
});
document.querySelector('#asset-search').addEventListener('input', renderAssets);
document.querySelector('#asset-type-filter').addEventListener('change', renderAssets);
document.querySelector('#asset-status-filter').addEventListener('change', renderAssets);
document.querySelector('#ticket-search').addEventListener('input', renderTickets);
document.querySelector('#ticket-status-filter').addEventListener('change', renderTickets);
document.querySelector('#ticket-priority-filter').addEventListener('change', renderTickets);
document.querySelector('#user-search').addEventListener('input', renderUsers);
document.querySelector('#user-role-filter').addEventListener('change', renderUsers);
document.querySelector('#refresh-button').addEventListener('click', () => yenile());
document.querySelector('#logout-button').addEventListener('click', logout);
document.querySelector('#logout-button').addEventListener('click', logout);

yenile({ silent: true });
