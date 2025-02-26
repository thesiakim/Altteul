module.exports = {
  env: {
    browser: true,
    es2021: true,
  },
  parser: "@typescript-eslint/parser",
  plugins: ["@typescript-eslint", "prettier"],
  extends: [
    "airbnb",
    "airbnb/hooks",
    "plugin:@typescript-eslint/recommended",
    "plugin:prettier/recommended", // Prettier 설정을 ESLint와 함께 적용
  ],
  rules: {
    "no-var": "warn",
    eqeqeq: "warn",
    "react/prop-types": "off",
    "no-extra-semi": "error",
    "react/jsx-filename-extension": [
      2,
      { extensions: [".js", ".jsx", ".ts", ".tsx"] },
    ],
    "arrow-parens": ["warn", "avoid"], // Prettier와 일관성 유지
    "no-unused-vars": "off",
    "no-console": "off",
    "import/prefer-default-export": "off",
    "react-hooks/exhaustive-deps": "warn",
    "react/jsx-pascal-case": "warn",
    "react/jsx-key": "warn",
    "no-debugger": "off",
    "prettier/prettier": ["error", { endOfLine: "lf" }], // Prettier와 일관되게 설정
    "react/function-component-definition": [
      2,
      { namedComponents: ["arrow-function", "function-declaration"] },
    ],
    "react/react-in-jsx-scope": "off",
    "react/prefer-stateless-function": "off",
    "react/jsx-one-expression-per-line": "off",
    "no-nested-ternary": "off",
    "react/jsx-curly-brace-presence": [
      "warn",
      { props: "never", children: "always" },
    ], // 불필요한 중괄호 금지
    "import/no-unresolved": ["error", { caseSensitive: false }],
    "react/jsx-props-no-spreading": ["warn", { custom: "ignore" }],
    "linebreak-style": "off",
    "import/extensions": "off",
    "no-use-before-define": "off",
    "import/no-extraneous-dependencies": "off",
    "no-shadow": "off",
    "jsx-a11y/no-noninteractive-element-interactions": "off",
  },
  settings: {
    "import/resolver": {
      node: {
        extensions: [".js", ".jsx", ".ts", ".tsx"],
      },
    },
  },
};
