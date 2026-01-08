# 스택(Stack)을 이용한 올바른 괄호

# def is_valid(string):
#     stack = []
#     for char in string:
#         if char == '(':
#             stack.append(char)
#             pass
#         elif char == ')':
#             if len(stack) != 0:
#                 stack.pop()
#             else:
#                 return False
#
#     return len(stack) == 0
#
# print(is_valid("(())()")) # Expected: True
# print(is_valid("(()"))    # Expected: False


# 심화 모든 닫는, 여는 괄호 응용 버전
def is_valid(string):
    stack = []
    mapping = {")": "(", "}": "{", "]": "["}

    for char in string:
        # 여는 괄호들
        if char in mapping.values():
            stack.append(char)

        # 닫는 괄호들
        elif char in mapping:
            if not stack or stack.pop() != mapping[char]:
                return False
    return not stack