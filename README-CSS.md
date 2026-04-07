# Patient Registration CSS Base Template for Thymeleaf + Spring Boot

This README describes how to use `patient-registration.css` as a reusable base styling template in a Spring Boot + Thymeleaf application.

## Thymeleaf Template Base

Example Thymeleaf page structure using this CSS:

```html
<!doctype html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Patient Management</title>

    <link
      href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
      rel="stylesheet"
    />
    <link
      href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css"
      rel="stylesheet"
    />
    <link th:href="@{/css/patient-registration.css}" rel="stylesheet" />
  </head>
  <body>
    <!-- Include navigation fragment -->
    <div
      th:replace="~{fragments/nav :: topNav(${appVersion}, ${menuGroups}, ${userId}, ${selectedApplicationSystemId})}"
    ></div>

    <main class="container-fluid py-4">
      <div class="gs-main mx-auto">
        <div class="card shadow-sm rounded-4">
          <div class="card-body p-5">
            <h2 class="card-title mb-4">
              <i class="bi bi-people"></i> Patient List
            </h2>
            <!-- Table and controls here -->
          </div>
        </div>
      </div>
    </main>

    <!-- Modals and toast -->
    <div class="toast position-fixed bottom-0 end-0 p-3" id="liveToast">
      <div class="toast-body bg-success text-white rounded"></div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
  </body>
</html>
```

## CSS Variables

Override the theme colors in your own stylesheet or in the same CSS file:

```css
:root {
  --primary-color: #1f6ac7;
  --primary-light: #e8f1fb;
  --primary-dark: #1557a0;
}
```

## UI Components Used in the Patient Registration System

### Modals

A modal is a popup window that appears on top of the page to focus the user on one task.

📌 In this system, modals are used for:

