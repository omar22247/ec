package E_commerce.com.SecureEcommerceApplication.service;

import E_commerce.com.SecureEcommerceApplication.entity.Inventory;
import E_commerce.com.SecureEcommerceApplication.exception.OutOfStockException;
import E_commerce.com.SecureEcommerceApplication.exception.ResourceNotFoundException;
import E_commerce.com.SecureEcommerceApplication.repository.InventoryRepository;
import E_commerce.com.SecureEcommerceApplication.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final InventoryRepository inventoryRepository;

    @Override
    @Transactional(readOnly = true)
    public void validateStock(Long productId, int quantityRequested) {

        // ONE query — findByProductId
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory", "productId", productId));

        if (inventory.getQuantity() <= 0) {
            throw new OutOfStockException(
                    inventory.getProduct().getName());
        }

        if (quantityRequested > inventory.getQuantity()) {
            throw new OutOfStockException(
                    inventory.getProduct().getName(),
                    quantityRequested,
                    inventory.getQuantity());
        }
    }
}