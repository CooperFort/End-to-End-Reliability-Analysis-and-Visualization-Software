# CS 2820 Homework 5 - WARP Code Refactoring

## Project Overview
This project focuses on refactoring the WARP code to enhance modularity, specifically by implementing the `numTxPerLinkAndTotalTxCost()` method in the `ReliabilityAnalysis` class. Additional constructors were added, and existing methods in `WorkLoad.java` were removed or replaced. The project follows object-oriented principles, aiming for clear documentation and maintainable code.

## Contributors
- **Cooper Fort** 
- **Jeff Bates** 

## Key Changes
- **ReliabilityAnalysis Class**: Added constructors and methods to handle dynamic and fixed transmission calculations.
  - `public ReliabilityAnalysis(Double e2e, Double minPacketReceptionRate)`
  - `public ReliabilityAnalysis(Integer numFaults)`
  - `public ArrayList<Integer> numTxPerLinkAndTotalTxCost(Flow flow)`
- **WorkLoad Class**: 
  - Removed `numTxAttemptsPerLinkAndTotalTxAttempts()` and `getFixedTxPerLinkAndTotalTxCost()`.
  - Replaced method calls with `numTxPerLinkAndTotalTxCost()` from `ReliabilityAnalysis`.

## Refactoring Details
### 1. `numTxPerLinkAndTotalTxCost()` Refactoring
- The `numTxPerLinkAndTotalTxCost()` method was moved from `WorkLoad.java` to `ReliabilityAnalysis`.
- It now serves as a public method and handles the same computation as the previously private methods `numTxAttemptsPerLinkAndTotalTxAttempts()` and `getFixedTxPerLinkAndTotalTxCost()`.
- Uses `ArrayList<Integer>` instead of arrays, improving type safety and modularity.
- Employs `ReliabilityRow` and `ReliabilityTable` classes for better structure.
- Implemented by **Jeff Bates**.

### 2. New Constructors
- Two new constructors were added to the `ReliabilityAnalysis` class:
  - `ReliabilityAnalysis(Double e2e, Double minPacketReceptionRate)`
  - `ReliabilityAnalysis(Integer numFaults)`
- These constructors set up the attributes necessary for the refactored method and handle default values.
- Implemented by **Cooper Fort**.

### 3. Refactor Style/Naming/JavaDocs
- The project followed the Google Java Style Guide for naming conventions and code structure.
- Comprehensive JavaDoc comments were added to all new methods, constructors, and updated existing comments.
- Implemented by **Cooper Fort**.

### 4. Class Diagrams
- Updated UML class diagrams to reflect the new methods, constructors, and overall changes.
- Screenshots of the updated diagrams were taken and saved.
- Implemented by **Jeff Bates**.

## UML and JavaDoc
- **UML Diagrams**: Updated to include new methods and constructors in `ReliabilityAnalysis`.
- **JavaDoc**: Generated for all public and private methods.



