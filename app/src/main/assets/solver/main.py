# main.py
import sys
from simple_xml import SimpleXml
from board import Board
from branch_bound_solver import branch_bound_solver


def main():
    if len(sys.argv) != 2:
        print(f"Usage: {sys.argv[0]} <levels.xml>")
        return

    xml_file = sys.argv[1]
    with open(xml_file, "r", encoding="utf-8") as f:
        xml_data = f.read()

    try:
        levels = SimpleXml.parse_levels(xml_data)
        print(f"Parsed {len(levels)} levels")
    except Exception as e:
        print(f"Error parsing XML: {e}")
        return

    for level_number, attrs in enumerate(levels):
        color = attrs["color"]
        modifier = attrs["modifier"]
        print(f"\nLevel {level_number}:")

        board = Board.from_strings(color, modifier)

        solved_board = branch_bound_solver(board)
        if solved_board:
            print(f"Solution found: {solved_board.move_sequence}")

            print("Completed board:")
            board.display()  # make sure Board has a display method
        else:
            print("No solution found.")

        print()
        input("# Press Enter to continue after solution...")


if __name__ == "__main__":
    main()
