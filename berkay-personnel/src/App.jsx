import { Route, Routes } from 'react-router-dom'
import './App.css'
import { Layout } from './components/Layout.jsx'
import { RequireAuth } from './auth/RequireAuth.jsx'
import { HomePage } from './pages/HomePage.jsx'
import { LoginPage } from './pages/auth/LoginPage.jsx'
import { CategoriesPage } from './pages/CategoriesPage.jsx'
import { CategoryChangeRequestsPage } from './pages/CategoryChangeRequestsPage.jsx'
import { ProductsPage } from './pages/ProductsPage.jsx'
import { PersonnelPage } from './pages/PersonnelPage.jsx'
import { UsersPage } from './pages/UsersPage.jsx'
import { IdApplicationsPage } from './pages/IdApplicationsPage.jsx'
import { SellerApplicationsPage } from './pages/SellerApplicationsPage.jsx'
import { UserLogsPage } from './pages/UserLogsPage.jsx'
import { PersonnelLogsPage } from './pages/PersonnelLogsPage.jsx'

function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route path="/login" element={<LoginPage />} />
        <Route
          path="/"
          element={
            <RequireAuth>
              <HomePage />
            </RequireAuth>
          }
        />
        <Route
          path="/categories"
          element={
            <RequireAuth>
              <CategoriesPage />
            </RequireAuth>
          }
        />
        <Route
          path="/category-change-requests"
          element={
            <RequireAuth>
              <CategoryChangeRequestsPage />
            </RequireAuth>
          }
        />
        <Route
          path="/products"
          element={
            <RequireAuth>
              <ProductsPage />
            </RequireAuth>
          }
        />
        <Route
          path="/personnel"
          element={
            <RequireAuth>
              <PersonnelPage />
            </RequireAuth>
          }
        />
        <Route
          path="/users"
          element={
            <RequireAuth>
              <UsersPage />
            </RequireAuth>
          }
        />
        <Route
          path="/id-applications"
          element={
            <RequireAuth>
              <IdApplicationsPage />
            </RequireAuth>
          }
        />
        <Route
          path="/seller-applications"
          element={
            <RequireAuth>
              <SellerApplicationsPage />
            </RequireAuth>
          }
        />
        <Route
          path="/user-logs"
          element={
            <RequireAuth>
              <UserLogsPage />
            </RequireAuth>
          }
        />
        <Route
          path="/personnel-logs"
          element={
            <RequireAuth>
              <PersonnelLogsPage />
            </RequireAuth>
          }
        />
      </Route>
    </Routes>
  )
}

export default App
