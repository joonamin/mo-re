import globals from 'globals';
import pluginJs from '@eslint/js';
import tseslint from 'typescript-eslint';
import pluginReact from 'eslint-plugin-react';
import stylisticJs from '@stylistic/eslint-plugin';


/** @type {import('eslint').Linter.Config[]} */
export default [
    {
        ignores: ['node_modules/*'],
    },
    {
        files: ['**/*.{js,mjs,cjs,ts,jsx,tsx}'],
        plugins: {
            '@stylistic': stylisticJs,
        },
        rules: {
            'react/react-in-jsx-scope': 'error',
            'react/jsx-uses-vars': 'error',
            '@stylistic/indent': ['error', 4],
            '@stylistic/arrow-parens': ['error'],
            '@stylistic/semi': ['error'],
            '@stylistic/quotes': [
                'error', 
                'single',
                {
                    allowTemplateLiterals: true,
                },
            ],
            '@stylistic/comma-dangle': ['error','always-multiline'],
            '@stylistic/keyword-spacing': ['error'],
            '@stylistic/space-before-blocks': ['error'],
            '@stylistic/space-infix-ops': ['error'],
            '@stylistic/max-len': ['error', 
                {
                    code: 80, 
                    tabWidth: 4,
                },
            ],
        },
    },
    {languageOptions: { globals: globals.browser }},
    pluginJs.configs.recommended,
    ...tseslint.configs.recommended,
    ...tseslint.configs.stylistic,
    pluginReact.configs.flat.recommended,
    pluginReact.configs.flat['jsx-runtime'],
];