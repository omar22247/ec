//package E_commerce.com.SecureEcommerceApplication.mapper;
//
//import E_commerce.com.SecureEcommerceApplication.dto.response.*;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//
//@Mapper(componentModel = "spring")
//public interface OrderMapper {
//
//    @Mapping(target = "address.fullName",          source = "projection.addressFullName")
//    @Mapping(target = "address.street",            source = "projection.addressStreet")
//    @Mapping(target = "address.city",              source = "projection.addressCity")
//    @Mapping(target = "address.country",           source = "projection.addressCountry")
//    @Mapping(target = "address.zipCode",           source = "projection.addressZipCode")
//    @Mapping(target = "address.phone",             source = "projection.addressPhone")
//    @Mapping(target = "shipment.status",           source = "projection.shipmentStatus")
//    @Mapping(target = "shipment.carrier",          source = "projection.shipmentCarrier")
//    @Mapping(target = "shipment.trackingNumber",   source = "projection.shipmentTrackingNumber")
//    @Mapping(target = "shipment.shippedAt",        source = "projection.shipmentShippedAt")
//    @Mapping(target = "shipment.estimatedDelivery",source = "projection.shipmentEstimatedDelivery")
//    @Mapping(target = "couponCode",                source = "projection.couponCode")
//    OrderResponse toResponse(OrderSummaryResponse projection);
//}