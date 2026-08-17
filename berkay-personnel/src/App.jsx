import { Route, Routes } from 'react-router-dom'
import './App.css'
import { Layout } from './components/Layout.jsx'
import { RequireAuth } from './auth/RequireAuth.jsx'
import { HomePage } from './pages/HomePage.jsx'
import { LoginPage } from './pages/auth/LoginPage.jsx'
import { CategoriesPage } from './pages/CategoriesPage.jsx'
import { CategoryChangeRequestsPage } from './pages/CategoryChangeRequestsPage.jsx'
import { ProductsPage } from './pages/ProductsPage.jsx'

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
      </Route>
    </Routes>
  )
}

export default App
