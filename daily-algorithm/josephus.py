"""
문제: 요세푸스 문제 (Josephus Problem)
주제: 자료구조 큐(Queue)를 활용한 원형 리스트 처리
학습 포인트:
    1. FIFO(First-In-First-Out) 구조의 이해
    2. collections.deque를 이용한 효율적인 데이터 회전 (O(1))
    3. 시간 복잡도 분석: O(N * K)
"""

from collections import deque

def josephus_problem(n, k):
    deq = deque(range(1, n + 1))
    result = []

    while deq:
        for _ in range(k - 1):
            person = deq.popleft()
            deq.append(person)

        removed_person = deq.popleft()
        result.append(removed_person)

    return result

print(josephus_problem(7, 3))

# rotate 사용
def solve_josephus_rotate(n, k):
    deq = deque(range(1, n + 1))
    result = []

    while deq:
        deq.rotate(-(k - 1))

        removed_person = deq.popleft()
        result.append(removed_person)

    return result


if __name__ == "__main__":
    n, k = 7, 3
    final_order = solve_josephus_rotate(n, k)

    print(f"<{', '.join(map(str, final_order))}>")