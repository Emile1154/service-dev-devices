package ru.emiljan.servicedevdevices.services;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.emiljan.servicedevdevices.models.Status;
import ru.emiljan.servicedevdevices.models.order.CustomOrder;
import ru.emiljan.servicedevdevices.models.order.FileInfo;
import ru.emiljan.servicedevdevices.models.order.TransferInfo;
import ru.emiljan.servicedevdevices.repositories.orderRepo.FileRepository;
import ru.emiljan.servicedevdevices.repositories.orderRepo.OrderRepository;
import ru.emiljan.servicedevdevices.services.notify.NotifyService;
import ru.emiljan.servicedevdevices.specifications.OrderSpecifications;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author EM1LJAN
 */
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final NotifyService notifyService;
    private final TransferInfo transferInfo;
    private final FileRepository fileRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        NotifyService notifyService,
                        TransferInfo transferInfo,
                        FileRepository fileRepository) {
        this.orderRepository = orderRepository;
        this.notifyService = notifyService;
        this.transferInfo = transferInfo;
        this.fileRepository = fileRepository;
    }

    public CustomOrder findById(Long id){
        return orderRepository.findById(id).orElse(null);
    }

    public List<CustomOrder> findAllOrders(){
        return orderRepository.findAll();
    }

    @Transactional
    public void deleteOrderById(Long id){
        orderRepository.deleteById(id);
    }

    public List<CustomOrder> findOrdersByKeyword(List<String> columns, String keyword){
        return orderRepository.findAll(OrderSpecifications.findByKeyword(keyword, columns));
    }

    public boolean checkTitle(CustomOrder order){
        if(orderRepository.findOrderByTitle(order.getTitle()) == null){
            return false;
        }
        return true;
    }

    @Transactional
    public void saveOrder(CustomOrder order){
        order.setOrderStatus(Status.NEW);

        notifyService.createNotify("order",order.getUser());
        orderRepository.save(order);
    }

    public List<CustomOrder> findOrdersByUserId(Long id){
        return orderRepository.findOrderByUserId(id);
    }

    @Transactional
    public FileInfo uploadFile(MultipartFile file) throws IOException {
        if(file.getSize() == 0){
            throw new FileUploadException("Загрузите файл");
        }
        final String path = transferInfo.getPath();
        final String contentType = file.getContentType();
        if(transferInfo.getAllowedTypes().stream().noneMatch(type->contentType.contains(type))){
            throw new FileUploadException("Ошибка формата, поддерживаются только pdf/word/doc");
        }
        Path dir = Path.of(path);
        if(Files.notExists(dir)){
            Files.createDirectory(dir);
        }
        String filename = createFilename()+getSuffixFile(file.getOriginalFilename());
        Path absolutePath = Path.of(path + filename);
        Files.copy(file.getInputStream(),absolutePath);
        return fileRepository.save(FileInfo.builder()
                            .filename(filename)
                            .contentType(contentType)
                        .build());
    }
    private String createFilename(){
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yy"));
        return "id#"+(this.orderRepository.getMaxId()+1)+"_"+date;
    }

    private String getSuffixFile(String filename){
        int dotIndex = filename.lastIndexOf(".");
        if(dotIndex != 0){
            return filename.substring(dotIndex);
        }
        return null;
    }

    @Transactional
    public void update(CustomOrder order, Status status){
        if(order==null){
            return;
        }
        order.setOrderStatus(status);
        orderRepository.save(order);
    }

    @Transactional
    public void update(CustomOrder order, BigDecimal price) {
        if(order==null){
            return;
        }
        order.setOrderStatus(Status.ACCEPTED);
        order.setPrice(price);
        orderRepository.save(order);
        notifyService.createNotify("info", order.getUser());
    }
}
