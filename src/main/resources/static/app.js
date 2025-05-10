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
// Load Financial News
/*async function loadNews() {
  try {
    const res = await fetch(`${baseApi}/news`);

    if (!res.ok) {  // Check if the response is successful
      throw new Error('Failed to fetch news');
    }

    const newsList = await res.json();

    const output = newsList.map(news => `
      <div class="card">
        <h5>📰 ${news.name}</h5>
        <p>${news.description}</p>
        <span class="badge bg-info text-dark">${news.category}</span>
        <span class="badge bg-success">Rating: ${news.averageReturn}</span>
      </div>
    `).join('');

    document.getElementById("newsResult").innerHTML = output;

  } catch (error) {
    console.error("Error fetching news:", error);
    alert("There was an issue loading the news.");
  }
}*/
// In your script.js file or inside <script> tag
/*const loadNewsBtn = document.getElementById("loadnews");*/
window.loadNews = async function() {
  console.log("Button clicked!");
  const res = await fetch(`${baseApi}/news`);
  const newsList = await res.json();
  const output = newsList.map(news => `
    <div class="card">
      <h5>📰 ${news.name}</h5>
      <p>${news.description}</p>
      <span class="badge bg-info text-dark">${news.category}</span>
      <span class="badge bg-success">Rating: ${news.averageReturn}</span>
    </div>
  `).join('');
  document.getElementById("newsResult").innerHTML = output;
}


//gemini
// Make sure this function is defined in your app.js (not inside another function)
async function generateGoalPlan() {
    const username = document.getElementById("goalUsername").value;
    const goalText = document.getElementById("goalText").value;

    if (!username || !goalText) {
        alert("Please enter both username and goal description");
        return;
    }

    try {
        // Show loading state
        const btn = document.getElementById('generateGoalBtn');
        btn.disabled = true;
        btn.innerHTML = '⏳ Generating...';

        const response = await fetch(`${baseApi}/username/${username}/goal-plan`, {
            method: "POST",
            headers: {"Content-Type": "text/plain"},
            body: goalText
        });

        // First check if the response is OK
        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Server error: ${response.status} - ${errorText}`);
        }

        // Try to parse JSON
        let plan;
        try {
            plan = await response.json();
        } catch (e) {
            throw new Error("Invalid JSON response from server");
        }

        // Check if we got a valid plan object
        if (!plan || typeof plan !== 'object') {
            throw new Error("Invalid response format from server");
        }

        // Calculate monthly savings if not provided
        if (!plan.monthlySavingsNeeded && plan.targetAmount && plan.years) {
            plan.monthlySavingsNeeded = plan.targetAmount / (plan.years * 12);
        }

        // Format the plan for display with fallback values
        const formattedPlan = `
            <div class="card">
                <h4>🎯 ${plan.goal || 'Financial Goal'}</h4>
                <div class="row mt-3">
                    <div class="col-md-6">
                        <p><strong>Target Amount:</strong> $${(plan.targetAmount || 0).toLocaleString()}</p>
                        <p><strong>Timeframe:</strong> ${plan.years || 0} years</p>
                    </div>
                    <div class="col-md-6">
                        <p><strong>Monthly Savings Needed:</strong> $${(plan.monthlySavingsNeeded || 0).toFixed(2)}</p>
                        <p><strong>Risk Level:</strong> <span class="badge ${getRiskBadgeClass(plan.risk)}">${plan.risk || 'UNKNOWN'}</span></p>
                    </div>
                </div>

                ${plan.suggestions ? `
                <h5 class="mt-3">💡 Investment Suggestions:</h5>
                <div class="row">
                    ${plan.suggestions.map(suggestion => `
                        <div class="col-md-6 mb-2">
                            <div class="card bg-light">
                                <h6>${suggestion.name || 'Investment'}</h6>
                                <p class="small mb-1">${suggestion.description || ''}</p>
                                ${suggestion.averageReturn ? `<span class="badge bg-info">Potential return: ${suggestion.averageReturn}%</span>` : ''}
                            </div>
                        </div>
                    `).join('')}
                </div>
                ` : ''}
            </div>
        `;

        document.getElementById("goalPlanResult").innerHTML = formattedPlan;
    } catch (error) {
        console.error("Error generating goal plan:", error);
        document.getElementById("goalPlanResult").innerHTML = `
            <div class="alert alert-danger">
                <h5>Error generating plan</h5>
                <p>${error.message}</p>
                <p class="small">Please check your input and try again. If the problem persists, contact support.</p>
            </div>
        `;
    } finally {
        // Reset button state
        const btn = document.getElementById('generateGoalBtn');
        if (btn) {
            btn.disabled = false;
            btn.innerHTML = '✨ Generate Plan';
        }
    }
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

document.addEventListener('DOMContentLoaded', function() {
    // Existing tab switching code (keep this if you have it)
    document.querySelectorAll("#tabs .nav-link").forEach(link => {
        link.addEventListener("click", e => {
            // ... your existing tab switching code ...
        });
    });

    // Add this NEW line for the goal planner button
    document.getElementById('generateGoalBtn')?.addEventListener('click', generateGoalPlan);

    // Keep any other existing event listeners you have
    document.getElementById("createForm")?.addEventListener("submit", async (e) => {
        // ... your existing create form code ...
    });

    document.getElementById("searchProfileForm")?.addEventListener("submit", async (e) => {
        // ... your existing profile search code ...
    });
});