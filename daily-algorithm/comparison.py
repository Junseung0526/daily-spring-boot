from collections import deque

# 테스트 미로 (1: 길, 0: 벽)
graph = [
    [1, 1, 1],
    [1, 0, 1],
    [1, 1, 1]
]

n, m = 3, 3


# BFS
def solve_bfs(maze):
    q = deque([(0, 0)])
    dist = [[0] * m for _ in range(n)]
    dist[0][0] = 1
    while q:
        x, y = q.popleft()
        for dx, dy in [(-1, 0), (1, 0), (0, -1), (0, 1)]:
            nx, ny = x + dx, y + dy
            if 0 <= nx < n and 0 <= ny < m and maze[nx][ny] == 1 and dist[nx][ny] == 0:
                dist[nx][ny] = dist[x][y] + 1
                q.append((nx, ny))
    return dist[n - 1][m - 1]


# DFS
visited = [[False] * m for _ in range(n)]


def solve_dfs(x, y):
    if x < 0 or x >= n or y < 0 or y >= m or graph[x][y] == 0 or visited[x][y]:
        return False
    visited[x][y] = True
    print(f"DFS 방문: ({x}, {y})")  # 방문 경로 출력
    solve_dfs(x + 1, y)
    solve_dfs(x - 1, y)
    solve_dfs(x, y + 1)
    solve_dfs(x, y - 1)
    return True


print("=== BFS 최단거리 ===")
print(solve_bfs(graph))

print("\n=== DFS 방문순서 ===")
solve_dfs(0, 0)