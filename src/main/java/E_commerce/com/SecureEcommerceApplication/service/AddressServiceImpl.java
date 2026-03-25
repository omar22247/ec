package E_commerce.com.SecureEcommerceApplication.service;

import E_commerce.com.SecureEcommerceApplication.dto.request.AddressRequest;
import E_commerce.com.SecureEcommerceApplication.dto.response.AddressResponse;
import E_commerce.com.SecureEcommerceApplication.entity.Address;
import E_commerce.com.SecureEcommerceApplication.entity.User;
import E_commerce.com.SecureEcommerceApplication.exception.BusinessException;
import E_commerce.com.SecureEcommerceApplication.exception.ResourceNotFoundException;
import E_commerce.com.SecureEcommerceApplication.exception.UnauthorizedException;
import E_commerce.com.SecureEcommerceApplication.repository.AddressRepository;
import E_commerce.com.SecureEcommerceApplication.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    // max addresses per user
    private static final int MAX_ADDRESSES = 5;

    private final AddressRepository addressRepository;

    // ════════════════════════════════════════════════════════
    //  READ
    // ════════════════════════════════════════════════════════

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> getAddresses(Long userId) {
        return addressRepository.findAllByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AddressResponse getAddressById(Long userId, Long addressId) {
        return toResponse(findAddressOrThrow(userId, addressId));
    }

    // ════════════════════════════════════════════════════════
    //  WRITE
    // ════════════════════════════════════════════════════════

    @Override
    @Transactional
    public AddressResponse createAddress(Long userId, AddressRequest request) {

        // limit max addresses per user
        if (addressRepository.countByUserId(userId) >= MAX_ADDRESSES) {
            throw new BusinessException(
                    "Maximum " + MAX_ADDRESSES + " addresses allowed per account");
        }

        // if this is the first address or marked as default
        boolean shouldBeDefault = request.isDefault() ||
                addressRepository.countByUserId(userId) == 0;

        // clear existing default if needed
        if (shouldBeDefault) {
            addressRepository.clearDefaultAddress(userId);
        }

        Address address = Address.builder()
                .user(User.builder().id(userId).build())
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .street(request.getStreet())
                .city(request.getCity())
                .country(request.getCountry())
                .zipCode(request.getZipCode())
                .isDefault(shouldBeDefault)
                .build();

        address = addressRepository.save(address);
        log.info("Address created: id={}, userId={}", address.getId(), userId);

        return toResponse(address);
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(Long userId, Long addressId,
                                         AddressRequest request) {

        Address address = findAddressOrThrow(userId, addressId);

        // if setting as default — clear old default first
        if (request.isDefault() && !address.isDefault()) {
            addressRepository.clearDefaultAddress(userId);
        }

        address.setFullName(request.getFullName());
        address.setPhone(request.getPhone());
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setCountry(request.getCountry());
        address.setZipCode(request.getZipCode());
        address.setDefault(request.isDefault());

        address = addressRepository.save(address);
        log.info("Address updated: id={}", addressId);

        return toResponse(address);
    }

    @Override
    @Transactional
    public void deleteAddress(Long userId, Long addressId) {

        Address address = findAddressOrThrow(userId, addressId);

        // cannot delete the default address — set another as default first
        if (address.isDefault()) {
            throw new BusinessException(
                    "Cannot delete default address. " +
                            "Please set another address as default first.");
        }

        address.setDeletedAt(LocalDateTime.now());
        addressRepository.save(address);
        log.info("Address soft-deleted: id={}", addressId);
    }

    @Override
    @Transactional
    public AddressResponse setDefaultAddress(Long userId, Long addressId) {

        Address address = findAddressOrThrow(userId, addressId);

        if (address.isDefault()) {
            throw new BusinessException("Address is already set as default");
        }

        // clear current default
        addressRepository.clearDefaultAddress(userId);

        // set new default
        address.setDefault(true);
        address = addressRepository.save(address);
        log.info("Default address set: id={}, userId={}", addressId, userId);

        return toResponse(address);
    }

    // ════════════════════════════════════════════════════════
    //  Private helpers
    // ════════════════════════════════════════════════════════

    // finds address and verifies it belongs to this user
    private Address findAddressOrThrow(Long userId, Long addressId) {
        return addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Address", "id", addressId));
    }

    private AddressResponse toResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .fullName(address.getFullName())
                .phone(address.getPhone())
                .street(address.getStreet())
                .city(address.getCity())
                .country(address.getCountry())
                .zipCode(address.getZipCode())
                .isDefault(address.isDefault())
                .build();
    }
}