package net.so_default.so_78490736;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.so_default.so_78490736.DefaultController.BookingResult.Status;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class DefaultController {

  @GetMapping("/test")
  public Mono<Void> test() {
    return prepareForBooking().then();
  }

  public record Booking(String name, String age) {

  }

  public record BookingResult(Booking booking, Status status) {

    public enum Status {
      SUCCESS,
      FAILURE,
      ;
    }

  }

  private Mono<List<BookingResult>> prepareForBooking() {
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
               .map(bookingList -> bookingList.stream().collect(Collectors.groupingBy(Booking::age)))
               .flatMap(bookingsByAge -> bookingsByAge.entrySet()
                                                      .stream()
                                                      .map(this::bookForValidCustomers)
                                                      .reduce(Flux.<BookingResult>empty(),
                                                              (listMono, listMono2) -> listMono.mergeWith(Flux.fromIterable(listMono2)
                                                                                                              .flatMap(Function.identity())),
                                                              Flux::mergeWith)
                                                      .collectList());
  }

  private List<Mono<BookingResult>> bookForValidCustomers(final Entry<String, List<Booking>> entry) {
    if (entry.getKey().equals("18") || entry.getKey().equals("17")) {
      return processBookings(entry.getValue());
    } else {
      return failBookings(entry.getValue());
    }
  }

  private List<Mono<BookingResult>> processBookings(List<Booking> bookings) {
    System.out.println("SuccessPath::2" + bookings);
    return bookings.stream()
                   .map(this::processBooking)
                   .toList();
  }

  private Mono<BookingResult> processBooking(Booking booking) {
    System.out.println("SuccessPath::3" + booking.name());
    return Mono.just(new BookingResult(booking, Status.SUCCESS));
  }

  private List<Mono<BookingResult>> failBookings(List<Booking> bookings) {
    System.out.println("FailurePath::2" + bookings);
    return bookings.stream()
                   .map(this::failBooking)
                   .toList();
  }

  private Mono<BookingResult> failBooking(Booking booking) {
    System.out.println("FailurePath::3" + booking.name());
    return Mono.just(new BookingResult(booking, Status.FAILURE));
  }

}