- ➕ **Add Patient** (#patientFormModal)
- ✏️ **Edit Patient** (same modal, different mode)
- 👁️ **View Patient Details** (#patientDetailsModal)
- 🔍 **Filter Patients** (#filterModal)

### Toasts

A toast is a small, temporary notification that appears in the corner of the screen.

📌 In this system, toasts are used for:

- ✅ Patient saved successfully
- ❌ Error saving patient
- 🗑️ Patient deleted successfully

### Buttons

Buttons trigger actions such as opening modals, submitting forms, or resetting filters.

📌 In this system, buttons are used for:

- ➕ **Add Patient** (opens #patientFormModal)
- ✏️ **Edit Patient** (opens #patientFormModal in edit mode)
- 👁️ **View Patient Details** (opens #patientDetailsModal)
- 🗑️ **Delete Patient** (performs delete action)
- 🔄 **Reset Filters** (#resetFiltersBtn)
- 🔍 **Apply Filters** (#applyFilterBtn)

### Tables

A table displays structured data in rows and columns.

📌 In this system, tables are used for:

- **Patient List** (.patients-table) showing: Name, Age, Gender, IC/Passport, Mobile, City, Chronic Disease, Actions

### Pagination

Pagination divides data into pages to avoid overwhelming the user.

📌 In this system, pagination is used for:

- Navigating patient list pages (#patientsPagination)

## Reusable Components

### Responsive Form Grid

The form uses a responsive grid that adapts to screen size:

```html
<form class="form-grid">
  <h3 class="section-heading col-span-2">
    <i class="bi bi-person"></i> Basic Information
  </h3>
  <div class="form-group-compact">
    <input class="input-field" type="text" placeholder="Full Name *" required />
  </div>
  <div class="form-group-compact">
    <input class="input-field" type="number" placeholder="Age *" required />
  </div>
  <div class="form-group-compact">
    <select class="input-field" required>
      <option value="">Select Gender *</option>
      <option value="Male">Male</option>
      <option value="Female">Female</option>
    </select>
  </div>
  <div class="form-group-compact">
    <input class="input-field" type="text" placeholder="Race" />
  </div>

  <h3 class="section-heading col-span-2">
    <i class="bi bi-card-text"></i> Identity
  </h3>
  <div class="form-group-compact col-span-2">
    <input
      class="input-field"
      type="text"
      placeholder="IC / Passport Number *"
      required
    />
  </div>
</form>
```

- Mobile: single column
- Desktop (≥ 768px): two columns
- Use `col-span-2` to span full width
- Use `section-heading` to group sections

### Section Titles

```html
<h3 class="section-heading"><i class="bi bi-person"></i> Patient Details</h3>
```

### Data Table

```html
<div class="table-responsive">
  <table class="table table-hover align-middle patients-table">
    <thead class="table-header-custom">
      <tr>
        <th class="sortable" data-sort="name">
          Name <span class="sort-arrow" data-field="name"></span>
        </th>
        <th class="sortable" data-sort="age">
          Age <span class="sort-arrow" data-field="age"></span>
        </th>
        <th class="sortable" data-sort="gender">
          Gender <span class="sort-arrow" data-field="gender"></span>
        </th>
        <th>IC/Passport</th>
        <th>Mobile</th>
        <th class="sortable" data-sort="city">
          City <span class="sort-arrow" data-field="city"></span>
        </th>
        <th>Chronic Disease</th>
        <th>Actions</th>
      </tr>
    </thead>
    <tbody id="patientsList">
      <!-- dynamically populated rows -->
    </tbody>
  </table>
</div>

<!-- Pagination -->
<div class="pagination-wrapper">
  <div id="patientsInfo" class="text-muted">Showing 1 to 10 of 50 patients</div>
  <nav>
    <ul id="patientsPagination" class="pagination mb-0"></ul>
  </nav>
</div>
```

Action buttons in table row:

```html
<td class="text-nowrap">
  <button
    class="btn btn-sm btn-info btn-sm-custom shadow-sm me-1 action-icon"
    onclick="viewPatientDetails(this)"
    data-id="123"
    title="View"
  >
    <i class="bi bi-eye"></i>
  </button>
  <button
    class="btn btn-sm btn-warning btn-sm-custom shadow-sm me-1 action-icon"
    onclick="editPatient(this)"
    data-id="123"
    title="Edit"
  >
    <i class="bi bi-pencil"></i>
  </button>
  <button
    class="btn btn-sm btn-danger btn-sm-custom shadow-sm action-icon"
    onclick="deletePatient(this)"
    data-id="123"
    title="Delete"
  >
    <i class="bi bi-trash"></i>
  </button>
</td>
```

### Table Controls

```html
<div class="table-tools">
  <div class="input-group flex-grow-1">
    <span class="input-group-text"><i class="bi bi-search"></i></span>
    <input
      class="form-control"
      type="search"
      placeholder="Search by name, city, IC, mobile"
    />
  </div>
  <div class="d-flex gap-2">
    <button
      class="btn btn-outline-primary"
      data-bs-toggle="modal"
      data-bs-target="#filterModal"
    >
      <i class="bi bi-funnel"></i> Filter
    </button>
    <button class="btn btn-outline-secondary" id="resetFiltersBtn">
      <i class="bi bi-arrow-counterclockwise"></i> Reset
    </button>
    <button
      class="btn btn-primary"
      data-bs-toggle="modal"
      data-bs-target="#patientFormModal"
    >
      <i class="bi bi-plus"></i> Add Patient
    </button>
  </div>
</div>
```

### Modal Dialogs

Form modal example (with form-grid inside):

```html
<div class="modal fade" id="patientFormModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-xl">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="patientFormModalLabel">Add Patient</h5>
        <button
          type="button"
          class="btn-close btn-close-white"
          data-bs-dismiss="modal"
        ></button>
      </div>
      <div class="modal-body">
        <form id="patientForm" class="form-grid">
          <input type="hidden" id="patientId" name="id" />

          <h3 class="section-heading col-span-2">
            <i class="bi bi-person"></i> Basic Information
          </h3>
          <div class="form-group-compact">
            <input
              class="input-field"
              type="text"
              id="name"
              name="name"
              placeholder="Full Name *"
              required
            />
          </div>
          <div class="form-group-compact">
            <input
              class="input-field"
              type="number"
              id="age"
              name="age"
              placeholder="Age *"
              min="0"
              max="120"
              required
            />
          </div>

          <h3 class="section-heading col-span-2">
            <i class="bi bi-card-text"></i> Identity
          </h3>
          <div class="form-group-compact col-span-2">
            <input
              class="input-field"
              type="text"
              id="icPassportNo"
              name="icPassportNo"
              placeholder="IC / Passport Number *"
              required
            />
          </div>
        </form>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
          Cancel
        </button>
        <button type="submit" form="patientForm" class="btn btn-primary">
          Save
        </button>
      </div>
    </div>
  </div>
</div>
```

Details modal example:

```html
<div
  class="modal fade"
  id="patientDetailsModal"
  tabindex="-1"
  aria-hidden="true"
>
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Patient Details</h5>
        <button
          type="button"
          class="btn-close btn-close-white"
          data-bs-dismiss="modal"
        ></button>
      </div>
      <div class="modal-body">
        <div class="details-grid">
          <div class="detail-item">
            <div class="detail-label">Full Name</div>
            <div class="detail-value">John Doe</div>
          </div>
          <div class="detail-item">
            <div class="detail-label">Age</div>
            <div class="detail-value">35</div>
          </div>
          <div class="detail-item">
            <div class="detail-label">Chronic Disease</div>
            <div class="detail-value">
              <span class="badge badge-success-soft">No</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
```

Toast Notification Example:

```html
<div
  class="toast position-fixed bottom-0 end-0 p-3"
  id="liveToast"
  role="alert"
>
  <div class="toast-body bg-success text-white rounded"></div>
</div>
```

Show toast in JavaScript:

```javascript
function showToast(message) {
  const toastEl = document.getElementById("liveToast");
  toastEl.querySelector(".toast-body").textContent = message;
  const toast = new bootstrap.Toast(toastEl);
  toast.show();
}
```

## Action Buttons and Badges

```html
<button class="btn btn-primary">Save</button>
<button class="btn btn-outline-secondary">Cancel</button>
<button class="btn btn-sm btn-info btn-sm-custom action-icon">
  <i class="bi bi-eye"></i>
</button>

<span class="badge badge-success-soft">Active</span>
<span class="badge badge-danger-soft">Inactive</span>
```

## Form Elements

```html
<input class="input-field" type="text" placeholder="Enter value" />
```

Checkbox example:

```html
<div class="form-check">
  <input
    class="form-check-input filter-checkbox"
    type="checkbox"
    id="hasChronic"
  />
  <label class="form-check-label" for="hasChronic">Has chronic disease</label>
</div>
```

## Reusable JavaScript Patterns

This CSS is designed to work with common page behaviors. Example patterns:

### Sorting

Attach event listeners to sortable headers with `data-sort` attribute:

```javascript
document.querySelectorAll("th.sortable").forEach((th) => {
  th.addEventListener("click", () => {
    const newSortBy = th.getAttribute("data-sort");
    if (newSortBy === currentSortBy) {
      currentSortDir = currentSortDir === "asc" ? "desc" : "asc";
    } else {
      currentSortBy = newSortBy;
      currentSortDir = "asc";
    }
    updateSortArrows();
    renderTable();
  });
});

function updateSortArrows() {
  document.querySelectorAll(".sort-arrow").forEach((arrow) => {
    arrow.classList.remove("active", "asc", "desc");
    if (arrow.getAttribute("data-field") === currentSortBy) {
      arrow.classList.add("active");
      arrow.classList.add(currentSortDir === "asc" ? "asc" : "desc");
    }
  });
}
```

### Modal Management

```javascript
function getPatientFormModal() {
  if (!patientFormModal) {
    patientFormModal = new bootstrap.Modal(
      document.getElementById("patientFormModal"),
    );
  }
  return patientFormModal;
}

// View patient details
async function viewPatientDetails(button) {
  const id = button.getAttribute("data-id");
  const response = await fetch(`/patient/${id}`);
  const patient = await response.json();
  displayPatientDetails(patient);
  getPatientDetailsModal().show();
}

// Edit patient
async function editPatient(button) {
  const id = button.getAttribute("data-id");
  const response = await fetch(`/patient/${id}`);
  const patient = await response.json();
  populateForm(patient);
  getPatientFormModal().show();
}

// Delete patient
async function deletePatient(button) {
  if (!confirm("Are you sure?")) return;
  const id = button.getAttribute("data-id");
  await fetch(`/patient/${id}`, { method: "DELETE" });
  loadPatients();
  showToast("Patient deleted successfully!");
}
```

### Toast Notifications

```javascript
function showToast(message) {
  const toastEl = document.getElementById("liveToast");
  toastEl.querySelector(".toast-body").textContent = message;
  const toast = new bootstrap.Toast(toastEl);
  toast.show();
}
```

### Filtering Modal

```html
<div class="modal fade" id="filterModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Filter Patients</h5>
        <button
          type="button"
          class="btn-close"
          data-bs-dismiss="modal"
        ></button>
      </div>
      <div class="modal-body">
        <h6 class="mb-3">Gender</h6>
        <div class="form-check">
          <input
            class="form-check-input filter-checkbox"
            type="checkbox"
            id="filterMale"
            value="Male"
            data-filter="gender"
          />
          <label class="form-check-label" for="filterMale">Male</label>
        </div>
        <div class="form-check">
          <input
            class="form-check-input filter-checkbox"
            type="checkbox"
            id="filterFemale"
            value="Female"
            data-filter="gender"
          />
          <label class="form-check-label" for="filterFemale">Female</label>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
          Close
        </button>
        <button type="button" id="applyFilterBtn" class="btn btn-primary">
          Apply Filter
        </button>
      </div>
    </div>
  </div>
</div>
```

## File Structure

Spring Boot automatically serves static content from `src/main/resources/static/`.

```
src/main/resources/
├── static/css/
│   └── patient-registration.css
└── templates/
    └── patient-registration.html
```

## Integration Notes

- Use `th:href="@{/css/patient-registration.css}"` in Thymeleaf layouts.
- Keep Bootstrap CSS and JS imports in the template.
- Load scripts after the page content so modal and table interactions work correctly.

## Customization

Override theme colors:

```css
:root {
  --primary-color: #your-color;
  --primary-light: #your-light-color;
  --primary-dark: #your-dark-color;
}
```

Adjust spacing:

```css
.form-grid {
  gap: 1rem;
}
```

Add new styles if you need additional variants or layout helpers.

## Contributing

When extending this template:

1. Keep component names consistent with the CSS classes in this file.
2. Preserve the responsive behavior on mobile and desktop.
3. Test pages in a Spring Boot environment with Thymeleaf.
4. Update this README when new reusable components are added.

## License

This template is part of the GoSmart BackOffice project and is intended as a reusable base for patient management pages.
