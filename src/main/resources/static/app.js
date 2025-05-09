// Switching tabs
/*document.querySelectorAll("#tabs .nav-link").forEach(link => {
  link.addEventListener("click", e => {
    e.preventDefault();
    document.querySelectorAll(".tab-section").forEach(section => section.classList.add("d-none"));
    document.getElementById(link.dataset.section).classList.remove("d-none");
    document.querySelectorAll("#tabs .nav-link").forEach(nav => nav.classList.remove("active"));
    link.classList.add("active");
  });
});*/
document.querySelectorAll("#tabs .nav-link").forEach(link => {
  link.addEventListener("click", e => {
    e.preventDefault();
    document.querySelectorAll(".tab-section").forEach(section => {
      section.classList.add("d-none");
      section.classList.remove("active");
    });
    const target = document.getElementById(link.dataset.section);
    target.classList.remove("d-none");
    target.classList.add("active");
    document.querySelectorAll("#tabs .nav-link").forEach(nav => nav.classList.remove("active"));
    link.classList.add("active");
  });
});

// Default: show create user
/*document.getElementById("createUser").classList.add("active");
document.getElementById("createUser").classList.remove("d-none");*/

const defaultTab = document.getElementById("createUser");
defaultTab.classList.add("active");
defaultTab.classList.remove("d-none");

/*
document.querySelectorAll("#tabs .nav-link").forEach(link => {
  link.addEventListener("click", async (e) => {
    e.preventDefault();

    // Remove 'active' from all tabs and add 'd-none' with fade out
    document.querySelectorAll(".tab-section").forEach(section => {
      section.classList.add("fade-out");
      setTimeout(() => {
        section.classList.add("d-none");
        section.classList.remove("active", "fade-out");
      }, 300); // 300ms matches CSS fade speed
    });

    // Activate clicked tab
    const target = document.getElementById(link.dataset.section);
    setTimeout(() => {
      target.classList.remove("d-none");
      target.classList.add("fade-in", "active");
      setTimeout(() => {
        target.classList.remove("fade-in");
      }, 300);
    }, 300);

    // Update tab link active status
    document.querySelectorAll("#tabs .nav-link").forEach(nav => nav.classList.remove("active"));
    link.classList.add("active");
  });
});

// Default: show create user
const defaultTab = document.getElementById("createUser");
defaultTab.classList.add("active");
defaultTab.classList.remove("d-none");

*/

// Base API URL
const baseApi = "/api/users";

// Create User
document.getElementById("createForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const formData = Object.fromEntries(new FormData(e.target));
  const res = await fetch(baseApi, {
    method: "POST",
    headers: {"Content-Type": "application/json"},
    body: JSON.stringify(formData)
  });
  const data = await res.json();
  document.getElementById("createUserResult").innerHTML = `✅ User <b>${data.username}</b> created successfully!`;
  e.target.reset();
});

// Load Profile
document.getElementById("searchProfileForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const username = document.getElementById("profileUsername").value;
  const res = await fetch(`${baseApi}/username/${username}`);
  if (!res.ok) {
    document.getElementById("updateProfileResult").innerText = "❌ User not found!";
    return;
  }
  const user = await res.json();
  const form = document.getElementById("updateProfileForm");
  form.monthlyIncome.value = user.monthlyIncome;
  form.monthlyExpense.value = user.monthlyExpense;
  form.riskLevel.value = user.riskLevel;
  form.classList.remove("d-none");

  form.onsubmit = async function(ev) {
    ev.preventDefault();
    const updatedData = Object.fromEntries(new FormData(form));
    await fetch(`${baseApi}/${user.id}`, {
      method: "PUT",
      headers: {"Content-Type": "application/json"},
      body: JSON.stringify(updatedData)
    });
    document.getElementById("updateProfileResult").innerText = "✅ Profile updated!";
  };
});

// Load Investment Suggestions
async function loadSuggestions() {
  const username = document.getElementById("suggestUsername").value;
  const res = await fetch(`${baseApi}/username/${username}/investment_suggestions`);
  const investments = await res.json();
  const output = investments.map(inv => `
    <div class="card">
      <h5>📈 ${inv.name}</h5>
      <p>${inv.description}</p>
      <span class="badge bg-success">${inv.category}</span>
      <span class="badge bg-primary">Return: ${inv.averageReturn}%</span>
    </div>
  `).join('');
  document.getElementById("suggestionsResult").innerHTML = output;
}

// Load Dip Opportunities
async function loadDips() {
  const username = document.getElementById("dipUsername").value;
  const res = await fetch(`${baseApi}/username/${username}/dip_suggestions`);
  const dips = await res.json();
  const output = dips.map(dip => `
    <div class="card">
      <h5>📉 ${dip.name}</h5>
      <p>${dip.description}</p>
      <span class="badge bg-warning text-dark">Price: $${dip.averageReturn}</span>
    </div>
  `).join('');
  document.getElementById("dipsResult").innerHTML = output;
}
