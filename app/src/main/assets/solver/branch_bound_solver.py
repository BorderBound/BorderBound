# branch_bound_solver.py
from board import Board, Position, rows, cols, max_steps
from collections import deque


def branch_bound_solver(initial_board: Board):
    """
    BFS solver with onlyReachableFrom pruning:
    - Processes the queue level by level
    - Skips moves that don't change the board
    - Respects max_steps
    """
    queue = deque([initial_board])
    seen = set()
    seen.add(initial_board.hash())
    steps = 0

    while queue:
        level_size = len(queue)
        print(f"# BFS level {steps}, {level_size} boards in queue")

        if steps >= max_steps:
            print(f"# Reached max_steps ({max_steps}), stopping BFS")
            break

        for _ in range(level_size):
            board = queue.popleft()

            for r in range(rows):
                for c in range(cols):
                    field = board.fields[r][c]
                    if not field.is_clickable():
                        continue

                    # Skip if onlyReachableFrom is set and move is not the allowed one
                    if field.onlyReachableFrom != board.fields[r][
                        c
                    ].onlyReachableFrom and field.onlyReachableFrom != Position(15, 15):
                        # Only allow move if it's the unique reachable source
                        if not (
                            r == field.onlyReachableFrom.row
                            and c == field.onlyReachableFrom.col
                        ):
                            continue

                    new_board = board.copy()
                    changed = new_board.click(r, c)
                    if not changed:
                        continue

                    if new_board.is_solved():
                        return new_board

                    code = new_board.hash()
                    if code not in seen:
                        seen.add(code)
                        queue.append(new_board)

        steps += 1

    return None
