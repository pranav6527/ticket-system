import reducer, { setToken, logout } from '../authSlice';

describe('authSlice', () => {
  it('sets token', () => {
    const state = reducer({ token: null }, setToken('abc'));
    expect(state.token).toBe('abc');
  });

  it('clears token on logout', () => {
    const state = reducer({ token: 'abc' }, logout());
    expect(state.token).toBeNull();
  });
});
