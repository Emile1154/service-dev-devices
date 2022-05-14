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

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Service class for {@link ru.emiljan.servicedevdevices.models.order.CustomOrder}
 *
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
        return this.orderRepository.findById(id).orElse(null);
    }

    public List<CustomOrder> findAllOrders(){
        return this.orderRepository.findAll();
    }

    public File getFileByName(String filename){
        return new File(transferInfo.getPath()+filename);
    }

    @Transactional
    public void deleteOrderById(Long id){
        final CustomOrder order = this.orderRepository.getById(id);
        new File(transferInfo.getPath() + order.getFileInfo().getFilename()).delete();
        this.orderRepository.deleteById(id);
    }

    /**
     * this method filters orders by columns and keyword
     * @param columns
     * @param keyword
     * @return list of orders matching the specification
     */
    public List<CustomOrder> findOrdersByKeyword(List<String> columns, String keyword){
        return this.orderRepository.findAll(OrderSpecifications.findByKeyword(keyword, columns));
    }

    /**
     * Checks repeat order title
     * @param order {@link ru.emiljan.servicedevdevices.models.order.CustomOrder}
     * @return false - title not busy, true - title busy
     */
    public boolean checkTitle(CustomOrder order){
        if(this.orderRepository.findOrderByTitle(order.getTitle()) == null){
            return false;
        }
        return true;
    }

    /**
     * add new order to database
     * @param order {@link ru.emiljan.servicedevdevices.models.order.CustomOrder}
     */
    @Transactional
    public void saveOrder(CustomOrder order){
        order.setOrderStatus(Status.NEW);
        this.notifyService.createNotify("order",order.getUser(), null);
        this.orderRepository.save(order);
    }

    public List<CustomOrder> findOrdersByUserId(Long id){
        return this.orderRepository.findOrderByUserId(id);
    }

    /**
     * upload file method
     * @param file pdf/msdoc/vnd format file
     * @return {@link ru.emiljan.servicedevdevices.models.order.FileInfo}
     * @throws IOException
     */
    @Transactional
    public FileInfo uploadFile(MultipartFile file) throws IOException {
        if(file.getSize() == 0){
            throw new FileUploadException("Загрузите файл");
        }
        if(file.getSize() > MAX_UPLOAD_SIZE){
            throw new FileSizeLimitExceededException("Максимальный размер файла превышен", file.getSize(), MAX_UPLOAD_SIZE);
        }
        FileInfo fileInfo = this.uploadBuilder.uploadFile(file,transferInfo);
        this.fileService.save(fileInfo);
        return fileInfo;
    }

    /**
     * for progress bootstrap method
     * @param status {@link ru.emiljan.servicedevdevices.models.Status}
     * @return Integer value [0-100]
     */
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


    /**
     * set order status method
     * @param order {@link ru.emiljan.servicedevdevices.models.order.CustomOrder}
     * @param status {@link ru.emiljan.servicedevdevices.models.Status}
     * @param request for build link
     */
    @Transactional
    public void update(CustomOrder order, Status status, HttpServletRequest request){
        if(order==null){
            return;
        }
        order.setOrderStatus(status);
        this.orderRepository.save(order);
        this.notifyService.createNotify("info", order.getUser(),
                URIBuilder.buildURI(request, "/orders/"+order.getId()));
    }

    /**
     * set order price method
     * @param order {@link ru.emiljan.servicedevdevices.models.order.CustomOrder}
     * @param price price value
     * @param request for build link
     */
    @Transactional
    public void update(CustomOrder order, BigDecimal price, HttpServletRequest request) {
        if(order==null){
            return;
        }
        order.setOrderStatus(Status.ACCEPTED);
        order.setPrice(price);
        this.orderRepository.save(order);
        this.notifyService.createNotify("info", order.getUser(),
                URIBuilder.buildURI(request, "/orders/"+order.getId()));
    }
}
