# multi_solver_parallel.py
from board import Board, Position, rows, cols, max_steps
from multiprocessing import Pool, Manager
import itertools
import copy
from collections import deque
import heapq


# ------------------------------
# DFS Solver
# ------------------------------
def dfs_solver(initial_board: Board):
    best_solution = None
    visited = set()

    def dfs(board: Board):
        nonlocal best_solution
        if board.move_sequence.n > max_steps:
            return

        if board.is_solved():
            if (
                best_solution is None
                or board.move_sequence.n < best_solution.move_sequence.n
            ):
                best_solution = copy.deepcopy(board)
            return

        board_hash = board.hash()
        if board_hash in visited:
            return
        visited.add(board_hash)

        for r in range(rows):
            for c in range(cols):
                f = board.fields[r][c]
                if not f.is_clickable():
                    continue

                if f.onlyReachableFrom != Position(15, 15):
                    if r != f.onlyReachableFrom.row or c != f.onlyReachableFrom.col:
                        continue

                new_board = copy.deepcopy(board)
                changed = new_board.click(r, c)
                if changed:
                    dfs(new_board)

    dfs(initial_board)
    return best_solution


# ------------------------------
# BFS Solver
# ------------------------------
def bfs_solver(initial_board: Board):
    queue = deque()
    visited = set()

    queue.append(initial_board)
    visited.add(initial_board.hash())

    while queue:
        board = queue.popleft()
        if board.is_solved():
            return board
        if board.move_sequence.n >= max_steps:
            continue

        for r in range(rows):
            for c in range(cols):
                f = board.fields[r][c]
                if not f.is_clickable():
                    continue

                if f.onlyReachableFrom != Position(15, 15):
                    if r != f.onlyReachableFrom.row or c != f.onlyReachableFrom.col:
                        continue

                new_board = copy.deepcopy(board)
                changed = new_board.click(r, c)
                if not changed:
                    continue

                h = new_board.hash()
                if h in visited:
                    continue
                visited.add(h)
                queue.append(new_board)
    return None


# ------------------------------
# A* / Branch-and-Bound Solver
# ------------------------------
def branch_bound_solver(initial_board: Board):
    heap = []
    counter = itertools.count()
    seen = set()

    def heuristic(board: Board):
        return sum(1 for row in board.fields for f in row if not f.is_correct())

    heapq.heappush(heap, (heuristic(initial_board), 0, next(counter), initial_board))
    seen.add(initial_board.hash())

    while heap:
        priority, moves_count, _, board = heapq.heappop(heap)
        if moves_count >= max_steps:
            continue
        if board.is_solved():
            return board

        for r in range(rows):
            for c in range(cols):
                f = board.fields[r][c]
                if not f.is_clickable():
                    continue

                if f.onlyReachableFrom != Position(15, 15):
                    if r != f.onlyReachableFrom.row or c != f.onlyReachableFrom.col:
                        continue

                new_board = copy.deepcopy(board)
                changed = new_board.click(r, c)
                if not changed:
                    continue

                h = new_board.hash()
                if h in seen:
                    continue
                seen.add(h)

                new_priority = new_board.move_sequence.n + heuristic(new_board)
                heapq.heappush(
                    heap,
                    (new_priority, new_board.move_sequence.n, next(counter), new_board),
                )

    return None


# ------------------------------
# Parallel solver with immediate printing
# ------------------------------
def _solver_worker(board, solver_func, solver_name, return_dict):
    solution = solver_func(board)
    if solution:
        print(f"# {solver_name} finished with {solution.move_sequence.n} moves")
        return_dict[solver_name] = solution
    else:
        print(f"# {solver_name} finished with no solution")
    return solution


def solve_with_all_strategies_parallel(board: Board):
    """
    Runs DFS, BFS, and A* in parallel and returns the shortest solution.
    Prints each solver's result immediately.
    """
    solvers = [("DFS", dfs_solver), ("BFS", bfs_solver), ("A*", branch_bound_solver)]

    manager = Manager()
    return_dict = manager.dict()

    with Pool(processes=len(solvers)) as pool:
        results = []
        for name, func in solvers:
            r = pool.apply_async(_solver_worker, args=(board, func, name, return_dict))
            results.append(r)

        # Wait for all solvers to complete
        for r in results:
            r.wait()

    # Collect solutions from the return_dict
    solutions = list(return_dict.values())

    if not solutions:
        print("# No solution found by any strategy")
        return None

    # Pick the shortest move sequence
    best = min(solutions, key=lambda b: b.move_sequence.n)
    print(f"# Best solution uses {best.move_sequence.n} moves")
    return best
