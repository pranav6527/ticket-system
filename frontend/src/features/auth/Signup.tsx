import { useState } from 'react';
import { useSignupMutation } from './authApi';
import { Link, useNavigate } from 'react-router-dom';

export default function Signup() {
  const navigate = useNavigate();
  const [signup, { isLoading, error }] = useSignupMutation();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await signup({ email, password }).unwrap();
      navigate('/tickets');
    } catch (_) {}
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-gray-100 px-4">
      <div className="w-full max-w-md rounded-xl bg-white p-6 shadow">
        <h1 className="mb-6 text-center text-2xl font-semibold">
          Create Account
        </h1>

        <form onSubmit={onSubmit} className="space-y-4">
          <input
            type="email"
            placeholder="Email"
            className="w-full rounded-lg border px-4 py-2 focus:outline-none focus:ring"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />

          <input
            type="password"
            placeholder="Password"
            className="w-full rounded-lg border px-4 py-2 focus:outline-none focus:ring"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />

          {error && <p className="text-sm text-red-600">Signup failed</p>}

          <button
            type="submit"
            disabled={isLoading}
            className="w-full rounded-lg bg-green-600 py-2 text-white transition hover:bg-green-700 disabled:opacity-50"
          >
            {isLoading ? 'Creating...' : 'Sign Up'}
          </button>
        </form>

        <p className="mt-4 text-center text-sm">
          Already have an account?{' '}
          <Link to="/login" className="text-blue-600 hover:underline">
            Login
          </Link>
        </p>
      </div>
    </div>
  );
}
