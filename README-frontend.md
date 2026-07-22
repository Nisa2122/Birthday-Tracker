# Frontend (React / Vite)

This app uses a login page plus a dashboard page. The dashboard loads member birthday data automatically from the SQLite-backed Spring Boot backend.

## Run the demo

1) Start the backend (repo root):

```powershell
cd C:\learning\bdaytracker
.\mvnw.cmd spring-boot:run
```

2) In a second terminal, start the frontend:

```powershell
cd C:\learning\bdaytracker\frontend
npm install
npm run dev
```

The frontend will run on `http://localhost:5174` and proxy `/api` requests to `http://localhost:8082`.

3) Open the UI in your browser:

```powershell
start http://localhost:5174
```

## Login credentials

- Username: `admin`
- Password: `password`

## What the app now includes

- Login page and dashboard page
- Member list with weekend birthdays highlighted
- Today / This week / This month counts
- Editable member form with add/update
- Create banner button with a funny quote
- Data persistence via SQLite
- Auto-loading data from the database on each refresh

## Single-step launch

You can also use the included helper script from repo root:

```powershell
.\run-demo.ps1
```

This opens one window for backend and one for frontend.
