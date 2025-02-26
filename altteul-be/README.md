# docker-compose 를 통한 local 개발 환경 구축
## docker-compose 띄우기

```
docker-compose up

# 완전 종료 : 모든 데이터 사라짐
docker-compose down

# DDL 새로 실행할 경우
1. docker-compose down -v
2. mysql의 data 폴더 내용 삭제
3. docker-compose up --build

# 뭔가 업데이트가 안되는 느낌 (캐시 자체를 삭제하고 새로 빌드)
docker-compose build --no-cache

# 일시 정지 : 데이터 유지
docker-compose stop
```

## 초기화 파일
* `mysql-init.d` : `*.sql` 파일들로 database 생성 등을 수행한다.

* 초기화 파일 수정시에는 `docker-compose down` 으로 완전 초기화를 해야한다.

```shell
docker exec -it mysql
mysql -u root -p
# 비밀번호 root 로 접속
```
