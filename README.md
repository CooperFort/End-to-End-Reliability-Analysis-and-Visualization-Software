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
  - Changed 
      
      `ArrayList<Integer> linkTxAndTotalCost = getFixedTxPerLinkAndTotalTxCost(flowNode);`
      `flowNode.linkTxAndTotalCost = linkTxAndTotalCost;`
    
    to 
    
      `ReliabilityAnalysis ra = new ReliabilityAnalysis(e2e, minPacketReceptionRate);`
      `ArrayList<Integer> linkTxAndTotalCost = ra.numTxPerLinkAndTotalTxCost(flowNode);`
      `flowNode.linkTxAndTotalCost = linkTxAndTotalCost;`

  - Changed
      
      `ArrayList<Integer> linkTxAndTotalCost =`
      `numTxAttemptsPerLinkAndTotalTxAttempts(flowNode, e2e, m, true);`
      `flowNode.linkTxAndTotalCost = linkTxAndTotalCost;`

    to
     
      `ReliabilityAnalysis ra = new ReliabilityAnalysis(numFaults);`
      `ArrayList<Integer> linkTxAndTotalCost = ra.numTxPerLinkAndTotalTxCost(flowNode);`
      `flowNode.linkTxAndTotalCost = linkTxAndTotalCost;`
      
- Implemented by **Cooper Fort**.

### 4. Class Diagrams
- Updated UML class diagrams to reflect the new methods, constructors, and overall changes.
- Screenshots of the updated diagrams were taken and saved.
  - `HW5ReliabilityAnalysis.png`
  - `HW5WorkLoad.png`
- Implemented by **Jeff Bates**.

## UML and JavaDoc
- **UML Diagrams**: Updated to include new methods and constructors in `ReliabilityAnalysis`.
- **JavaDoc**: Generated for all public and private methods.



##CS 2820 Sprint1 - 
##Jeff Bates
- responsible for writing and updating the README.md file, outlining your high-level plan and status.

##Cooper Fort 
- Focus on creating the UML Sequence Diagram that shows the program flow starting from the Warp processing the 'ra' option
- Use sequencediagram.org to create the diagram.
- Ensure that the diagram clearly shows the sequence of interactions between program components.

##Colin Miller
- Worked closely with the sequence diagram designer to understand and implement the program logic for processing the 'ra' option in the Warp program.
- Assisted in writing pseudocode snippets for testing and implementing the 'ra' option.
- Document methods and flow in the README documents as needed. 

##Yash Bandla
- Write how the project will be tested and documented. 
- Make sure the plan is clear.

##Graham Besser
- Make a class diagram and a project plan that lists tasks in order
- Describes key methods for the Visualization class





