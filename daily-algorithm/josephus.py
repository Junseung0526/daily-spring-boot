from collections import deque

def josephus_problem(n, k):
    dep = deque(range(1, n + 1))
    result = []

    while dep:
        for i in range(k - 1):
            person = dep.popleft()
            dep.append(person)

        remove_person = dep.popleft()
        result.append(remove_person)

    return result

print(josephus_problem(7, 3))




# rotate 사용
def josephus_with_rotate(n, k):
    deq = deque(range(1, n + 1))
    result = []

    while deq:
        deq.rotate(-(k - 1))

        removed_person = deq.popleft()
        result.append(removed_person)

    return result

n, k = 7, 3
print(f"제거 순서: {josephus_with_rotate(n, k)}")