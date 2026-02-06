module.exports = {
  testEnvironment: 'jsdom',
  setupFilesAfterEnv: ['<rootDir>/src/setupTests.ts'],
  transform: {
    '^.+\\.(ts|tsx)$': 'babel-jest',
  },
  transformIgnorePatterns: ['/node_modules/(?!(nanoid|lucide-react)/)'],
  testPathIgnorePatterns: ['/node_modules/', '<rootDir>/tests/e2e/'],
  moduleFileExtensions: ['js', 'jsx', 'ts', 'tsx'],
};
