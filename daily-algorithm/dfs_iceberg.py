import sys

# 재귀 한도를 100만 번으로 늘려줌
sys.setrecursionlimit(10**6)

def dfs(x, y, n, m, graph):
    if x <= -1 or x >= n or y <= -1 or y >= m:
        return False

    if graph[x][y] == 1:
        graph[x][y] = 0

        dfs(x - 1, y, n, m, graph)
        dfs(x + 1, y, n, m, graph)
        dfs(x, y - 1, n, m, graph)
        dfs(x, y + 1, n, m, graph)
        return True

    return False


graph = [
    [1, 1, 0, 0, 0],
    [0, 1, 1, 0, 0],
    [0, 0, 0, 1, 1],
    [1, 0, 1, 1, 1]
]

n, m = 4, 5
result = 0

for i in range(n):
    for j in range(m):
        if dfs(i, j, n, m, graph) == True:
            result += 1

print(f"전체 덩어리 개수: {result}")
