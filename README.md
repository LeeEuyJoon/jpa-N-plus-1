## JPA N + 1 Problem Experiment

간단하게 JPA의 N+1 문제가 발생하는 여러 케이스를 실험하고 정리하기 위한 프로젝트입니다.

<br>

## Entity
- **Member**
  - `@ManyToOne(fetch = LAZY/EAGER)` Team
  - `@ManyToOne(fetch = LAZY/EAGER)` Major
- **Team**
- **Major**

<br>

## 실험 시나리오

총 12가지 경우로 나눠서 진행했습니다.
기본적으로 모든 멤버를 조회하는 흐름이며, 멤버 정보만 조회하는지, 관련 Team과 Major까지 조회하는지의 분기를 두고 있습니다.
이 시나리오를 `LAZY` 로딩과 `EAGER` 로딩으로 나누어 진행합니다.

1. 조회 방식
   - `findAll()`
   - JPQL 단순 조회 (`SELECT m FROM Member m`)
   - JPQL + Fetch Join (`JOIN FETCH`)

2. Fetch 전략
   - `LAZY`
   - `EAGER`

3. 연관 엔티티 접근 여부
   - `X` → 단순히 `member.getName()` 만 사용
   - `O` → DTO 변환 과정에서 `member.getTeam().getName()`, `member.getMajor().getName()` 접근
  
<br>

## 테스트 데이터
Member를 세 명으로 두고, Team과 Major는 멤버마다 모두 고유하도록 설정했습니다. <br>
적은 Member 수 안에서 N+1 문제 발생 여부에 따라 가장 극단적인 쿼리 수 차이를 유도하기 위함입니다.

<br>

## 실험 결과
| 조회 방법 | Fetch 전략 | 연관 접근 여부 | 실행 쿼리 수 |
| --- | --- | --- | --- |
| **findAll()** | LAZY | ❌ | 1 |
| **findAll()** | LAZY | ⭕ | 1 + N(3) + N(3) = 7 |
| **findAll()** | EAGER | ❌ | 1 + N(3) + N(3) = 7 |
| **findAll()** | EAGER | ⭕ | 1 + N(3) + N(3) = 7 |
| **JPQL (SELECT m)** | LAZY | ❌ | 1 |
| **JPQL (SELECT m)** | LAZY | ⭕ | 1 + N(3) + N(3) = 7 |
| **JPQL (SELECT m)** | EAGER | ❌ | 1 + N(3) + N(3) = 7 |
| **JPQL (SELECT m)** | EAGER | ⭕ | 1 + N(3) + N(3) = 7 |
| **JPQL + Fetch Join** | LAZY | ❌ | 1 |
| **JPQL + Fetch Join** | LAZY | ⭕ | 1 |
| **JPQL + Fetch Join** | EAGER | ❌ | 1 |
| **JPQL + Fetch Join** | EAGER | ⭕ | 1 |

`JpaRepository`의 `findAll()`을 사용하는 것과 `JPQL`로 직접 select문을 작성하는 것에 대해 결과는 차이가 없었습니다.<br>
두 방식의 동작이 같아 결과적으로 동일한 JPQL(SELECT m FROM Member m)을 실행하기 때문입니다.

페치 타입이 `LAZY` 로딩인지 `EAGER` 로딩인지에 대한 결과 차이는 연관 접근이 없는 경우에 대해 `findAll()` 조회와 페치 조인을 사용하지 않는 `JPQL` 조회 방식에서 드러났습니다.<br>
`LAZY`의 경우 `findAll()`이나 단순 JPQL로 조회하면 `Member`만 select하고, `Team`과 `Major`는 프록시로 남아 있어 추가 쿼리가 발생하지 않습니다.<br>
`EAGER`의 경우 `Member`를 조회한 직후 연관 엔티티를 즉시 초기화하기 위해 `Team`과 `Major`를 각각 추가 select문으로 불러오므로, N+1 문제가 발생합니다.

연관 접근이 있는 경우 페치 조인을 사용하지 않는 경우는 `LAZY`/`EAGER` 할 것 없이 모두 N+1 문제가 발생합니다.<br>
차이가 있다면 `Team`과 `Major`를 select 하는 쿼리가 실행되는 시점일텐데,<br>
`LAZY` 로딩의 경우 최초 `Member`를 조회할 때 `Team`과 `Major`를 프록시로 두고 이후 dto 변환 과정에서 `.getTeam().getName()`과 `.getMajor().getName()`이 실행될 때 실제 `Team`과 `Major` 엔티티를 조회하는 흐름이라면,
`EAGER` 로딩의 경우는 애당초 `Member`를 조회한 직후 `Team`과 `Major`를 모두 실제 엔티티로 추가 조회한다는 점에서 차이가 있습니다.

12가지 모든 요청에서 발생한 쿼리는 아래 주소에 정리해두었습니다. <br>
https://noon-blizzard-1ca.notion.site/N-1-25825c4dfa9f80adae64e6f9ba4537a4?source=copy_link

<br>

## 결론

실험을 진행하기 전에는 결과가 단순히 "LAZY라서 N+1이 발생한다" 또는 "EAGER라서 N+1이 발생한다"와 같이 피상적으로 설명될 것이라 생각했습니다.<br>
또한 JPQL을 사용하는 것 자체가 원인이 될 수도 있다는 오해도 있었습니다.<br>
본질은 페치 타입이나 JPQL 자체가 아니라 연관 엔티티 로딩이 개별 쿼리로 반복 실행되는 상황에 있었으며,<br>
`LAZY`는 접근 시점에서 프록시 초기화가 반복되면서 N+1이 발생하고<br>
`EAGER`는 접근 여부와 상관 없이 즉시 로딩을 보장하기 위해 추가 쿼리가 발생함을 이해했습니다.<br>
때문에 이런 상황에서는 Fetch Join과 같은 최적화 기법을 통해서만 문제를 실질적으로 해결할 수 있음을 확인했습니다.

물론 동일한 상황에서 `LAZY` 로딩에서는 발생하지 않는 N+1 문제가 `EAGER` 로딩에서 발생하는 경우가 있습니다.<br>
또한 `EAGER` 로딩의 경우 접근 여부와 상관없이 쿼리를 추가 실행하기 때문에 쿼리 최적화 제어권을 개발자가 갖기 어렵습니다.<br>
이러한 이유로 대부분의 상황에서는 연관관계의 페치 타입을 `LAZY`로 두는 것이 안전하고 예측 가능하다고 볼 수 있겠습니다.

<br>


