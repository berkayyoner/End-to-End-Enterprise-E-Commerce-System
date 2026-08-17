import { Route, Routes } from 'react-router-dom'
import './App.css'
import { Layout } from './components/Layout.jsx'
import { RequireAuth } from './auth/RequireAuth.jsx'
import { HomePage } from './pages/HomePage.jsx'
import { SignUpPage } from './pages/auth/SignUpPage.jsx'
import { LoginPage } from './pages/auth/LoginPage.jsx'
import { ProfilePage } from './pages/auth/ProfilePage.jsx'
import { IdVerificationPage } from './pages/auth/IdVerificationPage.jsx'
import { SearchPage } from './pages/SearchPage.jsx'

function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route path="/" element={<HomePage />} />
        <Route path="/search" element={<SearchPage />} />
        <Route path="/sign-up" element={<SignUpPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route
          path="/profile"
          element={
            <RequireAuth>
              <ProfilePage />
            </RequireAuth>
          }
        />
        <Route
          path="/id-verification"
          element={
            <RequireAuth>
              <IdVerificationPage />
            </RequireAuth>
          }
        />
      </Route>
    </Routes>
  )
}

export default App
