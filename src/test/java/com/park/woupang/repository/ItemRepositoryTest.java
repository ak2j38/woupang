package com.park.woupang.repository;

import com.park.woupang.constant.ItemSellStatus;
import com.park.woupang.entity.Item;
import com.park.woupang.entity.QItem;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    Item item;
    Item item2;

    @PersistenceContext
    EntityManager em;

    @BeforeEach
    void setUp() {
        item = new Item();
        item.setItemName("테스트용");
        item.setPrice(10000);
        item.setItemDetail("테스트용으로 등록한 상품");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(200);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        itemRepository.save(item);

        item2 = new Item();
        item2.setItemName("테스트용2");
        item2.setPrice(50000);
        item2.setItemDetail("테스트용으로 등록한 상품2");
        item2.setItemSellStatus(ItemSellStatus.SOLD_OUT);
        item2.setStockNumber(0);
        item2.setRegTime(LocalDateTime.now());
        item2.setUpdateTime(LocalDateTime.now());
        itemRepository.save(item2);

        for (int i = 3; i <= 12; i++) {
            Item newitem = new Item();
            newitem.setItemName("테스트용" + i);
            newitem.setPrice(10000 * i);
            newitem.setItemDetail("테스트용으로 등록한 상품" + i);
            if (i % 2 == 0) {
                newitem.setItemSellStatus(ItemSellStatus.SOLD_OUT);
            } else {
                newitem.setItemSellStatus(ItemSellStatus.SELL);
            }
            newitem.setStockNumber(i);
            newitem.setRegTime(LocalDateTime.now());
            newitem.setUpdateTime(LocalDateTime.now());
            itemRepository.save(newitem);
        }
    }

    @AfterEach
    void cleanUp() {
        itemRepository.deleteAll();
    }

    @Test
    @DisplayName("상품 저장 테스트")
    public void createItemTest() {
        List<Item> newItem = itemRepository.findByItemName("테스트용");

        assertThat(newItem.get(0).getItemName()).isEqualTo("테스트용");
        assertThat(newItem.get(0).getPrice()).isEqualTo(10000);
        assertThat(newItem.get(0).getStockNumber()).isNotEqualTo(150);
    }

    @Test
    @DisplayName("상품명으로 목록 출력 테스트")
    public void ListByItemNameTest() {
        assertThat(itemRepository.findByItemName("테스트용").toString()).isEqualTo(Arrays.asList(item).toString());
    }

    @Test
    @DisplayName("@Query를 이용한 상품상세정보 검색 테스트")
    public void ListByItemDetail() {
        assertThat(itemRepository.findByItemDetail("상품2").toString()).isEqualTo(Arrays.asList(item2).toString());
    }

    @Test
    @DisplayName("Querydsl 조회 테스트1")
    public void querydslTest() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QItem qItem = QItem.item;
        JPAQuery<Item> query = queryFactory.selectFrom(qItem)
                .where(qItem.itemSellStatus.eq(ItemSellStatus.SELL))
                .where(qItem.itemDetail.like("%" + "테스트" + "%"))
                .orderBy(qItem.price.desc());

        List<Item> itemList = query.fetch();

        for (Item eachItem : itemList) {
            System.out.println(eachItem.toString());
        }
    }

    @Test
    @DisplayName("Querydsl 조회 테스트2")
    public void querydslTest2() {
        BooleanBuilder bb = new BooleanBuilder();
        QItem qItem = QItem.item;

        String itemDetail = "테스트용으로 등록한 상품";
        int price = 10003;
        String itemSellStatus = "SELL";

        bb.and(qItem.itemDetail.like("%" + itemDetail + "%"));
        bb.and(qItem.price.gt(price));

        if(StringUtils.equals(itemSellStatus, ItemSellStatus.SELL)){
            bb.and(qItem.itemSellStatus.eq(ItemSellStatus.SELL));
        }

        Pageable pa = PageRequest.of(0, 5);
        Page<Item> itemPagingResult = itemRepository.findAll(bb, pa);
        List<Item> resultItemList = itemPagingResult.getContent();
        for (Item eachItem : resultItemList) {
            System.out.println(eachItem.toString());
        }
    }
}