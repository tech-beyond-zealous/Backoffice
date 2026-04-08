# Database Setup Guide

This README explains how to initialize and configure the GoSmart BackOffice database with essential tables, data, and user permissions.

## Overview

The database setup script performs five main operations:

1. Clear existing data from key tables
2. Insert application systems
3. Create function groups and assign functions
4. Grant user permissions for all modules
5. Create the patient registration table
6. Create the caregiver table

---

## Step 1: Clear Existing Data

Before running the setup, clear all existing data from key tables to start fresh.

⚠️ **WARNING:** This will delete all existing data. Use with caution in production environments.

```sql
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE user_function;
TRUNCATE TABLE `function`;
TRUNCATE TABLE function_group;
TRUNCATE TABLE application_system;

SET FOREIGN_KEY_CHECKS = 1;
```

**What this does:**

- Temporarily disables foreign key constraints to prevent errors during truncation
- Clears user permissions (user_function)
- Clears all functions
- Clears all function groups
- Clears all application systems
- Re-enables foreign key constraints

---

## Step 2: Insert Application Systems

Create entries for the systems that the application manages.

```sql
INSERT INTO application_system (application_system_id, system_code, system_name, remark, status)
VALUES
(1, 'GOSMART', 'GoSmart', ' ', 'A'),
(2, 'MEDICAL', '2nd Medical', ' ', 'A');
```

**Field Descriptions:**

- `application_system_id`: Unique identifier for the system
- `system_code`: Short abbreviation used internally (e.g., GOSMART, MEDICAL)
- `system_name`: Human-readable system name displayed in the UI
- `remark`: Additional notes (optional)
- `status`: 'A' = Active, 'I' = Inactive

**Systems created:**

- **GoSmart** (ID: 1) – Main health management system
- **2nd Medical** (ID: 2) – Secondary medical system for clinics/hospitals

---

## Step 3: Insert Function Groups and Functions

### Function Groups

Define groups that organize functions by feature area.

```sql
INSERT INTO function_group (group_id, application_system_id, group_code, group_name, sort_order, remark, status)
VALUES
(201, 2, 'PATIENT', 'Patients', 2, ' ', 'A'),
(202, 2, 'CGV', 'Caregiver', 3, ' ', 'A'),
(203, 2, 'APPT', 'Appointments', 4, ' ', 'A'),
(204, 2, 'BILL', 'Billing', 5, ' ', 'A'),
(205, 2, 'DASH', 'Dashboard', 6, ' ', 'A');
```

**Field Descriptions:**

- `group_id`: Unique ID for the function group
- `application_system_id`: Links to the system (2 = 2nd Medical)
- `group_code`: Short code for the group (PATIENT, CGV, etc.)
- `group_name`: Display name shown in navigation menus
- `sort_order`: Order in which groups appear in the UI
- `status`: 'A' = Active, 'I' = Inactive

**Groups created:**

- **Patients** (ID: 201) – Patient management features
- **Caregiver** (ID: 202) – Caregiver management features
- **Appointments** (ID: 203) – Appointment scheduling
- **Billing** (ID: 204) – Payment and invoice management
- **Dashboard** (ID: 205) – Analytics and reporting

### Functions

Define individual features and their URL paths.

```sql
INSERT INTO function (function_id, group_id, function_code, function_name, path, sort_order, remark, status)
VALUES
-- Patient
(2001, 201, 'PAT_REG', 'Registration', '/patient/registration', 1, '', 'A'),
(2002, 201, 'PAT_SUB', 'Subscription', '/patient/subscription', 2, '', 'A'),
(2003, 201, 'PAT_MED', 'Medical Record', '/patient/medical-record', 3, '', 'A'),

-- Caregiver
(2004, 202, 'CGV_REG', 'Registration', '/caregiver/registration', 1, ' ', 'A'),

-- Appointment
(2005, 203, 'APPT_REG', 'Registration', '/appointment/registration', 1, '', 'A'),

-- Billing
(2006, 204, 'PAYMENT', 'Payment', '/billing/payment', 1, '', 'A'),

-- Dashboard
(2007, 205, 'ANALYTICS', 'Analytics', '/dashboard/analytics', 1, '', 'A');
```

**Field Descriptions:**

- `function_id`: Unique identifier for the function
- `group_id`: Links to a function group
- `function_code`: Short code for the function (PAT_REG, CGV_REG, etc.)
- `function_name`: Display name in menus
- `path`: URL route in the application (e.g., /patient/registration)
- `sort_order`: Order within its group
- `status`: 'A' = Active, 'I' = Inactive

