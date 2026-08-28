package Frontend;

import com.fasterxml.jackson.databind.ObjectMapper;
import entity.Order;
import entity.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/data")
public class DataController {

    private final ShopData shopData;

    public DataController() {
        this.shopData = loadShopDataFromJson();
    }

    @GetMapping("/full")
    public ResponseEntity<ShopData> getFullData() {
        return ResponseEntity.ok(shopData);
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getProducts() {
        return ResponseEntity.ok(shopData.getProducts());
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getOrders() {
        return ResponseEntity.ok(shopData.getOrders());
    }

    private ShopData loadShopDataFromJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(
                    (getClass().getClassLoader().getResourceAsStream("data.json")),
                    ShopData.class);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка загрузки JSON", e);
        }
    }
}

