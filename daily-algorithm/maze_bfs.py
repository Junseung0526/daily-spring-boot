from collections import deque


def bfs_maze(maze, n, m):
    dx = [-1, 1, 0, 0]
    dy = [0, 0, -1, 1]

    queue = deque([(0, 0)])

    while queue:
        x, y = queue.popleft()

        for i in range(4):
            nx = x + dx[i]
            ny = y + dy[i]

            if 0 <= nx < n and 0 <= ny < m:
                if maze[nx][ny] == 1:
                    maze[nx][ny] = maze[x][y] + 1
                    queue.append((nx, ny))

    return maze[n - 1][m - 1]


example_maze = [
    [1, 1, 0, 1],
    [0, 1, 1, 0],
    [1, 1, 1, 1],
    [0, 0, 0, 1]
]

n, m = 4, 4
result = bfs_maze(example_maze, n, m)
print(f"최단 거리: {result}")