**Functions created:**

- **PAT_REG** – Patient registration page
- **PAT_SUB** – Patient subscription management
- **PAT_MED** – Patient medical records
- **CGV_REG** – Caregiver registration
- **APPT_REG** – Appointment scheduling
- **PAYMENT** – Billing and payments
- **ANALYTICS** – Dashboard analytics

---

## Step 4: Assign User Permissions

Grant a user full CRUD (Create, Read, Update, Delete) permissions for all functions.

```sql
INSERT INTO user_function
(function_id, user_id, `create`, `edit`, `delete`, `view`, status)
SELECT
    function_id,
    'adriananuarkamal@gmail.com',
    'Y',
    'Y',
    'Y',
    'Y',
    'A'
FROM `function`;
```

**Field Descriptions:**

- `function_id`: Links to a function
- `user_id`: Email or identifier of the user
- `create`: 'Y' = User can create records, 'N' = Cannot create
- `edit`: 'Y' = User can edit records, 'N' = Cannot edit
- `delete`: 'Y' = User can delete records, 'N' = Cannot delete
- `view`: 'Y' = User can view records, 'N' = Cannot view
- `status`: 'A' = Active permission, 'I' = Inactive permission

**Example:** User `adriananuarkamal@gmail.com` gets full access (Y for all operations) to every function.

**To customize permissions:**
Replace the `SELECT` query with explicit INSERT statements:

```sql
INSERT INTO user_function (function_id, user_id, `create`, `edit`, `delete`, `view`, status)
VALUES
(2001, 'adriananuarkamal@gmail.com', 'Y', 'Y', 'Y', 'Y', 'A'),  -- Full access to Patient Registration
(2002, 'adriananuarkamal@gmail.com', 'N', 'N', 'N', 'Y', 'A'),  -- View-only access to Subscription
(2005, 'adriananuarkamal@gmail.com', 'Y', 'Y', 'N', 'Y', 'A');  -- Create/Edit/View only for Appointments
```

---

## Step 5: Create Patient Registration Table

Set up the table that stores all patient information.

```sql
CREATE TABLE patient_registration (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INT,
    gender VARCHAR(10),
    race VARCHAR(50),
    ic_passport_no VARCHAR(20) NOT NULL,
    mobile_no VARCHAR(20),
    emergency_contact_name VARCHAR(100),
    emergency_contact_no VARCHAR(20),
    relationship VARCHAR(50),
    address VARCHAR(255),
    area VARCHAR(100),
    postcode VARCHAR(10),
    city VARCHAR(100),
    has_chronic_disease CHAR(1) DEFAULT 'N',
    chronic_disease VARCHAR(255),
    gosmart_user_id BIGINT,
    create_dt DATETIME DEFAULT CURRENT_TIMESTAMP,
    modify_dt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    modify_by BIGINT,
    status CHAR(1) DEFAULT 'A'
);
```

**Column Descriptions:**

**Basic Information:**

- `id`: Auto-incrementing primary key
- `name`: Patient's full name (required)
- `age`: Patient's age in years
- `gender`: M/F or other designation
- `race`: Ethnicity or race information

**Identity:**

- `ic_passport_no`: National ID or passport number (required, unique identifier)

**Contact:**

- `mobile_no`: Primary phone number
- `emergency_contact_name`: Name of emergency contact person
- `emergency_contact_no`: Emergency contact phone number
- `relationship`: Relationship to emergency contact (e.g., Spouse, Parent, Child)

**Address:**

- `address`: Street address
- `area`: District or neighborhood
- `postcode`: Postal code
- `city`: City name

**Medical:**

- `has_chronic_disease`: 'Y' or 'N' flag
- `chronic_disease`: Details of chronic diseases (comma-separated)

**System:**

- `gosmart_user_id`: Link to the user account in GoSmart
- `create_dt`: Timestamp when record was created (auto-set)
- `modify_dt`: Timestamp when record was last modified (auto-updated)
- `create_by`: User ID who created the record
- `modify_by`: User ID who last modified the record
- `status`: 'A' = Active, 'I' = Inactive, 'D' = Deleted (soft delete)

---

## Step 6: Create Caregiver Table

Set up the table that stores caregiver information.

