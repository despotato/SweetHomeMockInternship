# Mock internship: implementation and review

Prepared with ChatGPT assistance. Review the changes and be able to explain them before submitting, as required by ASSIGNMENT.md.

## Task 2: editable descriptions

HomeFurnitureController.isPropertyEditable no longer rejects DESCRIPTION. The existing HomeFurniturePanel field binding and controller save/undo code already handle the description; no duplicate save path was added. Price remains read-only.

## Task 3: calculated volume

PieceOfFurniture exposes getVolume(); catalog and placed furniture calculate width * depth * height from their current dimensions, rather than storing a value that could become stale. HomePieceOfFurniture adds a VOLUME sorting comparator. A new Home displays the column by default; existing saved homes keep their saved columns.

HomeView and HomeController expose/enable the two Volume actions. HomePane maps those actions to the existing furniture sorting and column toggling methods. Its shared menu-building methods supply both the menu bar and context menu. FurnitureTable adds the header, renderer, clipboard/export value and column width. English labels and tooltips are in swing/package.properties. Number formatting intentionally follows the existing dimension renderer, as required by the assignment, even though the quantity is a volume.

## Task 4: printing all levels

PlanComponent maps each nonnegative plan page index to a viewable level in home order. It temporarily selects that level and fits its plan onto one page. A finally block restores the original selection on success, an invalid page, or a rendering exception. Empty viewable levels still get a page. Homes with no levels keep their original scaling and pagination.

HomePrintableComponent prints furniture across all viewable levels instead of filtering to the selected level. It restores the original furniture filter in a finally block. FurnitureTable builds a separate printable column model, appends Level if needed, and restores the original column model and grid color in a finally block. It does not add Level to the home's on-screen visible columns.

## Verification

- Java 11 / Maven: mvn clean test passes all 6 supplied tests.
- Twelve additional checks cover catalog volume, ascending/descending sorting, re-sorting after resizing, toggling the column, hidden levels, invalid/nonsequential page requests, render failures, empty levels, and table-state restoration after a deliberate PrinterException.
- Real Swing application screenshots were captured in a Linux virtual display and visually inspected. The description was saved and checked on the furniture object.
- The sample print output has 4 pages: all-level furniture including Level, then Level 0, Level 1, Level 2. The selected level and visible columns were checked before and after PDF output.
- Provided assignment-tests were not modified.

## Run the additional checks

From the repository root after mvn test (on Windows, use ; instead of : in classpaths):

```sh
javac -cp 'target/classes:SweetHome3D/lib/*:SweetHome3D/libtest/*' development-tests/FeatureRegressionChecks.java
java -cp 'development-tests:target/classes:SweetHome3D/lib/*:SweetHome3D/libtest/*' FeatureRegressionChecks
```

Use JDK 8 on macOS, or JDK 8/11 on Windows/Linux, following QUICK_START.md. Confirm the application also launches in your own development setup.

## Questions to check your understanding

1. Why is calculated volume safer than a stored volume field after resizing?
2. Which existing code saves descriptions once the field is editable?
3. How do the menu and context menu invoke the same sorting action?
4. Why must selected level, furniture filter, and column model be restored in finally blocks?
5. Why is Level appended to a printable column model instead of the home's visible property list?
