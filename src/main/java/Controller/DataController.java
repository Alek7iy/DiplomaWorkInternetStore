package Controller;

import Frontend.ShopData;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.Order;
import entity.Product;
import entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import Exception.ResourceNotFoundException;
import Exception.ApiException;



import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/data")
public class DataController {

    private final ShopData shopData;

    public DataController() {
        try {
            this.shopData = loadShopDataFromJson();
        } catch (Exception e) {
            throw new ApiException("DATA_LOAD_ERROR", "Не удалось загрузить данные из JSON") {
            };
        }
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getProducts() {
        return ResponseEntity.ok(shopData.getProducts());
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getOrders() {
        return ResponseEntity.ok(shopData.getOrders());
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(shopData.getUsers());
    }

    @GetMapping("/full")
    public ResponseEntity<ShopData> getFullData() {
        if (shopData == null) {
            throw new ResourceNotFoundException("Данные не загружены");
        }
        return ResponseEntity.ok(shopData);
    }


    private ShopData loadShopDataFromJson() throws JsonMappingException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(
                    (getClass().getClassLoader().getResourceAsStream("data.json")),
                    ShopData.class);
        } catch (IOException e) {
            throw new JsonMappingException("Ошибка чтения JSON файла", e);
        }
    }
}
