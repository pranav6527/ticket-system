import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';
import Signup from '../Signup';

const mockSignup = jest.fn();
let mockSignupState: { isLoading: boolean; error?: unknown } = {
  isLoading: false,
  error: undefined,
};

jest.mock('../authApi', () => ({
  useSignupMutation: () => [mockSignup, mockSignupState],
}));

const mockNavigate = jest.fn();

jest.mock('react-router-dom', () => {
  const actual = jest.requireActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

const renderSignup = () =>
  render(
    <MemoryRouter>
      <Signup />
    </MemoryRouter>
  );

describe('Signup', () => {
  beforeEach(() => {
    mockSignup.mockReset();
    mockNavigate.mockReset();
    mockSignupState = { isLoading: false, error: undefined };
  });

  it('shows email validation error', async () => {
    renderSignup();

    const user = userEvent.setup();
    await user.type(screen.getByPlaceholderText('Password'), 'Pass123!');
    await user.click(screen.getByRole('button', { name: 'Sign Up' }));

    expect(
      screen.getByText('Please enter a valid email address')
    ).toBeInTheDocument();
    expect(mockSignup).not.toHaveBeenCalled();
  });

  it('submits signup and navigates after success', async () => {
    jest.useFakeTimers();
    const unwrapMock = jest.fn().mockResolvedValue({ accessToken: 'token' });
    mockSignup.mockReturnValue({ unwrap: unwrapMock });

    renderSignup();

    const user = userEvent.setup({ advanceTimers: jest.advanceTimersByTime });
    await user.type(
      screen.getByPlaceholderText('Email address'),
      'user@example.com'
    );
    await user.type(screen.getByPlaceholderText('Password'), 'Pass123!');

    await user.click(screen.getByRole('button', { name: 'Sign Up' }));

    await waitFor(() =>
      expect(mockSignup).toHaveBeenCalledWith({
        email: 'user@example.com',
        password: 'Pass123!',
      })
    );

    jest.advanceTimersByTime(500);

    await waitFor(() =>
      expect(mockNavigate).toHaveBeenCalledWith('/tickets')
    );

    jest.useRealTimers();
  });

  it('shows api error message when signup fails', () => {
    mockSignupState = { isLoading: false, error: { status: 400 } };

    renderSignup();

    expect(
      screen.getByText('Signup failed. Please try again.')
    ).toBeInTheDocument();
  });
});
