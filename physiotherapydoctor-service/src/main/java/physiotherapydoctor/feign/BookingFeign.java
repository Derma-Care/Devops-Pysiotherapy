package physiotherapydoctor.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import physiotherapydoctor.dto.BookingResponse;
import physiotherapydoctor.dto.ResponseStructure;

@FeignClient(name = "bookingservice")
public interface BookingFeign {

    @GetMapping("/api/v1/getBookedServiceById/{id}")
    ResponseStructure<BookingResponse> getBookingById(@PathVariable("id") String id);

    @PutMapping("/api/v1/updateAppointment")
	public ResponseEntity<?> updateAppointment(@RequestBody BookingResponse bookingResponse );
}