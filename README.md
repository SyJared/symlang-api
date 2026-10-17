# Symlang API

Symlang is a small educational programming language served by a Java 17 and Spring Boot API. Write a short program in English or Tagalog, send it to `/run`, and receive its printed output, final variables, and any interpreter error as JSON. English remains the default, and Tagalog programs use the same lexer, parser, AST, and tree walking interpreter.

## English and Tagalog

English:

```text
function factorial(n) {
  if (n <= 1) { return 1; }
  else { return n * factorial(n - 1); }
}
let answer = factorial(5);
print("5! =", answer);
```

Tagalog:

```text
gawain factorial(n) {
  kung (n <= 1) { ibalik 1; }
  kundi { ibalik n * factorial(n - 1); }
}
itakda answer = factorial(5);
ilimbag("5! =", answer);
```

## Keywords

| English | Tagalog | Meaning |
| --- | --- | --- |
| `let` | `itakda` | variable declaration |
| `if` | `kung` | conditional |
| `else` | `kundi` | alternate branch |
| `while` | `habang` | loop |
| `function` | `gawain` | function declaration |
| `return` | `ibalik` | return value |
| `true` | `totoo` | true literal |
| `false` | `mali` | false literal |
| `print(...)` | `ilimbag(...)` | built in output function |

Operators and punctuation are shared. Built-in output names depend on the selected language: Tagalog mode does not define `print`.

## Run locally

Requirements: Java 17. Start the API with:

```sh
./mvnw spring-boot:run
```

On Windows, use `mvnw.cmd spring-boot:run`.

## Run with Docker

Build the image and run the API:

```sh
docker build -t symlang-api .
docker run --rm -p 8080:8080 symlang-api
```

## API

Send `POST /run` with a `code` string and optional `lang` (`en` or `tl`). Omitting `lang` selects English.

```json
{
  "lang": "tl",
  "code": "itakda x = 5; ilimbag(x);"
}
```

A successful response contains output and a snapshot of global variables. Errors are returned in the `error` field:

```json
{
  "output": ["5"],
  "variables": {"x": 5},
  "error": null
}
```

Unsupported language codes produce a clear error response. Programs are bounded by an interpreter step limit to prevent an endless loop from holding the request indefinitely.

## Architecture

The request source travels through **Lexer -> Parser -> AST -> Interpreter**. The lexer maps each language's words to shared token types. The parser builds the same AST types for both languages, and the tree walking interpreter evaluates that AST while collecting printed output and global variables.
