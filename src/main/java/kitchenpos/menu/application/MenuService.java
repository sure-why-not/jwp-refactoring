package kitchenpos.menu.application;

import kitchenpos.menu.dto.MenuProductRequest;
import kitchenpos.menu.dto.MenuRequest;
import kitchenpos.menu.dto.MenuResponse;
import kitchenpos.menu.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class MenuService {

    private final MenuGroupRepository menuGroupRepository;
    private final MenuRepository menuRepository;
    private final ProductRepository productRepository;
    private final MenuProductRepository menuProductRepository;

    public MenuService(MenuGroupRepository menuGroupRepository, MenuRepository menuRepository, ProductRepository productRepository, MenuProductRepository menuProductRepository) {
        this.menuGroupRepository = menuGroupRepository;
        this.menuRepository = menuRepository;
        this.productRepository = productRepository;
        this.menuProductRepository = menuProductRepository;
    }

    public MenuResponse create(final MenuRequest request) {
        List<MenuProduct> menuProducts = saveMenuProducts(request.getMenuProductRequests());
        MenuGroup menuGroup = menuGroupRepository.findById(request.getMenuGroupId()).orElseThrow(IllegalArgumentException::new);
        Menu menu = menuRepository.save(new Menu(request.getName(), request.getPrice(), menuGroup, menuProducts));
        return MenuResponse.of(menu);
    }

    private List<MenuProduct> saveMenuProducts(List<MenuProductRequest> requests) {
        return requests.stream().map(this::saveMenuProduct).collect(Collectors.toList());
    }

    private MenuProduct saveMenuProduct(MenuProductRequest request) {
        Product product = productRepository.findById(request.getProductId()).orElseThrow(IllegalArgumentException::new);
        return menuProductRepository.save(new MenuProduct(request.getSeq(), product, request.getQuantity()));
    }

    public List<MenuResponse> list() {
        return MenuResponse.listOf(menuRepository.findAll());
    }
}