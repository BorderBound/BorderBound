# branch_bound_solver.py
from board import Board, Position, rows, cols, max_steps
import heapq
import itertools


def branch_bound_solver(initial_board: Board):
    """
    A* solver for Board with max_steps limit:
    - Prioritizes boards with fewer steps + heuristic
    - Uses onlyReachableFrom pruning
    """
    heap = []
    counter = itertools.count()
    seen = set()

    def heuristic(board: Board):
        wrong = 0
        for row in board.fields:
            for f in row:
                if not f.is_correct():
                    wrong += 1
        return wrong

    initial_priority = heuristic(initial_board)
    heapq.heappush(heap, (initial_priority, 0, next(counter), initial_board))
    seen.add(initial_board.hash())
    steps = 0

    while heap:
        priority, moves_count, _, board = heapq.heappop(heap)
        steps += 1

        if steps % 10 == 0:
            print(f"# Step {steps}, queue size {len(heap)}, moves {moves_count}")

        if moves_count >= max_steps:
            print(f"# Reached max_steps ({max_steps}), stopping search")
            break

        if board.is_solved():
            print(f"# Solution found in {board.move_sequence.n} steps")
            return board

        for r in range(rows):
            for c in range(cols):
                field = board.fields[r][c]
                if not field.is_clickable():
                    continue

                # onlyReachableFrom pruning
                if field.onlyReachableFrom != Position(15, 15):
                    if not (
                        r == field.onlyReachableFrom.row
                        and c == field.onlyReachableFrom.col
                    ):
                        continue

                new_board = board.copy()
                changed = new_board.click(r, c)
                if not changed:
                    continue

                code = new_board.hash()
                if code in seen:
                    continue
                seen.add(code)

                new_priority = new_board.move_sequence.n + heuristic(new_board)
                heapq.heappush(
                    heap,
                    (new_priority, new_board.move_sequence.n, next(counter), new_board),
                )

    print("# No solution found within max_steps")
    return None
