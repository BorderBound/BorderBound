# main.py
import sys
from simple_xml import SimpleXml
from board import Board
from multi_solver import solve_with_all_strategies_parallel


def replay_solution(board: Board, solution_str: str):
    b = board.copy()

    for move in solution_str.split(","):
        r, c = parse_move(move)
        changed = b.click(r, c)
        if not changed:
            raise ValueError(f"Invalid move in solution: {move}")

    return b


def parse_move(move: str):
    """
    Converts 'A3' → (row, col)
    """
    col_char = move[0].upper()
    row_num = int(move[1:])

    col = ord(col_char) - ord("A")
    row = row_num - 1

    return row, col


def main():
    if len(sys.argv) < 2:
        print(f"Usage: {sys.argv[0]} <levels.xml> [level_number[+]]")
        return

    xml_file = sys.argv[1]
    level_arg = sys.argv[2] if len(sys.argv) >= 3 else None

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
        existing_solution = attrs.get("solution")

        print(f"\nFinding Level {level_number} Solution...")

        board = Board.from_strings(color, modifier)

        # --- Test existing solution if present ---
        existing_moves = None
        if existing_solution:
            test_board = replay_solution(board, existing_solution)
            if test_board.is_solved():
                existing_moves = len(existing_solution.split(","))
                print(f"# Existing solution: {existing_moves} moves")
            else:
                print("# Existing solution is INVALID")

        # --- Run solver ---
        solved_board = solve_with_all_strategies_parallel(board)

        if not solved_board:
            print("No solution found.")
            continue

        new_moves = solved_board.move_sequence.n
        print(f"# Solver found: {new_moves} moves")

        # --- Compare ---
        if existing_moves is None:
            print("# New solution (no previous solution)")
        elif new_moves < existing_moves:
            print(f"# BETTER solution found ({existing_moves} -> {new_moves})")
        elif new_moves == existing_moves:
            print("# Solution matches existing length")
        else:
            print(f"# Worse than existing ({existing_moves} <- {new_moves})")

        print(f"Solution: {solved_board.move_sequence}")
        print("Completed board:")
        board.display()

        print()
        input("# Press Enter to continue after solution...")


if __name__ == "__main__":
    main()
