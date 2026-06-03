# MicroJava Compiler

A full compiler implementation for the MicroJava programming language, covering all major phases of a classical compiler pipeline: lexical analysis, syntax analysis, semantic analysis, and code generation for a custom virtual machine (MJVM).

The project demonstrates complete language processing from raw source code to executable bytecode, following strict compiler design principles and a modular, phase-separated architecture.

---

## Key Features

### Lexical Analysis
- Tokenization of MicroJava source code
- Supports identifiers, keywords, constants, operators, and comments
- Whitespace and comment handling
- Detailed lexical error reporting with line and column information

---

### Syntax Analysis
- LALR(1) grammar based on MicroJava language specification
- Full AST construction using parser generator
- Error recovery mechanisms for common syntax errors
- Structured and deterministic parsing process

---

### Semantic Analysis
- Visitor-based traversal of the Abstract Syntax Tree
- Integration with symbol table for scope management
- Type checking and semantic validation
- Detection of:
  - undeclared symbols
  - type mismatches
  - invalid usage of variables and functions

---

### Code Generation
- Translation of validated AST into MJVM bytecode
- Stack-based virtual machine target
- Supports:
  - expressions and assignments
  - control flow (if, for, switch)
  - functions and recursion
  - arrays and basic data structures
- Produces executable `.obj` output

---

## Language Support

MicroJava supports a subset of modern imperative programming features:

- Primitive types and arrays
- Functions and recursion
- Classes, abstract classes, enums
- Control structures:
  - if / else
  - for loops
  - switch-case
- Expressions:
  - arithmetic, logical, relational operators
  - ternary operator (`?:`)

---

## Compiler Architecture

The compiler follows a strict multi-phase pipeline:

```text id="pipeline-final"
Source Code (.mj)
      ↓
Lexical Analysis (JFlex)
      ↓
Syntax Analysis (AST-CUP)
      ↓
Abstract Syntax Tree (AST)
      ↓
Semantic Analysis (Visitor Pattern + Symbol Table)
      ↓
Code Generation (MJVM Bytecode)
      ↓
Executable .obj file
