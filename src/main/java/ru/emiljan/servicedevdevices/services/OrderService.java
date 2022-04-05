package ru.emiljan.servicedevdevices.services;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.emiljan.servicedevdevices.models.Status;
import ru.emiljan.servicedevdevices.models.order.CustomOrder;
import ru.emiljan.servicedevdevices.models.order.FileInfo;
import ru.emiljan.servicedevdevices.models.order.TransferInfo;
import ru.emiljan.servicedevdevices.repositories.orderRepo.OrderRepository;
import ru.emiljan.servicedevdevices.services.notify.NotifyService;
import ru.emiljan.servicedevdevices.services.project.FileService;
import ru.emiljan.servicedevdevices.specifications.OrderSpecifications;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author EM1LJAN
 */
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final NotifyService notifyService;
    private final TransferInfo transferInfo;
    private final UploadBuilder uploadBuilder;
    private final FileService fileService;

    private static final long MAX_UPLOAD_SIZE = 5000000L;


    @Autowired
    public OrderService(OrderRepository orderRepository,
                        NotifyService notifyService,
                        @Qualifier("transferOrder") TransferInfo transferInfo,
                        UploadBuilder uploadBuilder, FileService fileService) {
        this.orderRepository = orderRepository;
        this.notifyService = notifyService;
        this.transferInfo = transferInfo;
        this.uploadBuilder = uploadBuilder;
        this.fileService = fileService;
    }

    public CustomOrder findById(Long id){
        return orderRepository.findById(id).orElse(null);
    }

    public List<CustomOrder> findAllOrders(){
        return orderRepository.findAll();
    }

    public File getFileByName(String filename){
        return new File(transferInfo.getPath()+filename);
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
        if(file.getSize()>MAX_UPLOAD_SIZE){
            throw new FileSizeLimitExceededException("Максимальный размер файла превышен", file.getSize(), MAX_UPLOAD_SIZE);
        }
        FileInfo fileInfo = this.uploadBuilder.uploadFile(file,transferInfo);
        fileService.save(fileInfo);
        return fileInfo;
    }

    public int getValueStatus(Status status){
        if(Status.NEW == status){
            return 25;
        }
        if(Status.ACCEPTED == status){
            return 50;
        }
        if(Status.PAYED == status){
            return 75;
        }
        if(Status.CLOSED == status){
            return 100;
        }
        return 0;
    }


    @Transactional
    public void update(CustomOrder order, Status status){
        if(order==null){
            return;
        }
        order.setOrderStatus(status);
        orderRepository.save(order);
        notifyService.createNotify("info", order.getUser());
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
