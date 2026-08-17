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
import { ProductDetailPage } from './pages/ProductDetailPage.jsx'
import { ComingSoonPage } from './pages/ComingSoonPage.jsx'
import { AddProductPage } from './pages/AddProductPage.jsx'
import { MyProductsPage } from './pages/MyProductsPage.jsx'
import { SellerProfilePage } from './pages/SellerProfilePage.jsx'
import { BasketPage } from './pages/BasketPage.jsx'
import { CheckoutPage } from './pages/CheckoutPage.jsx'
import { MyOrdersPage } from './pages/MyOrdersPage.jsx'
import { OrderDetailsPage } from './pages/OrderDetailsPage.jsx'
import { MyReviewsPage } from './pages/MyReviewsPage.jsx'
import { MyCouponsPage } from './pages/MyCouponsPage.jsx'
import { SellerMessagesPage } from './pages/SellerMessagesPage.jsx'
import { MyUserInfoPage } from './pages/MyUserInfoPage.jsx'
import { MyFavoritesPage } from './pages/MyFavoritesPage.jsx'
import { CampaignsPage } from './pages/CampaignsPage.jsx'

function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route path="/" element={<HomePage />} />
        <Route path="/search" element={<SearchPage />} />
        <Route path="/campaigns" element={<CampaignsPage />} />
        <Route path="/products/:id" element={<ProductDetailPage />} />
        <Route path="/coming-soon" element={<ComingSoonPage />} />
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
        <Route
          path="/add-product"
          element={
            <RequireAuth>
              <AddProductPage />
            </RequireAuth>
          }
        />
        <Route
          path="/my-products"
          element={
            <RequireAuth>
              <MyProductsPage />
            </RequireAuth>
          }
        />
        <Route path="/sellers/:id" element={<SellerProfilePage />} />
        <Route
          path="/basket"
          element={
            <RequireAuth>
              <BasketPage />
            </RequireAuth>
          }
        />
        <Route
          path="/checkout"
          element={
            <RequireAuth>
              <CheckoutPage />
            </RequireAuth>
          }
        />
        <Route
          path="/my-orders"
          element={
            <RequireAuth>
              <MyOrdersPage />
            </RequireAuth>
          }
        />
        <Route
          path="/order-details/:orderId"
          element={
            <RequireAuth>
              <OrderDetailsPage />
            </RequireAuth>
          }
        />
        <Route
          path="/my-reviews"
          element={
            <RequireAuth>
              <MyReviewsPage />
            </RequireAuth>
          }
        />
        <Route
          path="/my-coupons"
          element={
            <RequireAuth>
              <MyCouponsPage />
            </RequireAuth>
          }
        />
        <Route
          path="/seller-messages"
          element={
            <RequireAuth>
              <SellerMessagesPage />
            </RequireAuth>
          }
        />
        <Route
          path="/my-user-info"
          element={
            <RequireAuth>
              <MyUserInfoPage />
            </RequireAuth>
          }
        />
        <Route
          path="/my-favorites"
          element={
            <RequireAuth>
              <MyFavoritesPage />
            </RequireAuth>
          }
        />
      </Route>
    </Routes>
  )
}

export default App
