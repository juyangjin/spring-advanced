# SPRING ADVANCED

## 크게 고쳐야 할 필요가 있는 부분들
- exception 메시지 한글화 (현재는 영어, 한국어 섞여 있음 전체 같은 형태로 통일 필요)
- else 지양하기
- jwtfilter 책임분리 (데이터 저장과 인가가 섞여있음)
- return을 entity로 통일할 것인지에 대한 문제(회원가입, 회원 로그인에 대하여 entity 형태가 아닌 response로 return이 되어있음)
- o to m 과 m to o를 동시에 사용 중인 todo entity 문제
- 
