package com.keduit.shop.repository;

import com.keduit.shop.constant.ItemSellStatus;
import com.keduit.shop.dto.ItemSearchDTO;
import com.keduit.shop.dto.MainItemDTO;
import com.keduit.shop.dto.QMainItemDTO;
import com.keduit.shop.entity.Item;
import com.keduit.shop.entity.QItem;
import com.keduit.shop.entity.QItemImg;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

// ItemRepositoryCustom의 구현체에는 ~~~~~Impl을 붙여야 잘 작동 함.
public class ItemRepositoryCustomImpl implements ItemRepositoryCustom{

  //    동적 쿼리를 생성하기 위한 클래스
  private JPAQueryFactory queryFactory; //jpaDLS 를 쓰지 않은 이유?

//  ItemRepositoryCustomImpl 클래스는 EntityManager가 필요하고,
//  queryFactory를 생성함.
  public ItemRepositoryCustomImpl(EntityManager em){
    this.queryFactory = new JPAQueryFactory(em);
  }

//  상품 판매 상태 조건이 전체이면 null을 리턴되고 결과값이 null이면 where절에서 해당 조건은 무시됨.
//  상태조건이 있으면 해당 조건을 사용하여 where구문이 만들어짐.
  private BooleanExpression searchSellStatuEq(ItemSellStatus searchSellStatus){
    return searchSellStatus == null? null: QItem.item.itemSellStatus.eq(searchSellStatus);
  }

  private BooleanExpression regDtsAfter(String searchDateType){
    LocalDateTime dateTime = LocalDateTime.now();
    if (StringUtils.equals("all", searchDateType) || searchDateType == null) {
      return null;
    } else if(StringUtils.equals("1d", searchDateType)){
      dateTime = dateTime.minusDays(1);
    } else if(StringUtils.equals("1w", searchDateType)) {
      dateTime = dateTime.minusWeeks(1);
    } else if(StringUtils.equals("1m", searchDateType)) {
      dateTime = dateTime.minusMonths(1);
    } else if(StringUtils.equals("6m", searchDateType)){
      dateTime = dateTime.minusMonths(6);
    }
    return QItem.item.regTime.after(dateTime);
  }

  private BooleanExpression searchByLike(String searchBy, String searchQuery){
   if(StringUtils.equals("itemNm", searchBy)){
     return QItem.item.itemNm.like("%" + searchQuery + "%");
   } else if (StringUtils.equals("createBy", searchBy)){
     return QItem.item.createdBy.like("%" + searchQuery + "%");
   }
   return null;
  }
  @Override
  public Page<Item> getAdminItemPage(ItemSearchDTO itemSearchDTO, Pageable pageable) {

    System.out.println("itemSearchDTO-----> " + itemSearchDTO);
    System.out.println("pageable------> " + pageable);
    List<Item> result = queryFactory
        .selectFrom(QItem.item)
        .where(regDtsAfter(itemSearchDTO.getSearchDateType()),
            searchSellStatuEq(itemSearchDTO.getSearchSellStatus()),
            searchByLike(itemSearchDTO.getSearchBy(),
                itemSearchDTO.getSearchQuery())
            )
        .orderBy(QItem.item.id.desc())
        .offset(pageable.getOffset())   // 데이터를 가지고 올 시작 인덱스
        .limit(pageable.getPageSize())   // 한 번에 가지고 올 최대 갯수
        .fetch();

    long total = queryFactory
        .select(Wildcard.count)
        .from(QItem.item)
        .where(regDtsAfter(itemSearchDTO.getSearchDateType()),
            searchSellStatuEq(itemSearchDTO.getSearchSellStatus()),
            searchByLike(itemSearchDTO.getSearchBy(),
                itemSearchDTO.getSearchQuery()))
        .fetchOne();    // 하나의 결과를 가져옴.

    return new PageImpl<>(result, pageable, total);
  }

  @Override
  public Page<MainItemDTO> getMainItemPage(ItemSearchDTO itemSearchDTO, Pageable pageable) {
    QItem item = QItem.item;
    QItemImg itemImg = QItemImg.itemImg;

    List<MainItemDTO> result = queryFactory
            .select(
                    new QMainItemDTO(item.id, item.itemNm, item.itemDetail, itemImg.imgUrl, item.price)
            ).from(itemImg)
            .join(itemImg.item, item)
            .where(itemImg.repimgYn.eq("Y"))
            .where(itemNmlike(itemSearchDTO.getSearchQuery()))
            .orderBy(item.id.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

            long total = queryFactory
                    .select(Wildcard.count)
                    .from(itemImg)
                    .join(itemImg.item, item)
                    .where(itemImg.repimgYn.eq("Y"))
                    .where(itemNmlike(itemSearchDTO.getSearchQuery()))
                    .fetchOne();

    return new PageImpl<>(result, pageable, total);
  }

  private Predicate itemNmlike(String searchQuery) {
    return StringUtils.isEmpty(searchQuery) ? null :
            QItem.item.itemNm.like("%" + searchQuery + "%");
  }
}
