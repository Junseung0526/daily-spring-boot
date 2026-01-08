# 문자열 입력받기
sentence = input()

# 공백 기준으로 쪼개서 리스트 만들기
words = sentence.split()

# 중간 점검: 리스트에 뭐가 들어있을까?
print(f"분리된 결과: {words}")

# 최종 개수 출력
print(f"단어 개수: {len(words)}")