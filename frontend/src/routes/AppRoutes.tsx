import { Routes, Route, Navigate } from 'react-router-dom';
import Login from '../features/auth/Login';
import { ProtectedRoute } from './ProtectedRoute';
import Signup from '../features/auth/Signup';
import TicketsList from '../features/tickets/TicketsList';

export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/login" replace />} />
      <Route path="/login" element={<Login />} />
      <Route path="/signup" element={<Signup />} />
      <Route
        path="/tickets"
        element={
          <ProtectedRoute>
            <TicketsList />
          </ProtectedRoute>
        }
      />
    </Routes>
  );
}
