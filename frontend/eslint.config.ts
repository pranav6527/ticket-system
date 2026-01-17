import js from '@eslint/js';
import tseslint from 'typescript-eslint';
import reactPlugin from 'eslint-plugin-react';
import reactHooks from 'eslint-plugin-react-hooks';
import unusedImports from 'eslint-plugin-unused-imports';

export default tseslint.config(
  {
    ignores: ['dist', 'node_modules', 'build', 'coverage/**', '*.cjs'],
  },

  // Base JS & TS Configs
  js.configs.recommended,
  ...tseslint.configs.recommended,

  {
    files: ['**/*.{ts,tsx,js,jsx}'],
    plugins: {
      react: reactPlugin,
      'react-hooks': reactHooks,
      'unused-imports': unusedImports,
    },
    languageOptions: {
      parserOptions: {
        ecmaFeatures: { jsx: true },
        sourceType: 'module',
      },
    },
    settings: {
      react: {
        version: 'detect',
        runtime: 'automatic', // <--- THIS IS THE KEY FIX
      },
    },
    rules: {
      // React (JSX runtime aware)
      ...reactPlugin.configs.flat.recommended.rules,
      ...reactPlugin.configs.flat['jsx-runtime'].rules,

      'react/react-in-jsx-scope': 'off',
      'react/jsx-uses-react': 'off',

      // Hooks
      ...reactHooks.configs.recommended.rules,

      // Disable default unused rules
      'no-unused-vars': 'off',
      '@typescript-eslint/no-unused-vars': 'off',

      // Enable auto-fix unused imports
      'unused-imports/no-unused-imports': 'error',
      'unused-imports/no-unused-vars': [
        'warn',
        {
          vars: 'all',
          varsIgnorePattern: '^_',
          args: 'after-used',
          argsIgnorePattern: '^_',
        },
      ],
    },
  }
);
