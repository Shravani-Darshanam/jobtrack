const ROOT = "http://localhost:8080/api";   // change when deploying
const API = ROOT + "/applications";
const STATUSES = {APPLIED:"Applied",ONLINE_ASSESSMENT:"Online Assessment",INTERVIEW:"Interview",OFFER:"Offer",REJECTED:"Rejected"};
const TYPES = {INTERNSHIP:"Internship",FULL_TIME:"Full-time",PART_TIME:"Part-time"};
const $ = id => document.getElementById(id);
let editingId = null, page = 0;
const token = () => localStorage.getItem("jt_token");
function showApp() { const on = !!token(); $("auth").hidden = on; $("app").hidden = !on; if (on) refresh(); }
function logout() { localStorage.removeItem("jt_token"); showApp(); }
async function authSubmit(kind) {
  $("authMsg").textContent = "";
  try {
    const r = await api("/" + kind, { method: "POST", body: JSON.stringify({ email: $("email").value, password: $("password").value }) }, ROOT + "/auth");
    localStorage.setItem("jt_token", r.token); $("password").value = ""; showApp();
  } catch (e) { $("authMsg").textContent = e.fields ? Object.values(e.fields).join(", ") : e.message; }
}

const esc = s => String(s ?? "").replace(/[&<>"']/g, c => ({"&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#39;"}[c]));
function opts(sel, map, withAll) { sel.innerHTML = (withAll ? '<option value="">All</option>' : "") + Object.entries(map).map(([k, v]) => `<option value="${k}">${v}</option>`).join(""); }
function notify(text, ok = true) { $("message").innerHTML = `<div class="msg ${ok ? "ok" : "err"}">${esc(text)}</div>`; setTimeout(() => $("message").innerHTML = "", 4000); }

async function api(path = "", options = {}, base = API) {
  let res;
  try { res = await fetch(base + path, { ...options, headers: { "Content-Type": "application/json", ...(token() ? { Authorization: "Bearer " + token() } : {}) } }); }
  catch { throw new Error("Cannot reach the server. Is the backend running on port 8080?"); }
  if (res.status === 204) return null;
  if (res.status === 401 && token()) { logout(); throw new Error("Session expired. Please log in again."); }
  const data = await res.json().catch(() => ({}));
  if (!res.ok) { const e = new Error(data.message || "Request failed (" + res.status + ")"); e.fields = data.errors; throw e; }
  return data;
}

async function loadStats() {
  try {
    const s = await api("/stats");
    const items = [["Total", s.total], ["Applied", s.APPLIED], ["Online Assessments", s.ONLINE_ASSESSMENT], ["Interviews", s.INTERVIEW], ["Offers", s.OFFER], ["Rejections", s.REJECTED]];
    $("stats").innerHTML = items.map(([l, n]) => `<div class="stat"><b>${n}</b><span>${l}</span></div>`).join("");
  } catch (e) { notify(e.message, false); }
}

async function loadList() {
  const p = new URLSearchParams();
  if ($("q").value.trim()) p.set("q", $("q").value.trim());
  if ($("fStatus").value) p.set("status", $("fStatus").value);
  if ($("fType").value) p.set("jobType", $("fType").value);
  p.set("sort", $("fSort").value); p.set("page", page); p.set("size", 10);
  $("loading").hidden = false;
  try {
    const data = await api("?" + p); const rows = data.content;
    $("list").innerHTML = rows.length ? `<table><thead><tr><th>Company</th><th>Title</th><th>Type</th><th>Location</th><th>Applied</th><th>Follow-up</th><th>Status</th><th>Actions</th></tr></thead><tbody>` +
      rows.map(a => `<tr><td data-label="Company">${a.jobUrl ? `<a href="${esc(a.jobUrl)}" target="_blank" rel="noopener">${esc(a.companyName)}</a>` : esc(a.companyName)}</td>
        <td data-label="Title">${esc(a.jobTitle)}</td><td data-label="Type">${TYPES[a.jobType]}</td><td data-label="Location">${esc(a.location)}</td>
        <td data-label="Applied">${a.applicationDate}</td><td data-label="Follow-up">${a.followUpDate || "—"}</td><td data-label="Status"><span class="badge ${a.status}">${STATUSES[a.status]}</span></td>
        <td data-label="Actions"><button class="btn" data-edit="${a.id}">Edit</button> <button class="btn danger" data-del="${a.id}">Delete</button></td></tr>`).join("") + "</tbody></table>"
      : `<div class="empty">No applications found. Try changing the filters or add a new one.</div>`;
    $("pager").innerHTML = data.totalPages > 1 ? `<button class="btn" id="prev" ${page === 0 ? "disabled" : ""}>Prev</button> Page ${page + 1} of ${data.totalPages} <button class="btn" id="next" ${page + 1 >= data.totalPages ? "disabled" : ""}>Next</button>` : "";
  } catch (e) { $("list").innerHTML = ""; notify(e.message, false); }
  $("loading").hidden = true;
}
const refresh = () => Promise.all([loadStats(), loadList()]);

async function openForm(id) {
  editingId = id; $("form").reset(); $("formError").textContent = ""; $("history").textContent = "";
  $("dlgTitle").textContent = id ? "Edit application" : "Add application";
  $("applicationDate").max = new Date().toISOString().slice(0, 10);
  if (id) {
    try { const a = await api("/" + id); for (const k of ["companyName","jobTitle","jobType","location","applicationDate","status","followUpDate","jobUrl","notes"]) $(k).value = a[k] ?? "";
      $("history").textContent = "History: " + a.statusHistory.map(h => STATUSES[h.status] + " (" + h.changedAt.slice(0, 10) + ")").join(" → "); }
    catch (e) { return notify(e.message, false); }
  } else $("applicationDate").value = $("applicationDate").max;
  $("dlg").showModal();
}

$("form").addEventListener("submit", async ev => {
  ev.preventDefault();
  const body = Object.fromEntries(new FormData($("form")));
  if (!body.followUpDate) delete body.followUpDate;
  const missing = ["companyName","jobTitle","location","applicationDate"].filter(k => !String(body[k]).trim());
  if (missing.length) { $("formError").textContent = "Please fill: " + missing.join(", "); return; }
  if (body.jobUrl && !/^https?:\/\//i.test(body.jobUrl)) { $("formError").textContent = "Job URL must start with http:// or https://"; return; }
  try {
    await api(editingId ? "/" + editingId : "", { method: editingId ? "PUT" : "POST", body: JSON.stringify(body) });
    $("dlg").close(); notify(editingId ? "Application updated" : "Application added"); refresh();
  } catch (e) { $("formError").textContent = e.fields ? Object.values(e.fields).join(", ") : e.message; }
});

$("list").addEventListener("click", async ev => {
  const edit = ev.target.dataset.edit, del = ev.target.dataset.del;
  if (edit) openForm(edit);
  if (del && confirm("Delete this application? This cannot be undone.")) {
    try { await api("/" + del, { method: "DELETE" }); notify("Application deleted"); refresh(); } catch (e) { notify(e.message, false); }
  }
});

opts($("jobType"), TYPES); opts($("status"), STATUSES); opts($("fStatus"), STATUSES, true); opts($("fType"), TYPES, true);
$("addBtn").onclick = () => openForm(null);
$("cancelBtn").onclick = () => $("dlg").close();
let t; $("q").addEventListener("input", () => { clearTimeout(t); t = setTimeout(() => { page = 0; loadList(); }, 300); });
["fStatus", "fType", "fSort"].forEach(id => $(id).addEventListener("change", () => { page = 0; loadList(); }));
$("pager").addEventListener("click", ev => { if (ev.target.id === "prev") page--; else if (ev.target.id === "next") page++; else return; loadList(); });
$("authForm").addEventListener("submit", ev => { ev.preventDefault(); authSubmit("login"); });
$("registerBtn").onclick = () => authSubmit("register");
$("logoutBtn").onclick = logout;
showApp();
