package com.example.springbootjpatest.repository;


import com.example.springbootjpatest.domain.OrderModel;
import org.apache.commons.lang3.RandomStringUtils;
import org.aspectj.weaver.ast.Or;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public class OrderRepositoryTests {
    @Autowired
    private OrderRepository orderRepository;

    private OrderModel orderModel;

    @BeforeEach
    public void setUp() {
        String randomCode = RandomStringUtils.randomAlphanumeric(15);
        orderModel = OrderModel
                .builder()
                .orderCode(randomCode)
                .orderUsername("test_user")
                .shopName("test_shop")
                .build();
    }

    @DisplayName("주문 저장 테스트")
    @Test
    @Order(1)
    public void testSave() {
        System.out.println(">>> SetUp OrderModel: " + orderModel.toString());

        // given
        String randomCode = RandomStringUtils.randomAlphanumeric(15);
        orderModel = OrderModel
                .builder()
                .orderCode(randomCode)
                .orderUsername("test_user")
                .shopName("test_shop")
                .build();

        System.out.println(">>> Origin OrderModel: " + orderModel.toString());


        // when
        OrderModel savedOrderModel = orderRepository.save(orderModel);
        System.out.println(">>> Saved OrderModel: " + savedOrderModel);

        // then
        assertThat(savedOrderModel.getId()).isNotNull();
        assertThat(savedOrderModel.getOrderCode()).isEqualTo(randomCode);
        assertThat(savedOrderModel.getOrderUsername()).isEqualTo("test_user");
        assertThat(savedOrderModel.getShopName()).isEqualTo("test_shop");
        assertThat(savedOrderModel.getId()).isGreaterThan(0);

    }

    @DisplayName("주문 벌크 저장 테스트")
    @Test
    @Order(2)
    public void testBulkSave() {
        // given
        ArrayList<OrderModel> orderModelList = new ArrayList<OrderModel>();
        for (int i = 0; i < 100; i++) {
            String randomCode = RandomStringUtils.randomAlphanumeric(15);
            orderModel = OrderModel
                    .builder()
                    .orderCode(randomCode)
                    .orderUsername("test_user")
                    .shopName("test_shop")
                    .build();

            System.out.println(">>> Create OrderModel: " + orderModel.toString());
            orderModelList.add(orderModel);
        }

        orderRepository.saveAll(orderModelList);

        //when
        List<OrderModel> savedOrderModels = orderRepository.findAll();
        for (OrderModel orderModel : savedOrderModels) {
            System.out.println(">>> Saved #" + orderModel.getId() + " OrderModel: " + orderModel.toString());
        }

        //then
        for (OrderModel orderModel : savedOrderModels) {
            assertThat(orderModel.getId()).isNotNull();
            assertThat(orderModel.getOrderCode()).isNotNull();
            assertThat(orderModel.getOrderUsername()).isNotNull();
            assertThat(orderModel.getShopName()).isNotNull();
            assertThat(orderModel.getId()).isGreaterThan(0);
        }

    }

    @DisplayName("주문정보 전체 불러오기")
    @Test
    @Order(3)
    public void testReadAll() {
        //given
        ArrayList<OrderModel> orderModelList = new ArrayList<OrderModel>();
        for (int i = 0; i < 100; i++) {
            String randomCode = RandomStringUtils.randomAlphanumeric(15);
            orderModel = OrderModel
                    .builder()
                    .orderCode(randomCode)
                    .orderUsername("test_user")
                    .shopName("test_shop")
                    .build();

            System.out.println(">>> Create OrderModel: " + orderModel.toString());
            orderModelList.add(orderModel);
        }

        orderRepository.saveAll(orderModelList);

        //when
        List<OrderModel> orderModels = orderRepository.findAll();
        System.out.println("####### " + orderModels);
        //then
        assertThat(orderModels.size()).isGreaterThan(0);
        assertThat(orderModels).isNotNull();
    }

    @DisplayName("주문정보 Order Id로 불러오기")
    @Test
    @Order(4)
    public void testReadById() {

        //given
        int min = 0;
        int max = 0;


        ArrayList<OrderModel> orderModelList = new ArrayList<OrderModel>();
        for (int i = 0; i < 100; i++) {
            String randomCode = RandomStringUtils.randomAlphanumeric(15);
            orderModel = OrderModel
                    .builder()
                    .orderCode(randomCode)
                    .orderUsername("test_user")
                    .shopName("test_shop")
                    .build();

            System.out.println(">>> Create OrderModel: " + orderModel.toString());
            orderModelList.add(orderModel);
//            if (i == 0) {
//                System.out.println("$$$$$$$$$$$$$$$$$$$$$" +orderModel.getId());
//                min = orderModel.getId().intValue();
//            }
//            if (i == 99) {
//                max = orderModel.getId().intValue();
//            }
        }
        orderRepository.saveAll(orderModelList);

        min = orderRepository.findAll().get(0).getId().intValue();
        max = orderRepository.findAll().get(99).getId().intValue();


        // when
        int callId = (int) Math.floor(Math.random() * (max - min + 1) + min);
        System.out.println(">>> Call Id: " + callId);
        Optional<OrderModel> orderModel = orderRepository.findById((long) callId);
        System.out.println(">>> Read OrderModel: " + orderModel.toString());

        // then
        assertThat(orderModel.get().getId()).isEqualTo((long) callId);
        assertThat(orderModel.get().getOrderCode()).isNotNull();
        assertThat(orderModel.get().getOrderUsername()).isNotNull();
        assertThat(orderModel.get().getShopName()).isNotNull();
        assertThat(orderModel.get().getId()).isGreaterThan(0);


    }

}
