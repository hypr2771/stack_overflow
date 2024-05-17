package net.so_default.so_78476856;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

@RestController
public class SO78476856 {

  private final InBetweenService inBetweenService;

  public SO78476856(InBetweenService inBetweenService) {
    this.inBetweenService = inBetweenService;
  }

  @GetMapping(path = "78476856")
  public Mono<ResponseEntity<NetworkTokenInformationResponse>> execute(@NonNull String gatewayToken) {
    return inBetweenService.execute(gatewayToken)
                           .map(ResponseEntity::ok)
                           .defaultIfEmpty(ResponseEntity.noContent().build());
  }

  public record NetworkTokenInformationResponse(String gatewayToken) {

  }

  public record Token(String gatewayToken) {

    NetworkTokenInformationResponse networkTokenInformationResponseExtracted() {
      return new NetworkTokenInformationResponse(gatewayToken);
    }
  }

  public static class TokenizationNotFoundException extends RuntimeException {

    public TokenizationNotFoundException() {
    }
  }
}

