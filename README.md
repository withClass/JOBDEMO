# nGrinder를 활용한 캐싱 적용 전후 성능 테스트

## 환경 구성

Docker Desktop 환경에서 `nGrinder`를 사용해 간단한 API의 캐싱 효과를 수치로 검증했습니다.  
컨트롤러와 에이전트를 각각 Docker 컨테이너로 구성했으며, 아래와 같이 `docker-compose`를 활용하여 실행 환경을 손쉽게 구축했습니다:

```yml
version: '3.8'
services:
  controller:
    image: ngrinder/controller
    restart: always
    ports:
      - "9000:80"
    volumes:
      - ngrinder-data:/opt/ngrinder-controller/repos

  agent:
    image: ngrinder/agent
    restart: always
    links:
      - controller

volumes:
  ngrinder-data:
```

---

## 테스트 목적

단순한 API 응답을 대상으로, **캐시 적용 전후의 성능 차이**를 nGrinder로 정량 분석했습니다.  
Postman을 통해 수동으로 응답 시간만 확인하던 기존 방식에서 벗어나, TPS, 평균 응답 시간, 에러율 등 **실제 부하 상황에서의 성능 지표**를 확보하는 것이 목적입니다.

---

## 테스트 시나리오

- 요청 수: 245명의 가상 유저, 1분간 지속
- 요청 방식: HTTP GET
- 테스트 조건:
  - **V1**: 캐시 미적용 (DB 직접 조회)
  - **V2**: 캐시 적용 (Redis 캐시)

---

## 테스트 결과

### V1 – 캐시 미적용

![V1 결과](https://velog.velcdn.com/images/pospara9356/post/c839b693-c057-419a-a0ed-b29cc2dd84a0/image.PNG)

- **평균 TPS**: 10.9
- **평균 응답 시간**: 3,203ms
- **총 요청 수**: 1,195
- **성공 요청**: 417
- **에러 수**: 778

> ⚠️ 처리량이 낮고 에러 비율이 상당히 높아, 실 운영에선 병목이 심각할 수 있는 구조입니다.

---

### V2 – 캐시 적용

![V2 결과](https://velog.velcdn.com/images/pospara9356/post/88b5cead-9470-4ec7-a5fe-29c73e81098c/image.PNG)

- **평균 TPS**: 69.3
- **평균 응답 시간**: 802ms
- **총 요청 수**: 9,347
- **성공 요청**: 3,342
- **에러 수**: 6,005

> ✅ TPS는 약 **6.3배 증가**, 평균 응답 시간은 **4배 이상 단축**되었습니다.  
> 전체적인 처리 능력은 **약 10배 가까이 향상**된 것으로 해석할 수 있습니다.

---

## 결론

이번 테스트를 통해, **캐시 적용이 시스템 처리 성능에 얼마나 큰 영향을 미치는지**를 명확히 확인할 수 있었습니다.  
단순한 응답 시간 체크(Postman 수준)를 넘어서, **부하 상황에서의 안정성, 처리량(TPS), 에러율 등 정량적 지표 확보가 매우 중요**함을 체감했습니다.

> 특히 실서비스에서는 캐시를 활용한 구조가 **서비스 확장성과 안정성 확보에 필수적**임을 입증한 사례였습니다.
