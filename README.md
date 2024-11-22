# CS 2820 Sprint 2

## Project Overview
This project focuses on creating code to analyze and visualize the reliability of end-to-end message transmissions in the WARP system. In Sprint 2, the primary objective was to complete or make significant progress on the **ReliabilityVisualization** class, ensuring it integrates with the `-gui` and `-ra` options, and adheres to the defined project specifications.

---

## Sprint 2 Deliverables

1. **Code Updates**:
   - Implemented methods in `ReliabilityVisualization` and `VisualizationImplementation` for data creation, file output, and GUI-based visualization.
   - Added high-level helper methods for stubbed-out functionality with JavaDoc comments to guide future implementation.
   - Ensured compatibility with the `-gui` option, leveraging `ProgramVisualization` logic.
   - Began implementing end-to-end reliability calculations (`createVisualizationData()`).

2. **UML Diagrams**:
   - Updated the sequence diagram to reflect new and modified methods.
   - Adjusted the class diagram to include newly added or stubbed-out methods and their visibility (public/protected).

3. **JUnit Tests**:
   - Created unit tests for all public/protected methods in `ReliabilityVisualization`, ensuring partial functionality is validated.

4. **Documentation**:
   - JavaDoc comments added for all newly created or updated methods.
   - Updated project documentation files with instructions for Sprint 2 progress.

---

## Team Member Contributions

### Jeff Bates
- Created JUnit tests to cover edge cases for all methods in **ReliabilityVisualization** to ensure stability.
- Tests were created for the following methods:
  - `ReliabilityVisualization.getReliabilities()`
  - `createVisualizationData()`
  - `createColumnHeader()`
  - `saveVisualization()`

### Cooper Fort
- Updated sequence diagram using sequencediagram.org to include new methods and saved the diagram in the **ARTIFACTS** folder.
- Updated method calls in:
  - `ReliabilityVisualization.getReliabilities`
  - `WorkLoad.getFlowNamesInPriorityOrder`
  - `WorkLoad.getNodesInFlow`
  - `createHeader`
  - `createColumnHeader`
  - `createVisualizationData`
  - `saveVisualization`

### Colin Miller
- Worked on refactoring and cleaning up code for **ReliabilityVisualization** to improve readability and adhere to the style guide.
- Implemented logging to improve debugging for `createVisualizationData()` and `saveVisualization()` methods.

### Yash Bandla
- Responsible for updating the **README.md** file with high-level plans for improving the **ReliabilityVisualization** class.
- Clearly outlined tasks assigned to each team member and explained all artifacts in detail.

### Graham Besser
- Created comprehensive JavaDoc comments for all methods in **ReliabilityVisualization**.
- Updated documentation files to describe each method's purpose, parameters, and expected outputs at a high level.

---

## ARTIFACTS
1. **CS2820_Project ToDo**:
   - Updated with Sprint 2 tasks for each team member, ensuring clear deliverables and deadlines.

2. **Sprint 2.pdf**:
   - Documented plans for completing Sprint 2 tasks.
   - Included details on how the team will stay organized and the modifications made to:
     - UML Sequence Diagrams
     - UML Class Diagrams
     - **ReliabilityVisualization** code.

3. **WARP Sequence Diagram (SequenceDiagramRaw and Sprint2 Image1-4.PNG)**:
   - Updated to reflect the post-**ReliabilityVisualization** code changes.
   - Highlights the new flow of methods and interactions in the WARP codebase.

---

## Plan for Sprint 3

### Objectives:
- Complete all unfinished methods in `ReliabilityVisualization` and `ReliabilityAnalysis`.
- Finalize JUnit tests for `ReliabilityAnalysis`.
- Implement error handling and ensure smooth user experience for both `-gui` and `-ra` options.
- Integrate all modules for end-to-end functionality.

---

## How to Run the Program

1. Compile the project in Eclipse.
2. Use the following commands to test:
   - For GUI visualization: `java Warp gui`
   - For reliability analysis visualization: `java Warp ra`
3. Ensure all `.ra` input files are available in the correct directory.








