package com.keduit.bpro00.repository;


import com.keduit.bpro00.entity.Memo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;

import javax.transaction.Transactional;
import java.beans.Transient;
import java.util.List;
import java.util.Optional;
import java.util.logging.SocketHandler;
import java.util.stream.IntStream;

@SpringBootTest
public class MemoRepositoryTests {

    @Autowired // 인터페이스 주입 memoRepository 로 모든 함수 다룰 수 있음.
            // 의존성 주입.
    MemoRepository memoRepository;

    @Test
    public void testClass(){
        System.out.println(memoRepository.getClass().getName());
    }

    @Test
    public void testInsertDummies(){
        IntStream.range(1, 10).forEach(i->{
           Memo memo = Memo.builder().memoText("Sample..." ).build();
           memoRepository.save(memo);

        });
    }

    @Test
    public void testSelect(){
        Long mno = 99L;

        // null 체크를 위한 optional , findById()가 optional을 return.
        Optional<Memo> result = memoRepository.findById(mno);

        System.out.println("--------------------------------");
        if(result.isPresent()){ // optional 클레스 함수. 값이 존재한다면 꺼낼 수 있음
            // 핵심은 null일 경우에도 행동을 정할 수 있고, 예외를 회피할 수 있음.
            Memo memo = result.get();
            System.out.println(memo);
        }
        System.out.println("-------------------------");
    }

    // 핵심 중 하나인 트렌젝션. 이것을 사용하면 함수 내 모든 작업들이 트렌젝션으로 묶임.
    // 함수 내 작업 실행 중 하나라도 예외 발생 시 함수 자체가 롤백됨.
    @Transactional
    @Test
    public void testSelect2(){
        Long mno = 79L;

        // lazy 방식으로 쿼리를 처리 : 실제 객체가 필요한 순간까지 sql을 실행하지 않음.
        // findById(mno); 와 getOne() 의 차이이다.
        Memo memo = memoRepository.getOne(mno);

        System.out.println("-----------------------");
        System.out.println(memo);
        System.out.println("--------------------");
    }


    // wlfa
    @Test
    public void testUpdate(){
        // builder(). 필드에 접근하겠다. 그래서 mno값을 다룰 수 있음.
        // memoRepository 를 사용 전 빌더 부분은 어떨때 사용하는지? 질문.
        Memo memo = Memo.builder().mno(99L).memoText("update test....").build();

        System.out.println("ddddddddddddddddd"+memoRepository.save(memo));

    }

    @Test
    public  void delete(){
        memoRepository.deleteById(89l);

    }

    @Test
    public void testPageDefault(){
        Pageable pageable = PageRequest.of(9,10); // 1page 10개
        Page<Memo> result = memoRepository.findAll(pageable);
        System.out.println(result);
        System.out.println("----------------------------");
        System.out.println("total pages: "+result.getTotalPages());
        System.out.println("total count " + result.getTotalElements());
        System.out.println("현재 페이지 번호 : " + result.getNumber());
        System.out.println("페이지 당 데이터 갯수 : " + result.getSize());
        System.out.println("다음페이지의 존재 여부 : " + result.hasNext());
        System.out.println("시작페이지 여부 : " + result.isFirst() );
        System.out.println("마지막 페이지 여부 : " + result.isLast() );
        System.out.println("현제 페이지의 데이터 갯수 : " + result.getNumberOfElements());
        System.out.println("getContent"+result.getContent());

    }

    @Test
    public void testSort(){
        Sort sort1 = Sort.by("mno").descending();
        Sort sort2 = Sort.by("memoText").ascending();
        Sort sortAll = sort1.and(sort2);
        // 질문. mno 값이 같을땐 sort2 방식으로 정렬한다는것?

//        Pageable pageable = PageRequest.of(0,10,sort1);
        Pageable pageable = PageRequest.of(0,10,sortAll);
        Page<Memo> result = memoRepository.findAll(pageable);

        result.get().forEach(memo -> {
            System.out.println(memo);
        });
    }

    @Test
    public void testQueryMethods(){
        // between 이므로 2개의 파라미터 가짐. findBy는 접두사
        // 두 Mno 사이의 값을 찾겠다. 단. OrderBy + 정렬법. 여기선 Mno를 내림차 정렬.
        List<Memo> List = memoRepository.findByMnoBetweenOrderByMnoDesc(70L, 80L);

        for (Memo memo : List){
            System.out.println(memo);
        }
    }

    @Test
    public void testQueryMethodWithPagable(){

        Pageable pageable = PageRequest.of(0,10,Sort.by("mno").descending());

        // 이런식으로 3가지 파라미터를 받아 원하는 정렬법, 혹은 Pageable 를 사용할 수 있다.
        Page<Memo> result = memoRepository.findByMnoBetween(10L,50L,pageable);

        result.get().forEach(memo -> System.out.println(memo));

        // 10부터 50까지이나 pagealbe 이며 디센딩이기에 마지막 페이지인 4페이지의 10개를 가져온다.
        // 결과값음 41뷰터 50까지만 가져옴.
    }


    @Test
    @Transactional // 삭제이므로 트렌젝셔널 필요.
    @Commit // 커밋이 없는 디폴트 상태는 삭제이미로 안전을 위한 커밋. 롤백기능이 있다. 그리고 커밋이 없을 시 적용되지않는다?
    public void testDeleteQueryMethods(){

        // memo 엔티티에서 10번 보다 작은애들은 다 삭제하겠다.
        memoRepository.deleteMemoByMnoLessThan(10L);

    }
}
