# main.py
import sys
from simple_xml import SimpleXml
from board import Board
from multi_solver import solve_with_all_strategies_parallel


def main():
    if len(sys.argv) < 2:
        print(f"Usage: {sys.argv[0]} <levels.xml> [level_number[+]]")
        return

    xml_file = sys.argv[1]
    level_arg = sys.argv[2] if len(sys.argv) >= 3 else None

    # Determine start level and whether to continue after it
    level_to_start = None
    continue_after = False
    if level_arg:
        if level_arg.endswith("+"):
            level_to_start = int(level_arg[:-1])
            continue_after = True
        else:
            level_to_start = int(level_arg)

    with open(xml_file, "r", encoding="utf-8") as f:
        xml_data = f.read()

    try:
        levels = SimpleXml.parse_levels(xml_data)
        print(f"Parsed {len(levels)} levels")
    except Exception as e:
        print(f"Error parsing XML: {e}")
        return

    for level_number, attrs in enumerate(levels):
        if level_to_start is not None:
            if continue_after:
                if level_number < level_to_start:
                    continue
            else:
                if level_number != level_to_start:
                    continue

        color = attrs["color"]
        modifier = attrs["modifier"]
        print(f"\nFinding Level {level_number} Solution...")

        board = Board.from_strings(color, modifier)

        solved_board = solve_with_all_strategies_parallel(board)
        if solved_board:
            print(f"Solution found: {solved_board.move_sequence}")
            print(f"Level {level_number}:")
            print("Completed board:")
            board.display()
        else:
            print("No solution found.")

        print()
        input("# Press Enter to continue after solution...")


if __name__ == "__main__":
    main()
