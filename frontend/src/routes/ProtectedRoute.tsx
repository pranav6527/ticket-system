import { Navigate } from 'react-router-dom';
import { useAppSelector } from '../app/hooks';

export const ProtectedRoute = ({ children }: { children: JSX.Element }) => {
  const token = useAppSelector((s) => s.auth.token);
  return token ? children : <Navigate to="/login" replace />;
};
