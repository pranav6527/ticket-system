import { useState, useEffect } from 'react';
import { useSignupMutation } from './authApi';
import { Link, useNavigate } from 'react-router-dom';
import {
  ShieldCheck,
  Eye,
  EyeOff,
  Mail,
  Lock,
  Loader2,
  Check,
  X,
  AlertCircle,
} from 'lucide-react';

export default function Signup() {
  const navigate = useNavigate();
  const [signup, { isLoading, error: apiError }] = useSignupMutation();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [isPasswordTouched, setIsPasswordTouched] = useState(false);
  const [emailError, setEmailError] = useState('');

  // Password Requirements Logic
  const passwordRequirements = [
    { label: 'Min 5 chars', valid: password.length >= 5 },
    { label: 'Uppercase (A-Z)', valid: /[A-Z]/.test(password) },
    { label: 'Number (0-9)', valid: /[0-9]/.test(password) },
    {
      label: 'Special Char (!@#$)',
      valid: /[!@#$%^&*(),.?":{}|<>]/.test(password),
    },
  ];

  const isPasswordValid = passwordRequirements.every((req) => req.valid);

  const validateEmail = (emailStr: string) => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(emailStr);
  };

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsPasswordTouched(true);

    // Validate Email
    if (!validateEmail(email)) {
      setEmailError('Please enter a valid email address');
      return;
    } else {
      setEmailError('');
    }

    // Validate Password
    if (!isPasswordValid) {
      return; // The UI already shows what is wrong
    }

    try {
      await signup({ email, password }).unwrap();
      setTimeout(() => {
        navigate('/tickets');
      }, 500);
    } catch (err) {
      console.error('Signup failed', err);
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-gray-50 px-4 py-12 sm:px-6 lg:px-8">
      <div className="w-full max-w-md space-y-8 rounded-2xl border border-gray-100 bg-white p-8 shadow-xl">
        {/* Header */}
        <div className="flex flex-col items-center">
          <div className="mb-4 flex h-16 w-16 items-center justify-center rounded-full bg-green-100">
            <ShieldCheck className="h-8 w-8 text-green-600" />
          </div>
          <h2 className="text-center text-3xl font-bold tracking-tight text-gray-900">
            Create Account
          </h2>
          <p className="mt-2 text-center text-sm text-gray-600">
            Join us to manage your tickets efficiently
          </p>
        </div>

        <form onSubmit={onSubmit} className="mt-8 space-y-6">
          <div className="space-y-4">
            {/* Email Input */}
            <div>
              <div className="relative">
                <div className="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3">
                  <Mail
                    className={`h-5 w-5 ${emailError ? 'text-red-400' : 'text-gray-400'}`}
                  />
                </div>
                <input
                  id="email"
                  type="email"
                  placeholder="Email address"
                  className={`block w-full rounded-lg border bg-gray-50 py-3 pl-10 pr-3 text-gray-900 placeholder-gray-400 transition-all duration-200 focus:bg-white focus:outline-none focus:ring-1 sm:text-sm ${
                    emailError
                      ? 'border-red-300 focus:border-red-500 focus:ring-red-500'
                      : 'border-gray-300 focus:border-green-500 focus:ring-green-500'
                  }`}
                  value={email}
                  onChange={(e) => {
                    setEmail(e.target.value);
                    if (emailError) setEmailError('');
                  }}
                />
              </div>
              {emailError && (
                <p className="mt-1 flex items-center text-xs text-red-600">
                  <AlertCircle className="mr-1 h-3 w-3" /> {emailError}
                </p>
              )}
            </div>

            {/* Password Input */}
            <div>
              <div className="relative">
                <div className="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3">
                  <Lock className="h-5 w-5 text-gray-400" />
                </div>
                <input
                  id="password"
                  type={showPassword ? 'text' : 'password'}
                  placeholder="Password"
                  className={`block w-full rounded-lg border bg-gray-50 py-3 pl-10 pr-10 text-gray-900 placeholder-gray-400 transition-all duration-200 focus:bg-white focus:outline-none focus:ring-1 sm:text-sm ${
                    isPasswordTouched && !isPasswordValid
                      ? 'border-red-300 focus:border-red-500 focus:ring-red-500'
                      : 'border-gray-300 focus:border-green-500 focus:ring-green-500'
                  }`}
                  value={password}
                  onFocus={() => setIsPasswordTouched(true)}
                  onChange={(e) => setPassword(e.target.value)}
                />
                <button
                  type="button"
                  className="absolute inset-y-0 right-0 flex items-center pr-3 text-gray-400 hover:text-gray-600 focus:outline-none"
                  onClick={() => setShowPassword(!showPassword)}
                >
                  {showPassword ? (
                    <EyeOff className="h-5 w-5" />
                  ) : (
                    <Eye className="h-5 w-5" />
                  )}
                </button>
              </div>

              {/* Password Requirements Checklist - Always visible when typing or touched */}
              {(isPasswordTouched || password.length > 0) && (
                <div className="mt-3 rounded-lg bg-gray-50 p-3">
                  <p className="mb-2 text-xs font-medium text-gray-500">
                    Password must contain:
                  </p>
                  <div className="grid grid-cols-2 gap-2">
                    {passwordRequirements.map((req, index) => (
                      <div
                        key={index}
                        className={`flex items-center text-xs transition-colors duration-200 ${req.valid ? 'font-medium text-green-600' : 'text-gray-500'}`}
                      >
                        {req.valid ? (
                          <Check className="mr-1.5 h-3.5 w-3.5" />
                        ) : (
                          <div className="mr-1.5 h-3.5 w-3.5 rounded-full border border-gray-400" />
                        )}
                        {req.label}
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </div>
          </div>

          {/* API Error Message */}
          {apiError && (
            <div className="flex items-center rounded-md border border-red-100 bg-red-50 p-3 text-sm text-red-600">
              <AlertCircle className="mr-2 h-4 w-4" />
              Signup failed. Please try again.
            </div>
          )}

          {/* Submit Button */}
          <button
            type="submit"
            disabled={isLoading}
            className="group flex w-full items-center justify-center rounded-lg bg-green-600 px-4 py-3 text-sm font-semibold text-white transition-all hover:bg-green-700 hover:shadow-lg hover:shadow-green-600/20 focus:outline-none focus:ring-2 focus:ring-green-500 focus:ring-offset-2 disabled:opacity-70 disabled:hover:shadow-none"
          >
            {isLoading ? (
              <>
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                Creating Account...
              </>
            ) : (
              'Sign Up'
            )}
          </button>
        </form>

        <div className="text-center text-sm">
          <span className="text-gray-500">Already have an account? </span>
          <Link
            to="/login"
            className="font-medium text-green-600 transition-colors hover:text-green-500 hover:underline"
          >
            Log in
          </Link>
        </div>
      </div>
    </div>
  );
}
