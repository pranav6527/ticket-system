import { Route, Routes, MemoryRouter } from 'react-router-dom';
import { renderWithProviders } from '../../test-utils';
import { ProtectedRoute } from '../ProtectedRoute';

describe('ProtectedRoute', () => {
  const renderAt = (token: string | null) =>
    renderWithProviders(
      <MemoryRouter initialEntries={['/protected']}>
        <Routes>
          <Route
            path="/protected"
            element={
              <ProtectedRoute>
                <div>Secret Content</div>
              </ProtectedRoute>
            }
          />
          <Route path="/login" element={<div>Login Page</div>} />
        </Routes>
      </MemoryRouter>,
      {
        preloadedState: {
          auth: { token },
        },
      }
    );

  it('redirects to login when token is missing', () => {
    const { getByText } = renderAt(null);
    expect(getByText('Login Page')).toBeInTheDocument();
  });

  it('renders protected content when token is present', () => {
    const { getByText } = renderAt('test.token.signature');
    expect(getByText('Secret Content')).toBeInTheDocument();
  });
});
