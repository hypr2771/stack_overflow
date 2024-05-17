package net.so_default.so_78490736;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class OldController {

  @GetMapping("/test_old")
  public Mono<Void> test() {
    return prepareForBooking();
  }

  public record Booking(String name, String age) {

  }

  private Mono<Void> prepareForBooking() {
    System.out.println("Booking::1");

    Booking booking0 = new Booking("San", "18");
    Booking booking1 = new Booking("Man", "19");
    Booking booking2 = new Booking("Dan", "18");
    Booking booking3 = new Booking("Can", "17");

    List<Booking> bookings = new ArrayList<>();
    bookings.add(booking0);
    bookings.add(booking1);
    bookings.add(booking2);
    bookings.add(booking3);

    return Flux.fromIterable(bookings)
               .collectList()
               .map(
                   bookingList -> {
                     var bookingsByAge =
                         bookingList.stream()
                                    .collect(Collectors.groupingBy(Booking::age));

                     return bookingsByAge.keySet().stream()
                                         .map(
                                             age -> {
                                               if (age.equals("18") || age.equals("17")) {
                                                 return processBookings(bookingsByAge.get(age));
                                               } else {
                                                 return Mono.empty();
                                               }
                                             });
                   })
               .flatMapMany(monoStream -> monoStream.reduce(Flux.<Object>empty(),
                                                            (flux, mono) -> flux.mergeWith(mono),
                                                            (flux, mono) -> flux.mergeWith(mono)))
               .single()
               .then();
  }

  private Mono<Void> processBookings(List<Booking> bookings) {

    System.out.println("Booking::2" + bookings);
    return Flux.fromIterable(bookings)
               .flatMap(booking -> this.processBooking(booking))
               .collectList()
               .flatMap(bookingNames -> {
                 System.out.println("Booking::4" + bookingNames);
                 return Mono.empty();
               });
  }

  private Mono<String> processBooking(Booking booking) {
    System.out.println("Booking::3" + booking.name());
    return Mono.just(booking.name());
  }

}
