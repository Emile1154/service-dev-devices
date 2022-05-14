package ru.emiljan.servicedevdevices.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;
import ru.emiljan.servicedevdevices.models.Status;
import ru.emiljan.servicedevdevices.models.order.CustomOrder;
import ru.emiljan.servicedevdevices.models.order.FileInfo;
import ru.emiljan.servicedevdevices.models.order.TransferInfo;
import ru.emiljan.servicedevdevices.repositories.orderRepo.OrderRepository;
import ru.emiljan.servicedevdevices.services.notify.NotifyService;
import ru.emiljan.servicedevdevices.services.project.FileService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private NotifyService notifyService;

    @MockBean
    private UploadBuilder uploadBuilder;

    @MockBean
    @Qualifier("transferOrder")
    private TransferInfo transferInfo;

    @MockBean
    private FileService fileService;

    private CustomOrder order;

    @BeforeEach
    public void setUp(){
        order = CustomOrder.builder()
                .id(99L)
                .title("test")
                .description("this is a some description of test order")
                .build();
    }


    @Test
    void findById() {
        Long id = 99L;
        Mockito.when(orderRepository.findById(anyLong())).thenReturn(java.util.Optional.ofNullable(order));
        final CustomOrder f = this.orderService.findById(id);
        Assertions.assertEquals(id, f.getId());

    }

    @Test
    void saveOrder() {
        orderService.saveOrder(order);
        Assertions.assertEquals(Status.NEW, order.getOrderStatus());
        Mockito.verify(notifyService, Mockito.times(1)).createNotify(
                ArgumentMatchers.eq("order"),
                ArgumentMatchers.any(),
                ArgumentMatchers.isNull()
        );
        Mockito.verify(orderRepository,Mockito.times(1)).save(ArgumentMatchers.any());
    }

    @Test
    void uploadFile() throws IOException {
        File testFile = new File("./src/main/resources/resources/test/test1.txt");
        if (testFile.createNewFile()) {
            FileWriter writer = new FileWriter(testFile);
            writer.write("this is a test file");
            writer.close();
        }
        FileInputStream fileInputStream = new FileInputStream(testFile);

        MultipartFile mf =
                new MockMultipartFile(
                        "file",
                        testFile.getName(),
                        "text/plain",
                        fileInputStream
                );

        FileInfo file = orderService.uploadFile(mf);
        Mockito.verify(uploadBuilder, Mockito.times(1)).uploadFile(
                ArgumentMatchers.eq(mf),
                ArgumentMatchers.eq(transferInfo)
        );
        Mockito.verify(fileService, Mockito.times(1)).save(
                ArgumentMatchers.eq(file)
        );


    }

    @Test
    void updateStatus() {
        Status status = Status.CLOSED;
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/test/");

        orderService.update(order, status, request);
        Assertions.assertEquals(status, order.getOrderStatus());
        Mockito.verify(orderRepository, Mockito.times(1)).save(order);
        Mockito.verify(notifyService,Mockito.times(1)).createNotify(
                ArgumentMatchers.eq("info"),
                ArgumentMatchers.any(),
                ArgumentMatchers.isNotNull()
               );
    }

    @Test
    void updatePrice() {
        BigDecimal price = new BigDecimal(2500);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/test/");
        orderService.update(order, price, request);
        Assertions.assertEquals(Status.ACCEPTED, order.getOrderStatus());
        Assertions.assertEquals(price, order.getPrice());
        Mockito.verify(orderRepository, Mockito.times(1)).save(order);
        Mockito.verify(notifyService,Mockito.times(1)).createNotify(
                ArgumentMatchers.eq("info"),
                ArgumentMatchers.any(),
                ArgumentMatchers.isNotNull()
        );
    }

    @Test
    void updatePriceFail(){
        BigDecimal price = new BigDecimal(2500);
        orderService.update(null, price, null);
    }

    @Test
    void updateStatusFail(){
        Status status = Status.CLOSED;
        orderService.update(null, status, null);
    }
}