```sql
CREATE TABLE caregiver (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  medical_provider_id INT NULL,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(100) NOT NULL,
  mobile_number VARCHAR(20) NOT NULL,
  user_id VARCHAR(100) NULL,
  create_dt DATETIME DEFAULT CURRENT_TIMESTAMP,
  create_by VARCHAR(100) NULL,
  modify_dt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  modify_by VARCHAR(100) NULL,
  status char(1) DEFAULT 'A'
);
```

**Column Descriptions:**

- `id`: Auto-incrementing primary key
- `medical_provider_id`: Optional identifier for the medical provider
- `name`: Caregiver's full name (required)
- `email`: Caregiver's email address (required)
- `mobile_number`: Caregiver's primary phone number (required)
- `user_id`: Link to the system user ID (optional)
- `create_dt`: Timestamp when record was created (auto-set)
- `create_by`: User ID (email) who created the record
- `modify_dt`: Timestamp when record was last modified (auto-updated)
- `modify_by`: User ID (email) who last modified the record
- `status`: 'A' = Active, 'I' = Inactive, 'D' = Deleted (soft delete)

---

## Running the Setup

### Option 1: MySQL Command Line

```bash
mysql -u root -p gosmart < setup.sql
```

Replace:

- `gosmart` with your database name
- `setup.sql` with the path to your SQL script file

### Option 2: MySQL Workbench

1. Open MySQL Workbench
2. Connect to your database server
3. Create a new SQL editor tab
4. Paste the entire setup script
5. Click **Execute** (⚡ icon)

### Option 3: Spring Boot JPA/Hibernate

If using JPA with Spring Boot, you can also define entities in Java and use:

```properties
spring.jpa.hibernate.ddl-auto=create-drop
```

in `application.yml` to auto-generate tables. Then insert the data separately.

---

## Verification

After running the setup, verify that all data is correctly inserted:

```sql
-- Check application systems
SELECT * FROM application_system;

-- Check function groups
SELECT * FROM function_group;

-- Check functions
SELECT * FROM `function`;

-- Check user permissions
SELECT * FROM user_function WHERE user_id = 'adriananuarkamal@gmail.com';

-- Check patient registration table structure
DESCRIBE patient_registration;

-- Check caregiver table structure
DESCRIBE caregiver;
```

Expected results:

- 2 application systems
- 5 function groups
- 7 functions
- 7 user_function entries (one per function)
- Patient registration table with all columns

---

## Troubleshooting

### Foreign Key Errors

If you get foreign key constraint errors, ensure the script includes `SET FOREIGN_KEY_CHECKS = 0;` at the start.

### User Email Format

The user_id is stored as text. Use the exact email format consistently throughout the application:

- `adriananuarkamal@gmail.com` ✅
- `adriana@gmail.com` (different user) ❌

### Duplicate Entry Errors

If you get duplicate key errors, run Step 1 (Clear Existing Data) first to truncate tables.

### Table Already Exists

If the `patient_registration` table already exists, use:

```sql
DROP TABLE IF EXISTS patient_registration;
```

before creating it.

---

## Adding New Functions

To add a new function later:

```sql
-- 1. Add the function to the function table
INSERT INTO `function` (function_id, group_id, function_code, function_name, path, sort_order, remark, status)
VALUES (2008, 201, 'PAT_LAB', 'Lab Results', '/patient/lab-results', 4, '', 'A');

-- 2. Grant the user permission
INSERT INTO user_function (function_id, user_id, `create`, `edit`, `delete`, `view`, status)
VALUES (2008, 'adriananuarkamal@gmail.com', 'Y', 'Y', 'Y', 'Y', 'A');
```

---

## Database Diagram

```
application_system (1) ──┐
                          │
                          ├──> function_group (5)
                          │
                          └──> function (7)
                                    │
                                    └──> user_function (permissions)

patient_registration (N records)
```

---

## Notes

- All status fields default to 'A' (Active)
- Timestamps (`create_dt`, `modify_dt`) are automatically managed by the database
- The `has_chronic_disease` field is a simple Y/N flag; details go in `chronic_disease`
- User access is controlled at the function level through the `user_function` table
- The `gosmart_user_id` in `patient_registration` links each patient to a system user

---

## Next Steps

After database setup:

1. Start the Spring Boot application
2. Log in with the configured user account
3. Navigate to Patient Registration (`/patient/registration`)
4. Begin adding patient records
5. Use the CSS framework ([README-CSS.md](README-CSS.md)) for UI styling
