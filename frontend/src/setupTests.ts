import '@testing-library/jest-dom';

jest.mock('nanoid', () => ({
  nanoid: () => 'fixed-id',
}));
