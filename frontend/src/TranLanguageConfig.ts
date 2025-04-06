export const tran = {
  defaultToken: "",
  tokenPostfix: ".custom",

  keywords: [
    "accessor", "mutator", "implements", "class", "interface", "loop", 
    "if", "else", "true", "false", "new", "private", "shared", 
    "construct", "null", "and", "or", "not"
  ],

  operators: [
    "=", "==", "!=", "<", ">", "<=", ">=", "+", "-", "*", "/", 
    "%", ":", ".", ","
  ],

  symbols: /[=><!~?:&|+\-*\/^%]+/,
  escapes: /\\(?:[abfnrtv\\"']|x[0-9A-Fa-f]{1,4}|u\{[0-9A-Fa-f]{1,6}\})/,

  tokenizer: {
    root: [
      // Keywords
      [/\b(accessor|mutator|implements|class|interface|loop|if|else|new|private|shared|construct)\b/, "keyword"],

      // Operators and Symbols
      [/[=]=?|!=|[<>]=?|[+\-*/%:.,]/, "operator"],

      // Parentheses
      [/[()]/, "@brackets"],

      // Numbers
      [/\b\d+(\.\d+)?\b/, "number"],

      // Strings
      [/"/, { token: "string.quote", next: "@string" }],
      [/'/, { token: "string.quote", next: "@stringSingle" }],

      // Multi-line Comments
      [/\{/, { token: "comment", next: "@comment" }],
    ],

    // String handling
    string: [
      [/[^\\"]+/, "string"],
      [/\\./, "string.escape"],
      [/"/, { token: "string.quote", next: "@pop" }],
    ],
    stringSingle: [
      [/[^\\']+/, "string"],
      [/\\./, "string.escape"],
      [/'/, { token: "string.quote", next: "@pop" }],
    ],

    // Multi-line comments
    comment: [
      [/[^{}]+/, "comment"],
      [/\{/, { token: "comment", next: "@comment" }],
      [/\}/, { token: "comment", next: "@pop" }],
      [/./, "comment"]
    ],
  },
};