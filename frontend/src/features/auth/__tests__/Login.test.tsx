import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';
import Login from '../Login';

const mockLogin = jest.fn();
let mockLoginState: { isLoading: boolean; error?: unknown } = {
  isLoading: false,
  error: undefined,
};

jest.mock('../authApi', () => ({
  useLoginMutation: () => [mockLogin, mockLoginState],
}));

const mockNavigate = jest.fn();

jest.mock('react-router-dom', () => {
  const actual = jest.requireActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

const renderLogin = () =>
  render(
    <MemoryRouter>
      <Login />
    </MemoryRouter>
  );

describe('Login', () => {
  beforeEach(() => {
    mockLogin.mockReset();
    mockNavigate.mockReset();
    mockLoginState = { isLoading: false, error: undefined };
  });

  it('shows validation errors when fields are empty', async () => {
    renderLogin();

    const user = userEvent.setup();
    await user.click(screen.getByRole('button', { name: 'Login' }));

    expect(screen.getByText('Email is required')).toBeInTheDocument();
    expect(screen.getByText('Password is required')).toBeInTheDocument();
    expect(mockLogin).not.toHaveBeenCalled();
  });

  it('submits credentials and navigates on success', async () => {
    const unwrapMock = jest.fn().mockResolvedValue({ accessToken: 'token' });
    mockLogin.mockReturnValue({ unwrap: unwrapMock });

    renderLogin();

    const user = userEvent.setup();
    await user.type(
      screen.getByPlaceholderText('Email address'),
      'user@example.com'
    );
    await user.type(screen.getByPlaceholderText('Password'), 'Pass123!');

    await user.click(screen.getByRole('button', { name: 'Login' }));

    await waitFor(() =>
      expect(mockLogin).toHaveBeenCalledWith({
        email: 'user@example.com',
        password: 'Pass123!',
      })
    );

    await waitFor(() =>
      expect(mockNavigate).toHaveBeenCalledWith('/tickets')
    );
  });

  it('shows api error message when login fails', () => {
    mockLoginState = { isLoading: false, error: { status: 400 } };

    renderLogin();

    expect(
      screen.getByText('Invalid email or password')
    ).toBeInTheDocument();
  });
});
